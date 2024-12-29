package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.FormBuilder;
import com.ontimize.cache.CacheManager;
import com.ontimize.cache.CachedComponent;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.util.ExportTxt;
import com.ontimize.gui.attachment.AttachmentButtonListener;
import com.ontimize.gui.attachment.AttachmentMenuListener;
import com.ontimize.gui.button.AttachmentButtonSelection;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.button.ButtonSelectionInternationalization;
import com.ontimize.gui.button.FormBundleButton;
import com.ontimize.gui.button.FormHeaderPopupButton;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.container.FormHeader;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.container.TabPanel;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.AdvancedDataComponent;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.HyperlinkComponent;
import com.ontimize.gui.field.IDefaultValueComponent;
import com.ontimize.gui.field.IFilterElement;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.field.ImageDataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.ReferenceComboDataField;
import com.ontimize.gui.field.ReferenceExtDataField;
import com.ontimize.gui.field.SubForm;
import com.ontimize.gui.field.SubFormComponent;
import com.ontimize.gui.field.TextFieldDataField;
import com.ontimize.gui.formtemplates.PopupPrintingTemplateList;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.BaseFormManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.AbstractApplicationPreferences;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.ApplicationPreferencesListener;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.db.SecureEntity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.DataFile;
import com.ontimize.jee.common.gui.attachment.AttachmentAttribute;
import com.ontimize.jee.common.gui.field.EntityFunctionAttribute;
import com.ontimize.jee.common.gui.field.MultipleReferenceDataFieldAttribute;
import com.ontimize.jee.common.gui.field.MultipleTableAttribute;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.ApplicationPermission;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.printing.AbstractPrintingElement;
import com.ontimize.printing.PrintingElement;
import com.ontimize.printing.TemplateElement;
import com.ontimize.report.ReportUtils;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.CollectionTools;
import com.ontimize.util.FileUtils;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.PreferenceUtils;
import com.ontimize.util.pdf.PdfFiller;
import com.ontimize.util.swing.CollapsibleButtonPanel;
import com.ontimize.util.swing.IdentifiedFocusTraversalPolicy;
import com.ontimize.util.swing.RolloverButton;
import com.ontimize.util.swing.border.SoftBevelBorder2;
import com.ontimize.util.swing.icon.CompoundIcon;
import com.ontimize.util.templates.TemplateUtils;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.ontimize.xml.XMLTemplateBuilder;

/**
 * The base class that is used for show a form that contains any set of components, data components,
 * reference components. The base component where any component is displayed.
 * <p>
 * The <code>Form</code> can be added to form xml definition by using the default XML label Form.
 * The structure is as follows:
 * <p>
 * &lt;Form {attribute=value} /&gt;
 * <P>
 * The attributes allowed can be found in the method {@link #init(Map params)} where
 * parameters contains the attributes information.
 */

public class Form extends JPanel implements FormComponent, CreateForms, OpenDialog, SelectCurrencyValues,
FontAndEncodingSelector, SecureElement, HasPreferenceComponent, Printable,
HasHelpIdComponent, Freeable {

	/**
	 * DEBUG mode. By default, false
	 */
	// public static boolean DEBUG = false;

	private static final Logger logger = LoggerFactory.getLogger(Form.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String FORM = "Form";

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String FORMBODYPANEL = "FormBodyPanel";

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.0.5
	 */
	public static final String FORMSCROLLPANE = "FormScrollPane";

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String FORMBUTTONPANEL = "FormButtonPanel";

	public static int defaultTableViewMinRowHeight = Table.MIN_ROW_HEIGHT;

	/**
	 * llt size for status bar buttons (navigation, delete fields, ...).
	 *
	 * @since 5.2060EN-0.6
	 */
	public static int defaultFormButtonSize = 22;

	protected int tableViewMinRowHeight;

	protected MouseListener listenerHighlightButtons;

	public static boolean defaultBorderButtons = true;

	protected boolean borderbuttons;

	public static boolean defaultOpaqueButtons = true;

	protected boolean opaquebuttons;

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String MULTIPLE_DATA = "multipledata";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TEMPLATES = "templates";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String ATTACHMENT = "attachment";

	/**
	 * Attribute name allowed in the XML to configure or not the database bundle configuration button.
	 *
	 * @see #init
	 */
	public static final String DATABASE_BUNDLE = "databasebundle";

	public static final String SCRIPT_ENABLED = "scriptenabled";

	public static final String PERMISSION_BUTTON = "permissionbutton";

	public static String defaultScriptButtonTip = "script.define_script_actions";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TITLE = "title";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TEMPLATE = "template";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String HEADER_PANEL_POSITION = "headerPanelPosition";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String PRINTABLE = "printable";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String ENTITY = "entity";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String SET_VALUE_ORDER = "setvalueorder";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String SCROLL = "scroll";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CUSTOMFOCUS = "customfocus";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String HELPKEY = "helpkey";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String STATUS = "status";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String BUTTONS = "buttons";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String RESOURCES = "resources";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String PARENT_KEYS = "parentkeys";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String KEYS = "keys";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String HELP_ID = "helpid";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TABLE_VIEW_SCROLL_H = "tableviewscrollh";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String COLUMNS = "columns";

	protected static final String M_RELOAD_FORM = "form.reload";

	/**
	 * Identifies the filling type will be used to fill the word templates.
	 */
	public static int WORD_TEMPLATES = 0;

	/**
	 * Identifies the filling type will be used to fill the word templates.
	 */
	public static int DOCX_TEMPLATES = 0;

	protected static final String FORM_DATA = "form.form_data";

	/**
	 * Establishes where the title is situated in. If the values is true, the form title is situated on
	 * top the navigation buttons.
	 */
	public static boolean TITLE_ON_TOP = false;

	/**
	 * Establishes the default margin value for all application forms.
	 */
	public static Insets DEFAULT_FORM_MARGIN = null;

	/**
	 * Establishes if the reload button is created when the form is built.
	 */
	public static boolean RELOAD_BUTTON_VISIBLE = false;

	/**
	 * Establishes the default value for the attachment button in all application. If the value is true
	 * the attachment button will be shown in the form whether the default attachments are configured.
	 */
	public static boolean DEFAULT_ATTACHMENT = true;

	public static boolean DEFAULT_DATABASE_BUNDLE = false;

	public static boolean defaultPermissionButton = true;

	public static boolean DEFAULT_SCRIPT_ENABLED = false;

	/**
	 * Established the default value for the template button in all application. If the value is true
	 * the template button will be shown in the form whether the default templates are configured.
	 */
	public static boolean DEFAULT_TEMPLATES = true;

	/**
	 * Established the default value for the behavior when a query with multiple results is done in each
	 * application form. If the value is true the table view with all results is showed
	 */

	public static boolean DEFAULT_TABLEVIEW_MULTIPLEDATA = true;

	/**
	 * Variable used to printing templates
	 */

	public static String TEMPLATE_ID = "TemplateId";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_WAREHOUSE = "TemplateWarehouse";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_FORM = "TemplateForm";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_DEFAULT = "TemplateDefault";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_USER = "TemplateUser";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_PRIVATE = "TemplatePrivate";

	/**
	 * Variable used to printing templates
	 */
	public static String TEMPLATE_NAME = "TemplateName";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_ID = "AttachmentId";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_KEYS = "AttachmentKeys";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_COLUMNS_KEYS = "AttachmentColumnsKeys";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_SOURCE_TABLE = "AttachmentSourceTable";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_DATE = "AttachmentDate";

	/**
	 * Constants used to form attachment
	 */
	public static final String PRIVATE_ATTACHMENT = "PrivateAttachment";

	/**
	 * Constants used to form attachment
	 */
	public static final String USER = "User";

	/**
	 * Constants used to form attachment
	 */
	public static final String DESCRIPTION_FILE = "DescriptionFile";

	/**
	 * Constants used to form attachment
	 */
	public static final String SIZE = "Size";

	/**
	 * Constants used to form attachment
	 */
	public static final String ORIGINAL_FILE_NAME = "OriginalFileName";

	/**
	 * Constants used to form attachment
	 */
	public static final String FILE_NAME = "FileName";

	/**
	 * Constants used to form attachment
	 */
	public static final String ATTACHMENT_ENTITY_NAME = "AttachmentEntityName";

	/**
	 * GUI text
	 */
	public static String messageAddTemplate = "form.add_template";

	/**
	 * GUI text
	 */
	public static String messageCreateWordTemplate = "form.create_word_template";

	/**
	 * GUI text
	 */
	public static String messageCreateDocxTemplate = "form.create_docx_template";

	/**
	 * GUI text
	 */
	public static String messageCreateOpenOfficeTemplate = "form.create_openoffice_template";

	/**
	 * GUI text
	 */
	public static String messageHelpTemplate = "form.template_help";

	/**
	 * GUI text
	 */
	public static String messageNameFieldTemplate = "form.templates_fields_must_be_one_of_next";

	/**
	 * GUI text
	 */
	public static String messageEmptyTemplate = "form.there_are_not_available_templates";

	/**
	 * GUI text
	 */
	public static String messageInfoTemplate = "form.find_?_templates";

	/**
	 * GUI text
	 */
	public static String messageDeletedTemplate = "form.templates_deleted_correctly";

	/**
	 * GUI text
	 */
	public static String messageQueryDeleted = "form.template_will_be_deleted";

	/**
	 * GUI text
	 */
	public static String messageInsertionSucessful = "form.template_has_been_added_correctly";

	/**
	 * GUI text
	 */
	public static String messageErrorInsert = "form.template_insert_error";

	/** GUI text */
	public static String messageErrorGeneratePDF = "form.error_to_generate_pdf_template";

	public static final int CONFIRMATION = 0;

	public static final int INPUT = 1;

	public static final int MESSAGE = 2;

	public static final int YES = MessageDialog.YES_OPTION;

	public static final int NO = MessageDialog.NO_OPTION;

	/** Used for information messages. */
	public static final int INFORMATION_MESSAGE = MessageDialog.INFORMATION_MESSAGE;

	/** Used for error messages. */
	public static final int ERROR_MESSAGE = MessageDialog.ERROR_MESSAGE;

	/** Used for warning messages. */
	public static final int WARNING_MESSAGE = MessageDialog.WARNING_MESSAGE;

	/** Used for questions. */
	public static final int QUESTION_MESSAGE = MessageDialog.QUESTION_MESSAGE;

	/**
	 * Value returned in {@link #setCurrentDate} method when the data field hasn't been found.
	 */
	public static final int FIELD_NOT_FOUND = 100;

	/**
	 * Value returned in {@link #setCurrentDate} when the data field isn't a DateDataField.
	 */
	public static final int NOT_DATEDATAFIELD = 101;

	/**
	 * Value returned in {@link #setCurrentDate} when the operation was successful.
	 */
	public static final int CORRECT = 102;

	/** Key of the query button */
	protected String f7Button = InteractionManager.QUERY_KEY;

	/** Key of the insert button */
	protected String f9Button = InteractionManager.INSERT_KEY;

	/** Key of the update button */
	protected String f11Button = InteractionManager.UPDATE_KEY;

	/** Key of the delete button */
	protected String f12Button = InteractionManager.DELETE_KEY;

	/** GUI text */
	public static String orderWindowTitleKey = "TITULO_VENTANA_ORDENAR";

	/** GUI text */
	public static String orderAscendingKey = "ORDENAR_ASCENDENTE";

	/** GUI text */
	public static String descendentOrderKey = "DESCENDENT_ORDER";

	/** GUI text */
	public static String acceptButtonKey = "application.accept";

	/** GUI text */
	public static String cancelButtonKey = "application.cancel";

	/** GUI text */
	public static String headButtonKey = "form.information";

	/** GUI text */
	public static String tableWindowKey = "form.search_results";

	/** GUI text */
	public static final String TIP_TABLEVIEW_SAVEPREFERENCE = "form.store_selected_column_preferences";

	protected String helpId = null;

	protected Locale locale = Locale.getDefault();

	/** List where the navigation listeners are stored in */
	protected List dataNavigationListeners = null;

	/** List where the record listeners are stored in */
	protected List dataRecordListeners = null;

	/** Visible form permission */
	protected FormPermission visiblePermission = null;

	/** Enabled form permission */
	protected FormPermission enabledPermission = null;

	/**
	 * Default column list that is shown in table view. This list is defined by 'columns' XML parameter
	 * See {@link #init}
	 */
	protected List tableViewColumns = new Vector();

	/**
	 * Column list that are added to default column list from table view.
	 */
	protected List additionalTableViewColumns = null;

	/**
	 * Dialog window where the form is contained in. see {@link #putInModalDialog}
	 */
	protected JDialog dialog = null;

	/**
	 * Scroll Panel where the form is contained in. see {@link #init}
	 */
	protected JScrollPane scrollPanel = new JScrollPane() {

		private static final long serialVersionUID = 1L;

		@Override
		public String getName() {
			return Form.FORMSCROLLPANE;
		};
	};

	/** Main panel where the form is contained in */
	protected JPanel bodyPanel = null;

	/**
	 * @deprecated */
	@Deprecated
	protected JTabbedPane tabPane = new JTabbedPane();

	/** Variable where the printing template name is stored in */
	protected String printingTemplateName = null;

	protected String helpKey = null;

	protected HelpPopup helpPopup = null;

	/** Form file name */
	protected String fileName = null;

	protected boolean attachment = Form.DEFAULT_ATTACHMENT;

	protected boolean databaseBundle = Form.DEFAULT_DATABASE_BUNDLE;

	protected boolean permissionButtonEnabled = Form.defaultPermissionButton;

	protected boolean scriptEnabled = Form.DEFAULT_SCRIPT_ENABLED;

	protected boolean templates = Form.DEFAULT_TEMPLATES;

	/** Popup component where the printing template list is shown in */
	protected PopupPrintingTemplateList templateList = null;

	protected DynamicFormManager dynamicFormManager = null;

	/** Modified field list */
	protected List checkModifiedFieldList = null;

	private final boolean initializedFocusOrder = false;

	protected boolean useModifiedFocusOrder = true;

	private boolean checkModifiedData = true;

	private boolean createdList = false;

	// Table view horizontal scroll
	private boolean scrollHTableView = false;

	protected Object headerPanelPosition = BorderLayout.NORTH;

	protected static class NextButtonListener implements ActionListener {

		protected Form form;

		public NextButtonListener(final Form form) {
			this.form = form;
		}

		@Override
		public void actionPerformed(final ActionEvent evento) {
			if (this.form.vectorSize <= 1) {
				return;
			}
			int newIndex = this.form.currentIndex;
			if (this.form.currentIndex < (this.form.vectorSize - 1)) {
				newIndex++;
			}
			this.form.updateDataFieldNavegationButton(newIndex);
			this.form.startButton.setEnabled(true);
			this.form.previousButton.setEnabled(true);
			if (this.form.currentIndex < (this.form.vectorSize - 1)) {
				this.form.nextButton.setEnabled(true);
				this.form.endButton.setEnabled(true);
			} else {
				this.form.nextButton.setEnabled(false);
				this.form.endButton.setEnabled(false);
			}
			this.form.resultCountLabel
			.setText(new Integer(newIndex + 1).toString() + "/" + new Integer(this.form.vectorSize).toString());
		}

	}

	protected static class PreviousButtonListener implements ActionListener {

		protected Form form;

		public PreviousButtonListener(final Form form) {
			this.form = form;
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (this.form.vectorSize <= 1) {
				return;
			}
			// Shows the first data.
			int newIndex = this.form.currentIndex;
			if (this.form.currentIndex > 0) {
				newIndex--;
			}
			this.form.updateDataFieldNavegationButton(newIndex);
			if (this.form.currentIndex > 0) {
				this.form.startButton.setEnabled(true);
				this.form.previousButton.setEnabled(true);
			} else {
				this.form.startButton.setEnabled(false);
				this.form.previousButton.setEnabled(false);
			}
			this.form.nextButton.setEnabled(true);
			this.form.endButton.setEnabled(true);
			this.form.resultCountLabel
			.setText(new Integer(newIndex + 1).toString() + "/" + new Integer(this.form.vectorSize).toString());
		}

	}

	public static class EndButtonListener implements ActionListener {

		protected Form form;

		public EndButtonListener(final Form form) {
			this.form = form;
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			// Shows the last data
			if (this.form.vectorSize <= 1) {
				return;
			}
			final int newIndex = this.form.vectorSize - 1;
			this.form.updateDataFieldNavegationButton(newIndex);
			this.form.startButton.setEnabled(true);
			this.form.previousButton.setEnabled(true);
			this.form.nextButton.setEnabled(false);
			this.form.endButton.setEnabled(false);
			this.form.resultCountLabel
			.setText(new Integer(newIndex + 1).toString() + "/" + new Integer(this.form.vectorSize).toString());
		}

	}

	protected static class StartButtonListener implements ActionListener {

		protected Form form;

		public StartButtonListener(final Form form) {
			this.form = form;
		}

		@Override
		public void actionPerformed(final ActionEvent evento) {
			// Shows the first data
			if (this.form.vectorSize <= 1) {
				return;
			}
			final int newIndex = 0;
			this.form.updateDataFieldNavegationButton(newIndex);
			this.form.startButton.setEnabled(false);
			this.form.previousButton.setEnabled(false);
			this.form.nextButton.setEnabled(true);
			this.form.endButton.setEnabled(true);
			this.form.resultCountLabel
			.setText(new Integer(newIndex + 1).toString() + "/" + new Integer(this.form.vectorSize).toString());
		}

	}

	protected class HelpPopup extends JPopupMenu {

		protected JScrollPane scrollPane = new JScrollPane();

		protected JPanel headPanel = new JPanel(new BorderLayout());

		protected JLabel head = new JLabel();

		protected JButton closeButton = new JButton(" x ");

		protected JTextArea textArea = new JTextArea();

		public HelpPopup() {
			this.setBorder(null);
			this.setBackground(DataComponent.VERY_LIGHT_YELLOW);
			this.add(this.headPanel, BorderLayout.NORTH);
			this.headPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
			this.headPanel.setBackground(DataComponent.VERY_LIGHT_RED);
			this.headPanel.add(this.closeButton, BorderLayout.EAST);
			this.headPanel.add(this.head);
			this.add(this.scrollPane);
			this.textArea.setOpaque(true);
			this.textArea.setBackground(DataComponent.VERY_LIGHT_YELLOW);
			this.textArea.setLineWrap(true);
			this.textArea.setEditable(false);
			this.textArea.setWrapStyleWord(true);
			this.textArea.setColumns(50);
			this.textArea.setRows(10);
			this.scrollPane.getViewport().add(this.textArea);
			this.scrollPane.setBorder(new LineBorder(Color.black));
			this.closeButton.setMargin(new Insets(0, 0, 0, 0));
			this.closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					HelpPopup.this.setVisible(false);
				}
			});

			if (Form.this.resourcesFile != null) {
				this.textArea.setText(ApplicationManager.getTranslation(Form.this.helpKey, Form.this.resourcesFile));
				this.head.setText(ApplicationManager.getTranslation(Form.headButtonKey, Form.this.resourcesFile));
			} else {
				this.textArea.setText(Form.this.helpKey);
				this.head.setText(Form.headButtonKey);
			}

			final ImageIcon helpIcon = ImageManager.getIcon(ImageManager.HELP);
			if (helpIcon != null) {
				this.head.setIcon(helpIcon);
			}
			this.headPanel.setPreferredSize(new Dimension(this.head.getPreferredSize().width, 20));
			// scrollPane.setPreferredSize(new Dimension(200,100));
			this.pack();
		}

		public void show(final Component c) {
			super.show(c, 0, 0);
		}

		@Override
		public void setVisible(final boolean b) {
			super.setVisible(b);
			if (b) {
				if (Form.this.resourcesFile != null) {
					this.textArea
					.setText(ApplicationManager.getTranslation(Form.this.helpKey, Form.this.resourcesFile));
					this.head.setText(ApplicationManager.getTranslation(Form.headButtonKey, Form.this.resourcesFile));
				} else {
					this.textArea.setText(Form.this.helpKey);
					this.head.setText(Form.headButtonKey);
				}
			}
		}

	}

	protected class FormTitleLabel extends JLabel implements Internationalization {

		protected ResourceBundle languageResource = null;

		protected String title = null;

		protected Border border = new SoftBevelBorder(SoftBevelBorder.LOWERED);

		public FormTitleLabel(final String title) {
			this.setFont(this.getFont().deriveFont((float) (this.getFont().getSize() + 6)));
			this.setHorizontalAlignment(SwingConstants.CENTER);
			final Object c = UIManager.get("MenuItem.selectionBackground");

			if (c instanceof Color) {
				this.setForeground((Color) c);
			}

			this.setTitle(title);
		}

		public void setTitle(final String t) {
			if ((this.title == null) || (this.title.length() == 0)) {
				this.setBorder(null);
			} else {
				this.setBorder(this.border);
			}
			this.title = t;
			super.setText(
					this.title != null ? ApplicationManager.getTranslation(this.title, this.languageResource) : "");

		}

		@Override
		public List getTextsToTranslate() {
			final List v = new Vector();
			if (this.title != null) {
				v.add(this.title);
			}
			return v;
		}

		@Override
		public void setResourceBundle(final ResourceBundle resources) {
			this.languageResource = resources;
			if (this.title != null) {
				this.setTitle(this.title);
			}
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

	}

	/** Paint used to draw the title background */
	protected static Paint titleBackgroundPaint = null;

	/** Color user to draw the title */
	protected static Color cTitleForeground = null;

	public static boolean toUppercaseTitle = false;

	protected String originalTitle = null;

	protected String currentTitle = null;

	/**
	 * Sets the Paint attribute for the Graphics2D context of the Title component
	 * @param paint the Paint object to be used to generate color during the rendering process, or null
	 */

	public static void setDefaultTitlePaint(final Paint paint) {
		Form.titleBackgroundPaint = paint;
	}

	public static Paint getDefaultTitlePaint() {
		return Form.titleBackgroundPaint;
	}

	/**
	 * Sets the color of the title text
	 * @param c the desired <code>Color</code>
	 */
	public static void setDefaultColorTitle(final Color c) {
		Form.cTitleForeground = c;
	}

	public static Color getDefaultColorTitle() {
		return Form.cTitleForeground;
	}

	public void setFormTitle(final String title) {
		if (title != null) {
			this.currentTitle = title;
			if (this.formTitleLabel != null) {
				this.formTitleLabel.setTitle(title.toString());
			} else if (this.statusBar != null) {
				this.statusBar.setTitle(title.toString());
			}
		} else {
			this.currentTitle = null;
			if (this.originalTitle != null) {
				if (this.formTitleLabel != null) {
					this.formTitleLabel.setTitle(this.originalTitle);
				} else if (this.statusBar != null) {
					this.statusBar.setTitle(this.originalTitle);
				}
			} else {
				if (this.formTitleLabel != null) {
					this.formTitleLabel.setTitle("");
				} else if (this.statusBar != null) {
					this.statusBar.setTitle("");
				}
			}
		}
	}

	public String getFormTitle() {
		if (this.currentTitle != null) {
			return this.currentTitle;
		}
		return this.originalTitle;
	}

	public class StatusBar extends JTextField implements Internationalization {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		public static final String FORMTITLE = "FormTitle";

		protected int deletedTime = 0;

		protected ResourceBundle languageResource = null;

		protected String message = "";

		protected DeletedThread deletedThread = null;

		protected ExtendedJPopupMenu popup = new ExtendedJPopupMenu();

		protected MouseListener popupListener = null;

		protected JScrollPane scroll = null;

		protected String title = null;

		protected JList popupList = new JList() {

			@Override
			protected void fireSelectionValueChanged(final int firstIndex, final int lastIndex, final boolean isAdjusting) {
			}
		};

		protected int historicalSize = 20;

		protected Font sourceFont = null;

		protected Font customFont;

		protected boolean lfconfiguration;

		protected Color shadowColor = ParseUtils.getColor("#616C73", Color.black);

		protected boolean shadowMode = false;

		public StatusBar() {
			this.setName(StatusBar.FORMTITLE);
			this.sourceFont = this.getFont();
			this.setEditable(false);
			if (!this.lfconfiguration) {
				this.setBorder(new SoftBevelBorder2(BevelBorder.LOWERED));
			}

			this.popupList.setModel(new DefaultListModel());
			this.scroll = new JScrollPane(this.popupList);
			this.popup.add(this.scroll);
			this.popupList.setBackground(this.getBackground());
			this.scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.popupList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			this.popupList.setVisibleRowCount(5);
			this.popup.pack();
			this.popup.setBorderPainted(true);
			this.popup.setBorder(BorderFactory.createLineBorder(Color.black));
			this.popup.setOpaque(false);
			this.popup.setDoubleBuffered(true);
			this.popup.setRequestFocusEnabled(false);
			this.popupListener = this.createPopupList();
			this.addMouseListener(this.popupListener);
			ToolTipManager.sharedInstance().registerComponent(this);
			this.setOpaque(false);

			final Color c = UIManager.getColor("\"FormTitle\".textForegroundShadow");
			if (c != null) {
				this.shadowColor = c;
			}
		}

		@Override
		public String getName() {
			return StatusBar.FORMTITLE;
		}

		@Override
		public Color getForeground() {
			if (this.isShadowMode() && (this.getShadowColor() != null)) {
				return this.getShadowColor();
			}
			return super.getForeground();
		}

		public Color getShadowColor() {
			return this.shadowColor;
		}

		public void setShadowColor(final Color shadowColor) {
			this.shadowColor = shadowColor;
		}

		public boolean isShadowMode() {
			return this.shadowMode;
		}

		public void setShadowMode(final boolean shadowMode) {
			this.shadowMode = shadowMode;
		}

		protected MouseListener createPopupList() {
			return new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if ((!StatusBar.this.popup.isShowing())
							&& (((DefaultListModel) StatusBar.this.popupList.getModel()).getSize() > 0)) {
						final Dimension d = new Dimension(StatusBar.this.getWidth(),
								(int) StatusBar.this.popupList.getPreferredScrollableViewportSize().getHeight());
						StatusBar.this.scroll.setMaximumSize(d);
						StatusBar.this.scroll.setPreferredSize(d);
						StatusBar.this.scroll.setMinimumSize(d);
						StatusBar.this.popup.show(StatusBar.this, 0, StatusBar.this.getHeight());
					}

					if (SwingUtilities.isRightMouseButton(e) && e.isAltDown() && e.isControlDown() && e.isShiftDown()) {
						final URL url = Form.this.formManager.getURL(Form.this.getArchiveName());
						final String sText = url != null ? url.toString() : Form.this.getArchiveName();
						final StringSelection sSelection = new StringSelection(sText);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sSelection, sSelection);
					}

					if (SwingUtilities.isLeftMouseButton(e) && e.isAltDown() && e.isControlDown() && e.isShiftDown()) {
						String path = "";
						try {
							final URL url = Form.this.interactionManager.getClass().getResource("");
							if (url != null) {
								String name = Form.this.interactionManager.getClass().getName();
								final int index = name.lastIndexOf(".");
								if (index != -1) {
									name = name.substring(index + 1);
								}
								path = url.toString() + name + ".java";
							}
						} catch (final Exception ex) {
							Form.logger.trace(null, ex);
						}
						final StringSelection sselection = new StringSelection(path);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
					}
				}

			};

		}

		public void setTitle(final String t) {
			this.title = t;
			if (this.popup.isShowing()) {
				this.popup.setVisible(false);
			}
			// If status="no" StatusBar is created but is not assigned to any
			// container.
			// In this situation the method super.setText() throw a
			// NullPoineterException (at
			// sun.font.FontDesignMetrics$MetricsKey.init(FontDesignMetrics.java:199))
			if (this.getParent() != null) {
				String text = this.title != null ? ApplicationManager.getTranslation(this.title, this.languageResource)
						: "";
				if (Form.toUppercaseTitle) {
					text = text.toUpperCase();
				}
				super.setText(text);

				this.setFont(this.customFont != null ? this.customFont : this.sourceFont);
				if (!this.lfconfiguration) {
					if (this.getFont() != null) {
						this.setFont(this.getFont().deriveFont((float) (this.getFont().getSize() + 6)));
					}
				}
				this.setHorizontalAlignment(SwingConstants.CENTER);

				if (Form.cTitleForeground != null) {
					this.setForeground(Form.cTitleForeground);
				} else {
					if (!this.lfconfiguration) {
						final Object c = UIManager.get("MenuItem.selectionBackground");
						if (c instanceof Color) {
							this.setForeground((Color) c);
						}
					}
				}
				this.revalidate();
			}
		}

		public void setLFConfiguration(final boolean value) {
			this.lfconfiguration = value;
		}

		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		class DeletedThread extends Thread {

			private int tDelete = 0;

			public DeletedThread(final int deletedThread) {
				super();
				this.tDelete = deletedThread;
			}

			@Override
			public void run() {
				if (this.tDelete == 0) {
					return;
				}
				try {
					Thread.sleep(this.tDelete);
					if (StatusBar.this.title != null) {
						StatusBar.this.setTitle(StatusBar.this.title);
					} else {
						StatusBar.this.setText("");
					}
				} catch (final Exception e) {
					Form.logger.trace(null, e);
				}
			}

		};

		@Override
		public void paint(final Graphics g) {
			try {
				if (Form.titleBackgroundPaint != null) {
					final Graphics2D g2d = (Graphics2D) g;
					g2d.setPaint(Form.titleBackgroundPaint);
					final Insets insets = this.getInsets();
					g2d.fillRect(insets.left, insets.top, this.getWidth() - insets.right - insets.left,
							this.getHeight() - insets.bottom - insets.top);
				}
			} catch (final Exception e) {
				Form.logger.error(null, e);
			}
			super.paint(g);
		}

		@Override
		public String getToolTipText(final MouseEvent e) {
			if (e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
				final StringBuilder buffer = new StringBuilder();
				buffer.append("<html>");
				try {
					final URL url = Form.this.formManager.getURL(Form.this.getArchiveName());
					buffer.append("<B>form:  </B>" + (url != null ? url.toString() : Form.this.getArchiveName()));
				} catch (final Exception ex) {
					Form.logger.trace(null, ex);
				}
				try {
					final URL url = Form.this.interactionManager.getClass().getResource("");
					if (url != null) {
						buffer.append("<br>");
						buffer.append("<B>im:  </B>" + Form.this.interactionManager.getClass().getName());
					}
				} catch (final Exception ex1) {
					Form.logger.trace(null, ex1);
				}
				buffer.append("<br>");
				buffer.append("<B>entity:  </B>" + Form.this.entityName);

				if (Form.this.formManager != null) {
					buffer.append("<br>");
					buffer.append("<B>fm:  </B>" + Form.this.formManager.getId());
					buffer.append("<br>");
					if (Form.this.formManager instanceof FormManager) {
						buffer.append("<B>tree:  </B>" + ((FormManager) Form.this.formManager).treeFileName);
					}
				}
				buffer.append("</html>");
				return buffer.toString();
			} else {
				return null;
			}
		}

		protected void addToHistoricalPopup(final String text) {
			if (((DefaultListModel) this.popupList.getModel()).getSize() > (this.historicalSize - 1)) {
				((DefaultListModel) this.popupList.getModel()).remove(0);
			}
			((DefaultListModel) this.popupList.getModel()).addElement(text);
		}

		public void setText(final String text, final int timeMillis) {
			if (this.popup.isShowing()) {
				this.popup.setVisible(false);
			}
			this.setFont(this.customFont != null ? this.customFont : this.sourceFont);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			this.message = text;
			this.deletedTime = timeMillis;
			if (timeMillis != 0) {
				try {
					if ((this.deletedThread != null) && this.deletedThread.isAlive()) {
						this.deletedThread.interrupt();
					}
					this.deletedThread.join(10);
				} catch (final Exception e) {
					Form.logger.trace(null, e);
				}
				this.deletedThread = null;
				this.deletedThread = new DeletedThread(timeMillis);
				this.deletedThread.start();
			}
			if (this.languageResource != null) {
				try {
					final String textoTrad = this.languageResource.getString(this.message);
					super.setText(textoTrad);
					this.revalidate();
					this.addToHistoricalPopup(textoTrad);
				} catch (final Exception e) {
					if (Form.logger.isDebugEnabled()) {
						Form.logger.error(null, e);
					} else {
						Form.logger.trace(null, e);
					}
					super.setText(text);
					this.revalidate();
					this.addToHistoricalPopup(text);
				}
			} else {
				super.setText(text);
				this.revalidate();
				this.addToHistoricalPopup(text);
			}
			this.paintImmediately(0, 0, this.getWidth(), this.getHeight());

		}

		@Override
		public void updateUI() {
			super.updateUI();
			try {
				if (this.title != null) {
					if (Form.cTitleForeground != null) {
						this.setForeground(Form.cTitleForeground);
					} else {
						if (!this.lfconfiguration) {
							final Color backgroundColor = UIManager.getColor("MenuItem.selectionBackground");
							this.setForeground(backgroundColor);
						}
					}
				}
				if (!this.lfconfiguration) {
					// If there is not specific font to use then use the default
					// one
					if (this.customFont == null) {
						this.sourceFont = UIManager.getFont("TextField.font");
						this.setFont(this.sourceFont.deriveFont((float) (this.sourceFont.getSize() + 6)));
					}
				}
			} catch (final Exception e) {
				Form.logger.trace(null, e);
			}
		}

		public void setCustomFont(final Font f) {
			this.customFont = f;
			this.setFont(this.customFont);
		}

		@Override
		public Font getFont() {
			return super.getFont();
		}

		@Override
		public List getTextsToTranslate() {
			final List v = new Vector();
			v.add(this.message);
			return v;
		}

		@Override
		public void setResourceBundle(final ResourceBundle resources) {
			this.languageResource = resources;
			if (this.title != null) {
				this.setTitle(this.title);
			} else if (this.message != null) {
			}
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

	};

	protected StatusBar statusBar; // = new StatusBar();

	protected FormTitleLabel formTitleLabel = null;

	/**
	 * Map where the components that implement {@link DataComponent} are stored in. This variable
	 * provides a reference of each <code>DataComponent</code> that is contained in the form. This
	 * variable is filled when the {@link #createLists} method is called and this method must only be
	 * called when all components have been added to the form.
	 */

	protected Map dataComponentList = new Hashtable();

	/** Variable where the entity name is stored in. */
	protected String entityName = null;

	/** Variable where the entity name is stored in. */
	protected String xmlEntityName = null;

	/** List where the group data component is stored in. */
	protected Map groupDataComponentList = new Hashtable(2);

	/**
	 * Form manager reference where the form is contained in. This reference must be assigned by the
	 * form manager when the form is loaded.
	 *
	 * @see {@link IFormManager}
	 */

	protected IFormManager formManager = null;

	/**
	 * Variable where the Frame of this <code>Form</code> is stored in
	 *
	 * @see {@link IFormManager}
	 */
	protected Frame parentFrame = null;

	/**
	 * {@link InteractionManager} class that this form has registered.
	 */
	protected InteractionManager interactionManager = null;

	/**
	 * Button list that implement {@link Button} that are contained in the form. This list is filled in
	 * the {@link #createLists} method.
	 */
	protected Map buttonList = new Hashtable();

	/**
	 * Component list that implement {@link Internationalization}.
	 */
	protected List internationalizeList = new Vector();

	protected JDialog messageWindow = null;

	/**
	 * Component list that are contained in the form.
	 */
	protected List componentList = new Vector();

	/**
	 * Component list that implement {@link AccessForm} interface.
	 */
	protected List accessFormComponentList = new Vector();

	/**
	 * Component list that implement {@link SelectCurrencyValues} interface.
	 */
	protected List currencyComponentsList = new Vector();

	/**
	 * {@link FormBuilder} reference that will be used by all components that implement
	 * {@link CreateForms} interface.
	 */
	protected FormBuilder formBuilder = null;

	/**
	 * {@link DetailForm reference} where the form is contained in.
	 */
	protected IDetailForm detailForm = null;

	protected boolean multipleData = Form.DEFAULT_TABLEVIEW_MULTIPLEDATA;

	/**
	 * Establishes if the buttons are displayed in the form.
	 *
	 * @see {@link #init}
	 */
	protected boolean buttons = true;

	/** Navigation button */
	public JButton nextButton = null;

	/** Navigation button */
	public JButton previousButton = null;

	/** Navigation button */
	public JButton endButton = null;

	/** Navigation button */
	public JButton startButton = null;

	public JButton refreshButton = null;

	public JButton helpButton = null;

	public JButton helpIdButton = null;

	/** Table view button */
	public TableButton tableButton = null;

	protected static class TableButtonIcon extends CompoundIcon {

		public TableButtonIcon(final ImageIcon icon, final ImageIcon a) {
			super(icon, a);

		}

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			if (c instanceof TableButton) {
				final TableButton tableButton = (TableButton) c;
				if (tableButton.getSelectionColumnsMode() && tableButton.hasSelectionColumns()) {
					super.paintIcon(c, g, x, y);
				} else {
					super.principal.paintIcon(c, g, x, y);
				}
			}
		}

	}

	public class TableButton extends FormButton {

		private boolean selectionColumnsMode = false;

		private boolean selectionColumns = true;

		public TableButton(final Icon icon) {
			super(icon);
		}

		public TableButton(final String t) {
			super(t);
		}

		public void setSelectionColumnsMode(final boolean sel) {
			this.selectionColumnsMode = sel;
			if (!sel) {
				this.setToolTipText(ApplicationManager.getTranslation("form.table_view", Form.this.resourcesFile));
			} else {
				this.setToolTipText(ApplicationManager.getTranslation("form.selection_columns_tabular_view",
						Form.this.resourcesFile));
			}
		}

		public void updateTip() {
			if (!this.selectionColumnsMode) {
				this.setToolTipText(ApplicationManager.getTranslation("form.table_view", Form.this.resourcesFile));
			} else {
				this.setToolTipText(ApplicationManager.getTranslation("form.selection_columns_tabular_view",
						Form.this.resourcesFile));
			}
		}

		public boolean getSelectionColumnsMode() {
			return this.selectionColumnsMode;
		}

		public void setSelectionColumns(final boolean selected) {
			this.selectionColumns = selected;
		}

		public boolean hasSelectionColumns() {
			return this.selectionColumns;
		}

	}

	/** Button that clear all data field values */
	public JButton clearDataFieldButton = new FormButton();

	protected JButton reloadButton = null;

	/** Label where the total record number is drawn in */
	public JLabel resultCountLabel = null;

	/** Panel where the buttons are contained in */
	public JPanel buttonPanel = null;

	protected static class ComponentConstraints {

		protected Component component;

		protected GridBagConstraints constraints;

		public ComponentConstraints(final Component c, final GridBagConstraints constraints) {
			this.component = c;
			this.constraints = constraints;
		}

		public Component getComponent() {
			return this.component;
		}

		public void setComponent(final Component component) {
			this.component = component;
		}

		public GridBagConstraints getConstraints() {
			return this.constraints;
		}

		public void setConstraints(final GridBagConstraints constraints) {
			this.constraints = constraints;
		}

	}

	protected static final String STATUS_KEY = "status";

	protected static final String COLLAPSIBLE_KEY = "collapsible";

	/**
	 * The default order for Form buttons and StatusBar showed (from left to right): "help", "helpId",
	 * "controls", "table", "start", "previous", "print", "next", "end", "countlabel", "refresh",
	 * "clear", "status".
	 */
	protected static String[] defaultButtonPanelPosition = new String[] { "help", "helpId", "controls", "table",
			"start", "previous", "print", "next", "end", "countlabel",
			"refresh", "clear", Form.STATUS_KEY };

	public static String originalButtonPosition = "help,helpId,controls,table,start,previous,print,next,end,countlabel,refresh,clear,status";

	public static String defaultButtonPosition = "start;previous;countlabel;next;end;status;collapsible(controls;help;print;table;refresh);clear";

	public static boolean useOriginalButtonPosition = false;

	protected Map buttonPanelObjects = new Hashtable();

	/**
	 * Panel where the new buttons are contained in
	 *
	 * @see {@link #addControls}
	 */
	protected JPanel newButtonPanel = null;

	public ImageIcon nextIcon = null;

	public ImageIcon previousIcon = null;

	public ImageIcon endIcon = null;

	public ImageIcon startIcon = null;

	public ImageIcon orderIcon = null;

	public ImageIcon printIcon = null;

	public ImageIcon refreshIcon = null;

	public ImageIcon attachmentIcon = null;

	public ImageIcon attachmentEmptyIcon = null;

	public ImageIcon databaseBundleIcon = null;

	public ImageIcon scriptIcon = null;

	public String deleteButtonText = "form.clear_fields";

	/** Button that print used the default printing xml template */
	public JButton printButton = null;

	/** Button that manage the printing template */
	public ButtonSelectionInternationalization printTemplateButton = null;

	/** Listener of the printButton */
	protected ActionListener printListener = null;

	/** Button that manage the attachment file */
	public ButtonSelectionInternationalization attachmentButton = null;

	/** Button that show the permission component */
	protected JButton permissionButton = null;

	protected JButton databaseBundleButton = null;

	protected JButton scriptButton = null;

	/**
	 * {@link ApplicationPermission} reference used to check the permission of permissionButton
	 */
	protected ApplicationPermission permissionButtonApplicationPermission = null;

	/**
	 * Entity reference used to attach files
	 * @deprecated Use ((UtilReferenceLocator) locator).getAttachmentEntity(locator.getSessionId())
	 */
	@Deprecated
	protected Entity attachmentEntity = null;

	/**
	 * Entity reference used to store printing template
	 * @deprecated Use ((UtilReferenceLocator)
	 *             locator).getPrintingTemplateEntity(locator.getSessionId())
	 */
	@Deprecated
	protected Entity ePrintingTemplate = null;

	/** Total data list from this form */
	protected Map								totalDataList							= null;

	/** Index of current record */
	protected int currentIndex = 0;

	protected Object parentKeyValue = null;

	/**
	 * Component list that provides the order in which the field values are filled.
	 */
	protected List setValuesOrder = null;

	/** Total data list size */
	protected int vectorSize = 0;

	protected String resourcesFileName = null;

	protected ResourceBundle resourcesFile = null;

	class FormAction extends AbstractAction {

		public FormAction() {
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
		}

	};

	/** Navigation listener */
	public ActionListener previousButtonListener = null;

	/** Navigation listener */
	public ActionListener nextButtonListener = null;

	/** Navigation listener */
	public ActionListener startButtonListener = null;

	/** Navigation listener */
	public ActionListener endButtonListener = null;

	/** Refresh button listener */
	public FormAction refreshButtonListener = null;

	/** Table view button listener */
	public ActionListener tableButtonListener = null;

	/**
	 * TreeNode associates with this form
	 */
	protected OTreeNode associatedNode = null;

	/**
	 * Path associates with this form
	 */
	protected TreePath associatedPath = null;

	protected Table table = null;

	protected JDialog tableWindow = null;

	protected List keys = new Vector();

	protected List parentKeys = new Vector();

	static class ScrollableColumn extends Column implements Scrollable {

		public ScrollableColumn(final Map h) {
			super(h);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return this.getPreferredSize();
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			if (this.getParent() instanceof JViewport) {
				final JViewport port = (JViewport) this.getParent();
				final int w = port.getWidth();
				final Dimension pref = this.getPreferredSize();
				final Dimension max = this.getMaximumSize();
				if ((w >= pref.width) && (w <= max.width)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			if (this.getParent() instanceof JViewport) {
				final JViewport port = (JViewport) this.getParent();
				final int h = port.getHeight();
				final Dimension pref = this.getPreferredSize();
				if (h >= pref.height) {
					final Dimension max = this.getMaximumSize();
					if (h <= max.height) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
			if (orientation == SwingConstants.VERTICAL) {
				return this.getHeight();
			} else {
				return this.getWidth();
			}
		}

		@Override
		public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
			return 30;
		}

	}

	/**
	 * Creates a Form instance with the parameters establishes in <code>Hastable</code>
	 * @param params
	 */
	public Form(final Map params) {
		super();
		final Map auxParams = new Hashtable();

		final Object margin = params.get("margin");
		if (margin != null) {
			auxParams.put("margin", margin);
		} else {
			if (Form.DEFAULT_FORM_MARGIN != null) {
				auxParams.put("margin",
						Form.DEFAULT_FORM_MARGIN.top + ";" + Form.DEFAULT_FORM_MARGIN.left + ";"
								+ Form.DEFAULT_FORM_MARGIN.bottom + ";" + Form.DEFAULT_FORM_MARGIN.right);
			}
		}

		if (params.containsKey(DataField.BGCOLOR)) {
			auxParams.put(DataField.BGCOLOR, params.get(DataField.BGCOLOR));
		}
		if (params.containsKey("bgpaint")) {
			auxParams.put("bgpaint", params.get("bgpaint"));
		}

		if (params.containsKey("bgimage")) {
			auxParams.put("bgimage", params.get("bgimage"));
		}

		this.bodyPanel = new ScrollableColumn(auxParams) {

			@Override
			public String getName() {
				return Form.FORMBODYPANEL;
			}
		};

		this.init(params);

		if (this.refreshButton != null) {
			this.refreshButton.setVisible(false);
		}
		this.registerKeyBindings();
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Method that configures the component. The param <code>Hashtable</code> contains the values set in
	 * the XML in which the <code>Form</code> is placed.
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
	 * <td>attachment</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>If form attachments are implemented in the application, this parameter allows to hide the
	 * attachment button</td>
	 * </tr>
	 * <tr>
	 * <td>buttons</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Establishes if the data navigation buttons are visible.</td>
	 * </tr>
	 * <tr>
	 * <td>columns</td>
	 * <td>attr1;attr2;..;attrN</td>
	 * <td></td>
	 * <td></td>
	 * <td>Establishes the list of attributes that are showed in the table view</td>
	 * </tr>
	 * <tr>
	 * <td>customfocus</td>
	 * <td>yes/no/attr1;attr2;...;attrn/QUERY{attr1;attr2...}-INSERT{attr1;attr2...
	 * }-UPDATE{attr1;attr2...}</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Establishes the focus traversal policy used. The last option sets the order in which the
	 * focus moves down the components of the form for each action(QUERY,INSERT,UPDATE). For the other
	 * components of the form that aren't included into the brackets the focus order is from left to
	 * right and top to bottom. In this option only one mode can be parameterized(e.g.
	 * QUERY{attr1;attr2} if it is wished. Into other modes the behavior is the previously described. If
	 * it is wanted that this order is setted for all actions the name of the attributes are inserted
	 * into brackets.</td>
	 * </tr>
	 * <tr>
	 * <td>entity</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Name of the entity related to the Form</td>
	 * </tr>
	 * <tr>
	 * <td>helpid</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Establishes help key uses for identify the help section dedicates a this form</td>
	 * </tr>
	 * <tr>
	 * <td>helpkey</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>If this parameter exists the help button is displayed into the form. The parameter value is
	 * used as key for searching the resource text.</td>
	 * </tr>
	 * <tr>
	 * <td>keys</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>List of names of the entity primary key.</td>
	 * </tr>
	 * <tr>
	 * <td>multipledata</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>When a query with multiple results is done the table view with all results is showed if this
	 * parameter is established a true.</td>
	 * </tr>
	 * <tr>
	 * <td>parentkeys</td>
	 * <td>colname1;colname2;..;colnameN</td>
	 * <td></td>
	 * <td>
	 * <td>List of foreign key column names.</td></td>
	 * </tr>
	 * <tr>
	 * <td>permissionbutton</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>(since 5.2067EN) Allows to hide permission button for each form individually (By default,
	 * this button appears always when permissionComponent library is included in classpath)</td>
	 * </tr>
	 * <tr>
	 * <td>printable</td>
	 * <td>true/false</td>
	 * <td>true</td>
	 * <td></td>
	 * <td>Establishes if the form can be printed</td>
	 * </tr>
	 * <tr>
	 * <td>resources</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Resources file name that this form going to use. Example: <br>
	 * If you have a specific bundle file for this form with path
	 * com/app/client/formA/resources/bundle_en_US.properties. You must specify the parameter:
	 * resources=com/app/client/formA/resources/bundle</td>
	 * </tr>
	 * <tr>
	 * <td>scroll</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Establishes if the form body will show the scrollbars when the components don't have enough
	 * space</td>
	 * </tr>
	 * <tr>
	 * <td>setvalueorder</td>
	 * <td>attr1;attr2;..attrN</td>
	 * <td></td>
	 * <td></td>
	 * <td>The form doesn't guarantee the order in which the fields will be filled. If a field value is
	 * required by another one (as a parentKey of a ReferenceDataField), an error could be produced. In
	 * principle, the default filler should be right in most cases, but with this parameter the filler
	 * order can be established. It isn't necessary to establish every field attribute. The attributes
	 * specified in this parameter are filled first.</td>
	 * </tr>
	 * <tr>
	 * <td>status</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Establishes if the form has a status bar (Part that shows the title in the top of form)</td>
	 * </tr>
	 * <tr>
	 * <td>tableviewscrollh</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Establishes if the table view has a horizontal scroll</td>
	 * </tr>
	 * <tr>
	 * <td>template</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Establishes the file name of template used to print the form data</td>
	 * </tr>
	 * <tr>
	 * <td>templates</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>If form templates are implemented in the application, this parameter allows to hide the
	 * template printing button</td>
	 * </tr>
	 * <tr>
	 * <td>title</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Establishes the value of the form title (placed in status container) that is displayed in the
	 * top of the form</td>
	 * </tr>
	 * <tr>
	 * <td>bgcolor</td>
	 * <td>A color name defined in the ColorConstants or a RGB value (like 0;0;0)</td>
	 * <td>The default look and feel background color</td>
	 * <td>no</td>
	 * <td>Sets the color to use in the form background</td>
	 * </tr>
	 * <tr>
	 * <td>bgimage</td>
	 * <td>A image resource</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The background image</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>nextbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>previousbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>endbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>startbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>printbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>refreshbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>attachbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>attachbuttonemptyicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>tablebuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>cleardatafieldbuttonicon</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Path to this form icon</td>
	 * </tr>
	 * <tr>
	 * <td>borderbuttons</td>
	 * <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover, it is
	 * also allowed a border defined in #BorderManager</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The border for buttons in Form</td>
	 * </tr>
	 * <tr>
	 * <td>highlightbuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Sets the highlight in button property when mouse is entered. See
	 * {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires opaque='no'.</td>
	 * </tr>
	 * <tr>
	 * <td>opaquebuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Data field opacity condition for Form buttons</td>
	 * </tr>
	 * <tr>
	 * <td>resultcountfont</td>
	 * <td>A string like : 'Arial-BOLD-18' (See #Font.decode())</td>
	 * <td></td>
	 * <td></td>
	 * <td>Font for result count label that show the number of records in Form</td>
	 * </tr>
	 * <tr>
	 * <td>resultcountfg</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Color for foreground that shows the result count label.</td>
	 * </tr>
	 * <tr>
	 * <td>statusborder</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Border for status (title of form).</td>
	 * </tr>
	 * <tr>
	 * <td>buttonpanelcolor</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Color for button panel.</td>
	 * </tr>
	 * <tr>
	 * <td>statusfont</td>
	 * <td>A string like : 'Arial-BOLD-18' (See #Font.decode())</td>
	 * <td></td>
	 * <td></td>
	 * <td>Font for status bar (title of form)</td>
	 * </tr>
	 * <tr>
	 * <td>buttonposition</td>
	 * <td>button1;button2;status;...;collapsible(button3;button4);...;buttonn</td>
	 * <td></td>
	 * <td></td>
	 * <td>Keys for buttons and status. Allow to define the both Form buttons and status(title)
	 * order.</td>
	 * </tr>
	 * <tr>
	 * <td>tableviewminrowheight</td>
	 * <td></td>
	 * <td>8</td>
	 * <td>no</td>
	 * <td>The minimum row height for each row in table that is showed when user presses twice to Query
	 * Button.</td>
	 * </tr>
	 * <tr>
	 * <td>margin</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Indicates the Form margin.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>headerPanelPosition</td>
	 * <td>top/bottom</td>
	 * <td>top</td>
	 * <td>no</td>
	 * <td>The position of the button panel on the form.</td>
	 * </tr>
	 *
	 * </Table>
	 */
	@Override
	public void init(final Map parameters) {

		this.setLayout(new BorderLayout());

		this.setColumns(parameters);
		this.setKeysAndParentKeys(parameters);
		final Object tablescrollh = parameters.get(Form.TABLE_VIEW_SCROLL_H);
		this.scrollHTableView = ApplicationManager.parseStringValue((String) tablescrollh, false);
		this.setResources(parameters);
		this.buttons = ParseUtils.getBoolean((String) parameters.get(Form.BUTTONS), true);
		final boolean bStatusBar = ParseUtils.getBoolean((String) parameters.get(Form.STATUS), true);
		this.setFocusPolicy(parameters);
		this.setScroll(parameters);
		this.setHeaderPosition(parameters);
		this.setSetValueOrder(parameters);
		this.setHelpId(parameters);
		this.setHelpKey(parameters);

		this.clearDataFieldButton = new FormButton();
		this.clearDataFieldButton.setToolTipText(this.deleteButtonText + " (ALT Supr)");
		this.clearDataFieldButton.setMnemonic(KeyEvent.VK_DELETE);

		this.attachment = ParseUtils.getBoolean((String) parameters.get(Form.ATTACHMENT), Form.DEFAULT_ATTACHMENT);
		this.templates = ParseUtils.getBoolean((String) parameters.get(Form.TEMPLATES), Form.DEFAULT_TEMPLATES);
		this.databaseBundle = ParseUtils.getBoolean((String) parameters.get(Form.DATABASE_BUNDLE),
				Form.DEFAULT_DATABASE_BUNDLE);
		this.scriptEnabled = ParseUtils.getBoolean((String) parameters.get(Form.SCRIPT_ENABLED),
				Form.DEFAULT_SCRIPT_ENABLED);
		this.permissionButtonEnabled = ParseUtils.getBoolean((String) parameters.get(Form.PERMISSION_BUTTON),
				Form.defaultPermissionButton);

		// Add buttons to the north in other panel
		// If Help button is visible put it at the beginning
		this.buttonPanel = new PaintPanel(new GridBagLayout()) {

			@Override
			public String getName() {
				return Form.FORMBUTTONPANEL;
			}
		};

		this.configureFormIcons(parameters);

		this.initButtons(parameters, bStatusBar);

		super.add(this.scrollPanel, BorderLayout.CENTER);
		this.bodyPanel.add(this.tabPane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.scrollPanel.getViewport().add(this.bodyPanel, null);

		this.configureEntity(parameters);

		if (this.buttons) {
			final Object printable = parameters.get(Form.PRINTABLE);
			if (printable == null) {
				this.printButton.setEnabled(true);
			} else {
				try {
					if (new Boolean(printable.toString()).booleanValue()) {
						this.printButton.setEnabled(true);
					} else {
						this.printButton.setEnabled(false);
					}
				} catch (final Exception e) {
					Form.logger.trace(null, e);
					this.printButton.setEnabled(false);
				}
			}
		}

		this.configureTemplate(parameters);

		this.configureTitle(parameters);

		this.installHelpId();

		this.multipleData = ParseUtils.getBoolean((String) parameters.get(Form.MULTIPLE_DATA),
				Form.DEFAULT_TABLEVIEW_MULTIPLEDATA);

		this.addComponentsToButtonPanel((String) parameters.get("buttonposition"));

		this.tableViewMinRowHeight = ParseUtils.getInteger((String) parameters.get("tableviewminrowheight"),
				Form.defaultTableViewMinRowHeight);

		final Border scrollBorder = ParseUtils.getBorder((String) parameters.get("formscrollborder"), null);
		if (scrollBorder != null) {
			this.scrollPanel.setBorder(scrollBorder);
		}
	}

	protected void configureTitle(final Map parameters) {
		final Object title = parameters.get(Form.TITLE);
		if (title != null) {
			this.originalTitle = title.toString();
			if (this.formTitleLabel != null) {
				this.formTitleLabel.setTitle(title.toString());
			} else if (this.statusBar != null) {
				this.statusBar.setTitle(title.toString());
			}
		}
	}

	protected void configureTemplate(final Map parameters) {
		// Name of the form template
		final Object oTemplate = parameters.get(Form.TEMPLATE);
		if (oTemplate != null) {
			this.printingTemplateName = oTemplate.toString();
		} else {
			if (this.printButton != null) {
				this.printButton.setVisible(false);
			}
		}
	}

	protected void configureEntity(final Map parameters) {
		final Object oEntity = parameters.get(Form.ENTITY);
		if (oEntity == null) {
			Form.logger.debug("'{}' parameter is required.", Form.ENTITY);
		} else {
			this.entityName = oEntity.toString();
			this.xmlEntityName = oEntity.toString();
		}
	}

	protected void initButtons(final Map parameters, final boolean bStatusBar) {
		this.buttonPanel.setOpaque(this.opaquebuttons);
		this.newButtonPanel = new PaintPanel(new GridBagLayout());
		this.newButtonPanel.setOpaque(false);

		this.setButtonPanelColor(parameters);

		this.startButton.setEnabled(false);
		this.previousButton.setEnabled(false);
		this.nextButton.setEnabled(false);
		this.endButton.setEnabled(false);
		this.refreshButton.setEnabled(false);

		if (this.tableButton.hasSelectionColumns()) {
			this.tableButton.setSelectionColumnsMode(true);
		} else {
			this.tableButton.setEnabled(false);
		}

		this.startButton.setMargin(new Insets(1, 1, 1, 1));
		this.previousButton.setMargin(new Insets(1, 1, 1, 1));
		this.nextButton.setMargin(new Insets(1, 1, 1, 1));
		this.endButton.setMargin(new Insets(1, 1, 1, 1));
		this.refreshButton.setMargin(new Insets(1, 1, 1, 1));
		this.printButton.setMargin(new Insets(1, 1, 1, 1));

		this.tableButton.setMargin(new Insets(1, 1, 1, 1));
		this.clearDataFieldButton.setMargin(new Insets(1, 1, 1, 1));

		this.buttonPanelObjects.put("controls", new ComponentConstraints(this.newButtonPanel,
				new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("table", new ComponentConstraints(this.tableButton,
				new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("start", new ComponentConstraints(this.startButton,
				new GridBagConstraints(6, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("previous", new ComponentConstraints(this.previousButton,
				new GridBagConstraints(7, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("print", new ComponentConstraints(this.printButton,
				new GridBagConstraints(8, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("next", new ComponentConstraints(this.nextButton,
				new GridBagConstraints(10, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("end", new ComponentConstraints(this.endButton,
				new GridBagConstraints(11, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 2), 0, 0)));
		this.buttonPanelObjects.put("countlabel", new ComponentConstraints(this.resultCountLabel,
				new GridBagConstraints(12, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 2), 0, 0)));
		this.buttonPanelObjects.put("refresh", new ComponentConstraints(this.refreshButton,
				new GridBagConstraints(13, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 0), 0, 0)));
		this.buttonPanelObjects.put("clear", new ComponentConstraints(this.clearDataFieldButton,
				new GridBagConstraints(14, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(1, 0, 1, 2), 0, 0)));

		// If status bar exists
		if (bStatusBar) {
			this.statusBar = new StatusBar();
			this.statusBar.setFont(this.statusBar.getFont().deriveFont(Font.BOLD));
			this.statusBar.setCustomFont(ParseUtils.getFont((String) parameters.get("statusfont"), null));
			this.statusBar
			.setBorder(ParseUtils.getBorder((String) parameters.get("statusborder"), this.statusBar.getBorder()));
			this.buttonPanelObjects.put(Form.STATUS_KEY, new ComponentConstraints(this.statusBar,
					new GridBagConstraints(15, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(1, 0, 1, 0), 0, 0)));
		}

		if (Form.RELOAD_BUTTON_VISIBLE) {
			this.setReloadButton();
		} else {
			if (Form.TITLE_ON_TOP) {
				this.formTitleLabel = new FormTitleLabel(null);
				final JPanel pAux = new JPanel(new BorderLayout());
				pAux.add(this.formTitleLabel, BorderLayout.NORTH);
				pAux.add(this.buttonPanel);
				super.add(pAux, BorderLayout.NORTH);
			} else {
				super.add(this.buttonPanel, this.headerPanelPosition);
			}
		}

		// Buttons events listeners
		this.printListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				if (Form.this.interactionManager != null) {
					try {
						Form.this.interactionManager.print();
					} catch (final Exception e) {
						Form.logger.error(null, e);
					}
				}
			}
		};

		this.printButton.addActionListener(this.printListener);

		this.startButtonListener = new StartButtonListener(this);

		this.startButton.addActionListener(this.startButtonListener);

		this.endButtonListener = new EndButtonListener(this);
		this.endButton.addActionListener(this.endButtonListener);

		this.previousButtonListener = new PreviousButtonListener(this);
		this.previousButton.addActionListener(this.previousButtonListener);

		this.nextButtonListener = new NextButtonListener(this);
		this.nextButton.addActionListener(this.nextButtonListener);

		this.refreshButtonListener = new FormAction() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Form.this.refreshCurrentDataRecord();
			}
		};
		this.refreshButton.addActionListener(this.refreshButtonListener);

		this.tableButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				if (Form.this.tableButton.getSelectionColumnsMode()) {
					Form.this.selectTableViewColumns();
				} else {
					Form.logger.debug("Opening table view");
					Form.this.showTableView();
				}
			}
		};
		this.tableButton.addActionListener(this.tableButtonListener);

		if (!this.buttons) {
			if (bStatusBar) {
				if (this.previousButton != null) {
					this.previousButton.setVisible(false);
				}
				if (this.clearDataFieldButton != null) {
					this.clearDataFieldButton.setVisible(false);
				}
				if (this.endButton != null) {
					this.endButton.setVisible(false);
				}
				if (this.printButton != null) {
					this.printButton.setVisible(false);
				}
				if (this.startButton != null) {
					this.startButton.setVisible(false);
				}
				if (this.refreshButton != null) {
					this.refreshButton.setVisible(false);
				}
				if (this.nextButton != null) {
					this.nextButton.setVisible(false);
				}
				if (this.tableButton != null) {
					this.tableButton.setVisible(false);
				}
			} else {
				this.buttonPanel.setVisible(false);
			}
		}

		if (this.helpButton != null) {
			this.helpButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent evento) {
					if (Form.this.helpPopup == null) {
						Form.this.helpPopup = new HelpPopup();
					}
					Form.this.helpPopup.show(Form.this.helpButton);
				}
			});
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setKeysAndParentKeys(final Map parameters) {
		final Object keys = parameters.get(Form.KEYS);
		if (keys == null) {
			Form.logger.debug("'{}' parameter hasn't been found in the form.", Form.KEYS);
		} else {
			final StringTokenizer st = new StringTokenizer(keys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.keys.add(st.nextToken());
			}
		}
		final Object parentkeys = parameters.get(Form.PARENT_KEYS);
		if (parentkeys == null) {
			Form.logger.debug("'{}' parameter hasn't been found in the form.", Form.PARENT_KEYS);
		} else {
			final StringTokenizer st = new StringTokenizer(parentkeys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.parentKeys.add(st.nextToken());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setColumns(final Map parameters) {
		final Object columns = parameters.get(Form.COLUMNS);
		if (columns != null) {
			final StringTokenizer st = new StringTokenizer(columns.toString(), ";");
			while (st.hasMoreTokens()) {
				this.tableViewColumns.add(st.nextToken());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setResources(final Map parameters) {
		// Resources
		final Object oResources = parameters.get(Form.RESOURCES);
		if (oResources == null) {
			Form.logger.debug("'{}' parameter hasn't been found in the form.", Form.RESOURCES);
		} else {
			this.resourcesFileName = oResources.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFocusPolicy(final Map parameters) {
		final Object customfocus = parameters.get(Form.CUSTOMFOCUS);
		boolean ownPolicy = false;
		if (customfocus != null) {
			if (customfocus.equals("no")) {
				this.useModifiedFocusOrder = false;
			} else if (customfocus.equals("yes")) {
				this.useModifiedFocusOrder = true;
			} else {
				this.useModifiedFocusOrder = true;
				ownPolicy = true;

			}
		}

		if (this.useModifiedFocusOrder) {
			if (ApplicationManager.jvmVersionHigherThan_1_4_0()) {
				try {
					if (ownPolicy) {
						this.setFocusTraversalPolicy(new com.ontimize.util.swing.IdentifiedFocusTraversalPolicy());
						((IdentifiedFocusTraversalPolicy) this.getFocusTraversalPolicy()).setForm(this);
						((IdentifiedFocusTraversalPolicy) this.getFocusTraversalPolicy())
						.parseFocusMode((String) customfocus);
					} else {
						this.setFocusTraversalPolicy(new com.ontimize.util.swing.PositionFocusTraversalPolicy());
					}
				} catch (final Exception ex) {
					Form.logger.error(null, ex);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setScroll(final Map parameters) {
		final Object scroll = parameters.get(Form.SCROLL);
		if (scroll != null) {
			final boolean bEnableScroll = ApplicationManager.parseStringValue(scroll.toString(), true);
			if (!bEnableScroll) {
				this.scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHeaderPosition(final Map parameters) {
		final String headerPosition = (String) parameters.get(Form.HEADER_PANEL_POSITION);
		if (headerPosition != null) {
			if (JSplitPane.TOP.equalsIgnoreCase(headerPosition)) {
				this.headerPanelPosition = BorderLayout.NORTH;
			} else if (JSplitPane.BOTTOM.equalsIgnoreCase(headerPosition)) {
				this.headerPanelPosition = BorderLayout.SOUTH;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setSetValueOrder(final Map parameters) {
		final Object setvalueorder = parameters.get(Form.SET_VALUE_ORDER);
		if (setvalueorder != null) {
			this.setValuesOrder = new Vector();
			final StringTokenizer st = new StringTokenizer(setvalueorder.toString(), ";");
			while (st.hasMoreTokens()) {
				this.setValuesOrder.add(st.nextToken());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHelpId(final Map parameters) {
		final Object helpid = parameters.get(Form.HELP_ID);
		boolean bPutHelpIdButton = false;
		if (helpid != null) {
			this.helpId = helpid.toString();
			bPutHelpIdButton = true;
		}

		if (bPutHelpIdButton) {
			this.helpIdButton = new FormButton();
			final ImageIcon help2Icon = ImageManager.getIcon(ImageManager.HELP_2);
			if (help2Icon != null) {
				this.helpIdButton.setIcon(help2Icon);
			} else {
				this.helpIdButton.setText("?");
			}
			this.helpIdButton.setMargin(new Insets(1, 1, 1, 1));
			this.configureButton(this.helpIdButton, (String) parameters.get("helpidbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.buttonPanelObjects.put("helpid", new ComponentConstraints(this.helpIdButton,
					new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(1, 5, 1, 1), 0, 0)));
			this.helpIdButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					HelpUtilities.showHelp(SwingUtilities.getWindowAncestor(Form.this), Form.this.getHelpIdString());
				}
			});
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHelpKey(final Map parameters) {
		final Object helpkey = parameters.get(Form.HELPKEY);
		boolean bHelpButton = false;
		if (helpkey == null) {
			bHelpButton = false;
		} else {
			bHelpButton = true;
			this.helpKey = helpkey.toString();
		}

		if (bHelpButton) {
			this.helpButton = new FormButton();
			final ImageIcon helpIcon = ImageManager.getIcon(ImageManager.HELP);
			if (helpIcon != null) {
				this.helpButton.setIcon(helpIcon);
			} else {
				this.helpButton.setText("?");
			}
			this.helpButton.setMargin(new Insets(1, 1, 1, 1));
			this.configureButton(this.helpButton, (String) parameters.get("helpbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.buttonPanelObjects.put("help", new ComponentConstraints(this.helpButton,
					new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(1, 5, 1, 1), 0, 0)));
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setButtonPanelColor(final Map parameters) {
		final Color color = ParseUtils.getColor((String) parameters.get("buttonpanelcolor"), null);
		if (color != null) {
			this.buttonPanel.setBackground(color);
			this.newButtonPanel.setBackground(color);
		} else if (!this.opaquebuttons) {
			((PaintPanel) this.buttonPanel).setBgPaint(Form.titleBackgroundPaint);
			((PaintPanel) this.newButtonPanel).setBgPaint(Form.titleBackgroundPaint);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 */
	protected void setReloadButton() {
		final JPanel jbAuxButtonsPanel = new JPanel(new BorderLayout());
		this.reloadButton = new JButton(ApplicationManager.getTranslation(Form.M_RELOAD_FORM));
		this.reloadButton
		.setToolTipText(ApplicationManager.getTranslation(Form.M_RELOAD_FORM) + " " + this.getArchiveName());
		this.reloadButton.setMargin(new Insets(0, 0, 0, 0));
		this.reloadButton.setForeground(Color.red);
		this.reloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (Form.this.detailForm == null) {
					((BaseFormManager) Form.this.formManager).reload(Form.this);
				} else {
					((DetailForm) Form.this.detailForm).reload(Form.this);
				}
			}
		});
		jbAuxButtonsPanel.add(this.reloadButton, BorderLayout.WEST);

		if (Form.TITLE_ON_TOP) {
			this.formTitleLabel = new FormTitleLabel(null);
			final JPanel pAux = new JPanel(new BorderLayout());
			pAux.add(this.formTitleLabel, BorderLayout.NORTH);
			pAux.add(this.buttonPanel);
			jbAuxButtonsPanel.add(pAux);
		} else {
			jbAuxButtonsPanel.add(this.buttonPanel);
		}

		super.add(jbAuxButtonsPanel, this.headerPanelPosition);
	}

	/**
	 * Method used to reduce de complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void configureFormIcons(final Map parameters) {
		try {
			this.nextIcon = ParseUtils.getImageIcon((String) parameters.get("nextbuttonicon"),
					ImageManager.getIcon(ImageManager.NEXT_2));
			this.previousIcon = ParseUtils.getImageIcon((String) parameters.get("previousbuttonicon"),
					ImageManager.getIcon(ImageManager.PREVIOUS_2));
			this.endIcon = ParseUtils.getImageIcon((String) parameters.get("endbuttonicon"),
					ImageManager.getIcon(ImageManager.END_2));
			this.startIcon = ParseUtils.getImageIcon((String) parameters.get("startbuttonicon"),
					ImageManager.getIcon(ImageManager.START_2));
			this.printIcon = ParseUtils.getImageIcon((String) parameters.get("printbuttonicon"),
					ImageManager.getIcon(ImageManager.PRINT));
			this.orderIcon = ImageManager.getIcon(ImageManager.ARRANGE);
			this.refreshIcon = ParseUtils.getImageIcon((String) parameters.get("refreshbuttonicon"),
					ImageManager.getIcon(ImageManager.REFRESH_2));

			if (this.attachment) {
				this.attachmentIcon = ParseUtils.getImageIcon((String) parameters.get("attachbuttonicon"),
						ImageManager.getIcon(ImageManager.ATTACH_FILE));
				this.attachmentEmptyIcon = ParseUtils.getImageIcon((String) parameters.get("attachbuttonemptyicon"),
						ImageManager.darker(this.attachmentIcon));
			}
			if (this.databaseBundle) {
				this.databaseBundleIcon = ParseUtils.getImageIcon((String) parameters.get("databasebundlebuttonicon"),
						ImageManager.getIcon(ImageManager.BUNDLE));
			}
			if (this.scriptEnabled) {
				this.scriptIcon = ParseUtils.getImageIcon((String) parameters.get("scriptbuttonicon"),
						ImageManager.getIcon(ImageManager.EDIT));
			}

			this.borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"),
					Form.defaultBorderButtons);
			this.opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"),
					Form.defaultOpaqueButtons);
			if (ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false)) {
				this.listenerHighlightButtons = new MouseAdapter() {

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

			this.nextButton = new FormButton(this.nextIcon);
			this.previousButton = new FormButton(this.previousIcon);
			this.endButton = new FormButton(this.endIcon);
			this.startButton = new FormButton(this.startIcon);

			this.refreshButton = new FormButton(this.refreshIcon);
			this.previousButton.setMnemonic(KeyEvent.VK_LEFT);
			this.nextButton.setMnemonic(KeyEvent.VK_RIGHT);
			final ImageIcon tableViewIcon = ParseUtils.getImageIcon((String) parameters.get("tablebuttonicon"),
					ImageManager.getIcon(ImageManager.TABLE_VIEW));
			final ImageIcon mask = ImageManager.getIcon(ImageManager.CHECK);
			final Icon iconTB = new TableButtonIcon(tableViewIcon, mask);
			this.tableButton = new TableButton(iconTB);

			this.printButton = new FormButton(this.printIcon);

			this.clearDataFieldButton
			.setIcon(ParseUtils.getImageIcon((String) parameters.get("cleardatafieldbuttonicon"),
					ImageManager.getIcon(ImageManager.DELETE_FIELDS)));
			this.resultCountLabel = new JLabel() {

				@Override
				public String getName() {
					return "ResultCountLabel";
				};
			};

			this.resultCountLabel.setFont(
					ParseUtils.getFont((String) parameters.get("resultcountfont"), this.resultCountLabel.getFont()));
			this.resultCountLabel.setForeground(ParseUtils.getColor((String) parameters.get("resultcountfg"),
					this.resultCountLabel.getForeground()));

			this.printButton.setToolTipText(ApplicationManager.getTranslation("form.print_data", this.resourcesFile));
			this.tableButton.setToolTipText(ApplicationManager.getTranslation("form.table_view", this.resourcesFile));
			this.refreshButton.setToolTipText(
					ApplicationManager.getTranslation("form.refresh_form_data", this.resourcesFile) + " (F5)");
			this.previousButton.setToolTipText(
					ApplicationManager.getTranslation("form.go_previous_record", this.resourcesFile) + " (ALT <-) ");
			this.nextButton.setToolTipText(
					ApplicationManager.getTranslation("form.go_next_record", this.resourcesFile) + " (ALT ->) ");
			this.startButton
			.setToolTipText(ApplicationManager.getTranslation("form.go_first_record", this.resourcesFile));
			this.endButton.setToolTipText(ApplicationManager.getTranslation("form.go_last_record", this.resourcesFile));

			this.configureButton(this.nextButton, (String) parameters.get("nextbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.previousButton, (String) parameters.get("previousbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.endButton, (String) parameters.get("endbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.startButton, (String) parameters.get("startbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.refreshButton, (String) parameters.get("refreshbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.clearDataFieldButton, (String) parameters.get("cleardatafieldbuttonicon"),
					this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.configureButton(this.printButton, (String) parameters.get("printbuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.configureButton(this.tableButton, (String) parameters.get("tablebuttonicon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);

		} catch (final Exception e) {
			Form.logger.error("Form. Icons error. ", e);

			this.nextButton = new FormButton("->");
			this.previousButton = new FormButton("<-");
			this.endButton = new FormButton(">>");
			this.startButton = new FormButton("<<");
			this.tableButton = new TableButton("...");
			this.refreshButton = new FormButton("...");
			this.printButton = new FormButton("print");

			this.resultCountLabel = new JLabel();
		}
	}

	protected void addComponentsToButtonPanel(String configuration) {
		Object[] positions = Form.defaultButtonPanelPosition;
		if (configuration == null) {
			if (Form.useOriginalButtonPosition) {
				configuration = Form.originalButtonPosition;
			} else {
				configuration = Form.defaultButtonPosition;
			}
		}
		final String configurationSource = configuration;
		try {
			if (configuration != null) {
				final List collapsibles = new ArrayList();
				while (configuration.indexOf(Form.COLLAPSIBLE_KEY + "(") >= 0) {
					final int start = configuration.indexOf(Form.COLLAPSIBLE_KEY + "(");
					final int end = configuration.indexOf(")", start);
					collapsibles.add(configuration.substring(start + Form.COLLAPSIBLE_KEY.length() + 1, end));
					configuration = configuration.substring(0, start + Form.COLLAPSIBLE_KEY.length())
							+ configuration.substring(end + 1);
				}

				final List tokensConfig = ApplicationManager.getTokensAt(configuration, ";");

				positions = tokensConfig.toArray();

				boolean isStatus = false;

				for (int i = 0; i < positions.length; i++) {
					if (Form.COLLAPSIBLE_KEY.equals(positions[i])) {
						final String currentCollapsible = (String) collapsibles.remove(0);
						this.processCollapsibleButton(currentCollapsible, isStatus, i);
						continue;
					}
					final Object object = this.buttonPanelObjects.get(positions[i]);
					if ((object != null) && (object instanceof ComponentConstraints)) {
						final ComponentConstraints cc = (ComponentConstraints) object;
						final GridBagConstraints gbc = cc.getConstraints();
						gbc.gridx = i;
						this.buttonPanel.add(cc.getComponent(), gbc);
						if (Form.STATUS_KEY.equals(positions[i])) {
							isStatus = true;
						}
					}
				}
			} else {
				this.setNormalConfiguration();
			}
		} catch (final Exception ex) {
			Form.logger.error("Position buttons is wrong defined:" + configurationSource, ex);
			this.setNormalConfiguration();
		}
	}

	protected void processCollapsibleButton(final String components, final boolean status, final int gridx) {
		final List tokensConfig = ApplicationManager.getTokensAt(components, ";");
		if (tokensConfig.size() > 0) {
			final CollapsibleButtonPanel collapsibleButton = new CollapsibleButtonPanel(status);
			if (!this.buttons) {
				collapsibleButton.setVisible(this.buttons);
			}
			collapsibleButton.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints(gridx, 1, 1, 1, 0, 1, GridBagConstraints.CENTER,
					GridBagConstraints.VERTICAL, new Insets(1, 0, 1, 0), 0, 0);
			this.buttonPanel.add(collapsibleButton, gbc);
			for (int i = 0; i < tokensConfig.size(); i++) {
				final Object key = tokensConfig.get(i);
				final Object object = this.buttonPanelObjects.get(key);
				if ((object != null) && (object instanceof ComponentConstraints)) {
					final ComponentConstraints cc = (ComponentConstraints) object;
					gbc = cc.getConstraints();
					gbc.gridx = i;
					collapsibleButton.add(cc.getComponent(), gbc);
				}
			}
		}
	}

	protected void setNormalConfiguration() {
		if (this.helpButton != null) {
			this.buttonPanel.add(this.helpButton, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 5, 1, 1), 0, 0));
		}
		if (this.helpIdButton != null) {
			this.buttonPanel.add(this.helpIdButton, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 5, 1, 1), 0, 0));
		}
		if (this.newButtonPanel != null) {
			this.buttonPanel.add(this.newButtonPanel, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.tableButton != null) {
			this.buttonPanel.add(this.tableButton, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.startButton != null) {
			this.buttonPanel.add(this.startButton, new GridBagConstraints(6, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.previousButton != null) {
			this.buttonPanel.add(this.previousButton, new GridBagConstraints(7, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.printButton != null) {
			this.buttonPanel.add(this.printButton, new GridBagConstraints(8, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.nextButton != null) {
			this.buttonPanel.add(this.nextButton, new GridBagConstraints(10, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.endButton != null) {
			this.buttonPanel.add(this.endButton, new GridBagConstraints(11, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 2), 0, 0));
		}
		if (this.resultCountLabel != null) {
			this.buttonPanel.add(this.resultCountLabel, new GridBagConstraints(12, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 0, 1, 2), 0, 0));
		}
		if (this.refreshButton != null) {
			this.buttonPanel.add(this.refreshButton, new GridBagConstraints(13, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 0, 1, 0), 0, 0));
		}
		if (this.clearDataFieldButton != null) {
			this.buttonPanel.add(this.clearDataFieldButton,
					new GridBagConstraints(14, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(1, 0, 1, 2), 0, 0));
		}
		if (this.statusBar != null) {
			this.buttonPanel.add(this.statusBar, new GridBagConstraints(15, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 0), 0, 0));
		}
	}

	protected void configureButton(final JButton button, final String iconPath, final boolean border, final boolean opaque,
			final MouseListener highlightListener) {
		if (button != null) {
			button.setFocusPainted(false);
			if (!border) {
				button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
			if (!opaque) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (highlightListener != null) {
				button.addMouseListener(highlightListener);
			}
			final Icon pressedIcon = ParseUtils.getPressedImageIcon("yes", iconPath, null);
			if (pressedIcon != null) {
				button.setPressedIcon(pressedIcon);
			}
			final Icon disabledIcon = ParseUtils.getDisabledImageIcon("yes", iconPath, null);
			if (disabledIcon != null) {
				button.setDisabledIcon(disabledIcon);
			}

		}
	}

	/**
	 * Returns the printing template name. This name is defined in xml form definition and if this
	 * parameter is not established this method returns <code>null</code>
	 * @return a <code>String</code> with the printing template name.
	 */
	public String getPrintingTemplateName() {
		return this.printingTemplateName;
	}

	public List getSetValuesOrder() {
		return this.setValuesOrder;
	}

	/**
	 * Establishes the <code>FormBuilder</code> that will be used by the components of this
	 * <code>Form</code> that implement {@link CreateForms} interface
	 */
	@Override
	public void setFormBuilder(final FormBuilder builder) {
		this.formBuilder = builder;
	}

	/**
	 * Appends the specified component to the end of this form. Also notifies the layout manager to add
	 * the component to this form's layout using the specified constraints object
	 * @param component the component to be added
	 * @param constraints an object expressing layout constraints for this component
	 */

	@Override
	public void add(final Component component, final Object constraints) {
		if (component instanceof Tab) {
			// Create a Tabbed Panel and put the tabs
			this.tabPane.addTab((String) constraints, ((Tab) component).getIcon(), component);
		} else if (component instanceof FormHeader) {
			// Adds the header
			this.buttonPanel.add(component,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(1, 1, 1, 10), 0, 0));
		} else {
			this.bodyPanel.add(component, constraints);
		}
	}

	@Override
	public void remove(final Component component) {
		if (component instanceof FormHeader) {
			this.buttonPanel.remove(component);
		} else {
			this.bodyPanel.remove(component);
		}

	}

	public JPanel getBodyPanel() {
		return this.bodyPanel;
	}

	public Map getDataComponentList() {
		return this.dataComponentList;
	}

	public void deleteDataComponentList() {
		this.dataComponentList.clear();
	}

	/**
	 * Creates and updates the component list contains in this form. This function must be called when
	 * the form is completely loaded.
	 */
	public void createLists() {
		// The form is the root of a tree with all the form components.
		// We use this tree and add the objects to the list with the data fields
		// of this form

		this.scanComponents(this, false);

		for (int i = 0; i < this.accessFormComponentList.size(); i++) {
			((AccessForm) this.accessFormComponentList.get(i)).setParentForm(this);
		}

		// Hides the tab panel if this has not elements
		if (this.tabPane.getComponentCount() == 0) {
			this.tabPane.setVisible(false);
		}
		// ////////////////////
		this.createdList = true;
		this.initPermissions();
	}

	/**
	 * Move across the tree with the form components
	 * @param container
	 * @param onlyReferenceDataFields
	 */
	protected void scanComponents(final Container container, final boolean onlyReferenceDataFields) {
		if (!onlyReferenceDataFields) {
			this.setContainerNotOnlyReferenceDataField(container);
		}
		if (container instanceof ReferenceComponent) {
			if (this.formManager != null) {
				final EntityReferenceLocator locator = this.formManager.getReferenceLocator();
				if (locator != null) {
					((ReferenceComponent) container).setReferenceLocator(locator);
				}
			} else {
				Form.logger.debug("FORM MANAGER is NULL trying to set reference locator for component: {} in Form",
						container);
			}
		}
		if (container instanceof CachedComponent) {
			if (this.formManager != null) {
				final EntityReferenceLocator locator = this.formManager.getReferenceLocator();
				if (locator == null) {
					Form.logger.debug("REFERENCE LOCATOR is NULL trying to set cache manager for component: {} in Form",
							container);
				} else {
					CacheManager.getDefaultCacheManager(locator).addCachedComponent((CachedComponent) container);
					((CachedComponent) container).setCacheManager(CacheManager.getDefaultCacheManager(locator));
				}
			} else {
				Form.logger.debug("FORM MANAGER is NULL trying to set cache manager for component: {} in Form",
						container);
			}
		}

		final int componentsCount = container.getComponentCount();
		for (int i = 0; i < componentsCount; i++) {
			final Component comp = container.getComponent(i);
			if ((comp instanceof Container) && (!(container instanceof IFormComponentManager))) {
				final Container childContainer = (Container) comp;
				this.scanComponents(childContainer, onlyReferenceDataFields);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #scanComponents(Container, boolean)}
	 * @param container
	 */
	protected void setContainerNotOnlyReferenceDataField(final Container container) {
		if (container instanceof SubFormComponent) {
			((SubFormComponent) container).setFormManager(this.formManager);
		}
		if (container instanceof DataComponent) {
			if (((DataComponent) container).getAttribute() == null) {
				throw new IllegalArgumentException(
						"Data component has not the attribute " + container.getClass().toString());
			}
			this.dataComponentList.put(((DataComponent) container).getAttribute(), container);
			container.setVisible(!((DataComponent) container).isHidden());
		}
		if (container instanceof DataComponentGroup) {
			this.groupDataComponentList.put(((DataComponentGroup) container).getAttribute(), container);
		}

		if ((container instanceof SelectCurrencyValues) && (!(container instanceof Form))) {
			this.currencyComponentsList.add(container);
		}
		if (container instanceof AccessForm) {
			this.accessFormComponentList.add(container);
		}
		if (container instanceof FormComponent) {
			if (!(container instanceof Form)) {
				this.componentList.add(container);
			}
		}

		if ((container instanceof Internationalization) && (!(container instanceof Form))) {
			this.internationalizeList.add(container);
		}

		if (container instanceof Button) {
			final String key = ((Button) container).getKey();
			if (key != null) {
				this.buttonList.put(key, container);
			}
		}

		if (container instanceof FormHeaderPopupButton) {
			final int n = ((FormHeaderPopupButton) container).getPopupComponentsCount();
			for (int i = 0; i < n; i++) {
				final Component c = ((FormHeaderPopupButton) container).getPopupComponentAt(i);
				if (c instanceof Button) {
					final String key = ((Button) c).getKey();
					if (key != null) {
						this.buttonList.put(key, c);
					}
				}
			}
		}

		if (container instanceof OpenDialog) {
			((OpenDialog) container).setParentFrame(this.parentFrame);
		}
		if (container instanceof CreateForms) {
			((CreateForms) container).setFormBuilder(this.formBuilder);
		}
	}

	/**
	 * Update the reference locator for all the reference data fields
	 */
	public void updateReferencesLocator() {
		this.scanComponents(this, true);
	}

	/**
	 * Returns a button reference that has the entry parameter as key
	 * @param key the key of the button requested
	 * @return a <code>Button</code> with this key
	 */

	public Button getButton(final String key) {
		return (Button) this.buttonList.get(key);
	}

	public Map getButtonList() {
		return this.buttonList;
	}

	/**
	 * Returns the value of data component which has the entry parameter as attribute. If the component
	 * attribute doesn't existe in the form, NULL will be returned.
	 * @param attribute the data field attribute to be obtained the value
	 * @return a <code>Object<code> with the data field value.
	 */

	public Object getDataFieldValue(final String attribute) {

		final FormComponent oDataComponent = this.getElementReference(attribute);
		if (oDataComponent == null) {
			return null;
		}
		Object oValue = null;
		if (oDataComponent instanceof DataComponent) {
			oValue = ((DataComponent) oDataComponent).getValue();
		} else if (oDataComponent instanceof DataComponentGroup) {
			oValue = ((DataComponentGroup) oDataComponent).getGroupValue();
		}
		return oValue;
	}

	/**
	 * Returns the data component value which has the entry parameter as attribute. But this value is
	 * not obtained from the data component. It is obtained from form data cache.
	 * <p>
	 * For example, in a form with data, if a user changes a data field the new value will be in form
	 * data field but the old value is stored in form data cache until the form is update with the call
	 * to method {@link #updateDataFields}
	 * @param attribute the data field attribute where the value is obtained from
	 * @return the <code>Object</code> with the data field value from the form data cache
	 */

	public Object getDataFieldValueFromFormCache(final String attribute) {

		if (this.totalDataList == null) {
			Form.logger.debug("Value for attribute: {} is not in the data list. Using the field value", attribute);
			return this.getDataFieldValue(attribute);
		}
		final Object oAttributeValues = this.totalDataList.get(attribute);
		if (oAttributeValues instanceof List) {
			// If for has no values currentIndex is -1 (fix problem deleting
			// data from a inserttable with otherkeys when the form is in search
			// mode)
			if (this.currentIndex < 0) {
				return null;
			}
			return ((List) oAttributeValues).get(this.currentIndex);
		} else {
			if (oAttributeValues != null) {
				return oAttributeValues;
			} else {
				Form.logger.debug("Value for attribute: {} is not in the data list. Using the field value", attribute);
				return this.getDataFieldValue(attribute);
			}
		}
	}

	/**
	 * Returns {@link DataComponentGroup} reference
	 * @param attribute {@link DataComponentGroup} attribute to be obtained the reference
	 * @return a <code>DataComponentGroup</code> reference
	 */
	public DataComponentGroup getGroupReference(final String attribute) {
		return (DataComponentGroup) this.groupDataComponentList.get(attribute);
	}

	/**
	 * Returns a {@link FormComponent} reference. To find a component in a form is necessary that the
	 * component implements {@link IdentifiedElement}
	 * @param attribute the data field attribute to be obtained the reference
	 * @return a {@@link FormComponent} reference
	 */

	public FormComponent getElementReference(final String attribute) {
		final DataComponent c = this.getDataFieldReference(attribute);
		if (c != null) {
			return c;
		}
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object el = this.componentList.get(i);
			if (el instanceof IdentifiedElement) {
				if (attribute.equals(((IdentifiedElement) el).getAttribute())) {
					return (FormComponent) el;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the reference of the data component which has the entry parameter as attribute
	 * @param attribute
	 * @return the {@link DataComponent} reference
	 */

	public DataComponent getDataFieldReference(final String attribute) {
		// The attribute of some fields like tables or reference data fields are
		// not strings
		// If the string attribute does not exist in the data list then search
		// for
		// other kind of attributes (
		final Object oReference = this.dataComponentList.get(attribute);
		if ((oReference != null) && (oReference instanceof DataComponent)) {
			return (DataComponent) oReference;
		} else {
			if (oReference == null) {
				// Search for not string attributes
				final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
				while (enumKeys.hasMoreElements()) {
					final Object oKey = enumKeys.nextElement();
					if (oKey instanceof Map) {
						// If the key of the Map is the attribute then
						// return
						// the component
						final Enumeration enumTableKeys = Collections.enumeration(((Map) oKey).keySet());
						if (enumTableKeys.hasMoreElements()) {
							final Object oTableKey = enumTableKeys.nextElement();
							if (oTableKey.equals(attribute)) {
								return (DataComponent) this.dataComponentList.get(oKey);
							}
						}
					} else if (oKey instanceof ReferenceFieldAttribute) {
						if (attribute.equals(((ReferenceFieldAttribute) oKey).getAttr())) {
							return (DataComponent) this.dataComponentList.get(oKey);
						}
					} else if (oKey instanceof MultipleTableAttribute) {
						if (attribute.equals(((MultipleTableAttribute) oKey).getAttribute())) {
							return (DataComponent) this.dataComponentList.get(oKey);
						}
					} else if (oKey instanceof MultipleReferenceDataFieldAttribute) {
						if (attribute.equals(((MultipleReferenceDataFieldAttribute) oKey).getAttr())) {
							return (DataComponent) this.dataComponentList.get(oKey);
						}
					} else if (oKey instanceof EntityFunctionAttribute) {
						if (attribute.equals(((EntityFunctionAttribute) oKey).getAttr())) {
							return (DataComponent) this.dataComponentList.get(oKey);
						}
					}
				}
				return (DataComponent) oReference;
			} else {
				return null;
			}
		}
	}

	/**
	 * Get the string that identify an attribute. The string is usually the name in the xml
	 * @param attribute
	 * @return
	 */
	protected String getAttributeName(final Object attribute) {
		if (attribute instanceof ReferenceFieldAttribute) {
			return ((ReferenceFieldAttribute) attribute).getAttr();
		} else if (attribute instanceof TableAttribute) {
			return ((TableAttribute) attribute).getEntity();
		} else if (attribute instanceof MultipleTableAttribute) {
			return ((MultipleTableAttribute) attribute).getAttribute().toString();
		} else if (attribute instanceof MultipleReferenceDataFieldAttribute) {
			return ((MultipleReferenceDataFieldAttribute) attribute).getAttr();
		} else if (attribute instanceof EntityFunctionAttribute) {
			return ((EntityFunctionAttribute) attribute).getAttr();
		}
		return attribute.toString();
	}

	/**
	 * Updates the values of each form data field and the values of the total data cache with the entry
	 * parameter. The entry parameter is a <code>Hashtable</code> whose keys are the data field
	 * attributes and the values are a <code>Vector</code> with the data for this attribute. If the
	 * <code>Vector</code> has more than one record the first record will be displayed.
	 * @param data
	 */

	public void updateDataFields(final Map data) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.updateDataFieldsEDTh(data);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Form.this.updateDataFieldsEDTh(data);
					}
				});
			} catch (final Exception e) {
				Form.logger.error("Exception (InvokeAndWait) establishing data field values", e);
			}
		}
	}

	protected void updateDataFieldsEDTh(final Map data) {
		this.setFormValues(data);
		if ((data != null) && !data.isEmpty()) {
			final DataNavigationEvent e = new DataNavigationEvent(Form.this, Form.this.getDataFieldValues(false, false),
					DataNavigationEvent.PROGRAMMATIC_NAVIGATION, 0, 0);
			Form.this.fireDataChanged(e);
		}
	}

	/**
	 * Updates the values of each form data field and the values of the total data cache with the data
	 * entry parameter. The data entry parameter is a <code>Hashtable</code> of which keys are the data
	 * field attributes and the values are a <code>Vector</code> with the data for this attribute. If
	 * the <code>Vector</code> has more than one record the record with index as currentIndex will be
	 * displayed.
	 * @param data
	 * @param currentIndex
	 */

	public void updateDataFields(final Map data, final int currentIndex) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.setFormValues(data);
			if ((data != null) && !data.isEmpty()) {
				this.updateDataFields(currentIndex);
			}
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Form.this.setFormValues(data);
						Form.this.updateDataFields(currentIndex);
					}
				});
			} catch (final Exception e) {
				Form.logger.error("Exception (InvokeAndWait) establishing data field values", e);
			}
		}
	}

	/**
	 * Establishes the values in each form data field that are obtained from entry parameter.
	 * @param data a <code>Hashtable</code> with the values for establish.
	 * @deprecated Use {@link #updateDataFields(Map)}
	 */

	@Deprecated
	protected void setFormValues(Map data) {
		final long initialTime = System.currentTimeMillis();
		if (data == null) {
			data = new Hashtable();
		}
		if (data.isEmpty()) {
			this.deleteDataFields();
			this.totalDataList = data;
			this.currentIndex = -1;
			this.vectorSize = 0;
			if (this.resultCountLabel != null) {
				this.resultCountLabel.setVisible(false);
				this.startButton.setEnabled(false);
				this.endButton.setEnabled(false);
				this.previousButton.setEnabled(false);
				this.nextButton.setEnabled(false);
				this.resultCountLabel.setText(
						new Integer(this.currentIndex + 1).toString() + "/" + new Integer(this.vectorSize).toString());
			}
			return;
		}
		if (this.refreshButton != null) {
			this.refreshButton.setEnabled(true);
		}
		this.totalDataList = ObjectTools.clone(data);
		final Enumeration enumComponents = Collections.enumeration(this.dataComponentList.values());
		final List vComponentList = new Vector();
		this.addComponentToComponentList(enumComponents, vComponentList);
		this.vectorSize = 0;
		for (int i = 0; i < vComponentList.size(); i++) {
			final Object comp = vComponentList.get(i);
			if (comp instanceof ReferenceComponent) {
				vComponentList.remove(i);
				vComponentList.add(vComponentList.size(), comp);
			}
		}

		final List filterComponentList = new Vector();
		for (int i = 0; i < vComponentList.size(); i++) {
			final Object comp = vComponentList.get(i);
			if (comp instanceof IFilterElement) {
				vComponentList.remove(i);
				filterComponentList.add(comp);
			}
		}

		int countFilterComponent = filterComponentList.size();
		int interactionNumber = 0;

		while ((!filterComponentList.isEmpty()) && (countFilterComponent >= interactionNumber)) {
			interactionNumber++;
			final IFilterElement current = (IFilterElement) filterComponentList.remove(0);
			if (this.setValuesOrder != null) {
				final String currentAttribute = ((DataComponent) current).getAttribute().toString();
				if (this.setValuesOrder.contains(currentAttribute)) {
					vComponentList.add(vComponentList.size(), current);
					interactionNumber = 0;
					continue;
				}
			}

			boolean delete = true;
			final List parentKeyList = current.getParentKeyList();
			if ((parentKeyList == null) || (parentKeyList.size() == 0)) {
				vComponentList.add(vComponentList.size(), current);
				interactionNumber = 0;
			} else {
				for (int i = 0; i < filterComponentList.size(); i++) {
					final Object comp = filterComponentList.get(i);
					Object oAttributeKey = ((DataComponent) comp).getAttribute();
					if (oAttributeKey instanceof ReferenceFieldAttribute) {
						oAttributeKey = ((ReferenceFieldAttribute) oAttributeKey).getAttr();
					}
					if (parentKeyList.contains(oAttributeKey)) {
						filterComponentList.add(filterComponentList.size(), current);
						delete = false;
						break;
					}
				}
				if (delete) {
					vComponentList.add(vComponentList.size(), current);
					interactionNumber = 0;
				}
			}
			countFilterComponent = filterComponentList.size();
		}

		if (!(countFilterComponent >= interactionNumber)) {
			final StringBuilder buffer = new StringBuilder("WARNING: ");
			for (int i = 0; i < filterComponentList.size(); i++) {
				buffer.append(((DataComponent) filterComponentList.get(i)).getAttribute().toString());
				if (i < (filterComponentList.size() - 1)) {
					buffer.append(",");
				}
			}
			buffer.append(" ");
			buffer.append(" data fields are related and must be established the 'setvalueorder' parameter");
			Form.logger.debug("Buffer: {}", buffer.toString());
		}

		this.setValueOrderComponents(vComponentList);

		final List vAttributes = new Vector();
		for (int i = 0; i < vComponentList.size(); i++) {
			final Object oComponent = vComponentList.get(i);
			final Object oAttributeKey = ((DataComponent) oComponent).getAttribute();
			vAttributes.add(i, oAttributeKey);
		}
		Form.logger.debug("Form: {}. Setting values order: ", this.getArchiveName(), vAttributes);

		for (int i = 0; i < vComponentList.size(); i++) {
			this.setValueOfComponents(data, vComponentList, i);
		}
		this.resultCountLabel
		.setText(new Integer(this.currentIndex + 1).toString() + "/" + new Integer(this.vectorSize).toString());
		final long endTime = System.currentTimeMillis();
		Form.logger.trace("Time setting form values: {} seconds", (endTime - initialTime) / 1000.0);
	}

	/**
	 * Method used to reduce the complexity of {@link #setFormValues(Map)}
	 * @param vComponentList
	 */
	protected void setValueOrderComponents(final List vComponentList) {
		if (this.setValuesOrder != null) {
			for (int j = this.setValuesOrder.size() - 1; j >= 0; j--) {
				for (int i = 0; i < vComponentList.size(); i++) {
					final Object comp = vComponentList.get(i);
					final Object oAttributeKey = ((DataComponent) comp).getAttribute();
					if (oAttributeKey instanceof ReferenceFieldAttribute) {
						if (this.setValuesOrder.get(j).equals(((ReferenceFieldAttribute) oAttributeKey).getAttr())) {
							vComponentList.remove(i);
							vComponentList.add(0, comp);
						}
					} else {
						if (this.setValuesOrder.get(j).equals(oAttributeKey)) {
							vComponentList.remove(i);
							vComponentList.add(0, comp);
						}
					}
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #setFormValues(Map)}
	 * @param data
	 * @param vComponentList
	 * @param i
	 */
	protected void setValueOfComponents(final Map data, final List vComponentList, final int i) {
		final Object oComponent = vComponentList.get(i);
		final Object oAttributeKey = ((DataComponent) oComponent).getAttribute();
		// If the attribute is a ReferenceFieldAttribute, then search in the
		// data
		Object oData = null;
		if (oAttributeKey != null) {
			oData = data.get(oAttributeKey);
			if (oData == null) {
				// Checks ReferenceFieldAttribute
				if (oAttributeKey instanceof ReferenceFieldAttribute) {
					final String attr = ((ReferenceFieldAttribute) oAttributeKey).getAttr();
					// Search if another field with this attribute exists
					if (this.getDataFieldReference(attr) == oComponent) {
						// It is the same component, then no other component
						// exist
						// with the same attribute
						oData = data.get(attr);
					}
				}
			}
		}
		if (oData != null) {
			if (oData instanceof List) {
				// If it is empty then no update is done
				if (((List) oData).size() == 0) {
					if (this.resultCountLabel != null) {
						this.resultCountLabel.setVisible(false);
						this.startButton.setEnabled(false);
						this.endButton.setEnabled(false);
						this.previousButton.setEnabled(false);
						this.nextButton.setEnabled(false);
						if (this.tableButton.hasSelectionColumns()) {
							this.tableButton.setSelectionColumnsMode(true);
						} else {
							this.tableButton.setEnabled(false);
						}

						this.currentIndex = 0;

					}

					if (((DataComponent) oComponent).isModifiable()) {
						((DataComponent) oComponent).deleteData();
					}

					return;
				}
				final Object oFieldValue = ((List) oData).get(0);
				if (oFieldValue != null) {
					final long t = System.currentTimeMillis();
					((DataComponent) oComponent).setValue(oFieldValue);
					final long lTotalTime = System.currentTimeMillis() - t;
					Form.logger.trace("Form.setFormValues() --> Setting field value: {}. Duration: {} ",
							oComponent.getClass(), lTotalTime);

				} else {
					if (((DataComponent) oComponent).isModifiable()) {
						((DataComponent) oComponent).deleteData();
					}
				}
				// If the List has more than one value then there are
				// multiple
				// results
				// And we can use the navigation buttons to see the
				// different
				// values.
				// Show the option to see the table view too
				if (((List) oData).size() > 1) {
					// Enable the buttons
					if (this.resultCountLabel != null) {
						this.startButton.setEnabled(true);
						this.endButton.setEnabled(true);
						this.previousButton.setEnabled(true);
						this.nextButton.setEnabled(true);

						if ((this.additionalTableViewColumns != null) && (this.additionalTableViewColumns.size() > 0)) {
							this.tableButton.setEnabled(true);
							this.tableButton.setSelectionColumnsMode(false);
						} else {
							if (this.tableButton.hasSelectionColumns()) {
								this.tableButton.setSelectionColumnsMode(true);
							} else {
								this.tableButton.setSelectionColumnsMode(false);
							}
						}

						this.vectorSize = ((List) oData).size();
						// only if buttons are visible then set visible the
						// result count label
						this.resultCountLabel.setVisible(this.buttons);
						this.currentIndex = 0;

					}
				} else {
					if (this.resultCountLabel != null) {
						this.resultCountLabel.setVisible(false);
						this.startButton.setEnabled(false);
						this.endButton.setEnabled(false);
						this.previousButton.setEnabled(false);
						this.nextButton.setEnabled(false);

						if (this.tableButton.hasSelectionColumns()) {
							this.tableButton.setSelectionColumnsMode(true);
						} else {
							this.tableButton.setEnabled(false);
						}

						this.currentIndex = 0;
						this.vectorSize = ((List) oData).size();
					}
				}
			} else {
				if ((oData instanceof Map) && (oComponent instanceof Table)) {
					final long t = System.currentTimeMillis();
					((DataComponent) oComponent).setValue(oData);
					final long lTotalTime = System.currentTimeMillis() - t;
					Form.logger.trace("Form.setFormValues() --> Setting field value: {}. Duration: {} ",
							oComponent.getClass(), lTotalTime);
				} else {
					final long t = System.currentTimeMillis();
					((DataComponent) oComponent).setValue(oData);
					final long lTotalTime = System.currentTimeMillis() - t;
					Form.logger.trace("Form.setFormValues() --> Setting field value: {}. Duration: {} ",
							oComponent.getClass(), lTotalTime);
				}
			}
		} else {
			// If there is no data to update the form then clear all fields
			if (((DataComponent) oComponent).isModifiable()) {
				((DataComponent) oComponent).deleteData();
			}

		}
	}

	/**
	 * Updates the form data field using the values stored in total data cache. Establishes the data
	 * field values from the record of the total data cache of which index is the entry parameter.
	 * @param index a <code>index</code> of the data record for update.
	 */

	public void updateDataFields(final int index) {

		final int previousIndex = this.currentIndex;
		final DataNavigationEvent eOld = new DataNavigationEvent(Form.this, Form.this.getDataFieldValues(false, false),
				DataNavigationEvent.PROGRAMMATIC_NAVIGATION, index,
				previousIndex);
		if (!this.fireDataWillChange(eOld)) {
			return;
		}
		this.updateDataFields_Internal(index);
		final DataNavigationEvent e = new DataNavigationEvent(Form.this, Form.this.getDataFieldValues(false, false),
				DataNavigationEvent.PROGRAMMATIC_NAVIGATION, index, previousIndex);
		Form.this.fireDataChanged(e);
	}

	/**
	 * Updates the form data field using the values stored in total data cache. Establishes the data
	 * field values from the record of the total data cache of which index is the entry parameter.
	 * @param index a <code>index</code> of the data record for update.
	 */
	protected void updateDataFields_Internal(final int index) {
		try {
			this.setCountLabel(index);
			this.enableRefreshButton();
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
		this.currentIndex = index;

		final Enumeration enumComponents = Collections.enumeration(this.dataComponentList.values());
		final List vComponentList = new Vector();
		this.addComponentToComponentList(enumComponents, vComponentList);

		// Put the reference components at the end of the list
		for (int i = 0; i < vComponentList.size(); i++) {
			final Object comp = vComponentList.get(i);
			if (comp instanceof ReferenceComponent) {
				vComponentList.remove(i);
				vComponentList.add(vComponentList.size(), comp);
			}
		}

		// Put the filter elements at the end of the list
		final List filterComponentList = new Vector();
		for (int i = 0; i < vComponentList.size(); i++) {
			final Object comp = vComponentList.get(i);
			if (comp instanceof IFilterElement) {
				vComponentList.remove(i);
				filterComponentList.add(comp);
			}
		}

		int countFilterComponent = filterComponentList.size();
		int interactionNumber = 0;

		while (!(filterComponentList.isEmpty()) && (countFilterComponent >= interactionNumber)) {
			interactionNumber++;
			final IFilterElement current = (IFilterElement) filterComponentList.remove(0);
			if (this.setValuesOrder != null) {
				final String currentAttribute = ((DataComponent) current).getAttribute().toString();
				if (this.setValuesOrder.contains(currentAttribute)) {
					vComponentList.add(vComponentList.size(), current);
					interactionNumber = 0;
					continue;
				}
			}

			boolean delete = true;
			final List parentKeyList = current.getParentKeyList();
			if ((parentKeyList == null) || (parentKeyList.size() == 0)) {
				vComponentList.add(vComponentList.size(), current);
				interactionNumber = 0;
			} else {
				for (int i = 0; i < filterComponentList.size(); i++) {
					final Object comp = filterComponentList.get(i);
					Object attributeKey = ((DataComponent) comp).getAttribute();
					if (attributeKey instanceof ReferenceFieldAttribute) {
						attributeKey = ((ReferenceFieldAttribute) attributeKey).getAttr();
					}
					if (parentKeyList.contains(attributeKey)) {
						filterComponentList.add(filterComponentList.size(), current);
						delete = false;
						break;
					}
				}
				if (delete) {
					vComponentList.add(vComponentList.size(), current);
					interactionNumber = 0;
				}
			}
			countFilterComponent = filterComponentList.size();
		}

		if (!(countFilterComponent >= interactionNumber)) {
			final StringBuilder buffer = new StringBuilder("WARNING: ");
			for (int i = 0; i < filterComponentList.size(); i++) {
				buffer.append(((DataComponent) filterComponentList.get(i)).getAttribute().toString());
				if (i < (filterComponentList.size() - 1)) {
					buffer.append(",");
				}
			}
			buffer.append(" ");
			buffer.append(" data fields are related and must be established the 'setvalueorder' parameter");
			Form.logger.debug("Buffer: {}", buffer.toString());
		}

		this.setValueOrderComponents(vComponentList);

		final StringBuilder buffer = new StringBuilder("Form value order: ");
		boolean init = false;
		for (final Object current : vComponentList) {
			if (current instanceof DataComponent) {
				if (init) {
					buffer.append(",");
				} else {
					init = true;
				}
				buffer.append(((DataComponent) current).getAttribute());
			}
		}
		Form.logger.debug("Buffer: {}", buffer.toString());

		for (int i = 0; i < vComponentList.size(); i++) {
			final Object oComponent = vComponentList.get(i);
			if (!(oComponent instanceof DataComponent)) {
				continue;
			}
			final Object oAttributeKey = ((DataComponent) oComponent).getAttribute();
			Object oData = null;
			if (oAttributeKey != null) {
				oData = this.getOData(oComponent, oAttributeKey);
			}
			if (oData != null) {
				this.updateFieldWithData(index, oComponent, oAttributeKey, oData);
			} else {
				// If there are not data to update the field then clear it
				if (((DataComponent) oComponent).isModifiable()) {
					((DataComponent) oComponent).deleteData();
				}
			}
		}
	}

	/**
	 * @param enumComponents
	 * @param vComponentList
	 */
	protected void addComponentToComponentList(final Enumeration enumComponents, final List vComponentList) {
		while (enumComponents.hasMoreElements()) {
			final Object oComponent = enumComponents.nextElement();
			if (oComponent instanceof AccessForm) {
				vComponentList.add(vComponentList.size(), oComponent);
			} else {
				vComponentList.add(0, oComponent);
			}
		}
	}

	/**
	 *
	 */
	protected void enableRefreshButton() {
		if (this.vectorSize > 0) {
			if (this.refreshButton != null) {
				this.refreshButton.setEnabled(true);
			}
		}
	}

	/**
	 * @param index
	 */
	protected void setCountLabel(final int index) {
		if (this.resultCountLabel != null) {
			this.resultCountLabel
			.setText(new Integer(index + 1).toString() + "/" + new Integer(this.vectorSize).toString());
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #updateDataFields_Internal(int)}
	 * @param oComponent
	 * @param oAttributeKey
	 * @return
	 */
	protected Object getOData(final Object oComponent, final Object oAttributeKey) {
		Object oData;
		oData = this.totalDataList.get(oAttributeKey);
		if (oData == null) {
			// Checks ReferenceFieldAttribute
			if (oAttributeKey instanceof ReferenceFieldAttribute) {
				final String attr = ((ReferenceFieldAttribute) oAttributeKey).getAttr();
				// Search for other field with the same attribute
				if (this.getDataFieldReference(attr) == oComponent) {
					// It is the same, then no other exists with the
					// same
					// attribute
					oData = this.totalDataList.get(attr);
				}
			} else if (oAttributeKey instanceof MultipleTableAttribute) {
				final String attr = ((MultipleTableAttribute) oAttributeKey).getAttribute().toString();
				if (this.getDataFieldReference(attr) == oComponent) {
					oData = this.totalDataList.get(attr);
				}
			} else if (oAttributeKey instanceof EntityFunctionAttribute) {
				final String attr = ((EntityFunctionAttribute) oAttributeKey).getAttr();
				if (this.getDataFieldReference(attr) == oComponent) {
					oData = this.totalDataList.get(attr);
				}
			} else if (oAttributeKey instanceof AttachmentAttribute) {
				final String attr = ((AttachmentAttribute) oAttributeKey).getAttribute();
				// There is no other equal field, because it is a protected
				// attribute for attachments that only the attachments button
				// has.
				oData = this.totalDataList.get(attr);
			}
		}
		return oData;
	}

	/**
	 * Method used to reduce the complexity of {@link #updateDataFields_Internal(int)}
	 * @param index
	 * @param oComponent
	 * @param oAttributeKey
	 * @param oData
	 */
	protected void updateFieldWithData(final int index, final Object oComponent, final Object oAttributeKey, final Object oData) {
		if (oData instanceof List) {
			if (index >= ((List) oData).size()) {
				((DataComponent) oComponent).deleteData();
				Form.logger.debug(
						"Form->updateDataFields_Internal(int index): List with values for the field {} has less elements that the specified index -> List size: {}. Index: {}",
						oAttributeKey, ((List) oData).size(), index);
			} else {
				final Object o = ((List) oData).get(index);
				if (o instanceof EntityResult) {
					final EntityResult re = (EntityResult) o;
					if (re.isEmpty()) {
						((DataComponent) oComponent).deleteData();
						if (oComponent instanceof SubForm) {
							((SubForm) oComponent).setParentKeys();
						}
					} else {
						((DataComponent) oComponent).setValue(re);
					}
				} else {
					((DataComponent) oComponent).setValue(o);
				}
			}
		} else {
			((DataComponent) oComponent).setValue(oData);
		}
	}

	protected void updateDataFieldNavegationButton(final int index) {
		final int previousIndex = this.currentIndex;
		final DataNavigationEvent eOld = new DataNavigationEvent(Form.this, Form.this.getDataFieldValues(false, false),
				DataNavigationEvent.BUTTON_NAVIGATION, index, previousIndex);
		if (!this.fireDataWillChange(eOld)) {
			return;
		}
		this.updateDataFields_Internal(index);
		final DataNavigationEvent e = new DataNavigationEvent(Form.this, Form.this.getDataFieldValues(false, false),
				DataNavigationEvent.BUTTON_NAVIGATION, index, previousIndex);
		Form.this.fireDataChanged(e);
	}

	/**
	 * Deletes data from all form data components.
	 */

	public void deleteDataFields() {
		final Enumeration enumComponents = Collections.enumeration(this.dataComponentList.values());
		while (enumComponents.hasMoreElements()) {
			final Object oComponent = enumComponents.nextElement();
			if (((DataComponent) oComponent).isModifiable()) {
				((DataComponent) oComponent).deleteData();
			}
		}

		if ((this.tableButton != null) && this.tableButton.hasSelectionColumns()) {
			this.tableButton.setSelectionColumnsMode(true);
		} else {
			this.tableButton.setEnabled(false);
		}

		if (this.refreshButton != null) {
			this.refreshButton.setEnabled(false);
		}
	}

	/**
	 * If parameter is true, data from all form data component will delete if not, only the modifiable
	 * components will delete.
	 * @param includeNonModif false if only the modifiable components should be deleted
	 */

	public void deleteDataFields(final boolean includeNonModif) {
		final Enumeration enumComponents = Collections.enumeration(this.dataComponentList.values());
		while (enumComponents.hasMoreElements()) {
			final Object oComponent = enumComponents.nextElement();
			if (((DataComponent) oComponent).isModifiable()) {
				((DataComponent) oComponent).deleteData();
			} else {
				if (includeNonModif) {
					((DataComponent) oComponent).deleteData();
				}
			}
		}

		if ((this.tableButton != null) && this.tableButton.hasSelectionColumns()) {
			this.tableButton.setSelectionColumnsMode(true);
		} else {
			this.tableButton.setEnabled(false);
		}

		if (this.refreshButton != null) {
			this.refreshButton.setEnabled(false);
		}
	}

	/**
	 * Warning!!!
	 * <p>
	 * This method must only be used internally by the detail form when the form is opened from a table.
	 * This method considers that only a simple parent key can exist. If the parent key is formed by
	 * multiple columns, this method will only return one of theirs values.
	 * @deprecated
	 * @see #getParentKeyValues
	 */
	@Deprecated
	public Object getParentKeyValue() {
		return this.parentKeyValue;
	}

	/**
	 * Returns the parent keys values
	 * @return
	 */
	public Map getParentKeyValues() {
		final Map current = new Hashtable();

		final List parentkeys = this.getParentKeys();
		if ((parentkeys != null) && (parentkeys.size() > 0)) {
			for (int i = 0; i < parentkeys.size(); i++) {
				final Object currentKey = parentkeys.get(i);
				final Object currentValue = this.getDataFieldValue(currentKey.toString());
				if (currentValue != null) {
					current.put(currentKey, currentValue);
				}
			}
		}
		return current;
	}

	/**
	 * Warning!!!
	 * <p>
	 * This method must only be used internally by the detail form when the form is opened from a table.
	 * This method considers that only a simple parent key can exist. If multiple parent keys values are
	 * established by this method, only the last call to this method will be stored internally and will
	 * be returned in the {@link getParentKeyValue}
	 *
	 * @see #getParentKeyValues
	 * @deprecated
	 */
	@Deprecated
	public void setParentKeyValue(final String attribute, final Object value) {
		this.parentKeyValue = value;
		// If there is a field with this attribute then set the value
		final DataComponent comp = this.getDataFieldReference(attribute);
		if (comp != null) {
			comp.setValue(value);
		}
	}

	/**
	 * Establishes the values in the data field that theirs attributes is into the parent key list Sets
	 * to parent keys values
	 * @param values a <code>Hashtable</code>
	 */

	public void setParentKeyValues(final Map values) {
		if ((values != null) && (values.size() > 0)) {
			final List parentkeys = this.getParentKeys();
			final Enumeration keys = Collections.enumeration(values.keySet());
			while (keys.hasMoreElements()) {
				final Object currentKey = keys.nextElement();
				if (parentkeys.contains(currentKey)) {
					this.setDataFieldValue(currentKey, values.get(currentKey));
				}
			}
		}
	}

	/**
	 * Enables the data component from this form with this attribute.
	 * @param attribute data component attribute to be enabled
	 */
	public void enableDataField(final String attribute) {
		this.enableDataField(attribute, false);
	}

	/**
	 * Enables the data component from this form with this attribute and if the requestfocus is true the
	 * enabled data component requests the focus
	 * @param attribute data component attribute to be enabled
	 * @param requestFocus true data component should request the focus
	 */

	public void enableDataField(final String attribute, final boolean requestFocus) {
		final DataComponent oField = this.getDataFieldReference(attribute);
		if (oField != null) {
			oField.setEnabled(true);
			if (requestFocus && (oField instanceof Component)) {
				((Component) oField).requestFocus();
			}
		}
	}

	/**
	 * Disables the data field with this attribute
	 * @param attribute attribute of data field to be disabled
	 */
	public void disableDataField(final String attribute) {
		final Object oField = this.getDataFieldReference(attribute);
		if (oField != null) {
			((FormComponent) oField).setEnabled(false);
		}
	}

	/**
	 * Sets a data component as modifiable or not modifiable
	 * @param attribute
	 * @param modifiable
	 */
	public void setModifiable(final String attribute, final boolean modifiable) {
		final DataComponent dataField = this.getDataFieldReference(attribute);
		if (dataField != null) {
			dataField.setModifiable(modifiable);
		}
	}

	/**
	 * Enables all data components from this form.
	 */
	public void enableDataFields() {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			// If the key is not a string can be a table or a reference data
			// field
			String sAttribute = null;
			if (oKey instanceof Map) {
				final Enumeration enumHashKeys = Collections.enumeration(((Map) oKey).keySet());
				final Object ent = enumHashKeys.nextElement();
				sAttribute = ent.toString();
			} else if (oKey instanceof ReferenceFieldAttribute) {
				sAttribute = ((ReferenceFieldAttribute) oKey).getAttr();
			} else {
				sAttribute = oKey.toString();
			}
			this.enableDataField(sAttribute);
		}
	}

	/**
	 * Enables all data components from this form. If modifiable is true the modifiable components will
	 * enable
	 * @param modifiable false if the modifiable components should not be enabled
	 */

	public void enableDataFields(final boolean modifiable) {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			String sAttribute = null;
			if (oKey instanceof Map) {
				final Enumeration enumHashtKeys = Collections.enumeration(((Map) oKey).keySet());
				final Object ent = enumHashtKeys.nextElement();
				sAttribute = ent.toString();
			} else if (oKey instanceof ReferenceFieldAttribute) {
				sAttribute = ((ReferenceFieldAttribute) oKey).getAttr();
			} else {
				sAttribute = oKey.toString();
			}
			final Object oField = this.dataComponentList.get(oKey);
			if (modifiable) {
				this.enableDataField(sAttribute);
			} else {
				if (((DataComponent) oField).isModifiable()) {
					this.enableDataField(sAttribute);
				}
			}
		}
	}

	public void setDefaultValues() {
		final Enumeration elements = Collections.enumeration(this.dataComponentList.values());
		while (elements.hasMoreElements()) {
			final Object current = elements.nextElement();
			if (current instanceof IDefaultValueComponent) {
				((IDefaultValueComponent) current).setDefaultValue();
			}
		}
	}

	/**
	 * Disables all data components from form
	 */
	public void disableDataFields() {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oField = this.dataComponentList.get(oKey);
			((FormComponent) oField).setEnabled(false);
		}
	}

	/**
	 * Disables all data components from form except the data component with this attribute
	 * @param except the attribute of data component that isn't disabled
	 */

	public void disableDataFields(final String except) {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oField = this.dataComponentList.get(oKey);
			if (oField instanceof IdentifiedElement) {
				if ((except != null) && except.equals(((IdentifiedElement) oField).getAttribute())) {
					continue;
				} else {
					((FormComponent) oField).setEnabled(false);
				}
			} else {
				((FormComponent) oField).setEnabled(false);
			}
		}
	}

	/**
	 * Returns a list of data field attributes that are contained in the form
	 * @return a <code>Vector</code> with the data field attribute list from the form
	 */

	public List getDataFieldAttributeList() {
		final long t = System.currentTimeMillis();
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		final List vAttributes = new Vector();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			if (oKey instanceof TableAttribute) {
				// refresh table attribute.Parameter can have changed.Ex:
				// recordNumberToInitiallyDownload
				vAttributes.add(((DataComponent) this.dataComponentList.get(oKey)).getAttribute());
			} else {
				vAttributes.add(oKey);
			}
		}
		Form.logger.trace("Time to create dataFieldAttributeList: {} ms", System.currentTimeMillis() - t);
		return vAttributes;
	}

	/**
	 * Returns a <code>Hashtable</code> where the attribute list and translated text are stored in. The
	 * key of each record of the <code>Hashtable</code> is the data field attribute and the value is the
	 * translated text.
	 * @return the <code>Hashtable</code>
	 */

	protected Map getTranslatedDataFieldLabel() {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		final Map hList = new Hashtable();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object comp = this.dataComponentList.get(oKey);
			if (comp instanceof DataComponent) {
				hList.put(oKey, ((DataComponent) comp).getLabelComponentText());
			} else if (comp instanceof DataComponentGroup) {
				hList.put(oKey, ((DataComponentGroup) comp).getLabel());
			}
		}
		return hList;
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */
	public int message(final String message, final int messageType) {
		return this.message(message, messageType, (String) null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param args a extra information list is used to create the message text. The message text is
	 *        created passing the message and the args through the resource bundle.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final String message, final Object[] args, final int messageType) {
		return this.message(message, messageType, null, args);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Dialog from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Dialog owner, final String message, final int messageType) {
		return this.message((Window) owner, message, messageType, null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final String message, final int messageType, final Throwable exception) {
		final StringBuilder buffer = ApplicationManager.printStackTrace(exception);
		return this.message(message, messageType, buffer != null ? buffer.toString() : null, (Object[]) null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final String message, final int messageType, final String detail) {
		return this.message(message, messageType, detail, (Object[]) null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @param args a extra information list is used to create the message text. The message text is
	 *        created passing the message and the args through the resource bundle.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final String message, final int messageType, final String detail, final Object[] args) {
		final Window w = SwingUtilities.getWindowAncestor(this);
		return this.message(w, message, messageType, detail, args);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Dialog from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Dialog owner, final String message, final int messageType, final String detail) {
		return this.message(owner, message, messageType, detail, null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Dialog from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @param args a extra information list is used to create the message text. The message text is
	 *        created passing the message and the args through the resource bundle.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Dialog owner, final String message, final int messageType, final String detail, final Object[] args) {
		switch (messageType) {
		case Form.QUESTION_MESSAGE:
			return MessageDialog.showMessage(owner, message, detail, messageType, JOptionPane.YES_NO_OPTION,
					this.resourcesFile, args);
		case Form.INFORMATION_MESSAGE:
			return MessageDialog.showMessage(owner, message, detail, messageType, this.resourcesFile, args);
		case Form.ERROR_MESSAGE:
			return MessageDialog.showMessage(owner, message, detail, messageType, this.resourcesFile, args);
		}
		return MessageDialog.showMessage(owner, message, detail, messageType, this.resourcesFile, args);
	}

	/**
	 * Shows a {@link MessageDialog} where the message type is established with
	 * <code>QUESTION_MESSAGE</code> in.
	 * @param message the text to be shown in the <code>MessageDialog</code>
	 * @return
	 */
	public boolean question(final String message) {
		final Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			final int res = MessageDialog.showMessage((Frame) w, message, null, Form.QUESTION_MESSAGE,
					JOptionPane.YES_NO_OPTION, this.resourcesFile);
			if (res == Form.YES) {
				return true;
			} else {
				return false;
			}
		} else if (w instanceof Dialog) {
			return this.question((Dialog) w, message);
		} else {
			final int res = MessageDialog.showMessage(this.parentFrame, message, null, Form.QUESTION_MESSAGE,
					JOptionPane.YES_NO_OPTION, this.resourcesFile);
			if (res == Form.YES) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Shows a {@link MessageDialog} where the message type is established with
	 * <code>QUESTION_MESSAGE</code> in.
	 * @param message the text to be shown in the <code>MessageDialog</code>
	 * @param owner the non-null Dialog from which the <code>MessageDialog</code> is displayed
	 * @return
	 */

	public boolean question(final Dialog owner, final String message) {

		final int res = MessageDialog.showMessage(owner, message, null, Form.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
				this.resourcesFile);
		if (res == Form.YES) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the form manager for this form
	 * @param formManager
	 */
	public void setFormManager(final IFormManager formManager) {
		this.formManager = formManager;

		if (this.formManager != null) {
			if (this.attachment && (this.getEntityName() != null) && !this.getEntityName().isEmpty()) {
				this.installAttachmentButton();
			}
			if (this.permissionButtonEnabled) {
				this.installPermissionButton();
			}

			if (this.templates) {
				this.installPrintingTemplatesButton();
			}

			if (this.databaseBundle) {
				this.installDatabaseBundleButton();
			}

			if (this.scriptEnabled) {
				this.installScriptButton();
			}

		}
	}

	/**
	 * Returns a {@link IFormManager} object that manages this form.
	 * @return a {@link IFormManager} object
	 */

	public IFormManager getFormManager() {
		return this.formManager;
	}

	/**
	 * Returns the string with the entity name is associated with the form
	 * @return a <code>String</code> with the entity name
	 */
	/*
	 */
	public String getEntityName() {
		return this.entityName;
	}

	/**
	 * Gets the parent <code>Frame</code> object reference. If this form is displayed in the main frame
	 * else returns <code>null</code>
	 * @return a <code>Frame</code> object
	 */

	public Frame getParentFrame() {
		return this.parentFrame;
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

	/**
	 * Enables all tables from this form
	 */
	public void enableTables() {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof Table) {
				((Table) comp).setEnabled(true);
			}
		}
	}

	/**
	 * Enables all buttons from this form.
	 */
	public void enableButtons() {
		final Enumeration enumKeys = Collections.enumeration(this.buttonList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			this.enableButton(oKey.toString());
		}
	}

	/**
	 * Disables all buttons from the form
	 */
	public void disableButtons() {
		final Enumeration enumKeys = Collections.enumeration(this.buttonList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oButton = this.buttonList.get(oKey);
			((FormComponent) oButton).setEnabled(false);
		}
	}

	/**
	 * Enables the button with this key
	 * @param key key of the button to be enabled
	 */
	public void enableButton(final String key) {
		if (this.buttonList.get(key) != null) {
			((FormComponent) this.buttonList.get(key)).setEnabled(true);
		}
	}

	/**
	 * Disables the button with this key
	 * @param key key of the button to be disabled
	 */

	public void disableButton(final String key) {
		if (this.buttonList.get(key) != null) {
			((FormComponent) this.buttonList.get(key)).setEnabled(false);
		}
	}

	/**
	 * Returns true if all fields have data. The tables too.
	 * @return true if all fields have data
	 */
	public boolean hasDataAllFields() {
		final boolean hasData = true;
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oComponent = this.dataComponentList.get(oKey);
			if (oComponent instanceof DataComponent) {
				if (((DataComponent) oComponent).isEmpty()) {
					return false;
				}
			}
		}
		return hasData;
	}

	/**
	 * Returns true if any required field is empty or false if all required fields have data.
	 */

	public boolean existEmptyRequiredDataField() {
		final boolean bEmptyRequiredFields = false;
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oComponent = this.dataComponentList.get(oKey);
			if (oComponent instanceof DataComponent) {
				if (((DataComponent) oComponent).isEmpty()) {
					if (((DataComponent) oComponent).isRequired()) {
						Form.logger.debug("Required field: {} without data",
								((DataComponent) oComponent).getAttribute());
						return true;
					}
				}
			}
		}
		return bEmptyRequiredFields;
	}

	/**
	 * Get the names (String) of all the empty required fields in the form
	 * @return
	 */
	public List getEmptyRequiredDataField() {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		final List emptyRequiredDataFields = new Vector();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oComponent = this.dataComponentList.get(oKey);
			if (oComponent instanceof DataComponent) {
				if (((DataComponent) oComponent).isEmpty()) {
					if (((DataComponent) oComponent).isRequired()) {
						Form.logger.debug("Required field: {} without data",
								((DataComponent) oComponent).getAttribute());
						emptyRequiredDataFields.add(this.getAttributeName(oKey));
					}
				}
			}
		}
		return emptyRequiredDataFields;
	}

	/**
	 * Returns a <code>Hashtable</code> with the data field values from the form that aren't empty. The
	 * keys of the <code>Hashtable</code> are the data field attributes. If the entry parameter is true,
	 * the table values will be returned too.
	 * @param tables true the table values are returned
	 * @return a <code>Hashtable</code> with the data field values
	 */

	public Map getDataFieldValues(final boolean tables) {
		final Map hResult = new Hashtable(30);
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			if (this.dataComponentList.get(oKey) instanceof Table) {
				if (tables) {
					hResult.put(oKey, ((DataComponent) this.dataComponentList.get(oKey)).getValue());
				} else {
					continue;
				}
			}
			if (this.dataComponentList.get(oKey) instanceof DataComponent) {
				if (((DataComponent) this.dataComponentList.get(oKey)).isEmpty()) {
					continue;
				}
			}
			final Object oValue = ((DataComponent) this.dataComponentList.get(oKey)).getValue();
			if (oValue != null) {
				hResult.put(oKey, oValue);
			}
			if (oKey instanceof ReferenceFieldAttribute) {
				// Put the attribute too
				hResult.put(((ReferenceFieldAttribute) oKey).getAttr(), oValue);
			}
		}
		return hResult;
	}

	/**
	 * Returns a <code>Hashtable</code> with the data field values from the form that aren't empty. The
	 * keys of the <code>Hashtable</code> are the data field attributes.
	 * <p>
	 * If the first entry parameter is true, the table values will be returned too. If the second entry
	 * parameter is true, the empty data field will be returned with a {@link NullValue} value
	 * @param tables true the table values are returned
	 * @param empty true the empty data field are returned with a {@link NullValue} value
	 * @return a <code>Hashtable</code> with all data field values
	 */
	public Map getDataFieldValues(final boolean tables, final boolean empty) {
		if (!empty) {
			return this.getDataFieldValues(tables);
		} else {
			// Return the values of the fields and NullValue for the empty
			// fields
			final Map hResult = new Hashtable();
			final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				if (this.dataComponentList.get(oKey) instanceof Table) {
					if (tables) {
						final Object oValue = ((DataComponent) this.dataComponentList.get(oKey)).getValue();
						if (oValue != null) {
							hResult.put(oKey, oValue);
						} else {
							hResult.put(oKey,
									new NullValue(((DataComponent) this.dataComponentList.get(oKey)).getSQLDataType()));
						}
					} else {
						continue;
					}
				}
				if (this.dataComponentList.get(oKey) instanceof DataComponent) {
					if (((DataComponent) this.dataComponentList.get(oKey)).isEmpty()) {
						hResult.put(oKey,
								new NullValue(((DataComponent) this.dataComponentList.get(oKey)).getSQLDataType()));
					} // continue;
					else {
						final Object oValue = ((DataComponent) this.dataComponentList.get(oKey)).getValue();
						hResult.put(oKey, oValue);
						if (oKey instanceof ReferenceFieldAttribute) {
							// Put the attribute too
							hResult.put(((ReferenceFieldAttribute) oKey).getAttr(), oValue);
						}
					}
				}
				if (this.dataComponentList.get(oKey) instanceof AttachmentAttribute) {
					hResult.put(oKey, (this.dataComponentList.get(oKey)));
					continue;
				}

			}
			return hResult;
		}
	}

	/**
	 * Adds the {@link AttachmentAttribute} component to the components list to query
	 */
	public void addAttachmentAttributeToComponents() {
		this.getDataComponentList().put(AttachmentAttribute.ATTR_STRING, this.getAttachmentAttribute());
	}

	/**
	 * Set the value to the form component with the attribute specified in <code>attribute</code>
	 * parameter.
	 * @param attribute
	 * @param value
	 */
	public void setDataFieldValue(final Object attribute, final Object value) {
		if (attribute instanceof String) {
			final DataComponent component = this.getDataFieldReference(attribute.toString());
			if (component != null) {
				component.setValue(value);
			}
		} else {
			final DataComponent component = (DataComponent) this.dataComponentList.get(attribute);
			if (component != null) {
				component.setValue(value);
			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List keyList = new Vector();
		keyList.add(this.deleteButtonText);
		keyList.add(this.helpKey);
		keyList.add(Form.headButtonKey);
		keyList.add(Form.tableWindowKey);

		if (this.originalTitle != null) {
			keyList.add(this.originalTitle);
		}
		if ((this.currentTitle != null) && ((this.originalTitle == null)
				|| ((this.originalTitle != null) && !this.currentTitle.equals(this.originalTitle)))) {
			keyList.add(this.currentTitle);
		}

		for (int i = 0; i < this.internationalizeList.size(); i++) {
			final List v2 = ((Internationalization) this.internationalizeList.get(i)).getTextsToTranslate();
			if (v2 != null) {
				keyList.addAll(v2);
			} else {
				Form.logger.debug("Component {} of the class: {} returns null texts to translate",
						this.internationalizeList.get(i), this.internationalizeList.get(i).getClass());
			}
		}

		if (this.statusBar != null) {
			keyList.addAll(this.statusBar.getTextsToTranslate());
		}
		if (this.formTitleLabel != null) {
			keyList.addAll(this.formTitleLabel.getTextsToTranslate());
		}

		keyList.addAll(MessageDialog.getTextsToTranslateS());
		final List vRes = new Vector();
		for (int i = 0; i < keyList.size(); i++) {
			if ((keyList.get(i) != null) && !vRes.contains(keyList.get(i))) {
				vRes.add(keyList.get(i));
			}
		}
		return vRes;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		if (this.resourcesFileName == null) {
			this.resourcesFile = resources;
			if (this.tableWindow != null) {
				try {
					if (resources != null) {
						this.tableWindow.setTitle(resources.getString(Form.tableWindowKey));
					}
				} catch (final Exception e) {
					Form.logger.error(null, e);
					this.tableWindow.setTitle(Form.tableWindowKey);
				}
			}
			try {
				if (resources != null) {
					this.clearDataFieldButton
					.setToolTipText(resources.getString(this.deleteButtonText) + " (ALT Supr)");
				}
			} catch (final Exception e) {
				Form.logger.error(null, e);
				this.clearDataFieldButton.setToolTipText(this.deleteButtonText + " (ALT Supr)");
			}
			// Update the resources for each component in the form
			for (int i = 0; i < this.internationalizeList.size(); i++) {
				try {
					((Internationalization) this.internationalizeList.get(i)).setResourceBundle(resources);
				} catch (final Exception e) {
					Form.logger.error(
							"Error setting resource bundle: " + this.internationalizeList.get(i).getClass().toString(),
							e);
				}
			}
			// Update the status bar too
			if (this.statusBar != null) {
				this.statusBar.setResourceBundle(resources);
			}
			if (this.formTitleLabel != null) {
				this.formTitleLabel.setResourceBundle(resources);
			}

		} else {
			final Locale lAux = this.locale;
			this.resourcesFile = ExtendedPropertiesBundle.getExtendedBundle(this.resourcesFileName, lAux);
			if (this.tableWindow != null) {
				try {
					if (this.resourcesFile != null) {
						this.tableWindow.setTitle(this.resourcesFile.getString(Form.tableWindowKey));
					}
				} catch (final Exception e) {
					Form.logger.error(null, e);
					this.tableWindow.setTitle(Form.tableWindowKey);
				}
			}
			try {
				if (this.resourcesFile != null) {
					this.clearDataFieldButton
					.setToolTipText(this.resourcesFile.getString(this.deleteButtonText) + " (ALT Supr)");
				}
			} catch (final Exception e) {
				Form.logger.error(null, e);
				this.clearDataFieldButton.setToolTipText(this.deleteButtonText + " (ALT Supr)");
			}
			// Update the resources file for each component in the form
			for (int i = 0; i < this.internationalizeList.size(); i++) {
				try {
					((Internationalization) this.internationalizeList.get(i)).setResourceBundle(this.resourcesFile);
				} catch (final Exception e) {
					Form.logger.error(
							"Error setting resource bundle: " + this.internationalizeList.get(i).getClass().toString(),
							e);
				}
			}
			// Update the status bar too
			if (this.statusBar != null) {
				this.statusBar.setResourceBundle(this.resourcesFile);
			}
			if (this.formTitleLabel != null) {
				this.formTitleLabel.setResourceBundle(this.resourcesFile);
			}
		}

		if ((this.dialog != null) && (this.modalDialogTitleKey != null)) {
			this.dialog.setTitle(ApplicationManager.getTranslation(this.modalDialogTitleKey, this.resourcesFile));
		}
		try {
			if (this.printButton != null) {
				this.printButton
				.setToolTipText(ApplicationManager.getTranslation("form.print_data", this.resourcesFile));
			} else if (this.printTemplateButton != null) {
				this.printTemplateButton.setResourceBundle(this.resourcesFile);
			}

			this.tableButton.updateTip();
			this.refreshButton.setToolTipText(
					ApplicationManager.getTranslation("form.refresh_form_data", this.resourcesFile) + " (F5)");
			this.previousButton.setToolTipText(
					ApplicationManager.getTranslation("form.go_previous_record", this.resourcesFile) + " (ALT <-) ");
			this.nextButton.setToolTipText(
					ApplicationManager.getTranslation("form.go_next_record", this.resourcesFile) + " (ALT ->) ");
			this.startButton
			.setToolTipText(ApplicationManager.getTranslation("form.go_first_record", this.resourcesFile));
			this.endButton.setToolTipText(ApplicationManager.getTranslation("form.go_last_record", this.resourcesFile));

			if (this.databaseBundleButton != null) {
				this.databaseBundleButton.setToolTipText(
						ApplicationManager.getTranslation(FormBundleButton.defaultFormBundleTip, this.resourcesFile));
			}
			if (this.scriptButton != null) {
				this.scriptButton
				.setToolTipText(ApplicationManager.getTranslation(Form.defaultScriptButtonTip, this.resourcesFile));
			}

		} catch (final Exception ex) {
			Form.logger.trace(null, ex);
		}
	}

	/**
	 * Returns the record current index from form cache data
	 * @return index of the current record
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}

	/**
	 * Returns the whole data list from the form
	 * @return a <code>Hashtable</code> with the whole form data.
	 */
	public Map getDataList() {
		if (this.totalDataList == null) {
			return this.totalDataList;
		}
		return ObjectTools.clone(this.totalDataList);
	}

	/**
	 * Returns the index of the record that has the entry parameter as key values
	 * @param keyValues the key values of the record
	 * @return the index of the record
	 */

	public int getIndex(final Map keyValues) {
		if (this.totalDataList == null) {
			return -1;
		}
		int[] index = new int[0];

		final Object initial = this.keys.get(0);

		if (initial instanceof String) {
			if (keyValues.containsKey(initial) && this.totalDataList.containsKey(initial)) {
				final Object valueCheck = keyValues.get(initial);
				final Object allValues = this.totalDataList.get(initial);
				if (allValues instanceof List) {
					final List values = (List) allValues;
					if (values.size() == 0) {
						return -1;
					}
					for (int i = 0; i < values.size(); i++) {
						final Object check = values.get(i);
						if (check.equals(valueCheck)) {
							final int[] newIndex = new int[index.length + 1];
							System.arraycopy(index, 0, newIndex, 0, index.length);
							newIndex[index.length] = i;
							index = newIndex;
						}
					}
				} else if (valueCheck.equals(allValues)) {
					return 0;
				} else {
					return -1;
				}

			} else {
				return -1;
			}
		}

		if (index.length == 0) {
			return -1;
		}
		if (index.length == 1) {
			return index[0];
		}

		for (int i = 1, j = this.keys.size(); i < j; i++) {
			int[] temp = new int[0];
			final Object o = this.keys.get(i);
			if (o instanceof String) {
				if (keyValues.containsKey(o) && this.totalDataList.containsKey(o)) {
					final Object valueCheck = keyValues.get(o);
					final Object allValues = this.totalDataList.get(o);
					if (allValues instanceof List) {
						final List values = (List) allValues;
						if (values.size() == 0) {
							return -1;
						}
						for (int k = 0; k < index.length; k++) {
							final Object check = values.get(index[k]);
							if (check.equals(valueCheck)) {
								final int[] newIndex = new int[temp.length + 1];
								System.arraycopy(temp, 0, newIndex, 0, temp.length);
								newIndex[temp.length] = index[k];
								temp = newIndex;
							}
						}
					} else {
						return -1;
					}
				}
			}
			if (temp.length == 0) {
				return -1;
			}
			if (temp.length == 1) {
				return temp[0];
			}
			index = temp;
		}

		return -1;
	}

	/**
	 * Returns whether the data field which attribute is the entry parameter hasn't data.
	 * @param attribute
	 * @return true if the data field is empty, otherwise false
	 */
	public boolean isEmpty(final String attribute) {
		final DataComponent dataField = this.getDataFieldReference(attribute);
		if (dataField == null) {
			return true;
		} else {
			return dataField.isEmpty();
		}
	}

	public void setInteractionManager(final InteractionManager formInteractionManager) {
		this.interactionManager = formInteractionManager;
		Form.logger.debug("Form with entity: {}. Established the interaction manager: {}", this.entityName,
				this.interactionManager.getClass().toString());
	}

	/**
	 * Returns the {@link InteractionManager} object that this form has registered
	 * @return a {@link InteractionManager} registered in this form
	 */
	public InteractionManager getInteractionManager() {
		return this.interactionManager;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		this.setLocale(l);
		// Updates the locale for each component in the form
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof Internationalization) {
				((Internationalization) this.componentList.get(i)).setComponentLocale(l);
			}
		}
	}

	/**
	 * Returns the data field text which has the entry parameter as attribute. If the data field doesn't
	 * be contained in the form, a empty string will be returned
	 * @param attribute
	 * @return a <code>String</code> with the text of the form data field
	 */
	public String getDataFieldText(final String attribute) {
		final DataComponent dataField = this.getDataFieldReference(attribute);
		if (dataField != null) {
			if (dataField instanceof TextFieldDataField) {
				return ((TextFieldDataField) dataField).getText();
			}
		}
		return "";
	}

	/**
	 * Return the text contained in every form data field. If the data field text is null, the field
	 * value will be returned
	 * @return a <code>Hashtable</code> with the text of all data fields.
	 */
	public Map getDataFieldText() {
		final Map tTexts = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oField = this.dataComponentList.get(oKey);
			if (oField != null) {
				if (oField instanceof DataField) {
					Object oValue = ((DataField) oField).getText();
					if (oValue == null) {
						oValue = ((DataField) oField).getValue();
					}
					if (oValue != null) {
						// If the attribute is ReferenceFieldAttribute then put
						// the
						// string attribute name as key
						if (oKey instanceof ReferenceFieldAttribute) {
							tTexts.put(((ReferenceFieldAttribute) oKey).getAttr(), oValue);
						} else {
							tTexts.put(oKey, oValue);
						}
					}
				}
			}
		}
		return tTexts;
	}

	/**
	 * Returns a text list from the data fields that are requested in the entry parameter
	 * @param attributeList attribute list of the requested data fields to obtain their text value
	 * @return a <code>Hashtable</code> with the values of all data fields. The keys are the attributes
	 *         of the data fields and the values are the data field text values
	 */
	public Map getDataFieldText(final List attributeList) {
		final Map hTexts = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			String keyString;
			if (oKey instanceof String) {
				keyString = (String) oKey;
			} else if (oKey instanceof ReferenceFieldAttribute) {
				keyString = ((ReferenceFieldAttribute) oKey).getAttr();
			} else {
				continue;
			}

			if (!attributeList.contains(keyString)) {
				continue;
			}

			final Object oField = this.dataComponentList.get(oKey);
			if (oField != null) {
				if (oField instanceof DataField) {
					Object oValue = ((DataField) oField).getText();
					if (oValue == null) {
						oValue = ((DataField) oField).getValue();
					}
					if (oValue != null) {
						// If attribute is ReferenceFieldAttributre put the name
						// of
						// the attribute as key
						if (oKey instanceof ReferenceFieldAttribute) {
							hTexts.put(((ReferenceFieldAttribute) oKey).getAttr(), oValue);
						} else {
							hTexts.put(oKey, oValue);
						}
					}
				}
			}
		}
		return hTexts;
	}

	public void setCurrentIndex(final int index) {
		this.currentIndex = index;
	}

	/**
	 * Set the text of the status bar. This text will show for the time specified in
	 * <code>timeMillis</code> parameter. If this time is 0 then the text will show until the next
	 * change. <br>
	 * If the text is in the bundle file this will be translated
	 * @param text
	 * @param timeMillis
	 */
	public void setStatusBarText(final String text, final int timeMillis) {
		this.setStatusBarText(text, timeMillis, true);
	}

	public void setStatusBarText(final String text, final int timeMillis, final boolean translate) {
		// If the status bar has not parent then is not visible in the form
		if ((this.statusBar != null) && (this.statusBar.getParent() != null)) {
			if (translate) {
				try {
					if (this.resourcesFile != null) {
						this.statusBar.setText(this.resourcesFile.getString(text), timeMillis);
					}
				} catch (final Exception e) {
					Form.logger.error(null, e);
					this.statusBar.setText(text, timeMillis);
				}
			} else {
				this.statusBar.setText(text, timeMillis);
			}
		}
	}

	/**
	 * Returns a {@link DetailForm} if the form is contained in one of them. This happens if this form
	 * is a detail form from a {@Table}. In other case this method returns <code>null<code>
	 * @return a {@link DetailForm} where the form is contained in
	 */
	public IDetailForm getDetailComponent() {
		return this.detailForm;
	}

	/**
	 * Sets the {@link DetailForm} for this form
	 * @param detailForm
	 */
	public void setDetailForm(final IDetailForm detailForm) {
		this.detailForm = detailForm;
	}

	@Override
	public void free() {
		if (this.getInteractionManager() != null) {
			this.getInteractionManager().free();
		}
		this.interactionManager = null;
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof ApplicationPreferencesListener) {
				final ApplicationPreferences prefs = this.formManager.getApplication().getPreferences();
				if ((prefs != null) && (prefs instanceof AbstractApplicationPreferences)) {
					((AbstractApplicationPreferences) prefs)
					.removeApplicationPreferencesListener((ApplicationPreferencesListener) comp);
				}
			}
		}

		this.parentFrame = null;
		this.dataNavigationListeners = null;
		this.dataRecordListeners = null;
		this.formBuilder = null;
		this.formManager = null;
		this.detailForm = null;
		FreeableUtils.clearCollection(this.componentList);
		this.componentList = null;
		FreeableUtils.clearMap(this.buttonList);
		this.buttonList = null;
		FreeableUtils.clearMap(this.dataComponentList);
		this.dataComponentList = null;
		FreeableUtils.clearMap(this.totalDataList);
		this.totalDataList = null;

		FreeableUtils.freeListeners(this.startButton);
		FreeableUtils.freeListeners(this.previousButton);
		FreeableUtils.freeListeners(this.endButton);
		FreeableUtils.freeListeners(this.startButton);
		FreeableUtils.freeListeners(this.refreshButton);
		FreeableUtils.freeListeners(this.helpButton);
		FreeableUtils.freeListeners(this.helpIdButton);
		FreeableUtils.freeListeners(this.tableButton);
		FreeableUtils.freeListeners(this.clearDataFieldButton);
		FreeableUtils.freeListeners(this.reloadButton);
		FreeableUtils.freeListeners(this.permissionButton);
		FreeableUtils.freeListeners(this.databaseBundleButton);
		FreeableUtils.freeListeners(this.scriptButton);
		FreeableUtils.freeListeners(this.printButton);
		FreeableUtils.freeListeners(this.printTemplateButton);
		FreeableUtils.freeListeners(this.attachmentButton);

		FreeableUtils.freeComponent(this.getComponents());
		FreeableUtils.freeComponent(this.tableWindow);

		this.nextIcon = null;
		this.previousIcon = null;
		this.endIcon = null;
		this.startIcon = null;
		this.orderIcon = null;
		this.printIcon = null;
		this.refreshIcon = null;
		this.attachmentIcon = null;
		this.databaseBundleIcon = null;
		this.scriptIcon = null;
		this.nextButton = null;
		this.previousButton = null;
		this.endButton = null;
		this.startButton = null;
		this.refreshButton = null;
		this.helpButton = null;
		this.helpIdButton = null;
		this.tableButton = null;
		this.clearDataFieldButton = null;
		this.reloadButton = null;
		this.resultCountLabel = null;
		this.buttonPanel = null;
		this.bodyPanel = null;
		this.permissionButton = null;
		this.databaseBundleButton = null;
		this.scriptButton = null;
		this.printButton = null;
		this.newButtonPanel = null;
		this.scrollPanel = null;
		this.associatedNode = null;
		this.associatedPath = null;
		this.printTemplateButton = null;
		this.attachmentButton = null;
		this.printListener = null;
		this.previousButtonListener = null;
		this.nextButtonListener = null;
		this.startButtonListener = null;
		this.endButtonListener = null;
		this.refreshButtonListener = null;
		this.tableButtonListener = null;

		if (this.dialog != null) {
			this.dialog.dispose();
		}
		if (this.messageWindow != null) {
			this.messageWindow.dispose();
		}
		this.dialog = null;
		this.messageWindow = null;
		this.tableWindow = null;
		this.parentFrame = null;
		this.detailForm = null;

		this.listenerHighlightButtons = null;
		this.locale = null;
		this.dataNavigationListeners = null;
		this.dataRecordListeners = null;
		this.visiblePermission = null;
		this.enabledPermission = null;
		this.tableViewColumns = null;
		this.additionalTableViewColumns = null;
		this.tabPane = null;
		this.helpPopup = null;
		this.templateList = null;
		this.dynamicFormManager = null;
		this.checkModifiedFieldList = null;

		this.statusBar = null;
		this.formTitleLabel = null;
		this.dataComponentList = null;
		this.groupDataComponentList = null;
		this.formManager = null;
		this.buttonList = null;
		this.internationalizeList = null;
		this.componentList = null;
		this.accessFormComponentList = null;
		this.currencyComponentsList = null;
		this.formBuilder = null;
		this.buttonPanelObjects = null;
		this.permissionButtonApplicationPermission = null;
		this.attachmentEntity = null;
		this.ePrintingTemplate = null;
		this.parentKeyValue = null;
		this.setValuesOrder = null;
		this.resourcesFile = null;
		this.table = null;
		this.keys = null;
		this.parentKeys = null;
		this.getActionMap().clear();

		this.setLayout(null);
		Form.logger.debug("Finalized 'free' method.");
	}

	/**
	 * Gets the resource bundle that contain locale-specific objects.
	 * @return a <code>ResourceBundle</code>
	 */

	public ResourceBundle getResourceBundle() {
		return this.resourcesFile;
	}

	public String getResourceFileName() {
		return this.resourcesFileName;
	}

	/**
	 * Method to set the current date in a DateDataField.<br>
	 * This method checks if a DateDataField with the specified name exists.<br>
	 * @param attribute
	 * @return Numeric code with the result of the operation
	 *         <ul>
	 *         <li>Form.FIELD_NOT_FOUND if the form does not contain a field with this attribute
	 *         <li>Form.CORRECT
	 *         <li>Form.NOT_DATEDATAFIELD if the field is not a DateDataField
	 *         </ul>
	 */
	public int setCurrentDate(final String attribute) {
		final DataComponent comp = (DataComponent) this.dataComponentList.get(attribute);
		if (comp == null) {
			return Form.FIELD_NOT_FOUND;
		} else {
			if (comp instanceof DateDataField) {
				((DateDataField) comp).setValue(new java.util.Date());
				return Form.CORRECT;
			} else {
				return Form.NOT_DATEDATAFIELD;
			}
		}
	}

	public void setCheckModifiedData(final boolean check) {
		this.checkModifiedData = check;
	}

	/**
	 * Checks if any fields have modified data. This method checks every data field into the form has
	 * modified data calling the {@see DataComponent#isModified} method.
	 */

	public boolean checkModifiedData() {
		if (!this.checkModifiedData) {
			return false;
		}
		this.isCreatedCheckModifiedFieldList();
		// Check with the data list values

		for (int i = 0; i < this.checkModifiedFieldList.size(); i++) {
			final Object oKey = this.checkModifiedFieldList.get(i);
			final DataComponent comp = (DataComponent) this.dataComponentList.get(oKey);
			if ((comp != null) && comp.isModified()) {
				Form.logger.debug("Modified data detected in component: {}", oKey.toString());
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes data from form data component with this attribute
	 * @param attribute data component attribute to be deleted
	 */

	public void deleteDataField(final String attribute) {
		final DataComponent c = this.getDataFieldReference(attribute);
		if (c != null) {
			c.deleteData();
		}
	}

	/**
	 * @deprecated */
	@Deprecated
	public void setTabVisible(final int index) {
		if (index < this.tabPane.getTabCount()) {
			this.tabPane.setSelectedIndex(index);
		}
	}

	/**
	 * @deprecated */
	@Deprecated
	public void setTabEnabled(final int index, final boolean enabled) {
		if (index < this.tabPane.getTabCount()) {
			this.tabPane.setEnabledAt(index, enabled);
		}
	}

	/**
	 * @deprecated */
	@Deprecated
	public void setTabEnabled(final String title, final boolean enabled) {
		for (int i = 0; i < this.tabPane.getTabCount(); i++) {
			if (this.tabPane.getComponentAt(i) instanceof Tab) {
				if (((Tab) this.tabPane.getComponentAt(i)).getTitleKey().equals(title)) {
					this.tabPane.setEnabledAt(i, enabled);
					return;
				}
			}
		}
	}

	/**
	 * @deprecated */
	@Deprecated
	public void setTabVisible(final String title) {
		for (int i = 0; i < this.tabPane.getTabCount(); i++) {
			if (this.tabPane.getComponentAt(i) instanceof Tab) {
				if (((Tab) this.tabPane.getComponentAt(i)).getTitleKey().equals(title)) {
					if (this.tabPane.isEnabledAt(i)) {
						this.tabPane.setSelectedIndex(i);
					}
					return;
				}
			}
		}
	}

	@Override
	public void showCurrencyValue(final String currencySymbol) {
		for (int i = 0; i < this.currencyComponentsList.size(); i++) {
			((SelectCurrencyValues) this.currencyComponentsList.get(i)).showCurrencyValue(currencySymbol);
		}
	}

	@Override
	public Font showAvaliableFonts(final boolean supportingEuro) {
		final FontSelector s = new FontSelector(this.parentFrame);
		return s.showAvaliableFonts(supportingEuro);
	}

	/**
	 * Check if the <code>Font</code> support the Euro character
	 * @param font
	 * @return true - if Euro characted is supported
	 */

	public static boolean supportsEuroSymbol(final Font font) {
		if (font.canDisplay('\u20AC')) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void useFont(final Font font) {
		this.setFont(font);
	}

	/**
	 * @deprecated */
	@Deprecated
	public void showMe(final Component c) {
		for (int i = 0; i < this.tabPane.getTabCount(); i++) {
			Form.logger.debug("Search component in Tab: {}",
					((Tab) this.tabPane.getComponentAt(i)).getConstraints(this.tabPane.getLayout()));
			if (this.findComponentInTab((Container) this.tabPane.getComponentAt(i), c)) {
				this.tabPane.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * Checks whether this component is contained in this container. This method not only searches the
	 * Container's immediate children;
	 * @param container the container in which the components are found
	 * @param component the component to find
	 * @return true if this the component is in this container
	 */

	protected boolean findComponentInTab(final Container container, final Component component) {
		final int componentCount = container.getComponentCount();
		boolean bFound = false;
		for (int i = 0; i < componentCount; i++) {
			final Component comp = container.getComponent(i);
			if (comp.equals(component)) {
				Form.logger.info("Component {} has been found.", comp);
				return true;
			}
			if (comp instanceof Container) {
				bFound = this.findComponentInTab((Container) comp, component);
			}
		}
		return bFound;
	}

	/**
	 * Set the node associated with the form. The form manager calls this method when a new node
	 * associated with the form is selected.
	 * @param node
	 */
	public void setAssociatedNode(final OTreeNode node) {
		this.associatedNode = node;
	}

	/**
	 * Set the tree path associated with the form
	 * @param path
	 */
	public void setLinkedTreePath(final TreePath path) {
		this.associatedPath = path;
	}

	/**
	 * Returns the tree node associated a this form.
	 * @return a tree node associates a this form or null if the tree don't exist
	 */

	public OTreeNode getAssociatedNode() {
		return this.associatedNode;
	}

	/**
	 * Returns the tree path associated a this form
	 * @return a <code>TreePath<code> associates a this form or null if the tree don't exist
	 */

	public TreePath getAssociatedTreePath() {
		return this.associatedPath;
	}

	/**
	 * Returns the list of data component references that are contained in the form.
	 * @return a <code>Vector</code> with a data component list
	 */

	public List getDataComponents() {
		final List v = new Vector();
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			Object oKey = enumKeys.nextElement();
			if (oKey instanceof Map) {
				final Enumeration enumKeysHashAttribute = Collections.enumeration(((Map) oKey).keySet());
				oKey = enumKeysHashAttribute.nextElement();
			}
			final Object o = this.dataComponentList.get(oKey);
			if (o != null) {
				v.add(this.dataComponentList.get(oKey));
			}
		}
		return v;
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * The dialog parent is the frame that has been established in the form manager for this form. A
	 * <code>JDialog</code> reference is stored in the form
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 * @deprecated Uses <code>putInModalDialog(Component parentComponent)</code>
	 */

	@Deprecated
	public JDialog putInModalDialog() {
		if (this.getParent().getParent() == null) {
			if (this.dialog == null) {
				this.dialog = new EJDialog(this.parentFrame, true) {

					@Override
					protected void processWindowEvent(final WindowEvent e) {
						super.processWindowEvent(e);
						if (e.getID() == WindowEvent.WINDOW_OPENED) {
							Form.this.requestDefaultFocus();
						}
					}
				};
				((EJDialog) this.dialog).setSizePositionPreference(this.getSizeDialogPreferenceKey());
				this.dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				this.dialog.getContentPane().add(this);
				this.dialog.pack();
				final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				this.dialog.setLocation((screenSize.width / 2) - (this.dialog.getSize().width / 2),
						(screenSize.height / 2) - (this.dialog.getSize().height / 2));
				return this.dialog;
			} else {
				return this.dialog;
			}
		} else {
			if (this.dialog != null) {
				return this.dialog;
			} else {
				return null;
			}
		}
	}

	/**
	 * Returns the string used as key to store the dialog size in the preferences
	 * @return a preference key
	 */
	public String getSizeDialogPreferenceKey() {
		final Form f = this;
		return BasicApplicationPreferences.DETAIL_DIALOG_SIZE_POSITION + "_" + f.getArchiveName();
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * A <code>JDialog</code> reference is stored in the form
	 * @param owner the parent <code>Frame</code> for the dialog
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 */

	public JDialog putInModalDialog(final Frame owner) {
		if (this.getParent().getParent() == null) {
			if (this.dialog == null) {
				this.dialog = new EJDialog(owner, true) {

					@Override
					protected void processWindowEvent(final WindowEvent e) {
						super.processWindowEvent(e);
						if (e.getID() == WindowEvent.WINDOW_OPENED) {
							Form.this.requestDefaultFocus();
						}
					}
				};
				((EJDialog) this.dialog).setSizePositionPreference(this.getSizeDialogPreferenceKey());
				this.dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				this.dialog.getContentPane().add(this);
				this.dialog.pack();
				final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				this.dialog.setLocation((screenSize.width / 2) - (this.dialog.getSize().width / 2),
						(screenSize.height / 2) - (this.dialog.getSize().height / 2));
				return this.dialog;
			} else {
				return this.dialog;
			}
		} else {
			if (this.dialog != null) {
				return this.dialog;
			} else {
				return null;
			}
		}
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * A <code>JDialog</code> reference is stored in the form
	 * @param window the parent <code>Window</code> for the dialog
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 */

	public JDialog putInModalDialog(final Window window) {
		if (window instanceof Frame) {
			return this.putInModalDialog((Frame) window);
		} else if (window instanceof Dialog) {
			return this.putInModalDialog((Dialog) window);
		} else {
			return this.putInModalDialog((Frame) null);
		}
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * A <code>JDialog</code> reference is stored in the form
	 * @param parentComponent the parent <code>Component</code> for the dialog
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 */

	public JDialog putInModalDialog(final Component parentComponent) {
		final Window w = SwingUtilities.getWindowAncestor(parentComponent);
		if (w instanceof Frame) {
			return this.putInModalDialog((Frame) w);
		} else if (w instanceof Dialog) {
			return this.putInModalDialog((Dialog) w);
		} else {
			return this.putInModalDialog((Frame) null);
		}
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * A <code>JDialog</code> reference is stored in the form
	 * @param owner the parent <code>Dialog</code> for the dialog
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 */

	public JDialog putInModalDialog(final Dialog owner) {
		if (this.getParent().getParent() == null) {
			if (this.dialog == null) {
				this.dialog = new EJDialog(owner, true) {

					@Override
					protected void processWindowEvent(final WindowEvent e) {
						super.processWindowEvent(e);
						if (e.getID() == WindowEvent.WINDOW_OPENED) {
							Form.this.requestDefaultFocus();
						}
					}

				};
				((EJDialog) this.dialog).setSizePositionPreference(this.getSizeDialogPreferenceKey());
				this.dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				this.dialog.getContentPane().add(this);
				this.dialog.pack();
				final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				this.dialog.setLocation((screenSize.width / 2) - (this.dialog.getSize().width / 2),
						(screenSize.height / 2) - (this.dialog.getSize().height / 2));
				return this.dialog;
			} else {
				return this.dialog;
			}
		} else {
			if (this.dialog != null) {
				return this.dialog;
			} else {
				return null;
			}
		}
	}

	protected String modalDialogTitleKey = null;

	/**
	 * Variable to store the {@link AttachmentAttribute} to check it when query a register
	 */
	protected AttachmentAttribute attachmentAttribute = null;

	/**
	 * Return the {@link AttachmentAttribute}
	 * @return The current {@link AttachmentAttribute}
	 */
	public AttachmentAttribute getAttachmentAttribute() {
		return this.attachmentAttribute;
	}

	/**
	 * Set an {@link AttachmentAttribute}
	 * @param attachmentAttribute The {@link AttachmentAttribute} to set
	 */
	public void setAttachmentAttribute(final AttachmentAttribute attachmentAttribute) {
		this.attachmentAttribute = attachmentAttribute;
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * The dialog parent is the frame that has been established in the form manager for this form. A
	 * <code>JDialog</code> reference is stored in the form
	 * @param title a <code>String</code> with the dialog title.
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 * @deprecated uses {@link #putInModalDialog(String, Component)}
	 */

	@Deprecated
	public JDialog putInModalDialog(final String title) {
		final JDialog d = this.putInModalDialog();
		if (d != null) {
			d.setTitle(ApplicationManager.getTranslation(title, this.resourcesFile));
		}
		this.modalDialogTitleKey = title;
		return d;
	}

	/**
	 * Puts the form in a modal <code>JDialog</code> if it doesn't contain in any window.
	 * <p>
	 * A <code>JDialog</code> reference is stored in the form
	 * @param title a <code>String</code> with the dialog title.
	 * @param parent the parent <code>Component</code> for the dialog
	 * @return a <code>JDialog</code> where the form is contained in or null if the form is contained in
	 *         a window.
	 * @see {@link #getJDialog}
	 */

	public JDialog putInModalDialog(final String title, final Component parent) {
		final JDialog d = this.putInModalDialog(parent);
		if (d != null) {
			d.setTitle(ApplicationManager.getTranslation(title, this.resourcesFile));
		}
		this.modalDialogTitleKey = title;
		return d;
	}

	/**
	 * Returns the <code>JDialog</code> object reference that has been created in the call to method
	 * {@link #putInModalDialog()}.
	 * @return a <code>JDialog</code> object reference
	 */

	public JDialog getJDialog() {
		return this.dialog;
	}

	/**
	 * Enables/Disables the advanced query mode for the component which has the entry parameter as
	 * attribute.
	 * @param attr The component attribute to be change the advanced query mode
	 * @param enable true to make the advancedQuery mode enable ; false to make it disable
	 * @return true if the component has been found and change the advanced query mode
	 */

	public boolean setAdvancedQueryMode(final String attr, final boolean enable) {
		final Object field = this.getDataFieldReference(attr);
		if ((field != null) && (field instanceof AdvancedDataComponent)) {
			((AdvancedDataComponent) field).setAdvancedQueryMode(enable);
			return true;
		}
		return false;
	}

	/**
	 * Enables/Disables the advanced query mode for all form component.
	 * @param enable true to make the advancedQuery mode enable ; false to make it disable
	 * @return true if the component has been found and change the advanced query mode
	 */

	public void setAdvancedQueryModeAll(final boolean enable) {
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oField = this.dataComponentList.get(oKey);
			if (oField instanceof AdvancedDataComponent) {
				((AdvancedDataComponent) oField).setAdvancedQueryMode(enable);
			}
		}
	}

	/**
	 * Creates the component used to show the table view. This component is used to show all data
	 * returned by the query performed from the form.
	 */
	protected void createTableViewTable() {
		final Map hTableParameter = DefaultXMLParametersManager.getParameters(Table.class.getName());
		List vAttributes = null;
		if (this.tableViewColumns == null) {
			final List aux = this.getDataFieldAttributeList();
			final List authorizedColumns = ObjectTools.clone(this.keys);
			for (int i = 0; i < aux.size(); i++) {
				final Object oAttribute = aux.get(i);
				if (oAttribute instanceof String) {
					if (this.checkAccessPermission(oAttribute.toString())) {
						authorizedColumns.add(oAttribute);
						break;
					}
				}
			}
			vAttributes = authorizedColumns;
		} else {
			final List vAuthorizedColumns = new Vector();
			for (int i = 0; i < this.tableViewColumns.size(); i++) {
				final Object oAttribute = this.tableViewColumns.get(i);
				if (oAttribute != null) {
					if (this.checkAccessPermission(oAttribute.toString())) {
						vAuthorizedColumns.add(oAttribute);
					}
				}
			}
			vAttributes = vAuthorizedColumns;
		}

		// Additional columns
		if (this.additionalTableViewColumns != null) {
			for (int i = 0; i < this.additionalTableViewColumns.size(); i++) {
				final Object oAttribute = this.additionalTableViewColumns.get(i);
				if ((oAttribute != null) && !vAttributes.contains(oAttribute)) {
					if (this.checkAccessPermission(oAttribute.toString())) {
						vAttributes.add(oAttribute);
					}
				}
			}
		}

		final StringBuilder sbColumns = new StringBuilder();
		for (int i = 0; i < vAttributes.size(); i++) {
			final Object oAttribute = vAttributes.get(i);
			if (oAttribute instanceof String) {
				sbColumns.append((String) oAttribute);
				if (i < (vAttributes.size() - 1)) {
					sbColumns.append(";");
				}
			}
		}
		hTableParameter.put("cols", sbColumns.toString());
		hTableParameter.put(Form.ENTITY, this.entityName);
		hTableParameter.put("rows", "20");
		if (this.scrollHTableView) {
			hTableParameter.put("scrollh", "yes");
		}
		if (!this.keys.isEmpty()) {
			hTableParameter.put("key", this.keys.get(0));
			if (this.keys.size() > 1) {
				final StringBuilder sbKeys = new StringBuilder();
				for (int i = 1; i < this.keys.size(); i++) {
					sbKeys.append(this.keys.get(i));
					if (i < (this.keys.size() - 1)) {
						sbKeys.append(";");
					}
				}
				hTableParameter.put(Form.KEYS, sbKeys.toString());
			}
		}
		try {
			this.table = new Table(hTableParameter);
			this.table.setParentForm(this);
			this.table.setParentFrame(this.parentFrame);
			this.table.setControlsVisible(false);
			this.table.setRowNumberColumnVisible(false);
			this.table.setMinRowHeight(this.tableViewMinRowHeight);
			this.table.setReferenceLocator(this.formManager.getReferenceLocator());
		} catch (final Exception e) {
			Form.logger.trace(null, e);
			// Table view can not be showed
			this.message("form.error_table_view_can_not_be_showed", Form.ERROR_MESSAGE, e);
			return;
		}

		this.table.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (Form.this.table.getSelectedRow() >= 0) {
						try {
							Form.this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							// If sort is enabled then the selected index in the
							// current view can be different to the index in the
							// data
							// list
							final int selectedRow = Form.this.table.getSelectedRow();
							final int dataIndex = Form.this.table.convertRowIndexToModel(selectedRow);
							Form.this.updateDataFields(dataIndex);
							try {
								final Map hFormData = Form.this.getDataList();
								final Map hTableData = (Map) Form.this.table.getValue();
								final Iterator enumKeys = hFormData.keySet().iterator();
								while (enumKeys.hasNext()) {
									final Object oKey = enumKeys.next();
									hTableData.put(oKey, ObjectTools.clone((List) hFormData.get(oKey)));
								}
								Form.this.table.getJTable().repaint();
							} catch (final Exception ex2) {
								Form.logger.error(null, ex2);
							}
						} catch (final Exception ex) {
							Form.logger.error(null, ex);
						} finally {
							Form.this.table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			}
		});
		this.table.getJTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					Form.this.tableWindow.setVisible(false);
				}
			}
		});
		final Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog) {
			this.tableWindow = new EJDialog((Dialog) w, Form.tableWindowKey, true);
		} else if (w instanceof Frame) {
			this.tableWindow = new EJDialog((Frame) w, Form.tableWindowKey, true);
		} else {
			this.tableWindow = new EJDialog(this.parentFrame, Form.tableWindowKey, true);
		}
		this.tableWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.tableWindow.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(final WindowEvent e) {
				Form.this.table.deleteData();
			}
		});
		this.tableWindow.getContentPane().add(this.table);
		if (this.tableWindow != null) {
			// Add the components in the result window to the
			// internationalizedList
			// to set the bundle to them
			this.addComponentsToInternationalizeList(this.tableWindow, true);
			if (this.resourcesFile != null) {
				this.tableWindow.setTitle(ApplicationManager.getTranslation(Form.tableWindowKey, this.resourcesFile));
			}
		}
		if (this.additionalTableViewColumns != null) {
			this.resetTableViewColumns();
			this.addColumnsToTableView(this.additionalTableViewColumns);
		}
	}

	/**
	 * Adds the component to the internationalizeList if the component implements
	 * {@link Internationalization}. If the <code>component</code> is a container then adds all the
	 * components inside it that implements {@link Internationalization} too
	 * @param component Component to add to the list
	 * @param setBundle When true set the bundle to the component
	 */
	public void addComponentsToInternationalizeList(final Component component, final boolean setBundle) {
		if (component instanceof Internationalization) {
			this.internationalizeList.add(component);
			if (setBundle && (this.resourcesFile != null)) {
				((Internationalization) component).setResourceBundle(this.resourcesFile);
				((Internationalization) component).setComponentLocale(this.locale);
			}
		}
		if (component instanceof Container) {
			final Component[] components = ((Container) component).getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i] instanceof Container) {
					this.addComponentsToInternationalizeList(components[i], setBundle);
				} else if (components[i] instanceof Internationalization) {
					this.internationalizeList.add(components[i]);
					if (setBundle && (this.resourcesFile != null)) {
						((Internationalization) components[i]).setResourceBundle(this.resourcesFile);
						((Internationalization) components[i]).setComponentLocale(this.locale);
					}
				}
			}
		}
	}

	public void removeComponentsToInternationalizeList(final Component component) {
		if ((component instanceof Internationalization) && this.internationalizeList.contains(component)) {
			this.internationalizeList.remove(component);
		}
	}

	/**
	 * Shows the table view. This component is used to show all data returned by the query performed
	 * from the form.
	 */

	public void showTableView() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (this.vectorSize > 1) {
				// If there are more than one result:
				// If the table is null then create it.
				if (this.table == null) {
					this.createTableViewTable();
				}
				final Map hFormData = this.getDataList();
				final EntityResult hTableData = new EntityResultMapImpl();
				final Iterator enumKeys = hFormData.keySet().iterator();
				while (enumKeys.hasNext()) {
					final Object oKey = enumKeys.next();
					List vValue = (List) hFormData.get(oKey);
					if (vValue != null) {
						vValue = ObjectTools.clone(vValue);
					}
					hTableData.put(oKey, vValue);
				}
				if (hFormData instanceof EntityResult) {
					hTableData.setColumnSQLTypes(((EntityResult) hFormData).getColumnSQLTypes());
				}
				this.table.setValue(hTableData);
				this.table.packTable();
				this.tableWindow.pack();
				ApplicationManager.center(this.tableWindow);
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				this.tableWindow.setVisible(true);
			} else {
				this.message("form.there_are_only_data_from_one_register", Form.INFORMATION_MESSAGE, (String) null);
			}
		} catch (final Exception e) {
			Form.logger.error(null, e);
		} finally {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Returns the table used to show the query result. The columns that will be shown in this table are
	 * established by the 'columns' parameter in the Form xml description
	 *
	 * @see #init
	 * @return a <code>Table</code> where the result query are stored in
	 */
	public Table getTableFromTableView() {
		if (this.table == null) {
			this.createTableViewTable();
		}
		return this.table;
	}

	/**
	 * Gets the layout manager for the form body
	 */
	@Override
	public LayoutManager getLayout() {
		return this.bodyPanel.getLayout();
	}

	@Override
	public void initPermissions() {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object c = this.componentList.get(i);
			if (c instanceof SecureElement) {
				((SecureElement) c).initPermissions();
			}
		}
	}

	/**
	 * Sets the file name which is stored the form xml description
	 * @param archiveName the file name
	 */

	public void setFileName(final String archiveName) {
		this.fileName = archiveName;
		if (this.reloadButton != null) {
			this.reloadButton
			.setToolTipText(ApplicationManager.getTranslation(Form.M_RELOAD_FORM) + ": " + this.getArchiveName());
		}
	}

	/**
	 * Returns the form archive name
	 * @return a String with the form archive name
	 */
	public String getArchiveName() {
		return this.fileName;
	}

	/**
	 * Checks if the attribute has any restriction by permissions.
	 * @param attribute the <code>String<code> of the attribute to be checked
	 * @return true if there aren't restrictions
	 */
	protected boolean checkAccessPermission(final String attribute) {
		try {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new FormPermission(this.fileName, "visible", "", true);
				}
				if (this.enabledPermission == null) {
					this.enabledPermission = new FormPermission(this.fileName, "enabled", "", true);
				}
				this.enabledPermission.setAttribute(attribute);
				manager.checkPermission(this.enabledPermission);
				this.visiblePermission.setAttribute(attribute);
				manager.checkPermission(this.visiblePermission);
			}
			return true;
		} catch (final Exception e) {
			ClientSecurityManager.logger.debug(null, e);
			return false;
		}
	}

	/**
	 * Returns the key list of this form
	 * @return a <code>Vector</code> with the list of keys
	 */
	public List getKeys() {
		return ObjectTools.clone(this.keys);
	}

	/**
	 * Sets programmatically the filter fields for Form. This filter is used to query against server.
	 *
	 * @see #init
	 * @since 5.3.8
	 * @param parentKeys the parent key attributes to fix
	 */
	public void setParentKeys(final List parentKeys) {
		this.parentKeys = parentKeys;
	}

	/**
	 * Returns a List containing the parent key names, this is, the names set in the XML to the
	 * attributes 'parentkey' and 'otherkeys'.
	 *
	 * @see #init
	 * @return the parent key names
	 */
	public List getParentKeys() {
		return ObjectTools.clone(this.parentKeys);
	}

	/**
	 * Registers a keyboard action for default buttons.
	 * <p>
	 * In the form, the keyboard action that is registered by default is the refresh button action. The
	 * action is registered in 'F5' key.
	 */
	protected void registerKeyBindings() {
		// Refresh
		final InputMap inMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap actMap = this.getActionMap();
		final KeyStroke ksRefresh = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true);
		inMap.put(ksRefresh, "Refresh");
		actMap.put("Refresh", this.refreshButtonListener);

		final KeyStroke k2 = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, true);
		inMap.put(k2, "RefreshScripts");
		actMap.put("RefreshScripts", new Act());
	}

	// TODO DELETE :TINO
	protected class Act extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				if (Form.this.getInteractionManager() instanceof BasicInteractionManager) {
					final IFormInteractionScriptManager interactionManager = ((BasicInteractionManager) Form.this
							.getInteractionManager()).getFormInteractionScriptManager();
					interactionManager.registerListeners(Form.this);
				}
				Form.this.message("Scripts have been reload successfully", JOptionPane.INFORMATION_MESSAGE);
			} catch (final Exception ex) {
				Form.logger.error(null, ex);
			}
		}

	}

	protected void unregisterKeyBindings() {
	}

	/**
	 * Disables all tables from this form
	 */
	public void disableTables() {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof Table) {
				((Table) comp).setEnabled(false);
			}
		}
	}

	/**
	 * Disables all subforms from this form
	 */
	public void disableSubForms() {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof SubForm) {
				((SubForm) comp).setEnabled(false);
			}
		}
	}

	/**
	 * Registers listener so that it will receive <code>DataRecordEvent</code> when a record has been
	 * modified correctly.
	 * @param listener the <code>DataRecordListener<code> to register
	 */

	public void addDataRecordListener(final DataRecordListener listener) {
		if (this.dataRecordListeners == null) {
			this.dataRecordListeners = new ArrayList();
		}
		if (!this.dataRecordListeners.contains(listener)) {
			this.dataRecordListeners.add(listener);
		}
	}

	/**
	 * Unregisters listener so that it will no longer receive <code>DataRecordEvent</code>.
	 * @param listener the <code>DataRecordListener</code> to be removed
	 */

	public void removeDataRecordListener(final DataRecordListener listener) {
		if (this.dataRecordListeners == null) {
			return;
		}
		if (this.dataRecordListeners.contains(listener)) {
			this.dataRecordListeners.remove(listener);
		}
	}

	/**
	 * Notifies all listeners that a record may have modified correctly.
	 * @param e the event to be forwarded
	 */

	public void fireDataRecordChange(final DataRecordEvent e) {
		if (this.dataRecordListeners != null) {
			for (int i = this.dataRecordListeners.size() - 1; i >= 0; i--) {
				((DataRecordListener) this.dataRecordListeners.get(i)).dataRecordChanged(e);
			}
		}
	}

	/**
	 * Registers listener so that it will receive <code>DataNavigationEvent</code> when there are
	 * multiple records and the current record is changed using the navigation buttons.
	 * @param listener the <code>DataNavigationListener<code> to register
	 */
	public void addDataNavigationListener(final DataNavigationListener listener) {
		if (this.dataNavigationListeners == null) {
			this.dataNavigationListeners = new Vector();
		}
		this.dataNavigationListeners.add(this.dataNavigationListeners.size(), listener);
	}

	/**
	 * Notifies all listeners that the current record will be changed due to a
	 * <code>DataNavigationEvent</code> is forwarded.
	 * @param e the event to be forwarded
	 */

	protected boolean fireDataWillChange(final DataNavigationEvent e) {
		if (this.dataNavigationListeners != null) {
			for (int i = 0; i < this.dataNavigationListeners.size(); i++) {
				final boolean accept = ((DataNavigationListener) this.dataNavigationListeners.get(i)).dataWillChange(e);
				if (!accept) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Notifies all listeners that the current record may be changed due to a
	 * <code>DataNavigationEvent</code> is forwarded.
	 * @param e the event to be forwarded
	 */
	protected void fireDataChanged(final DataNavigationEvent e) {
		if (this.dataNavigationListeners != null) {
			for (int i = 0; i < this.dataNavigationListeners.size(); i++) {
				((DataNavigationListener) this.dataNavigationListeners.get(i)).dataChanged(e);
			}
		}
	}

	/**
	 * Returns the size of the form data cache.
	 * @return a int with the size of the form data cache
	 */

	public int getFormCacheSize() {
		return this.vectorSize;
	}

	/**
	 * Deletes the record with this index from form cache
	 * @param index index of the record to be deleted
	 */

	public void deleteRecordFromFormCache(final int index) {
		// Delete the record of the data list
		if (index < this.vectorSize) {
			final Iterator enumKeys = this.totalDataList.keySet().iterator();
			while (enumKeys.hasNext()) {
				final Object oKey = enumKeys.next();
				final List oValue = (List) this.totalDataList.get(oKey);
				oValue.remove(index);
			}
			// Update the List size
			this.vectorSize--;
			// Update the current record.
			final int newIndex = Math.min(index, this.vectorSize - 1);
			this.updateDataFields(newIndex);
		}
	}

	/**
	 * Dispatch the focus to first enabled data component starting by the top-left corner of the form.
	 */
	public boolean requestFocusForFirstComponent() {

		if (this.getFocusTraversalPolicy() instanceof IdentifiedFocusTraversalPolicy) {
			final Component c = ((IdentifiedFocusTraversalPolicy) this.getFocusTraversalPolicy()).getFirstComponent(this);
			if ((c != null) && c.isEnabled() && c.isVisible() && c.isFocusTraversable() && c.isFocusable()) {
				c.requestFocus();
				return true;
			}
			return false;
		}
		final java.util.List l = this.getComponentsInOrderFocusInBody();
		for (int i = 0; i < l.size(); i++) {
			final Component c = (Component) l.get(i);
			if (c.isEnabled() && c.isVisible() && c.isFocusTraversable() && c.isFocusable()) {
				c.requestFocus();
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the location of this component in this form of a point specifying the component's top-left
	 * corner in the form's coordinate space.
	 * @param component the <code>Component</code>
	 * @return a <code>Point</code> with the location of the component's top-left corner
	 */
	protected Point getLocationInForm(final Component component) {
		final Point posicionRespectoForm = new Point(0, 0);
		Component comp = component;
		while ((comp != null) && (comp != this)) {
			final Point p = comp.getLocation();
			posicionRespectoForm.x += p.x;
			posicionRespectoForm.y += p.y;
			comp = comp.getParent();
		}
		return posicionRespectoForm;
	}

	@Override
	public boolean isFocusTraversable() {
		return true;
	}

	@Override
	public boolean requestDefaultFocus() {
		if (!this.requestFocusForFirstComponent()) {
			return super.requestDefaultFocus();
		} else {
			return true;
		}
	}

	/**
	 * Performs a query for a record of the total data cache which index is the entry parameter.
	 * @param index
	 * @return a <code>EntityResult</code> resulting of the query.
	 */
	protected EntityResult query(final int index) {
		EntityResult res = null;
		try {
			final Map hFilterKeys = new Hashtable();
			if (index >= 0) {
				// Put the keys
				final Iterator enumKeys = this.totalDataList.keySet().iterator();
				while (enumKeys.hasNext()) {
					final Object oKeyField = enumKeys.next();
					if (this.keys.contains(oKeyField)) {
						final List vKeyValues = (List) this.totalDataList.get(oKeyField);
						// If the key is not between the current data list
						// maybe it is a parent key (value fixed in a field).
						if (vKeyValues.size() <= index) {
							final Object oFieldValue = this.getDataFieldValue(oKeyField.toString());
							if (oFieldValue == null) {
								Form.logger.debug(
										"Value for the key {} has not been found in the data list or in a field with this attribute.",
										oKeyField);
								return new EntityResultMapImpl();
							} else {
								Form.logger.debug(
										"Value for the key {}  is not in the data list. The field value is used. Maybe this field must be a parentkey.",
										oKeyField);
								hFilterKeys.put(oKeyField, oFieldValue);
							}
						} else {
							hFilterKeys.put(oKeyField, vKeyValues.get(index));
						}
					}
				}
				for (int i = 0; i < this.keys.size(); i++) {
					final Object oKey = this.keys.get(i);
					if (!this.totalDataList.containsKey(oKey)) {
						final Object oDataFieldValue = this.getDataFieldValue(oKey.toString());
						if (oDataFieldValue != null) {
							Form.logger.debug(
									"Value for the key {}  is not in the data list. The field value is used. Maybe this field must be a parentkey.",
									oKey);
							hFilterKeys.put(oKey, oDataFieldValue);
						} else {
							Form.logger.debug(
									"Value for the key {}  is not in the data list and the form does not contain a field with this attribute.",
									oKey);
						}
					}
				}
				// Put the parentkeys
				for (int i = 0; i < this.parentKeys.size(); i++) {
					final Object oFieldValue = this.getDataFieldValue(this.parentKeys.get(i).toString());
					if (oFieldValue == null) {
						Form.logger.debug(
								"Value for the parentkey {} is not in the data list and the form does not contain a field with this attribute.",
								this.parentKeys.get(i));
						return new EntityResultMapImpl();
					} else {
						hFilterKeys.put(this.parentKeys.get(i), oFieldValue);
					}
				}
			} else {
				return new EntityResultMapImpl();
			}
			final EntityReferenceLocator locator = this.getFormManager().getReferenceLocator();
			final Entity entity = locator.getEntityReference(this.getEntityName());
			final List vAttributeList = ObjectTools.clone(this.getDataFieldAttributeList());
			for (int i = 0; i < this.keys.size(); i++) {
				if (!vAttributeList.contains(this.keys.get(i))) {
					vAttributeList.add(this.keys.get(i));
				}
			}
			// Search for fields that have not been queried because they are in
			// a
			// hide tab
			final List vNotQueryFields = new Vector();
			for (int i = 0; i < this.componentList.size(); i++) {
				if (this.componentList.get(i) instanceof TabPanel) {
					final List v = ((TabPanel) this.componentList.get(i)).initNotQueriedDataFieldAttributes();
					vNotQueryFields.addAll(v);
				}
			}
			for (int i = 0; i < vNotQueryFields.size(); i++) {
				if (!this.keys.contains(vNotQueryFields.get(i))) {
					vAttributeList.remove(vNotQueryFields.get(i));
				}
			}
			res = entity.query(hFilterKeys, vAttributeList, locator.getSessionId());
			// For each key, get the value and add it to the data
			return res;
		} catch (final Exception e) {
			Form.logger.error("FormExt: Querying error. Parameters, xml description and entity must be checked", e);
			this.message(SwingUtilities.getWindowAncestor(this), e.getMessage(), Form.ERROR_MESSAGE, (Object[]) null,
					e);
			return new EntityResultMapImpl();
		}
	}

	/**
	 * Brings up a dialog that displays a message using a default icon determined by the messageType
	 * parameter.
	 * @param owner the non-null Window from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */
	public int message(final Window owner, final String message, final int messageType) {
		if (owner instanceof Dialog) {
			return this.message((Dialog) owner, message, messageType);
		} else {
			return this.message((Frame) owner, message, messageType);
		}
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Window from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @param args a extra information list is used to create the message text. The message text is
	 *        created passing the message and the args through the resource bundle.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */
	public int message(final Window owner, final String message, final int messageType, final String detail, final Object[] args) {
		if (owner instanceof Dialog) {
			return this.message((Dialog) owner, message, messageType, detail, args);
		} else {
			return this.message((Frame) owner, message, messageType, detail, args);
		}
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Window from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param args a extra information list is used to create the message text. The message text is
	 *        created passing the message and the args through the resource bundle.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */
	public int message(final Window owner, final String message, final int messageType, final Object[] args) {
		if (owner instanceof Dialog) {
			return this.message((Dialog) owner, message, messageType, null, args);
		} else {
			return this.message((Frame) owner, message, messageType, null, args);
		}
	}

	public int message(final Window owner, final String message, final int messageType, final Object[] args, final Throwable exception) {
		final StringBuilder buffer = ApplicationManager.printStackTrace(exception);
		if (owner instanceof Dialog) {
			return this.message((Dialog) owner, message, messageType, buffer != null ? buffer.toString() : null, args);
		} else {
			return this.message((Frame) owner, message, messageType, buffer != null ? buffer.toString() : null, args);
		}
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Frame from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Frame owner, final String message, final int messageType) {
		return this.message((Window) owner, message, messageType, null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param owner the non-null Frame from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Frame owner, final String message, final int messageType, final String detail) {
		return this.message(owner, message, messageType, detail, null);
	}

	/**
	 * Shows a {@link MessageDialog} with the information collects from entry parameters
	 * @param f the non-null Frame from which the <code>MessageDialog</code> is displayed
	 * @param message a <code>String</code> with the text to show in the <code>MessageDialog</code>
	 * @param messageType the type of message to be displayed: <code>ERROR_MESSAGE</code>,
	 *        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
	 *        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
	 * @param detail a <code>String</code> with more detailed information that can be shown in the
	 *        <code>MessageDialog</code> to press a detail button. If detail parameter is null, the
	 *        detail button will not be shown
	 * @param args a extra information list is used to create the message text.
	 * @see MessageDialog
	 * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the
	 *         user closed the dialog
	 */

	public int message(final Frame f, final String message, final int messageType, final String detail, final Object[] args) {
		switch (messageType) {
		case Form.QUESTION_MESSAGE:
			return MessageDialog.showMessage(f, message, detail, messageType, JOptionPane.YES_NO_OPTION,
					this.resourcesFile, args);
		case Form.INFORMATION_MESSAGE:
			return MessageDialog.showMessage(f, message, detail, messageType, this.resourcesFile, args);
		case Form.ERROR_MESSAGE:
			return MessageDialog.showMessage(f, message, detail, messageType, this.resourcesFile, args);
		}
		return MessageDialog.showMessage(f, message, detail, messageType, this.resourcesFile, args);
	}

	/**
	 * Performs a query operation that return the set of data that matches the conditions specified by
	 * the <code>keysValues</code> parameter. The <code>attributes</code> defines which attributes must
	 * be recovered, and <code>keysValues</code> specifies which set of records must be recovered.
	 * <p>
	 * @param keysValues a Map specifying conditions that must comply the set of records returned.
	 *        Cannot be null.
	 * @param attributes a list of columns or attributes that must be recovered for each record
	 *        returned. Cannot be null. If empty, all attributes should be returned.
	 * @return a EntityResult with the resulting set of data.
	 */

	public EntityResult query(final Map keysValues, final List attributes) {
		EntityResult res = null;
		try {
			final List attributesValues = ObjectTools.clone(attributes);
			final EntityReferenceLocator locator = this.getFormManager().getReferenceLocator();
			final Entity entity = locator.getEntityReference(this.getEntityName());
			res = entity.query(keysValues, attributesValues, locator.getSessionId());
			// For each key get the value an add it to the data
			return res;
		} catch (final Exception e) {
			Form.logger.error("Form: Error executing query. Check parameters, form xml and entity", e);
			final EntityResult result = new EntityResultMapImpl();
			// TODO include the error message in the result
			// result.setCode(EntityResult.OPERATION_WRONG);
			// result.setMessage(e.getMessage());
			return result;
		}
	}

	/**
	 * Performs a query operation that return the set of data that matches the conditions specified by
	 * record index of total data cache. The <code>attributes</code> defines which attributes must be
	 * recovered, and <code>index</code> specifies the record of the total data cache that will be used
	 * as query condition.
	 * @param index a <code>int</code> that specify the record of the total data cache.
	 * @param attributes a list of columns or attributes that must be recovered for each record
	 *        returned. Cannot be null. If empty, all attributes should be returned.
	 * @return a EntityResult with the resulting set of data.
	 */
	public EntityResult query(final int index, final List attributes) {
		EntityResult res = null;
		try {
			final List vAttributes = ObjectTools.clone(attributes);
			final Map hKeysValues = new Hashtable();
			if (index >= 0) {
				// Put the keys
				final Iterator enumKeysValues = this.totalDataList.keySet().iterator();
				while (enumKeysValues.hasNext()) {
					final Object oKeyField = enumKeysValues.next();
					if (this.keys.contains(oKeyField)) {
						final List vKeyValues = (List) this.totalDataList.get(oKeyField);
						// If the key is not in the data list maybe it is a
						// parent key
						// and it is fixed in a field
						if (vKeyValues.size() <= index) {
							final Object oFieldValue = this.getDataFieldValue(oKeyField.toString());
							if (oFieldValue == null) {
								Form.logger.debug(
										"Value for the key {} has not been found in the data list or in a field with this attribute.",
										oKeyField);
								return new EntityResultMapImpl();
							} else {
								Form.logger.debug(
										"Value for the key {} has not been found in the data list or in a field with this attribute.",
										oKeyField);
								hKeysValues.put(oKeyField, oFieldValue);
							}
						} else {
							hKeysValues.put(oKeyField, vKeyValues.get(index));
						}
					}
				}
				for (int i = 0; i < this.keys.size(); i++) {
					final Object oKey = this.keys.get(i);
					if (!vAttributes.contains(oKey)) {
						vAttributes.add(oKey);
					}
					if (!this.totalDataList.containsKey(oKey)) {
						final Object fieldValue = this.getDataFieldValue(oKey.toString());
						if (fieldValue != null) {
							Form.logger.debug("Value for the key {} has been found.", oKey);
							hKeysValues.put(oKey, fieldValue);
						} else {
							Form.logger.debug(
									"Value for the key {} has not been found in the data list or in a field with this attribute.",
									oKey);
						}
					}
				}
				// Put the parentkeys
				for (int i = 0; i < this.parentKeys.size(); i++) {
					final Object oFieldValue = this.getDataFieldValue(this.parentKeys.get(i).toString());
					if (oFieldValue == null) {
						Form.logger.debug(
								"Value for the key {} has not been found in the data list or in a field with this attribute.",
								this.parentKeys.get(i));
						return new EntityResultMapImpl();
					} else {
						hKeysValues.put(this.parentKeys.get(i), oFieldValue);
					}
				}
			} else {
				return new EntityResultMapImpl();
			}

			// Check whether table parent keys is in the attributes values
			final int size = vAttributes.size();
			for (int i = 0; i < size; i++) {
				final Object current = vAttributes.get(i);
				if (current instanceof TableAttribute) {
					final List tableParentKey = ((TableAttribute) current).getParentKeys();
					for (int j = 0; j < tableParentKey.size(); j++) {
						if (!vAttributes.contains(tableParentKey.get(j))) {
							vAttributes.add(tableParentKey.get(j));
						}
					}
				}
			}

			final EntityReferenceLocator locator = this.getFormManager().getReferenceLocator();
			final Entity entity = locator.getEntityReference(this.getEntityName());

			res = entity.query(hKeysValues, vAttributes, locator.getSessionId());
			// For each key get the value and add it to the data
			return res;
		} catch (final Exception e) {
			Form.logger.error("Error in query. Check the parameters, the xml and the entity", e);
			return new EntityResultMapImpl();
		}
	}

	/**
	 * Refreshes the current data record that is displayed in the form, both the record of the total
	 * data cache and all values that are displayed in each data field.
	 */
	public void refreshCurrentDataRecord() {
		// The form stores the data in a list. Tenemos que
		// To refresh the current record the associated data in the list must be
		// update too
		// Use the record keys to make a query
		if ((this.keys != null) && !this.keys.isEmpty()) {
			try {
				// Query
				final EntityResult res = this.query(this.currentIndex);
				if (res.getCode() == EntityResult.OPERATION_WRONG) {
					Form.logger.debug("ERROR in the current record refresh caused by: {}", res.getMessage());
					return;
				}
				if (res.isEmpty()) {
					Form.logger.debug("ERROR in the current record refresh: No data found");
					return;
				}
				final List vResultKeys = (List) res.get(this.keys.get(0));
				if (vResultKeys.size() != 1) {
					Form.logger.debug("ERROR in the current record refresh: More than one record found");
					return;
				}
				// Replaces the data in the form data list
				final Enumeration enumKeys = res.keys();
				while (enumKeys.hasMoreElements()) {
					final Object oKey = enumKeys.nextElement();
					final List vResultData = (List) res.get(oKey);
					final List vDataListData = (List) this.totalDataList.get(oKey);
					if ((vResultData != null) && (vDataListData != null)) {
						if (this.currentIndex < vDataListData.size()) {
							vDataListData.remove(this.currentIndex);
							vDataListData.add(this.currentIndex, vResultData.get(0));
						}
					}
				}
				this.updateDataFields(this.currentIndex);
			} catch (final Exception e) {
				Form.logger.error("ERROR in the refresh of the current record.", e);
			}
		} else {
			Form.logger.debug("ERROR: Record can not be refresh because threre are no keys in this form");
		}
	}

	/**
	 * Returns true if the modified data field list has been created.
	 */
	protected void isCreatedCheckModifiedFieldList() {
		if (this.checkModifiedFieldList == null) {
			this.checkModifiedFieldList = new Vector();
			final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				this.checkModifiedFieldList.add(oKey);
			}
		}
	}

	/**
	 * Established if a data field, which attribute is the entry parameter, has enabled the data
	 * modified checking.
	 * @param attr data component attribute to be established the data modified checking.
	 * @param enable true to make the checking enable; false to make it disable
	 */
	public void setCheckModifiedDataField(final String attr, final boolean enable) {
		this.isCreatedCheckModifiedFieldList();
		if (!enable) {
			if (this.checkModifiedFieldList.contains(attr)) {
				this.checkModifiedFieldList.remove(attr);
				Form.logger.debug("Form: Disabled data modified checking for component {}", attr);
				return;
			} else {
				for (int i = 0; i < this.checkModifiedFieldList.size(); i++) {
					final Object atr = this.checkModifiedFieldList.get(i);
					if (atr instanceof ReferenceFieldAttribute) {
						if (((ReferenceFieldAttribute) atr).getAttr().equals(attr)) {
							this.checkModifiedFieldList.remove(atr);
							Form.logger.debug("Form: Disabled data modified checking for component {}", attr);
							return;
						}
					}
				}
			}
		} else {
			final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				if (oKey.equals(attr)) {
					this.checkModifiedFieldList.add(attr);
					Form.logger.debug("Form: checking modified daata enabled for component {}", attr);
					return;
				} else if (oKey instanceof ReferenceFieldAttribute) {
					if (((ReferenceFieldAttribute) oKey).getAttr().equals(attr)) {
						this.checkModifiedFieldList.add(oKey);
						Form.logger.debug("Form: checking modified daata enabled for component {}", attr);
						return;
					}
				}
			}
		}
	}

	/**
	 * Established the values in each form data field that is passed in the entry parameter. This method
	 * doesn't remove any data field, only established the values that has the entry parameter.
	 * @param values a <code>Hashtable</code> with the values to be established. The keys of the
	 *        <code>Hashtable</code> are the attributes of each data field and the values of the
	 *        <code>Hashtable</code> are the value of each data field.
	 */

	public void setDataFieldValues(final Map values) {
		final Enumeration enumKeys = Collections.enumeration(values.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			this.setDataFieldValue(oKey.toString(), values.get(oKey));
		}
	}

	/**
	 * Returns the {@link DynamicFormManager} object that is assigned to this form.
	 * @return
	 */
	public DynamicFormManager getDynamicFormManager() {
		return this.dynamicFormManager;
	}

	/**
	 * Sets the {@link DynamicFormManager} object for this form.
	 * @param dynamicfm
	 */
	public void setDynamicFormManager(final DynamicFormManager dynamicfm) {
		this.dynamicFormManager = dynamicfm;
	}

	/**
	 * Registers all components that implement {@link ApplicationPreferencesListener} so that they will
	 * receive PreferenceEvent when some preference change in the application.
	 */
	public void registerApplicationPreferencesListener() {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof ApplicationPreferencesListener) {
				final ApplicationPreferences prefs = this.formManager.getApplication().getPreferences();
				if ((prefs != null) && (prefs instanceof AbstractApplicationPreferences)) {
					((AbstractApplicationPreferences) prefs)
					.addApplicationPreferencesListener((ApplicationPreferencesListener) comp);
				}
			}
		}
	}

	/**
	 * Loads the table view preferences.In this preference the column list that will be showed in the
	 * table view is stored.
	 * @param prefs a {@link ApplicationPreferences} instance that manager the application preferences
	 * @return a <code>Vector</code> with the column list to table view table.
	 */
	protected List loadTableViewPreference(final ApplicationPreferences prefs) {
		final String sKey = this.getTableViewPreferenceKey();
		final String preference = PreferenceUtils.loadPreference(sKey, prefs);
		if (preference != null) {
			final List columns = ApplicationManager.getTokensAt(preference, ";");
			return columns;
		}
		return null;
	}

	/**
	 * Saves the table view preference.In this preference the column list that will be showed in the
	 * table view is stored.
	 * @param columns a <code>Vector</code> with the column list to table view table.
	 */

	protected void saveTableViewPreferenceForm(final List columns) {
		final String sKey = this.getTableViewPreferenceKey();
		String preference = null;
		if (columns != null) {
			preference = ApplicationManager.vectorToStringSeparateBy(columns, ";");
		}
		PreferenceUtils.savePreference(sKey, preference);
	}

	/**
	 * Returns the string used as key to store the table view columns in the preferences
	 * @return a preference key
	 */

	protected String getTableViewPreferenceKey() {
		return BasicApplicationPreferences.FORM_TABLEVIEW_COLUMNS + "_" + this.getArchiveName();
	}

	@Override
	public void initPreferences(final ApplicationPreferences ap, final String user) {
		for (int i = 0; i < this.componentList.size(); i++) {
			final Object comp = this.componentList.get(i);
			if (comp instanceof HasPreferenceComponent) {
				((HasPreferenceComponent) comp).initPreferences(ap, user);
			}
		}

		final List list = this.getVisibleDataComponentAttributes();
		if ((list != null) && (list.size() > 0)) {
			this.tableButton.setSelectionColumns(true);
		} else {
			this.tableButton.setSelectionColumns(false);
			this.tableButton.setEnabled(false);
		}

		if (this.tableButton.hasSelectionColumns()) {
			this.additionalTableViewColumns = this.loadTableViewPreference(ap);
			if (this.additionalTableViewColumns == null) {
				if (this.tableViewColumns != null) {
					this.additionalTableViewColumns = ObjectTools.clone(this.tableViewColumns);
				} else {
					this.additionalTableViewColumns = new Vector();
				}
			}
		}
	}

	/**
	 * Returns all form data field values in text format for being used in template filling.
	 * @return a <code>Hashtable</code> with the values of all data fields. The keys are the attributes
	 *         of the data fields and the values are the data in text format
	 */
	protected Map getDataFieldTemplateValues() {

		final Map texts = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(this.dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oField = this.dataComponentList.get(oKey);
			if (oField != null) {
				if (oField instanceof DataField) {
					Object oValue = ((DataField) oField).getText();
					if (oValue == null) {
						oValue = ((DataField) oField).getValue();
					}
					if (oValue != null) {
						// If the attribute is a ReferenceFieldAttribute then
						// put the
						// attribute as key
						if (oKey instanceof ReferenceFieldAttribute) {
							texts.put(((ReferenceFieldAttribute) oKey).getAttr(), oValue);
						} else {
							texts.put(oKey, oValue);
						}
					}
				}
			}
		}
		return texts;
	}

	public void fillODT(final File inputFile) throws Exception {
		TemplateUtils.fillTemplate(inputFile, this);
	}

	/**
	 * Fills a PDF Document using the form data.
	 * @param inputFile the PDF file to be filled
	 * @param outputFile the filled PDF file
	 * @throws Exception
	 */
	public void fillPDF(final File inputFile, final File outputFile) throws Exception {

		final Map h = Form.this.getDataFieldText();

		final List images = new Vector();
		final Enumeration enuK = Collections.enumeration(h.keySet());
		while (enuK.hasMoreElements()) {
			final Object k = enuK.nextElement();
			final Object o = h.get(k);
			if (o instanceof BytesBlock) {
				h.remove(k);
				images.add(k);
				h.put(k, ((BytesBlock) o).getBytes());
			}
		}

		PdfFiller.fillTextImageFields(new FileInputStream(inputFile), new FileOutputStream(outputFile), h, images,
				true);
		com.ontimize.windows.office.WindowsUtils.openFile_Script(outputFile);
	}

	/**
	 * Fills a WORD Document using the form data. The combination method is used to fill this WORD
	 * Document.
	 * @param file the WORD file to be filled
	 * @throws Exception
	 */
	protected void fillDocComb(final File file) throws Exception {
		// Map h = Form.this.getDataFieldText();
		final List vList = Form.this.getDataComponents();

		final Map hData = new Hashtable();
		for (int i = 0; i < vList.size(); i++) {
			final Object oField = vList.get(i);
			if (oField != null) {
				if (oField instanceof DataField) {
					Object oValue = ((DataField) oField).getText();
					if (oValue == null) {
						oValue = "";
					} else {
						final List v = new Vector();
						v.add(oValue);

						if (oField instanceof ReferenceFieldAttribute) {
							hData.put(((ReferenceFieldAttribute) oField).getAttr(), v);
						} else {
							hData.put(((DataField) oField).getAttribute(), v);
						}
					}
				}
			}
		}
		final String sUserDirectory = System.getProperty("java.io.tmpdir");
		File fData = new File(sUserDirectory, "datos.dat");
		String string = ExportTxt.exportData(hData, '#', '$');
		string = string.replace('\n', '\r');
		com.ontimize.util.FileUtils.saveFile(fData, string);
		fData.deleteOnExit();
		fData = null;
		com.ontimize.windows.office.WindowsUtils.openFile(file);
	}

	/**
	 * Fills a WORD Document using the form data. The replacement method is used to fill this WORD
	 * Document.
	 * @param file the WORD file to be filled
	 * @throws Exception
	 */

	public void fillDocSust(final File file) throws Exception {
		if (Form.WORD_TEMPLATES == 0) {
			if (Form.DOCX_TEMPLATES == 1) {
				TemplateUtils.fillTemplate(file, this);
			}
			final List vList = Form.this.getDataComponents();
			final Map hData = new Hashtable();
			for (int i = 0; i < vList.size(); i++) {
				final Object oField = vList.get(i);
				if (oField != null) {
					if (oField instanceof DataField) {
						String oValue = ((DataField) oField).getText();
						if ((oValue == null) || oValue.equals("")) {
							oValue = " ";
						}
						if (oValue.length() > 255) {
							oValue = oValue.substring(0, 255);
						}

						if (oField instanceof ReferenceFieldAttribute) {
							hData.put("%" + ((ReferenceFieldAttribute) oField).getAttr() + "%", oValue);
						} else {
							hData.put("%" + ((DataField) oField).getAttribute() + "%", oValue);
						}

					}
				}
			}
			com.ontimize.windows.office.ScriptUtilities.fillAndPrintWordDocument(file.toString(), hData);
		} else if (Form.WORD_TEMPLATES == 1) {
			TemplateUtils.fillTemplate(file, this);
		}
	}

	/**
	 * Installs the printing template button for this form. If the standard printing templates are
	 * configured, then the printing template button will allow to store and print the printing
	 * templates.
	 */

	protected void installPrintingTemplatesButton() {
		try {
			if (this.printButton == null) {
				return;
			}

			final EntityReferenceLocator locator = this.formManager.getReferenceLocator();
			if (locator instanceof UtilReferenceLocator) {

				final Entity ePrintingTemplate = ((UtilReferenceLocator) locator)
						.getPrintingTemplateEntity(locator.getSessionId());
				if (ePrintingTemplate != null) {

					this.ePrintingTemplate = ePrintingTemplate;
					final Container parent = this.printButton.getParent();
					if (parent == null) {
						// If parent is null then the print button is not
						// visible in the form
						return;
					}
					final GridBagConstraints constraints = ((GridBagLayout) parent.getLayout())
							.getConstraints(this.printButton);
					parent.remove(this.printButton);
					final Icon iconPrint = this.printButton.getIcon();
					this.printButton.removeActionListener(this.printListener);
					this.printButton.setVisible(false);
					this.printButton = null;

					this.printTemplateButton = new ButtonSelectionInternationalization(iconPrint, true);
					this.printTemplateButton.setParentForm(this);
					this.printTemplateButton
					.setToolTipText(ApplicationManager.getTranslation("form.print_data", this.resourcesFile));
					this.configureButton(this.printTemplateButton.getButton(), null, this.borderbuttons,
							this.opaquebuttons, this.listenerHighlightButtons);
					this.configureButton(this.printTemplateButton.getMenuButton(), null, this.borderbuttons,
							this.opaquebuttons, this.listenerHighlightButtons);

					this.printTemplateButton.setMargin(new Insets(1, 1, 1, 1));
					if (!this.buttons) {
						this.printTemplateButton.setVisible(false);
					}

					parent.add(this.printTemplateButton, constraints);

					this.printTemplateButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent event) {
							final EntityReferenceLocator locator = Form.this.formManager.getReferenceLocator();
							try {
								final Entity ePrintTemplatesEntity = ((UtilReferenceLocator) locator)
										.getPrintingTemplateEntity(locator.getSessionId());
								final Map kv = new Hashtable();
								kv.put(Form.TEMPLATE_FORM, Form.this.fileName);
								kv.put(Form.TEMPLATE_DEFAULT, new Integer(1));
								final List av = new Vector();
								final EntityResult rs = ePrintTemplatesEntity.query(kv, av, locator.getSessionId());
								// When there is only one default template then
								// open it
								if ((rs.getCode() == EntityResult.OPERATION_SUCCESSFUL)
										&& (rs.calculateRecordNumber() == 1)) {
									if (rs.containsKey(Form.TEMPLATE_NAME)) {
										final String sFileName = (String) CollectionTools
												.firstElement((List) rs.get(Form.TEMPLATE_NAME));
										final DataFile dataFile = new DataFile(sFileName,
												(BytesBlock) CollectionTools
												.firstElement((List) rs.get(Form.TEMPLATE_WAREHOUSE)));

										final String dataFileName = dataFile.getFileName();
										final String userDirectory = System.getProperty("java.io.tmpdir");
										final File f = new File(userDirectory, dataFileName);
										final File outputFile = new File(userDirectory,
												"" + System.currentTimeMillis() + dataFileName);
										f.deleteOnExit();
										if ((!f.exists())
												|| (f.length() != dataFile.getBytesBlock().getBytes().length)) {
											FileUtils.saveFile(f, dataFile.getBytesBlock().getBytes(), false);
										}

										final int index = sFileName.lastIndexOf(".");
										if (index != -1) {
											final String ext = sFileName.substring(index);
											if (".pdf".equalsIgnoreCase(ext)) {
												try {
													Form.this.fillPDF(f, outputFile);
												} catch (final Exception ex) {
													Form.logger.trace(null, ex);
													Form.this.message(Form.messageErrorGeneratePDF, Form.ERROR_MESSAGE);
												}
											} else if (".doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
												try {
													Form.this.fillDocSust(f);
												} catch (final Exception ex) {
													Form.logger.error(null, ex);
													Form.this.message("WordError", Form.ERROR_MESSAGE, ex);
												}
											} else if (".odt".equalsIgnoreCase(ext)) {
												try {
													Form.this.fillODT(f);
												} catch (final Exception ex) {
													Form.logger.error(null, ex);
													Form.this.message("OpennOfficeError: " + ex.getMessage(),
															Form.ERROR_MESSAGE, ex);
												}
											}
										}
									}
								} else {
									Form.this.printListener.actionPerformed(event);
								}
							} catch (final Exception e) {
								Form.logger.error(null, e);
								Form.this.message("Error opening template: " + e.getMessage(), Form.ERROR_MESSAGE, e);
							}
						}
					});

					this.printTemplateButton.addActionMenuListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {

							final EntityReferenceLocator locator = Form.this.formManager.getReferenceLocator();
							try {
								final Entity ePrintTemplateEntity = ((UtilReferenceLocator) locator)
										.getPrintingTemplateEntity(locator.getSessionId());
								final List v = Form.this.getKeys();
								final Map kv = new Hashtable();
								kv.put(Form.TEMPLATE_FORM, Form.this.fileName);
								kv.put(Form.TEMPLATE_USER, ((ClientReferenceLocator) locator).getUser());
								Collections.sort(v);
								final List vColumn = new Vector();
								vColumn.add(Form.TEMPLATE_ID);
								vColumn.add(Form.TEMPLATE_FORM);
								vColumn.add(Form.TEMPLATE_NAME);
								vColumn.add(Form.TEMPLATE_DEFAULT);
								vColumn.add(Form.TEMPLATE_USER);
								vColumn.add(Form.TEMPLATE_PRIVATE);
								final EntityResult res = ePrintTemplateEntity.query(kv, vColumn, locator.getSessionId());
								boolean bPrivateTemplates = false;
								if (res.containsKey(Form.TEMPLATE_USER) && res.containsKey(Form.TEMPLATE_PRIVATE)) {
									bPrivateTemplates = true;
								}
								if (Form.this.templateList == null) {
									Form.this.templateList = new PopupPrintingTemplateList(Form.this,
											bPrivateTemplates);
									Form.this.printTemplateButton.setMenuList(Form.this.templateList);
								}
								Form.this.templateList.setDataModel(res);

							} catch (final Exception ex) {
								Form.logger.error(null, ex);
							}
							Form.this.templateList.show(Form.this.printTemplateButton, 0,
									Form.this.printTemplateButton.getHeight());
						}
					});

				}
			}
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
	}

	protected void installDatabaseBundleButton() {
		try {
			if (this.checkApplicationBundleButton()) {
				final Map parameters = DefaultXMLParametersManager.getParameters(FormBundleButton.class.getName());
				parameters.put(Button.KEY, FormBundleButton.FORM_BUNDLE_BUTTON_DEFAULT_KEY);

				parameters.put("margin", "1;1;1;1");
				this.databaseBundleButton = new FormBundleButton(parameters);

				if (this.databaseBundleButton.getIcon() == null) {
					this.databaseBundleButton.setIcon(this.databaseBundleIcon);
				}

				this.configureButton(this.databaseBundleButton, null, this.borderbuttons, this.opaquebuttons,
						this.listenerHighlightButtons);

				if (!this.buttons) {
					this.databaseBundleButton.setVisible(false);
				}
				this.addControls(this.databaseBundleButton);
			}
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
	}

	protected boolean checkApplicationBundleButton() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			final ApplicationPermission bundleButtonApplicationPermission = new ApplicationPermission("BundleButton", true);
			try {
				if (bundleButtonApplicationPermission != null) {
					manager.checkPermission(bundleButtonApplicationPermission);
				}
				return true;
			} catch (final Exception e) {
				ClientSecurityManager.logger.debug(null, e);
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks if the the permission button is restricted by application permissions.
	 */

	protected boolean checkApplicationPermissionButton() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionButtonApplicationPermission == null) {
				this.permissionButtonApplicationPermission = new ApplicationPermission("PermissionButton", true);
			}
			try {
				if (this.permissionButtonApplicationPermission != null) {
					manager.checkPermission(this.permissionButtonApplicationPermission);
				}
				return true;
			} catch (final Exception e) {
				ClientSecurityManager.logger.debug(null, e);
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Installs the permission button for this form. If the permission component is configured, then the
	 * permission button will be showed and will displayed the permission component to configure the
	 * client and server permissions.
	 */
	protected void installPermissionButton() {
		try {
			if (this.checkApplicationPermissionButton()) {
				final Class permissionButtonClass = Class.forName("com.ontimize.permission.PermissionButton");
				final Object obj = permissionButtonClass.newInstance();
				if (obj instanceof JButton) {
					this.permissionButton = (JButton) obj;
					this.configureButton(this.permissionButton, null, this.borderbuttons, this.opaquebuttons,
							this.listenerHighlightButtons);
					if (!this.buttons) {
						((JButton) obj).setVisible(false);
					}
					this.addControls((JButton) obj);

				}

				if ((obj != null) && (obj instanceof JButton)) {
					final InputMap inMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
					final ActionMap actMap = this.getActionMap();
					final KeyStroke ksRefresh = KeyStroke.getKeyStroke(KeyEvent.VK_P,
							InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, false);
					inMap.put(ksRefresh, "com.ontimize.permission.PermissionButton");
					actMap.put("com.ontimize.permission.PermissionButton", new PermissionAction((JButton) obj));
				}
			}
		} catch (final IllegalArgumentException e) {
			Form.logger.debug(null, e);
		} catch (final ClassNotFoundException cE) {
			Form.logger.debug(null, cE);
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
	}

	protected class PermissionAction extends AbstractAction {

		protected JButton button = null;

		public PermissionAction(final JButton b) {
			this.button = b;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (this.button != null) {
				this.button.doClick();
			}
		}

	}

	/**
	 * Installs the attachment button for this form. If the standard attachments are configured, then
	 * the attachment button will allow to attach files to each one of the form records.
	 */

	protected void installAttachmentButton() {
		try {
			final EntityReferenceLocator locator = this.formManager.getReferenceLocator();
			if (locator instanceof UtilReferenceLocator) {
				final Entity attachmentEntity = ((UtilReferenceLocator) locator).getAttachmentEntity(locator.getSessionId());
				if (attachmentEntity != null) {
					this.attachmentEntity = attachmentEntity;
					// Create the attachment button
					this.attachmentButton = new AttachmentButtonSelection(this.attachmentIcon, this.attachmentEmptyIcon,
							true, ((SecureEntity) attachmentEntity).getName(), this);
					this.attachmentButton.setParentForm(this);
					// Configure the button appearance to fit in the form
					this.configureButton(this.attachmentButton.getButton(), null, this.borderbuttons,
							this.opaquebuttons, this.listenerHighlightButtons);
					this.configureButton(this.attachmentButton.getMenuButton(), null, this.borderbuttons,
							this.opaquebuttons, this.listenerHighlightButtons);
					this.attachmentButton.setMargin(new Insets(1, 1, 1, 1));
					if (!this.buttons) {
						this.attachmentButton.setVisible(false);
					}

					this.attachmentButton.setToolTipText("form.tip_attachment_button");
					this.internationalizeList.add(this.attachmentButton);
					this.addControls(this.attachmentButton);

					this.attachmentButton.setResourceBundle(this.resourcesFile);

					// Register listeners
					this.attachmentButton.addActionListener(new AttachmentButtonListener(this));
					this.attachmentButton.addActionMenuListener(new AttachmentMenuListener(this));
				}
			}
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
	}

	/**
	 * Returns the {@link AttachmentButtonSelection} for the current form
	 * @return the {@link AttachmentButtonSelection} for the current form
	 */
	public AttachmentButtonSelection getAttachmentButton() {
		return (AttachmentButtonSelection) this.attachmentButton;
	}

	protected boolean checkApplicationScriptButton() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			final ApplicationPermission scriptButtonApplicationPermission = new ApplicationPermission("ScriptButton", true);
			try {
				if (scriptButtonApplicationPermission != null) {
					manager.checkPermission(scriptButtonApplicationPermission);
				}
				return true;
			} catch (final Exception e) {
				ClientSecurityManager.logger.debug(null, e);
				return false;
			}
		} else {
			return true;
		}
	}

	protected void installScriptButton() {
		try {
			if (this.checkApplicationScriptButton()) {
				final Class scriptButtonClass = Class.forName("com.ontimize.scripting.gui.ScriptButton");
				final Map parameters = DefaultXMLParametersManager
						.getParameters("com.ontimize.scripting.gui.ScriptButton");
				parameters.put(Button.KEY, "com.ontimize.scripting.gui.ScriptButton");
				parameters.put("margin", "1;1;1;1");
				final Constructor scriptButtonConstructor = scriptButtonClass.getConstructor(new Class[] { Hashtable.class });
				final Object obj = scriptButtonConstructor.newInstance(new Object[] { parameters });
				if (obj instanceof JButton) {
					this.scriptButton = (JButton) obj;
					if (this.scriptButton.getIcon() == null) {
						this.scriptButton.setIcon(this.scriptIcon);
					}

					this.configureButton(this.scriptButton, null, this.borderbuttons, this.opaquebuttons,
							this.listenerHighlightButtons);
					if (!this.buttons) {
						((JButton) obj).setVisible(false);
					}
					this.addControls((JButton) obj);

				}

				if ((obj != null) && (obj instanceof JButton)) {
					final InputMap inMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
					final ActionMap actMap = this.getActionMap();
					final KeyStroke ksRefresh = KeyStroke.getKeyStroke(KeyEvent.VK_S,
							InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, false);
					inMap.put(ksRefresh, "com.ontimize.scripting.gui.ScriptButton");
					actMap.put("com.ontimize.scripting.gui.ScriptButton", ((JButton) obj).getAction());
				}
			}
		} catch (final IllegalArgumentException e) {
			Form.logger.error(null, e);
		} catch (final ClassNotFoundException cE) {
			Form.logger.error(null, cE);
		} catch (final Exception e) {
			Form.logger.error(null, e);
		}
	}

	public void setDataFieldValueToFormCache(final Object attr, final Object value) {
		if (this.totalDataList != null) {
			List vDataListData = (List) this.totalDataList.get(attr);
			if (vDataListData == null) {
				vDataListData = new Vector();
				for (int i = 0; i < this.getFormCacheSize(); i++) {
					vDataListData.add(i, null);
				}
				if (vDataListData.size() <= this.currentIndex) {
					return;
				}
				vDataListData.set(this.currentIndex, value);
				this.totalDataList.put(attr, vDataListData);
			} else {
				for (int i = 0; i < this.getFormCacheSize(); i++) {
					vDataListData.add(i, null);
				}
				if (vDataListData.size() <= this.currentIndex) {
					return;
				}
				vDataListData.set(this.currentIndex, value);
			}
		}
	}

	public void updateDataListDataCurrentRecord(final Map attributesValues) {
		this.updateDataListDataCurrentRecord(attributesValues, true);
	}

	public void updateDataListDataCurrentRecord(final Map attributesValues, final boolean dataFieldRefresh) {
		// Replace data in the data list
		final Enumeration c = Collections.enumeration(attributesValues.keySet());
		if (this.setValuesOrder != null) {
			final Enumeration equivalence = Collections.enumeration(attributesValues.keySet());
			final Map hIdentifier = new Hashtable();
			while (equivalence.hasMoreElements()) {
				final Object current = equivalence.nextElement();
				hIdentifier.put(current.toString(), current);
			}

			for (int i = 0; i < this.setValuesOrder.size(); i++) {
				final Object oKey = this.setValuesOrder.get(i);
				final Object attribute = hIdentifier.get(oKey);
				if (attribute == null) {
					continue;
				}
				final Object oValue = attributesValues.get(attribute);
				if (oValue != null) {
					this.replaceDataListIfExistValue(dataFieldRefresh, attribute, oValue);
				}
			}
		}

		while (c.hasMoreElements()) {
			final Object oKey = c.nextElement();
			if ((this.setValuesOrder != null) && this.setValuesOrder.contains(oKey.toString())) {
				continue;
			}
			final Object oValue = attributesValues.get(oKey);
			if (oValue != null) {
				if (oValue instanceof List) {
					final List vResultData = (List) oValue;
					final List vDataListData = (List) this.totalDataList.get(oKey);
					if (vDataListData != null) {
						if (this.currentIndex < vDataListData.size()) {
							vDataListData.remove(this.currentIndex);
							vDataListData.add(this.currentIndex, vResultData.get(0));
							if (dataFieldRefresh) {
								this.setDataFieldValue(oKey, vResultData.get(0));
							}
						}
					}
				} else {
					if (this.totalDataList == null) {
						this.totalDataList = new Hashtable();
					}
					List vDataListData = (List) this.totalDataList.get(oKey);
					if (vDataListData != null) {
						if (this.currentIndex < vDataListData.size()) {
							vDataListData.remove(this.currentIndex);
							vDataListData.add(this.currentIndex, oValue);
							if (dataFieldRefresh) {
								this.setDataFieldValue(oKey, oValue);
							}
						}
					} else {
						if (this.currentIndex == 0) {
							vDataListData = new Vector();
							// since 5.2069EN
							// using vectorsize it is created a List with
							// correct dimensions
							for (int j = 0; j < this.vectorSize; j++) {
								vDataListData.add(null);
							}
							//
							this.totalDataList.put(oKey, vDataListData);
							vDataListData.add(this.currentIndex, oValue);
							if (dataFieldRefresh) {
								this.setDataFieldValue(oKey, oValue);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of
	 * {@link #updateDataListDataCurrentRecord(Hashtable, boolean)}
	 * @param dataFieldRefresh
	 * @param attribute
	 * @param oValue
	 */
	protected void replaceDataListIfExistValue(final boolean dataFieldRefresh, final Object attribute, final Object oValue) {
		if (oValue instanceof List) {
			final List vResultData = (List) oValue;
			final List vDataListData = (List) this.totalDataList.get(attribute);
			if (vDataListData != null) {
				if (this.currentIndex < vDataListData.size()) {
					vDataListData.remove(this.currentIndex);
					vDataListData.add(this.currentIndex, vResultData.get(0));
					if (dataFieldRefresh) {
						this.setDataFieldValue(attribute, vResultData.get(0));
					}
				}
			}
		} else {
			if (this.totalDataList == null) {
				this.totalDataList = new Hashtable();
			}
			List vDataListData = (List) this.totalDataList.get(attribute);
			if (vDataListData != null) {
				if (this.currentIndex < vDataListData.size()) {
					vDataListData.remove(this.currentIndex);
					vDataListData.add(this.currentIndex, oValue);
					if (dataFieldRefresh) {
						this.setDataFieldValue(attribute, oValue);
					}
				}
			} else {
				if (this.currentIndex == 0) {
					// since 5.2073EN
					// using vectorsize it is created a List with
					// correct dimensions
					vDataListData = new Vector(this.vectorSize);
					for (int j = 0; j < this.vectorSize; j++) {
						vDataListData.add(null);
					}
					this.totalDataList.put(attribute, vDataListData);
					vDataListData.set(this.currentIndex, oValue);
					if (dataFieldRefresh) {
						this.setDataFieldValue(attribute, oValue);
					}
				}
			}
		}
	}

	/**
	 * Disables the data field with this attribute
	 * @param attr attribute of data field to be disabled
	 */
	public void disableDataField(final Object attr) {
		final Object oField = this.dataComponentList.get(attr);
		if (oField != null) {
			((FormComponent) oField).setEnabled(false);
		}
	}

	/**
	 * Hides the data field which attribute is the entry parameter.
	 * @param attr a <code>Object</code> with the data field attribute that will be hidden
	 */

	public void hideDataField(final Object attr) {
		final DataComponent c = this.getDataFieldReference(attr.toString());
		if (c != null) {
			c.setVisible(false);
		}
	}

	/**
	 * Shows the data field which attribute is the entry parameter.
	 * @param attr a <code>Object</code> with the data field attribute that will be shown
	 */
	public void showDataField(final Object attr) {
		final DataComponent c = this.getDataFieldReference(attr.toString());
		if (c != null) {
			c.setVisible(true);
		}
	}

	/**
	 * Shows the button which key is the entry parameter.
	 * @param key a <code>String</code> with the button key that will be shown
	 */

	public void showButton(final String key) {
		final Button b = this.getButton(key);
		if (b != null) {
			b.setVisible(true);
		}
	}

	/**
	 * Hides the button which attribute is the entry parameter.
	 * @param key a <code>String</code> with the button key that will be hidden
	 */

	public void hideButton(final String key) {
		final Button b = this.getButton(key);
		if (b != null) {
			b.setVisible(false);
		}
	}

	/**
	 * Hides the element which attribute is the entry parameter.
	 * @param attr a <code>String</code> with the form component attribute that will be hidden
	 */

	public void hideElement(final String attr) {
		final FormComponent el = this.getElementReference(attr);
		if (el != null) {
			el.setVisible(false);
		}
	}

	/**
	 * Shows the element which attribute is the entry parameter.
	 * @param attr a <code>String</code> with form component attribute that will be hidden
	 */
	public void showElement(final String attr) {
		final FormComponent el = this.getElementReference(attr);
		if (el != null) {
			el.setVisible(true);
		}
	}

	/**
	 * Establishes whether data field which attributes is the entry parameter is required.
	 * @param attributes a <code>Object</code> with the data field attribute
	 * @param required true if the data field is required.
	 */
	public void setRequired(final Object attributes, final boolean required) {
		if (attributes instanceof String) {
			final Object oField = this.getDataFieldReference((String) attributes);
			if (oField != null) {
				((DataComponent) oField).setRequired(required);
			}
		} else {
			final Object oField = this.dataComponentList.get(attributes);
			if (oField != null) {
				((DataComponent) oField).setRequired(required);
			}
		}
	}

	/**
	 * Establishes whether all data components are modifiable.
	 * @param modif true if all data components are modifiable.
	 */
	public void setAllModificable(final boolean modif) {
		for (int i = 0; i < this.componentList.size(); i++) {
			final FormComponent cData = (FormComponent) this.componentList.get(i);
			if (cData instanceof DataComponent) {
				((DataComponent) cData).setModifiable(modif);
			}
		}
	}

	@Override
	public synchronized int print(final Graphics g, final PageFormat pf, final int page) {
		if (page > 0) {
			return Printable.NO_SUCH_PAGE;
		}
		try {
			((Graphics2D) g).translate(pf.getImageableX(), pf.getImageableY());
			this.bodyPanel.setVisible(false);
			this.bodyPanel.setSize((int) pf.getImageableWidth(), (int) pf.getImageableHeight());
			this.bodyPanel.validate();
			this.bodyPanel.paint(g);
			return Printable.PAGE_EXISTS;
		} catch (final Exception e) {
			Form.logger.trace(null, e);
			return Printable.NO_SUCH_PAGE;
		} finally {
			this.invalidate();
			this.validate();
			this.bodyPanel.setVisible(true);
		}
	}

	@Override
	public String getHelpIdString() {
		if (this.helpId != null) {
			return this.helpId;
		}
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		return className + "HelpId";
	}

	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			Form.logger.error(null, e);
			return;
		}
	}

	public static class FocusComparableComponent implements Comparable {

		Component c = null;

		Point p = null;

		public FocusComparableComponent(final Component c, final Point p) {
			this.c = c;
			this.p = p;
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public int compareTo(final Object o) {
			if (o instanceof FocusComparableComponent) {
				final int ax = this.p.x;
				final int ay = this.p.y;
				final int bx = ((FocusComparableComponent) o).p.x;
				final int by = ((FocusComparableComponent) o).p.y;

				if (Math.abs(ay - by) < 15) {
					if (ax == bx) {
						if (ay == by) {
							return 0;
						}
						return ay < by ? -1 : 1;
					} else {
						return ax < bx ? -1 : 1;
					}
				} else {
					return ay < by ? -1 : 1;
				}
				// if (Math.abs(this.p.y - ((FocusComparableComponent) o).p.y) <
				// 15) {
				// return (this.p.x - ((FocusComparableComponent) o).p.x);
				// }
				// return (this.p.y - ((FocusComparableComponent) o).p.y);
			}
			return 0;
		}

		@Override
		public String toString() {
			String s = this.c.getClass().toString();
			if (this.c instanceof IdentifiedElement) {
				s = s + ((IdentifiedElement) this.c).getAttribute();
			}
			s = s + this.p.toString();
			return s;
		}

		public Component getComponent() {
			return this.c;
		}

	}

	public void resetFocusOrder() {
		for (int i = 0; i < this.componentList.size(); i++) {
			if ((!((JComponent) this.componentList.get(i)).isRequestFocusEnabled())
					|| !(((JComponent) this.componentList.get(i)).isFocusTraversable())
					|| !((JComponent) this.componentList.get(i)).isFocusable()) {
				continue;
			}
			((JComponent) this.componentList.get(i)).setNextFocusableComponent(null);
		}
	}

	/**
	 * Finds all components into this <code>Container</code> and filles the <code>List</code> with this
	 * components.
	 * @param container Container where the components are found
	 * @param result List in which the components are stored
	 */
	protected void findComponentInContainer(final Container container, final java.util.List result) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			final Component c = container.getComponent(i);
			result.add(c);
			if (c instanceof Container) {
				this.findComponentInContainer((Container) c, result);
			}
		}
	}

	/**
	 * Finds visible components into this <code>Container</code> and filles the <code>List</code> with
	 * this components.
	 * @param container Container where the components are found
	 * @param result List in which the components are stored
	 */
	protected void findVisibleComponentInContainer(final Container container, final java.util.List result) {
		if (container.isVisible()) {
			for (int i = 0; i < container.getComponentCount(); i++) {
				final Component c = container.getComponent(i);
				if (c.isVisible()) {
					result.add(c);
					if (c instanceof Container) {
						this.findVisibleComponentInContainer((Container) c, result);
					}
				}
			}
		}
	}

	@Override
	public boolean isFocusCycleRoot() {
		return true;
	}

	/**
	 * Returns the list of focusable component references that are contained in the form body. This list
	 * is ordered depending on how the focus traverses components in the form body.
	 * @return a <code>Vector</code> with the focusable component references from the form body.
	 */

	protected java.util.List getComponentsInOrderFocusInBody() {
		try {
			final java.util.List focusOrderComponents = new java.util.ArrayList();
			final java.util.List componentList = new java.util.ArrayList();
			final Container c = this.bodyPanel;
			this.findVisibleComponentInContainer(c, componentList);

			for (int i = 0; i < componentList.size(); i++) {
				if (!((Component) componentList.get(i)).isFocusable()
						|| !((Component) componentList.get(i)).isFocusTraversable()
						|| !((Component) componentList.get(i)).isEnabled()
						|| !((Component) componentList.get(i)).isVisible()) {
					continue;
				}

				final FocusComparableComponent fc = new FocusComparableComponent((Component) componentList.get(i),
						this.getLocationInForm((Component) componentList.get(i)));
				focusOrderComponents.add(fc);
			}

			// TODO cambiar los FocusComparableComponent por el
			// PositionComparator
			Collections.sort(focusOrderComponents);
			final java.util.List focusOrderCompnentsList = new java.util.ArrayList();
			for (int i = 0; i < focusOrderComponents.size(); i++) {
				focusOrderCompnentsList.add(((FocusComparableComponent) focusOrderComponents.get(i)).c);

			}

			return focusOrderCompnentsList;
		} catch (final Exception e) {
			Form.logger.error("Form: Error initializing focus", e);
			return new ArrayList();
		}
	}

	/**
	 * Returns the list of focusable component references that are contained in the form. This list is
	 * ordered depending on how the focus traverses components in the form.
	 * @return a <code>Vector</code> with the focusable component references from the form.
	 */

	public java.util.List getComponentsInOrderFocus() {
		try {
			final java.util.List focusOrderComponents = new java.util.ArrayList();
			final java.util.List componentList = new java.util.ArrayList();
			final Container c = this;
			this.findVisibleComponentInContainer(c, componentList);

			for (int i = 0; i < componentList.size(); i++) {
				if (!((Component) componentList.get(i)).isFocusable()
						|| !((Component) componentList.get(i)).isFocusTraversable()
						|| !((Component) componentList.get(i)).isEnabled()
						|| !((Component) componentList.get(i)).isVisible()) {
					continue;
				}
				final FocusComparableComponent fc = new FocusComparableComponent((Component) componentList.get(i),
						this.getLocationInForm((Component) componentList.get(i)));
				focusOrderComponents.add(fc);
			}

			Collections.sort(focusOrderComponents);
			final java.util.List focusOrderComponentsComp = new java.util.ArrayList();
			for (int i = 0; i < focusOrderComponents.size(); i++) {
				focusOrderComponentsComp.add(((FocusComparableComponent) focusOrderComponents.get(i)).c);

			}

			return focusOrderComponentsComp;
		} catch (final Exception e) {
			Form.logger.error("Form: Error initializing focus", e);
			return null;
		}
	}

	/**
	 * Returns the list of component and label references that is contained in the form. This list is
	 * ordered depending on how the components and labels are displayed in the form starting by the form
	 * top
	 * @return a <code>Vector</code> with the component references from the form.
	 */

	protected List getComponentsAndLabelsListInOrderTopDown() {
		final List focusOrderComponents = new Vector();
		for (int i = 0; i < this.componentList.size(); i++) {
			if (!((this.componentList.get(i) instanceof DataComponent))
					&& !((this.componentList.get(i) instanceof Label))) {
				continue;
			}
			final FocusComparableComponent fc = new FocusComparableComponent((Component) this.componentList.get(i),
					this.getLocationInForm((Component) this.componentList.get(i)));
			focusOrderComponents.add(fc);
		}
		Collections.sort(focusOrderComponents);
		final List cData = new Vector();
		for (int i = 0; i < focusOrderComponents.size(); i++) {
			cData.add(((FocusComparableComponent) focusOrderComponents.get(i)).c);
		}
		return cData;
	}

	public static class FormButton extends RolloverButton {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		public static final String FORMBUTTON_NAME = "FormButton";

		public static Boolean defaultPaintFocus;

		public static Boolean defaultContentAreaFilled;

		public static Boolean defaultCapable;

		Dimension dimension = new Dimension(Form.defaultFormButtonSize, Form.defaultFormButtonSize);

		public FormButton(final String text) {
			super(text);
		}

		public FormButton() {
			super();
		}

		@Override
		public String getName() {
			return FormButton.FORMBUTTON_NAME;
		}

		public FormButton(final Icon icon) {
			super(icon);
		}

		@Override
		public boolean isDefaultCapable() {
			if (FormButton.defaultCapable != null) {
				return FormButton.defaultCapable.booleanValue();
			}
			return false;
		}

		@Override
		public Dimension getMinimumSize() {
			return this.dimension;
		}

		@Override
		public Dimension getPreferredSize() {
			return this.dimension;
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (FormButton.defaultContentAreaFilled != null) {
				super.setContentAreaFilled(FormButton.defaultContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

		@Override
		public void setFocusPainted(final boolean focusable) {
			if (FormButton.defaultPaintFocus != null) {
				super.setFocusable(FormButton.defaultPaintFocus.booleanValue());
				return;
			}
			super.setFocusable(focusable);
		}

	}

	/**
	 * Prints the default template with the data from the form data components.
	 */

	public void printingFormWithDefaultTemplate() throws Exception {
		final XMLTemplateBuilder cons = new XMLTemplateBuilder(this.formManager.getLabelFileURI());
		final TemplateElement p = cons.buildTemplate(this.getDefaultXMLTemplate());
		final List cData = this.getDataComponents();
		for (int i = 0; i < cData.size(); i++) {
			final Object c = cData.get(i);
			if (c instanceof ImageDataField) {
				final Object attr = ((ImageDataField) c).getAttribute();
				final PrintingElement ei = p.getPrintingElement(attr.toString());
				if ((ei != null) && !((ImageDataField) c).isEmpty()) {
					ei.setContent(((ImageDataField) c).getImage());
				} else if (ei != null) {
					ei.setContent(((ImageDataField) c).getEmptyImage());
				}
			}
		}
		p.preview(this.parentFrame);
	}

	/**
	 * Returns the xml of the default template. This xml is created using the information obtains of the
	 * component list displayed in the form.
	 * <p>
	 * This method is used when the template parameter isn't defined and then the form needs create its
	 * xml template.
	 * @return a <code>StringBuilder</code> with the template xml
	 */
	public StringBuffer getDefaultXMLTemplate() {

		final int pageWidth = 158;
		final int pageHeight = 247;

		final List cData = this.getComponentsAndLabelsListInOrderTopDown();
		final String color = "208;210;232";
		final StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		sb.append("<TemplateElement>");
		sb.append("<RectangleElement id=\"cabecera\" xi=\"1\" yi=\"1\" width=\"158\" height=\"7\" bgcolor=\"" + color
				+ "\"/>");
		sb.append(
				"<PrintingLabel id=\"etcabecera\" xi=\"1\" yi=\"1\" width=\"158\" height=\"7\" align=\"center\" text=\"");
		sb.append(ApplicationManager.getTranslation(Form.FORM_DATA));
		sb.append("\" fontsize=\"14\" bold=\"yes\" />");

		final Map param = new Hashtable();
		param.put("fontsize", "10");
		final com.ontimize.printing.TextArea textArea = new com.ontimize.printing.TextArea(param);
		final Font f = textArea.getTextArea().getFont();
		final FontMetrics fontMetrics = textArea.getTextArea().getFontMetrics(f);
		int lastX = 1;
		int lastY = 9;
		int lastRowHeight = 0;

		for (int i = 0; i < cData.size(); i++) {

			int milimetersX = 0;
			int milimetersY = 0;

			final Object c = cData.get(i);
			if (c instanceof DataField) {
				if (!((DataField) c).isVisible()) {
					continue;
				}
				final String sLabel = ((DataField) c).getLabelComponentText();
				if ((((DataField) c).isLabelVisible()) && (sLabel != null)) {

					final int widthLabelPixels = fontMetrics.stringWidth(sLabel + ": ");
					final int mmLabel = AbstractPrintingElement.pagePixelsToMillimeters(widthLabelPixels);

					int pixelWidth = 0;
					String sFieldText = ((DataField) c).getText();
					if (sFieldText != null) {
						sFieldText = sFieldText.trim();
					}
					if ((sFieldText == null) || sFieldText.equals("")) {
						sFieldText = "Not found";
					}

					pixelWidth = fontMetrics.stringWidth(sFieldText);

					sFieldText = sFieldText.replaceAll("&", "&amp;");
					sFieldText = sFieldText.replaceAll("\"", "&quot;");
					sFieldText = sFieldText.replaceAll("<", "&lt;");
					sFieldText = sFieldText.replaceAll(">", "&gt;");
					sFieldText = sFieldText.replaceAll("'", "&#39;");
					sFieldText = sFieldText.replaceAll("•", "&#183;");
					if (c instanceof ImageDataField) {
						pixelWidth = ((ImageDataField) c).getDataField().getWidth();
					}
					int width = AbstractPrintingElement.pagePixelsToMillimeters(pixelWidth);
					width += 1;

					final int pixelHeight = ((DataField) c).getDataField().getPreferredSize().height;
					int height = AbstractPrintingElement.pagePixelsToMillimeters(pixelHeight);

					height += 1;
					if (height < 5) {
						height = 5;
					}
					milimetersY = lastY;
					lastRowHeight = Math.max(height, lastRowHeight);
					// If the las x is greater than the page width then put it
					// in the
					// next page
					milimetersX = lastX + 2;

					if ((milimetersX + mmLabel + width) > pageWidth) {
						// If it is at the beginning of the line then change the
						// size
						if (milimetersX < 5) {
							width = pageWidth - milimetersX - mmLabel;
						} else {
							// If in the beginning of the line there are no
							// space then
							// change the text area
							milimetersX = 2;
							if ((milimetersX + width) > (pageWidth - 4)) {
								double relation = (width + milimetersX) / (pageWidth - 4);
								width = pageWidth - milimetersX - 2;
								// Calculates the width to fit the text
								relation = relation + 1;
								final int rel = (int) relation;
								height = height * rel;
							}
							milimetersY = milimetersY + lastRowHeight;
							lastRowHeight = height;

						}
					}

					sb.append("<TextArea xi=\"" + milimetersX + "\" yi=\"" + milimetersY + "\" width=\"" + mmLabel
							+ "\" height=\"8\" id=\"Et" + i
							+ "\" color=\"blue\" bold=\"yes\" fontsize=\"10\" text=\"" + sLabel + ":\"/>");
					if (c instanceof ImageDataField) {
						sb.append("<PrintingImage xi=\"" + (milimetersX + mmLabel) + "\" yi=\"" + milimetersY
								+ "\" width=\"" + width + "\" height=\"" + height + "\" id=\""
								+ ((DataField) c).getAttribute() + "\" fontsize=\"10\"     />");
					} else {
						if (sFieldText.equals("Not found")) {
							sb.append("<TextArea xi=\"" + (milimetersX + mmLabel) + "\" yi=\"" + milimetersY
									+ "\" width=\"" + width + "\" height=\"" + height + "\" id=\"Field" + i
									+ "\" fontsize=\"10\" italic=\"yes\"  text=\"" + sFieldText
									+ "\"  color=\"red\" />");
						} else {
							sb.append("<TextArea xi=\"" + (milimetersX + mmLabel) + "\" yi=\"" + milimetersY
									+ "\" width=\"" + width + "\" height=\"" + height + "\" id=\"Field" + i
									+ "\" fontsize=\"10\" italic=\"yes\"  text=\"" + sFieldText + "\"  />");
						}
					}
					lastX = milimetersX + mmLabel + width;
					lastY = milimetersY;
				} else {
					int pixWidth = 0;
					String fieldText = ((DataField) c).getText();
					if (fieldText != null) {
						fieldText = fieldText.trim();
					}
					if ((fieldText == null) || fieldText.equals("")) {
						fieldText = "Not found";
					}
					pixWidth = fontMetrics.stringWidth(fieldText);

					fieldText = fieldText.replaceAll("&", "&amp;");
					fieldText = fieldText.replaceAll("\"", "&quot;");
					fieldText = fieldText.replaceAll("<", "&lt;");
					fieldText = fieldText.replaceAll(">", "&gt;");
					fieldText = fieldText.replaceAll("'", "&#39;");
					fieldText = fieldText.replaceAll("•", "&#183;");
					if (c instanceof ImageDataField) {
						pixWidth = ((ImageDataField) c).getDataField().getWidth();
					}
					int width = AbstractPrintingElement.pagePixelsToMillimeters(pixWidth);
					width += 2;

					final int pixHeight = ((DataField) c).getDataField().getPreferredSize().height;
					int height = AbstractPrintingElement.pagePixelsToMillimeters(pixHeight);
					if (width > (pageWidth - 2)) {
						double relation = width / (pageWidth - 2);
						width = pageWidth - 2;
						// Calculates the height to fit the text
						relation = relation + 0.5;
						final int rel = (int) relation;
						height = height * rel;
					}

					height += 1;
					if (height < 5) {
						height = 5;
					}

					milimetersY = lastY;
					lastRowHeight = Math.max(height, lastRowHeight);
					milimetersX = lastX + 2;
					if ((milimetersX + width) > pageWidth) {
						if (milimetersX < 5) {
							width = pageWidth - milimetersX;
						} else {
							milimetersX = 2;
							if ((milimetersX + width) > (pageWidth - 4)) {
								double relation = (width + milimetersX) / (pageWidth - 4);
								width = pageWidth - milimetersX - 2;
								relation = relation + 1;
								final int rel = (int) relation;
								height = height * rel;
							}
							milimetersY = milimetersY + lastRowHeight;
							lastRowHeight = height;

						}
					}

					sb.append(this.reduceComplexity(i, milimetersX, milimetersY, c, fieldText, width, height));
					lastX = milimetersX + width;
					lastY = milimetersY;
				}

			}

			else if (c instanceof Label) {
				String sLabel = ((Label) c).getText();
				final int labelWidthPixels = fontMetrics.stringWidth(sLabel + "  ");

				sLabel = sLabel.replaceAll("&", "&amp;");
				sLabel = sLabel.replaceAll("\"", "&quot;");
				sLabel = sLabel.replaceAll("<", "&lt;");
				sLabel = sLabel.replaceAll(">", "&gt;");
				sLabel = sLabel.replaceAll("'", "&#39;");
				sLabel = sLabel.replaceAll("•", "&#183;");
				int mmLabel = AbstractPrintingElement.pagePixelsToMillimeters(labelWidthPixels);
				final int pixelsWidth = AbstractPrintingElement.pagePixelsToMillimeters(((Label) c).getWidth());
				mmLabel = Math.max(pixelsWidth, mmLabel);
				mmLabel += 1;
				int width = 0;
				width = mmLabel;
				width += 1;

				if (width > (pageWidth - 2)) {
					width = pageWidth - 2;
				}

				final int iHeight = 6;

				milimetersY = lastY;
				lastRowHeight = Math.max(iHeight, lastRowHeight);
				milimetersX = lastX + 2;
				if ((milimetersX + width) > pageWidth) {
					if (milimetersX < 5) {
						width = pageWidth - milimetersX - mmLabel;
					} else {
						milimetersX = 2;
						milimetersY = milimetersY + lastRowHeight;
						lastRowHeight = iHeight;

					}
				}

				sb.append("<TextArea xi=\"" + milimetersX + "\" yi=\"" + milimetersY + "\" width=\"" + width
						+ "\" height=\"" + iHeight + "\" id=\"Etiqueta" + i
						+ "\" color=\"blue\" fontsize=\"10\" text=\"" + sLabel + "\" bold=\"yes\" />");

				lastX = milimetersX + width;
				lastY = milimetersY;
			}
		}

		sb.append("<LineElement id=\"lpie\" xi=\"1\" yi=\"" + (pageHeight - 4) + "\" xf=\"158\" yf=\""
				+ (pageHeight - 4) + "\" weight=\"1\" />");
		final java.util.Date fAct = new java.util.Date();
		final SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT);
		df.applyPattern("dd/MM/yyyy");
		final String fecha = df.format(fAct);
		sb.append("<PrintingLabel id=\"etpie\" xi=\"1\" yi=\"" + (pageHeight - 4)
				+ "\" width=\"158\" height=\"5\" align=\"right\" text=\"" + fecha
				+ "\" fontsize=\"8\" italic=\"yes\" />");

		sb.append("</TemplateElement>");
		Form.logger.error("default XML Template: {}", sb);
		return sb;

	}

	/**
	 * Method used to reduce the complexity of {@link #getDefaultXMLTemplate()}
	 * @param sb
	 * @param i
	 * @param milimetersX
	 * @param milimetersY
	 * @param c
	 * @param fieldText
	 * @param width
	 * @param height
	 */
	protected String reduceComplexity(final int i, final int milimetersX, final int milimetersY, final Object c, final String fieldText, final int width,
			final int height) {
		final StringBuilder sb = new StringBuilder();
		if (c instanceof ImageDataField) {
			sb.append("<PrintingImage xi=\"" + milimetersX + "\" yi=\"" + milimetersY + "\" width=\"" + width
					+ "\" height=\"" + height + "\" id=\""
					+ ((DataField) c).getAttribute() + "\" fontsize=\"10\"     />");
		} else {
			if (fieldText.equals("Not found")) {
				sb.append("<TextArea xi=\"" + milimetersX + "\" yi=\"" + milimetersY + "\" width=\"" + width
						+ "\" height=\"" + height + "\" id=\"Field" + i
						+ "\" fontsize=\"10\" italic=\"yes\"  text=\"" + fieldText + "\" color=\"red\" />");
			} else {
				sb.append("<TextArea xi=\"" + milimetersX + "\" yi=\"" + milimetersY + "\" width=\"" + width
						+ "\" height=\"" + height + "\" id=\"Field" + i
						+ "\" fontsize=\"10\" italic=\"yes\"  text=\"" + fieldText + "\"  />");
			}
		}

		return sb.toString();
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Returns the key of the query button.
	 * @return a <code>String</code> with the key of the query button
	 */
	public String getQueryButtonKey() {
		return this.f7Button;
	}

	/**
	 * Returns the key of the insert button.
	 * @return a <code>String</code> with the key of the insert button
	 */

	public String getInsertButtonKey() {
		return this.f9Button;
	}

	/**
	 * Returns the key of the update button.
	 * @return a <code>String</code> with the key of the update button
	 */
	public String getUpdateButtonKey() {
		return this.f11Button;
	}

	/**
	 * Returns the key of the delete button.
	 * @return a <code>String</code> with the key of the delete button
	 */
	public String getDeleteButtonKey() {
		return this.f12Button;
	}

	/**
	 * Sets the entity name associated with this form
	 * @param name a <code>String</code> with the entity name
	 */
	public void setEntityName(final String name) {
		this.entityName = name;
	}

	public void resetEntityName() {
		this.entityName = this.xmlEntityName.toString();
	}

	/**
	 * Appends the specified column list to the default column list of the table view. The default
	 * column list are established in the xml form definition with the 'column' parameter
	 * @param cols the column list to be added
	 */

	public void addColumnsToTableView(final List cols) {
		this.resetTableViewColumns();
		this.additionalTableViewColumns = cols;
		if (this.table != null) {
			for (int i = 0; i < cols.size(); i++) {
				if ((cols.get(i) != null) && !this.table.getAttributeList().contains(cols.get(i))) {
					this.table.addColumn(cols.get(i).toString());
				}
			}
			this.table.setVisibleColumns(cols);
		}
	}

	/**
	 * Delete all column that aren't default column from colum list of the table view
	 */
	public void resetTableViewColumns() {
		if ((this.table != null) && (this.additionalTableViewColumns != null)) {
			for (int i = 0; i < this.additionalTableViewColumns.size(); i++) {
				if ((this.additionalTableViewColumns.get(i) != null)
						&& !this.tableViewColumns.contains(this.additionalTableViewColumns.get(i))) {
					this.table.deleteColumn(this.additionalTableViewColumns.get(i).toString());
				}
			}
		}
	}

	/**
	 * Returns a array of buttons with all buttons from the form.
	 * @return a array of buttons
	 */

	public Button[] getButtons() {
		final int n = this.buttonList.size();
		final Button[] buttons = new Button[n];
		final Collection bots = this.buttonList.values();
		final Object[] b = bots.toArray();
		for (int i = 0; i < n; i++) {
			buttons[i] = (Button) b[i];
		}
		return buttons;
	}

	/**
	 * Disables all hiperlink components
	 */
	public void disableHyperlinkComponents() {
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof HyperlinkComponent) {
				((FormComponent) this.componentList.get(i)).setEnabled(false);
			}
		}
	}

	/**
	 * Enables all hiperlink components
	 */
	public void enableHyperlinkComponents() {
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof HyperlinkComponent) {
				((FormComponent) this.componentList.get(i)).setEnabled(true);
			}
		}
	}

	/**
	 * Appends the specified component to the control panel. The specified components are inserted
	 * before table view button in the control panel.
	 * @param c the component to be added
	 */
	public void addControls(final JComponent c) {
		this.newButtonPanel.add(c,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
	}

	/**
	 * Returns the list of component references that contains the form.
	 * @return a <code>List</code> with the component references from the form.
	 */
	public java.util.List getComponentList() {
		final List list = new ArrayList();
		for (int i = 0; i < this.componentList.size(); i++) {
			list.add(i, this.componentList.get(i));
		}
		return list;
	}

	/**
	 * Returns whether is this Form should use a modified focus order.
	 * @return true if this form use modified focus order, otherwise false
	 */
	public boolean isCustomFocusEnabled() {
		return this.useModifiedFocusOrder;
	}

	/**
	 * Prints the template which location is defined in templateURI parameter. The data for filling the
	 * template is obtained from the form data fields and one table if the entity parameter has been
	 * defined.
	 * @param templateURI <code>URI</code> in where the template is
	 * @param entity entity name of the table to print; null if there isn't table to print
	 * @param title
	 */
	public void printTemplate(final String templateURI, final String entity, final String title) throws Exception {
		this.printTemplate(templateURI, entity, title, true);
	}

	/**
	 * Prints the template which location is defined in templateURI parameter. The data for filling the
	 * template is obtained from the form data fields and one table if the entity parameter has been
	 * defined.
	 * @param templateURI <code>URI</code> in where the template is
	 * @param entity entity name of the table to print; null if there isn't table to print
	 * @param title
	 * @param texts true use the {@link #getDataFieldText} method for obtain the data; false use the
	 *        {@link #getDataFieldValues} method.
	 */

	public void printTemplate(final String templateURI, final String entity, final String title, final boolean texts) throws Exception {
		if (!com.ontimize.report.ReportManager.isReportsEnabled()) {
			throw new IllegalArgumentException(this.getClass().toString() + " -> Reports not enabled");
		}
		final URL templateURL = this.getClass().getClassLoader().getResource(templateURI);
		if (templateURL == null) {
			throw new IllegalArgumentException(
					this.getClass().toString() + " -> template hasn't been found : " + templateURI);
		}
		Map hFieldsValues = null;
		if (texts) {
			hFieldsValues = this.getDataFieldText();
		} else {
			hFieldsValues = this.getDataFieldValues(false, true);
		}

		if (hFieldsValues.isEmpty()) {
			throw new IllegalArgumentException(this.getClass().toString() + "-> data fields are empty");
		}
		EntityResult res2 = null;
		if (entity != null) {
			final Object oTableValue = this.getDataFieldValue(entity);
			if ((oTableValue instanceof Map) && !((EntityResult) oTableValue).isEmpty()) {
				final EntityResult reAux = new EntityResultMapImpl();
				final Enumeration enumKeys = Collections.enumeration(((Map) oTableValue).keySet());
				while (enumKeys.hasMoreElements()) {
					Object oKey = enumKeys.nextElement();
					final Object oValue = ((Map) oTableValue).get(oKey);
					oKey = entity + "." + oKey;
					reAux.put(oKey, oValue);
				}
				res2 = reAux;
			}

		}

		if (res2 == null) {
			res2 = new EntityResultMapImpl();
		}
		final EntityResult combinedResult = EntityResultUtils.combine(hFieldsValues, res2);
		Form.logger.info("CombinedResultSet: {}", combinedResult);
		final TableModel tm = EntityResultUtils.createTableModel(combinedResult);
		String trad = null;
		if (title != null) {
			trad = ApplicationManager.getTranslation(title, this.resourcesFile);
		}
		ReportUtils.showPreviewDialog(this, trad, tm, templateURL, null);
	}

	/**
	 * Gets the <code>JScrollPane</code> object when the body form is contained in. If the 'scroll'
	 * parameter in the xml description is established to 'no' the scrollbars are never showed
	 * @return
	 * @see #init
	 */

	public JScrollPane getScrollPane() {
		return this.scrollPanel;
	}

	protected class SelColumnsDialog extends JDialog implements Internationalization {

		private final JList columnJList = new JList();

		private List columnList = null;

		private List defaultColumnList = null;

		private final JButton bOK = new JButton(ImageManager.getIcon(ImageManager.OK));

		private final JButton bCancel = new JButton(ImageManager.getIcon(ImageManager.CANCEL));

		private final JButton bSavePreference = new JButton(ImageManager.getIcon(ImageManager.SAVE_DISC));

		private class SelectableItemsListCellRenderer extends JCheckBox implements ListCellRenderer {

			public SelectableItemsListCellRenderer() {
				this.setBorderPaintedFlat(true);
			}

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int r, final boolean selected,
					final boolean focus) {

				final Color selBgColor = UIManager.getColor("List.selectionBackground");
				final Color selFgColor = UIManager.getColor("List.selectionForeground");

				final Color noSelFg = UIManager.getColor("List.foreground");
				final Color noSelBg = UIManager.getColor("List.background");

				if (selected) {
					this.setForeground(selFgColor);
					this.setBackground(selBgColor);
				} else {
					this.setForeground(noSelFg);
					this.setBackground(noSelBg);
				}

				if (value instanceof SelectableItem) {
					this.setText(((SelectableItem) value).toString());
					final boolean isSelected = ((SelectableItem) value).isSelected();
					this.setSelected(isSelected);
					if (((SelectableItem) value).isDefaultColumn()) {
						this.setForeground(Color.RED);
					}
				}

				return this;
			}

		}

		private class ColumnSelectionListener extends MouseAdapter {

			private int[] removeValue(final int value) {
				final List l = new ArrayList();

				final int[] out = new int[l.size()];
				for (int i = 0, a = l.size(); i < a; i++) {
					out[i] = ((Integer) l.get(i)).intValue();
				}
				return out;
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.getModifiers() == InputEvent.META_MASK) {
					return;
				}
				if (e.getX() > 30) {
					return;
				}
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					final int index = ((JList) e.getComponent()).locationToIndex(e.getPoint());
					if (index < 0) {
						return;
					}
					final SelectableItem it = (SelectableItem) ((JList) e.getComponent()).getModel().getElementAt(index);
					final boolean isSelected = !it.isSelected();

					it.setSelected(isSelected);

					final Rectangle rect = ((JList) e.getComponent()).getCellBounds(index, index);
					((JList) e.getComponent()).repaint(rect);
				} catch (final Exception ex) {
					Form.logger.error(null, ex);
				} finally {
					e.getComponent().setCursor(Cursor.getDefaultCursor());

				}
			}

		};

		private class TranslatedItem implements Internationalization {

			protected String text = "";

			protected String translatedText = null;

			protected ResourceBundle res = null;

			public TranslatedItem(final String text, final ResourceBundle res) {
				this.text = text;
				this.translatedText = text;
				this.setResourceBundle(res);
			}

			@Override
			public void setResourceBundle(final ResourceBundle res) {
				this.res = res;
				if (res != null) {
					try {
						this.translatedText = res.getString(this.text);
					} catch (final Exception e) {
						Form.logger.error(null, e);
						this.translatedText = this.text;
					}
				}
			}

			@Override
			public void setComponentLocale(final Locale l) {
			}

			@Override
			public List getTextsToTranslate() {
				return null;
			}

			@Override
			public String toString() {
				return this.translatedText;
			}

			public String getText() {
				return this.text;
			}

			@Override
			public int hashCode() {
				return this.text.hashCode();
			}

			@Override
			public boolean equals(final Object o) {
				if (this == o) {
					return true;
				} else if (o instanceof SelectableItem) {
					if (this.text.equals(((SelectableItem) o).getText())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}

		}

		protected class SelectableItem extends TranslatedItem implements Internationalization, Comparator, Comparable {

			protected boolean selected = false;

			protected boolean defaultColumn = false;

			public SelectableItem(final String text, final boolean df, final ResourceBundle res) {
				super(text, res);
				this.defaultColumn = df;
			}

			public boolean isSelected() {
				return this.selected;
			}

			@Override
			public void setResourceBundle(final ResourceBundle res) {
				super.setResourceBundle(res);
			}

			public void setSelected(final boolean sel) {
				this.selected = sel;
			}

			public boolean isDefaultColumn() {
				return this.defaultColumn;
			}

			@Override
			public String toString() {
				if (!this.isSelected()) {
					return this.translatedText;
				}
				return this.translatedText;
			}

			@Override
			public int compareTo(final Object o) {
				if (!(o instanceof SelectableItem)) {
					return -1;
				} else {
					final SelectableItem item = (SelectableItem) o;
					return item.translatedText.compareTo(this.translatedText);
				}
			}

			@Override
			public boolean equals(final Object o) {
				return super.equals(o);
			}

			@Override
			public int hashCode() {
				return super.hashCode();
			}

			@Override
			public int compare(final Object o1, final Object o2) {
				final SelectableItem item1 = (SelectableItem) o1;
				final SelectableItem item2 = (SelectableItem) o2;
				return item2.compareTo(item1);
			}

		};

		public SelColumnsDialog(final Frame f, final List l, final List defaultValue, final ResourceBundle res) {
			super(f, "form.selection_of_columns_for_tabular_view", true);
			this.columnList = l;
			this.defaultColumnList = defaultValue;
			this.updateModel(l, defaultValue, res);
			this.init(res);
		}

		public SelColumnsDialog(final Dialog f, final List l, final List defaultValue, final ResourceBundle res) {
			super(f, "form.selection_of_columns_for_tabular_view", true);
			this.columnList = l;
			this.defaultColumnList = defaultValue;
			this.updateModel(l, defaultValue, res);
			this.init(res);
		}

		protected void init(final ResourceBundle res) {
			final JPanel pB = new JPanel();
			pB.add(this.bOK);
			pB.add(this.bCancel);
			this.getContentPane().add(pB, BorderLayout.SOUTH);
			this.getContentPane().add(new JScrollPane(this.columnJList));
			this.columnJList.setVisibleRowCount(10);
			this.setTitle(ApplicationManager.getTranslation("form.selection_of_columns_for_tabular_view", res));
			this.bOK.setText(ApplicationManager.getTranslation("application.accept", res));
			this.bCancel.setText(ApplicationManager.getTranslation("application.cancel", res));
			this.pack();
			ApplicationManager.center(this);
			this.bCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SelColumnsDialog.this.setVisible(false);
				}
			});
			this.bOK.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final List l = SelColumnsDialog.this.selectionColumns();
					Form.this.saveTableViewPreferenceForm(l);
				}
			});

			this.bSavePreference.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final List l = SelColumnsDialog.this.selectionColumns();
					Form.this.saveTableViewPreferenceForm(l);
				}
			});
			this.columnJList.addMouseListener(new ColumnSelectionListener());
			this.columnJList.setCellRenderer(new SelectableItemsListCellRenderer());
		}

		protected List selectionColumns() {
			final List l = new Vector();
			for (int i = 0; i < this.columnJList.getModel().getSize(); i++) {
				if (((SelectableItem) this.columnJList.getModel().getElementAt(i)).isSelected()) {
					l.add(((SelectableItem) this.columnJList.getModel().getElementAt(i)).getText());
				}
			}
			Form.this.addColumnsToTableView(l);
			this.setVisible(false);
			return l;
		}

		protected void updateModel(final List l, final List lDef, final ResourceBundle res) {
			final DefaultListModel m = new DefaultListModel();
			final List order = new Vector();
			try {
				boolean d = false;
				for (int i = 0; i < l.size(); i++) {
					if ((lDef != null) && lDef.contains(l.get(i))) {
						d = true;
					}
					order.add(i, new SelectableItem(l.get(i).toString(), d, res));
					d = false;
				}

				// Fields are in a different order than the columns
				if (lDef != null) {
					for (int j = 0; j < lDef.size(); j++) {
						if (!l.contains(lDef.get(j))) {
							order.add(new SelectableItem(lDef.get(j).toString(), true, res));
						}
					}
				}

				Collections.sort(order);
				for (int i = order.size() - 1; i >= 0; i--) {
					m.add(order.size() - i - 1, order.get(i));
				}
			} catch (final Exception ex) {
				Form.logger.error(null, ex);
			}
			this.columnJList.setModel(m);
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

		@Override
		public void setResourceBundle(final ResourceBundle resources) {
			this.updateModel(this.columnList, this.defaultColumnList, resources);
			this.setTitle(ApplicationManager.getTranslation("form.selection_of_columns_for_tabular_view", resources));
			this.bOK.setText(ApplicationManager.getTranslation("application.accept", resources));
			this.bCancel.setText(ApplicationManager.getTranslation("application.cancel", resources));
			this.bSavePreference
			.setToolTipText(ApplicationManager.getTranslation(Form.TIP_TABLEVIEW_SAVEPREFERENCE, resources));
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		public void setSelected(final java.util.List v) {
			final DefaultListModel m = (DefaultListModel) this.columnJList.getModel();
			try {
				for (int i = 0; i < m.size(); i++) {
					final SelectableItem item = (SelectableItem) m.get(i);
					if ((v != null) && v.contains(item.getText())) {
						item.setSelected(true);
					} else {
						item.setSelected(false);
					}
				}
				this.columnJList.repaint();
			} catch (final Exception ex) {
				Form.logger.error(null, ex);
			}
		}

	}

	/**
	 * The dialog where the visible columns are selected in
	 */
	protected SelColumnsDialog selColumnsDialog = null;

	/**
	 * Shows a dialog in where the user can select the visible columns from TableViewTable
	 */

	public void selectTableViewColumns() {
		if (this.table == null) {
			this.createTableViewTable();
		}

		final List visibleComponentsList = this.getVisibleDataComponentAttributes();
		final List visibleTableViewColumns = this.getVisibleComponentsAttributes(this.tableViewColumns,
				visibleComponentsList);
		final List visibleAdditionalTableViewColumns = this.getVisibleComponentsAttributes(this.additionalTableViewColumns,
				visibleComponentsList);

		if (this.selColumnsDialog == null) {
			final Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof Dialog) {
				this.selColumnsDialog = new SelColumnsDialog((Dialog) w, this.getVisibleDataComponentAttributes(),
						visibleTableViewColumns, this.resourcesFile);
			} else if (w instanceof Frame) {
				this.selColumnsDialog = new SelColumnsDialog((Frame) w, this.getVisibleDataComponentAttributes(),
						visibleTableViewColumns, this.resourcesFile);
			} else {
				this.selColumnsDialog = new SelColumnsDialog((Frame) null, this.getVisibleDataComponentAttributes(),
						visibleTableViewColumns, this.resourcesFile);
			}
		}

		this.selColumnsDialog.setResourceBundle(this.resourcesFile);
		if (visibleAdditionalTableViewColumns != null) {
			this.selColumnsDialog.setSelected(visibleAdditionalTableViewColumns);
		} else {
			this.selColumnsDialog.setSelected(visibleTableViewColumns);
		}

		this.selColumnsDialog.setVisible(true);
	}

	protected List getVisibleComponentsAttributes(final List columnsVector, final List visibleComponentsAttrList) {
		final List toRet = new Vector();
		for (final Object componentAttr : visibleComponentsAttrList) {
			if (columnsVector.contains(componentAttr)) {
				toRet.add(componentAttr);
			}
		}
		return toRet;

	}

	/**
	 * Returns a list of all visible components that are displayed in this form.
	 * @return a <code>ArrayList</code> with all visible components
	 */
	protected List getVisibleDataComponentAttributes() {
		final List l = new ArrayList();
		final List v = this.getDataComponents();
		for (int i = 0; i < v.size(); i++) {
			final DataComponent cD = (DataComponent) v.get(i);
			if (cD == null) {
				continue;
			}
			if (cD instanceof DataField) {

				if (!((DataField) cD).isVisible()) {
					continue;
				}

				if (!cD.isHidden()) {
					if (((cD instanceof ReferenceExtDataField) || (cD instanceof ReferenceComboDataField))
							&& !this.tableViewColumns.contains(cD.getAttribute())) {
						continue;
					}
					l.add(((DataComponent) v.get(i)).getAttribute());
				}

			}
		}
		return l;
	}

	/**
	 * The {@link SubForm} where the form is contained in
	 */
	protected SubForm subForm = null;

	/**
	 * Returns the {@link SubForm} component if this form is contained in one of them
	 * @return a {@link SubForm} object
	 */
	public SubForm getSubForm() {
		if (this.subForm == null) {
			final Container c = this.getParent();
			if (c instanceof SubForm) {
				this.subForm = (SubForm) c;
			}
		}
		return this.subForm;
	}

	/**
	 * Gets the permission button if exists
	 * @return
	 */
	public JButton getPermissionButton() {
		return this.permissionButton;
	}

	public JButton getDatabaseBundleButton() {
		return this.databaseBundleButton;
	}

	public JButton getScriptButton() {
		return this.scriptButton;
	}

	public StatusBar getStatusBar() {
		return this.statusBar;
	}

	public JPanel getButtonPanel() {
		return this.buttonPanel;
	}

	/**
	 * Set all navigation buttons and count as disabled, when a record in a filtered table change its
	 * value, but still continue showing if the table is not filtered. Only to prevent register change.
	 */
	public void disableNavigationButtons() {
		this.startButton.setEnabled(false);
		this.previousButton.setEnabled(false);
		this.nextButton.setEnabled(false);
		this.endButton.setEnabled(false);
		this.resultCountLabel.setText("");
	}

	/**
	 * Checks whether exist data fields that haven't been queried
	 * @return true exist not queried data fields; false otherwise.
	 */
	public List getNotQueriedDataFieldAttributes() {
		final List allAttributes = new Vector();
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof TabPanel) {
				final List v = ((TabPanel) this.componentList.get(i)).getNotQueriedDataFieldAttributes();
				allAttributes.addAll(v);
			}
		}
		return allAttributes;
	}

	public void clearNotQueriedTabs() {
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof TabPanel) {
				((TabPanel) this.componentList.get(i)).clearNotQueriedTabs();
			}
		}
	}

}
