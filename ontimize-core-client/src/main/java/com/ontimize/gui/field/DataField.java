package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.HasHelpIdComponent;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.RolloverButton;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.ApplicationPreferencesListener;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.preferences.PreferenceEvent;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.security.ApplicationPermission;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.border.SoftButtonBorder;
import com.ontimize.util.swing.layout.AbsoluteConstraints;
import com.ontimize.util.swing.layout.AbsoluteLayout;

/**
 * Abstract class to implement a component for data introduction. It is formed by a label and an
 * insert data component.
 * <p>
 *
 * @author Imatia Innovation
 */
public abstract class DataField extends JPanel
implements DataComponent, AccessForm, HasHelpIdComponent, ApplicationPreferencesListener,
HasPreferenceComponent, ValueChangeDataComponent, IDefaultValueComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(DataField.class);

	protected String defaultValue;

	/**
	 * The attribute key.
	 */
	public static final String DEFAULT_VALUE = "defaultvalue";

	/**
	 * The default top margin. By default, 2.
	 */
	public static int DEFAULT_TOP_MARGIN = 2;

	/**
	 * The default bottom margin. By default, 2.
	 */
	public static int DEFAULT_BOTTOM_MARGIN = 2;

	/**
	 * The default label left margin. By default, 10.
	 */
	public static int DEFAULT_LABEL_LEFT_MARGIN = 10;

	/**
	 * The default label right margin. By default, 10.
	 */
	public static int DEFAULT_LABEL_RIGHT_MARGIN = 10;

	/**
	 * The default left margin for fields. By default, 2.
	 */
	public static int DEFAULT_FIELD_LEFT_MARGIN = 2;

	/**
	 * The default right margin for fields. By default, 2.
	 */
	public static int DEFAULT_FIELD_RIGHT_MARGIN = 2;

	/**
	 * The default parent margin. By default, 1.
	 */
	public static int DEFAULT_PARENT_MARGIN = 1;

	/**
	 * The default parent margin for scrolling. By default, 2.
	 */
	public static int DEFAULT_PARENT_MARGIN_FOR_SCROLL = 2;

	/**
	 * The condition about asterisk required style. By default, false.
	 */
	public static boolean ASTERISK_REQUIRED_STYLE = false;

	public static boolean BORDER_REQUIRED_STYLE = false;

	public static Color defaultAsteriskColor = Color.red;

	public static int defaultFieldButtonHigh = 22;

	static {
		DataField.ASTERISK_REQUIRED_STYLE = ParseUtils
				.getBoolean(System.getProperty("com.ontimize.gui.field.DataField.AsteriskRequiredStyle"), false);
		DataField.BORDER_REQUIRED_STYLE = ParseUtils
				.getBoolean(System.getProperty("com.ontimize.gui.field.DataField.BorderRequiredStyle"), false);
	}

	/**
	 * The reference for default border. By default, null.
	 */
	public static String DEFAULT_BORDER = null;

	static final int NO = 0;

	static final int YES = 1;

	static final int TEXT = 2;

	public static final String X = "x";

	public static final String Y = "y";

	public static final String WIDTH = "width";

	public static final String HEIGHT = "height";

	protected int x = Integer.MAX_VALUE;

	protected int y = Integer.MAX_VALUE;

	protected int width = Integer.MAX_VALUE;

	protected int height = Integer.MAX_VALUE;

	/**
	 * Allows to set a common preferred width to align fields in containers.
	 *
	 * @since 5.2073EN-0.1
	 */
	protected int prefWidth = -1;

	/**
	 * The key for default border.
	 */
	public static final String DEFAULTBORDER = "default";

	/**
	 * The key for none border.
	 */
	public static final String NONE = "none";

	/**
	 * The key for raised border.
	 */
	public static final String RAISED = "raised";

	/**
	 * The key for lowered border.
	 */
	public static final String LOWERED = "lowered";

	/**
	 * The key for dimension component.
	 */
	public static final String DIM = "dim";

	/**
	 * The key for expand component.
	 */
	public static final String EXPAND = "expand";

	/**
	 * The key for resizing.
	 */
	public static final String YES_STR = "yes";

	/**
	 * The key for resizing.
	 */
	public static final String TEXT_STR = "text";

	/**
	 * The key for resizing.
	 */
	public static final String NO_STR = "no";

	/**
	 * The attribute key.
	 */
	public static final String ATTR = "attr";

	public static final String INSETS = "insets";

	/**
	 * The key for alignment.
	 */
	public static final String ALIGN = "align";

	/**
	 * The key for left alignment.
	 */
	public static final String LEFT = "left";

	/**
	 * The key for right alignment.
	 */
	public static final String RIGHT = "right";

	/**
	 * The key for top alignment.
	 */
	public static final String TOP = "top";

	/**
	 * The key for bottom alignment.
	 */
	public static final String BOTTOM = "bottom";

	/**
	 * The key for center alignment.
	 */
	public static final String CENTER = "center";

	/**
	 * The key for size specification.
	 */
	public static final String SIZE = "size";

	/**
	 * The key for visible condition.
	 */
	public static final String VISIBLE = "visible";

	/**
	 * The key for required condition.
	 */
	public static final String REQUIRED = "required";

	/**
	 * The key to manage the font size.
	 */
	public static final String FONTSIZE = "fontsize";

	/**
	 * The key to manage the bold writing.
	 */
	public static final String BOLD = "bold";

	/**
	 * The key to manage the font color.
	 */
	public static final String FONTCOLOR = "fontcolor";

	public static final String LABELFONTCOLOR = "labelfontcolor";

	public static final String FONT = "font";

	public static final String OPAQUE = "opaque";

	public static final String LABELFONT = "labelfont";

	/**
	 * The key to specify the label size.
	 */
	public static final String LABELSIZE = "labelsize";

	/**
	 * The key to specify the label position.
	 */
	public static final String LABELPOSITION = "labelposition";

	/**
	 * The key to specify a specific preferred width (5.2074EN).
	 */
	public static final String PREF_WIDTH = "prefwidth";

	/**
	 * The key to manage the label visibility.
	 */
	public static final String LABELVISIBLE = "labelvisible";

	/**
	 * The key to manage the border.
	 */
	public static final String BORDER = "border";

	public static final String REQUIREDBORDER = "requiredborder";

	/**
	 * The key to manage the background color.
	 */
	public static final String BGCOLOR = "bgcolor";

	/**
	 * The key to manage the background color when field is disabled.
	 */
	public static final String DISABLEDBGCOLOR = "disabledbgcolor";

	/**
	 * The key to manage the tip.
	 */
	public static final String TIP = "tip";

	/**
	 * The key to manage the text alignment.
	 */
	public static final String TEXTALIGN = "textalign";

	/**
	 * The key to manage the vertical alignment.
	 */
	public static final String VALIGN = "valign";

	/**
	 * The key to manage the out border.
	 */
	public static final String OUTBORDER = "outborder";

	/**
	 * The key to manage the enabled condition.
	 */
	public static final String ENABLED = "enabled";

	/**
	 * The key to manage the label alignment.
	 */
	public static final String LABELALIGN = "labelalign";

	/**
	 * The default left alignment. By default, 0.
	 */
	public static final int ALIGN_LEFT = 0;

	/**
	 * The default center alignment. By default, 1.
	 */
	public static final int ALIGN_CENTER = 1;

	/**
	 * The default right alignment. By default, 2.
	 */
	public static final int ALIGN_RIGHT = 2;

	/**
	 * The key to manage the copy operation to the clipboard.
	 */
	public static final String CLIPBOARD_COPY = "datafield.copy_to_clipboard";

	public static final String CLIPBOARD_COPY_es_ES = "datafield.copy_to_clipboard";

	/**
	 * The key to manage the cut operation to the clipboard.
	 */
	public static final String CLIPBOARD_CUT = "datafield.cut";

	public static final String CLIPBOARD_CUT_es_ES = "datafield.cut";

	/**
	 * The key to manage the paste operation to the clipboard.
	 */
	public static final String CLIPBOARD_PASTE = "datafield.paste";

	public static final String CLIPBOARD_PASTE_es_ES = "Pegar contenido del portapapeles";

	/**
	 * The key to manage the visualization of help preferences.
	 */
	public static final String VISUALIZE_HELP_FIELD_PREFERENCE = "datafield.help_about_this_field";

	/**
	 * The key to manage the definition of help preferences.
	 */
	public static final String DEFINE_HELP_FIELD_PREFERENCE = "datafield.define_help_about_this_field";

	/**
	 * The key to manage the help tip for field.
	 */
	public static final String FIELD_HELP_TIP = "field_help_tip";

	/**
	 * Default value to retrieve or not the help text from preferences.
	 */
	public static boolean defaultPreferenceTextNotRetrievedFromPreferences = false;

	/**
	 * The instance of background color RGB(255,255,230)
	 */
	public static Color BACKGROUNDCOLOR = Color.white;

	/**
	 * The instance of focus background color RGB(255,255,230)
	 */
	public static Color FOCUS_BACKGROUNDCOLOR = new Color(255, 255, 230);

	/**
	 * The background color for a required field.
	 * <p>
	 *
	 * @see DataComponent#VERY_LIGHT_SKYBLUE.
	 */
	public static Color requiredFieldBackgroundColor = DataComponent.VERY_LIGHT_SKYBLUE;

	public static Color requiredFieldForegroundColor = Color.black;

	/**
	 * The reference for a pop-up menu. By default, null.
	 */
	protected ExtendedJPopupMenu popupMenu = null;

	/**
	 * A menu item reference for copy operation. By default, null.
	 */
	protected JMenuItem menuCopy = null;

	/**
	 * A menu item reference for paste operation. By default, null.
	 */
	protected JMenuItem menuPaste = null;

	/**
	 * A menu item reference for cut operation. By default, null.
	 */
	protected JMenuItem menuCut = null;

	/**
	 * A menu item reference for help preferences. By default, null.
	 */
	protected JMenuItem menuHelpPreferences = null;

	/**
	 * A menu item reference for defining help preferences. By default, null.
	 */
	protected JMenuItem menuDefineHelpPreference = null;

	/**
	 * The text help preference. By default, null.
	 */
	protected String textHelpPreference = null;

	/**
	 * The condition about the help text has been retrieved from preferences.
	 */
	protected boolean initPreferenceHelpText = DataField.defaultPreferenceTextNotRetrievedFromPreferences;

	/**
	 * The condition about advanced query mode state. By default, false.
	 */
	protected boolean advancedQueryMode = false;

	/**
	 * The List instance for a value listener.
	 */
	protected List valueListener = new Vector();

	/**
	 * The condition to indicate when field is active. By default, true.
	 */
	protected boolean enabled = true;

	protected boolean isEnabled = true;

	/**
	 * The condition to indicate when field is required. By default, false.
	 */
	protected boolean required = false;

	/**
	 * The condition to indicate when field is empty. By default, true.
	 */
	protected boolean empty = true;

	/**
	 * The condition to indicate when field is editable. By default, true.
	 */
	protected boolean modifiable = true;

	/**
	 * The condition to indicate when data are valid. By default, false.
	 */
	protected boolean validData = false;

	/**
	 * The condition to indicate whether must be shown. By default, true.
	 */
	protected boolean show = true;

	/**
	 * The condition to show the label field. By default, true.
	 */
	protected boolean showLabel = true;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The default text alignment. By default, -1.
	 */
	protected int textAlignment = -1;

	/**
	 * The reference for border text. By default, null.
	 */
	protected String borderText = null;

	/**
	 * The label position. By default, left.
	 * <p>
	 * {@value SwingConstants#LEFT}
	 */
	protected int labelPosition = SwingConstants.LEFT;

	/**
	 * The condition to activate field events. By default, yes.
	 */
	protected boolean fireValueEvents = true;

	/**
	 * The label text reference. By default, null.
	 */
	protected String labelText = null;

	/**
	 * The name of the border to use in the required fields. This name must be exist in the
	 * BorderManager
	 */
	protected String requiredBorder;

	protected Border noRequiredBorder;

	protected class CopyActionListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			try {
				if (DataField.this.dataField instanceof JTextComponent) {
					final JTextComponent textComponent = (JTextComponent) DataField.this.dataField;
					if (((!textComponent.isEnabled()) && (textComponent.getSelectionStart() < 0))
							|| (textComponent.getSelectionStart() == textComponent.getSelectionEnd())) {
						ApplicationManager.copyToClipboard(textComponent.getText());
					} else {
						textComponent.copy();
					}
				}
			} catch (final Exception ex) {
				DataField.logger.trace(null, ex);
			}
		}

	}

	/**
	 * This class manages the mouse events in data field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class InfoMouseListener extends MouseAdapter {

		Cursor cAnt = null;

		private final DataField field;

		/**
		 * The class constructor. Fixes the <code>field</code> variable.
		 * <p>
		 * @param field the field reference
		 */
		public InfoMouseListener(final DataField field) {
			this.field = field;
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (this.cAnt == null) {
				this.cAnt = ((Component) e.getSource()).getCursor();
			}
			final Component component = (Component) e.getSource();
			final Thread thread = new Thread("Field Help") {

				@Override
				public void run() {
					super.run();
					// The first time this execute a remote call to query the
					// help text.
					// Maybe this only has sense if the field is enabled
					if (InfoMouseListener.this.field.isEnabled()
							&& InfoMouseListener.this.field.hasHelpInPreferences()) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								component.setCursor(ApplicationManager.getHelpOnFieldCursor());
							}
						});
					}
				}
			};
			thread.start();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			// w.setVisible(false);
			if (this.cAnt != null) {
				((Component) e.getSource()).setCursor(this.cAnt);
			}
		}

	}

	// Manages the data field label
	public static class ELabel extends JLabel {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		public static final String ELABEL = "ELabel";

		int labelSize = -1;

		int preferredWidth = 0;

		public ELabel(final int columnsPreferredSize) {
			super();

			this.labelSize = columnsPreferredSize;
			// this.setBorder(new LineBorder(Color.red));
		}

		@Override
		public String getName() {
			return ELabel.ELABEL;
		}

		public void setLabelSize(final int t) {
			this.labelSize = t;
		}

		public int getLabelSize() {
			return this.labelSize;
		}

		@Override
		public void setText(final String text) {
			super.setText(text);
			this.recalculatePreferredWidth();
		}

		@Override
		public void setFont(final Font font) {
			super.setFont(font);
			this.recalculatePreferredWidth();
		}

		@Override
		public Font getFont() {
			// TODO Auto-generated method stub
			return super.getFont();
		}

		protected void recalculatePreferredWidth() {
			try {
				if ((this.labelSize != -1) && (this.labelSize >= 0)) {
					final FontMetrics fontMetrics = this.getFontMetrics(this.getFont().deriveFont(Font.PLAIN));
					this.preferredWidth = this.labelSize * fontMetrics.charWidth('A');
				}
			} catch (final Exception e) {
				DataField.logger.trace(null, e);
			}
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			try {
				if ((this.labelSize != -1) && (this.labelSize >= 0)) {
					if (this.preferredWidth == 0) {
						this.recalculatePreferredWidth();
						if (this.preferredWidth == 0) {
							return d;
						}
					}
					return new Dimension(this.preferredWidth, d.height);
				}
			} catch (final Exception e) {
				DataField.logger.error(null, e);
			}
			return d;
		}

		@Override
		public void updateUI() {
			super.updateUI();
			this.recalculatePreferredWidth();
		}

	}

	/**
	 * A instance of a label component.
	 */
	protected ELabel labelComponent = new ELabel(-1);

	/**
	 * This class implements an auxiliary panel for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class AuxPanel extends JPanel {

		private JLabel asteriskLabel;

		/**
		 * The class constructor. Calls to <code>super()</code> with a new {@link GridBagLayout} instance
		 * like @param and sets the foreground color (red).
		 */
		public AuxPanel() {
			super(new GridBagLayout());
			if (DataField.ASTERISK_REQUIRED_STYLE) {
				this.createAsteriskLabel();
			}
			this.setOpaque(false);
		}

		protected void createAsteriskLabel() {
			this.asteriskLabel = new JLabel("") {

				@Override
				public Dimension getPreferredSize() {
					final Dimension d = super.getPreferredSize();
					d.width = 12;
					return d;
				}
			};
			this.asteriskLabel.setForeground(DataField.defaultAsteriskColor);
			this.asteriskLabel.setOpaque(false);
			this.add(this.asteriskLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		@Override
		public boolean isFocusable() {
			return false;
		}

		/**
		 * Sets to asterisk view according to condition parameter.
		 * <p>
		 * @param visible the condition to asterisk view
		 */
		public void setAsteriskVisible(final boolean visible) {
			if (visible) {
				this.asteriskLabel.setText("*");
			} else {
				this.asteriskLabel.setText("");
			}
		}

	}

	/**
	 * An instance of an auxiliary panel.
	 */
	protected JPanel panel = new AuxPanel();

	/**
	 * The conditions for combo box. By default, null.
	 */
	protected JComboBox conditions = null;

	/**
	 * Component for data insertions.
	 */
	protected JComponent dataField = null;

	/**
	 * The size of field in number of characters. By default, 10.
	 */
	protected int fieldSize = 10;

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The variable to store the field value when <code>setValue()</code> is called.
	 */
	protected Object valueSave = null;

	/**
	 * Variable to indicate the field resize.
	 */
	protected int dim = DataField.NO;

	/**
	 * The field alignment. By default, north.
	 */
	protected int alignment = GridBagConstraints.NORTH;

	/**
	 * The vertical field alignment. By default, north.
	 */
	protected int Valignment = GridBagConstraints.NORTH;

	/**
	 * The resize panel value. By default, none.
	 */
	protected int redimensionPanel = GridBagConstraints.NONE;

	/**
	 * The resize text field value. By default, none.
	 */
	protected int redimensJTextField = GridBagConstraints.NONE;

	/**
	 * The horizontal weight of panel. By default, 0.
	 */
	protected int weightPanelH = 0;

	/**
	 * The horizontal weight of data field. By default, 1.
	 */
	protected int weightDataFieldH = 1;

	/**
	 * The reference for resources file. By default, null.
	 */
	protected ResourceBundle resources = null;

	/**
	 * The reference for tip. By default, null.
	 */
	protected String tipKey = null;

	/**
	 * The default font size. By default, -1
	 */
	protected int fontSize = -1;

	/**
	 * The reference for original size. By default, -1.
	 */
	protected int originalSize = -1;

	/**
	 * The condition to active the incremental font. By default, false.
	 */
	protected boolean incrementalFont = false;

	/**
	 * The default font color. By default, black.
	 */
	protected Color fontColor;

	/**
	 * The default background color when field is disable. By default VERY_LIGHT_GRAY.
	 */
	public static Color defaultDisableBackgroundColor = DataComponent.VERY_LIGHT_GRAY;

	protected Color disabledbgcolor;

	/**
	 * The bold condition. By default, false.
	 */
	protected boolean bold = false;

	/**
	 * The default background color. By default, white.
	 */
	protected Color backgroundColor = DataField.BACKGROUNDCOLOR;

	/**
	 * The default locale application.
	 * <p>
	 *
	 * @see Locale#getDefault()
	 */
	protected Locale locale = Locale.getDefault();

	/**
	 * The reference to visible permission in form. By default, null.
	 */
	protected FormPermission permissionVisible = null;

	/**
	 * The reference to activate permission. By default, null.
	 */
	protected FormPermission permissionActivate = null;

	/**
	 * The reference for field focus listener. By default, null.
	 */
	protected FieldFocusListener fieldlistenerFocus = null;

	/**
	 * This class implements a field focus listener.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class FieldFocusListener extends FocusAdapter {

		Color sourceBackgroundColor = null;

		/**
		 * Sets the background color.
		 * <p>
		 * @param color the color for background
		 */
		public void setSourceBackgroundColor(final Color color) {
			this.sourceBackgroundColor = color;
		}

		@Override
		public void focusLost(final FocusEvent e) {

			final Object source = e.getSource();
			if (source instanceof Component) {
				final Component c = (Component) source;
				if (c.isEnabled() && (this.sourceBackgroundColor != null)) {
					c.setBackground(this.sourceBackgroundColor);
				}
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			final Object source = e.getSource();
			if (source instanceof Component) {
				final Component c = (Component) source;
				if (c.isEnabled()) {
					if (this.sourceBackgroundColor == null) {
						this.sourceBackgroundColor = c.getBackground();
					}
					c.setBackground(DataField.FOCUS_BACKGROUNDCOLOR);
				}
			}
		}

	};

	/**
	 * Class constructor. Makes data field transparent.
	 * <p>
	 *
	 * @see JPanel#setOpaque(boolean)
	 */
	public DataField() {
		super.setOpaque(false);
	}

	/**
	 * Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>dim</td>
	 *        <td><i>no/text/yes</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The resize possibilities (no resize, resize the insertion space, resize the space
	 *        between label and text ).</td>
	 *        </tr>
	 *        <tr>
	 *        <td>prefwidth</td>
	 *        <td><i></td>
	 *        <td>-1</td>
	 *        <td>no</td>
	 *        <td>Allows to fix a preferred width for fields in order to make easier alignment.
	 * @since 5.2073EN-0.1</td>
	 *        </tr>
	 *        <tr>
	 *        <td>size</td>
	 *        <td></td>
	 *        <td>10</td>
	 *        <td>no</td>
	 *        <td>The size of text in number of characters.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>center/left/right</td>
	 *        <td>center</td>
	 *        <td>no</td>
	 *        <td>The alignment for field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>visible</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The visibility condition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>required</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The required condition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>fontsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The font size.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>fontcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The font color.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The label size in number of characters.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelvisible</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td></td>
	 *        <td>The label visibility.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover,
	 *        it is also allowed a border defined in #BorderManager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border for datafield</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The tip for data field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>textalign</td>
	 *        <td><i>center/right/left</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text alignment.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelposition</td>
	 *        <td><i>top/bottom/right/left</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The label position.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>outborder</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The out border.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>enabled</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The field activation.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td>attr</td>
	 *        <td>no</td>
	 *        <td>Alternative for attr text.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelalign</td>
	 *        <td>left/right/center</td>
	 *        <td>left</td>
	 *        <td>no</td>
	 *        <td>The label alignment.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelfontcolor</td>
	 *        <td></td>
	 *        <td>black</td>
	 *        <td>no</td>
	 *        <td>The font color for label in field</td>
	 *        </tr>
	 *        <tr>
	 *        <td>font</td>
	 *        <td>A string like : 'Arial-BOLD-18' (See #Font.decode())</td>
	 *        <td>The default font for system</td>
	 *        <td>no</td>
	 *        <td>Font for data field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Data field opacity condition</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelfont</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The type of font for label of data field</td>
	 *        </tr>
	 *        <tr>
	 *        <td>requiredborder</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The border requirement condition. If it is specified to 'yes', it will be fixed to
	 *        #BorderManager.DEFAULT_DATA_FIELD_REQUIRED_BORDE</td>
	 *        </tr>
	 *        <tr>
	 *        <td>disabledbgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color when data field is disabled.</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		this.setLayout(new GridBagLayout());
		// Search the parameters to initialize the object
		// Fist parameter: resized. By default: none.
		this.setResizingParameters(parameters);

		// Second parameter: attribute 'attr'
		final String labelTextString = this.setAttributeParameterAndLabelText(parameters);
		this.labelComponent.setText(labelTextString);

		this.setTipParameter(parameters);

		// Next parameter: size
		final Object oSize = parameters.get(DataField.SIZE);

		this.setSizeParameter(oSize);

		// Next parameter: align
		this.setAlignParameter(parameters);

		// Text align
		this.setTextAlignParameter(parameters);

		this.setVerticalalignParameter(parameters);

		this.setLabelSizeParameter(parameters);

		this.setLabelPositionParameter(parameters);

		this.setVisibleParameter(parameters);

		this.setEnabledParameter(parameters);

		this.setTextParameter(parameters);

		this.setLabelVisibleParameter(parameters);

		this.setRequiredParameter(parameters);

		this.setBoldParameter(parameters);

		this.setBorderParameter(parameters);

		this.setOutBorderParameter(parameters);

		this.setFontSizeParameter(parameters);

		this.setFontColorParameter(parameters);

		this.setLabelFontColor(
				ParseUtils.getColor((String) parameters.get(DataField.LABELFONTCOLOR),
						this.fontColor != null ? this.fontColor : this.labelComponent.getForeground()));

		// Assigning lookAndFeel color...
		if ((this.dataField != null) && (this.fontColor == null)) {
			this.fontColor = this.dataField.getForeground();
		}

		this.setBackgroundColorParameter(parameters);

		this.setDisabledBackgroundColorParameter(parameters);

		this.setLabelAlignBackgroundParameter(parameters);

		this.labelComponent.setLabelFor(this.dataField);

		this.add(this.labelComponent,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_LABEL_LEFT_MARGIN,
								DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_LABEL_RIGHT_MARGIN),
						0, 0));

		this.add(this.panel, new GridBagConstraints(1, 0, 1, 1, this.weightPanelH, 0.0, GridBagConstraints.CENTER,
				this.redimensionPanel, new Insets(0, 0, 0, 0), 0, 0));

		// Add the JComponent with the appropriate constraints
		this.add(this.dataField,
				new GridBagConstraints(3, 0, 1, 1, this.weightDataFieldH, 0.0, GridBagConstraints.EAST,
						this.redimensJTextField,
						new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_FIELD_LEFT_MARGIN,
								DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_FIELD_RIGHT_MARGIN),
						0, 0));
		try {
			this.valueSave = this.getValue();
		} catch (final Exception e) {
			DataField.logger.trace(null, e);
			this.valueSave = null;
		}
		this.installPopupMenuListener();
		this.fieldlistenerFocus = this.createFocusListener();

		this.installFocusListener();
		this.installHelpId();

		this.installPreferenceHelpListener();
		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}

		try {
			final MouseListener l = new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					if (e.isAltDown() && e.isControlDown() && e.isShiftDown()) {
						DataField.logger.error("{} -> {} field text: {} >> field value ", DataField.this.getAttribute(),
								DataField.this.getText(), DataField.this.getValue());
					}
				}
			};
			this.dataField.addMouseListener(l);
			this.labelComponent.addMouseListener(l);
		} catch (final Exception ex) {
			DataField.logger.trace(null, ex);
		}

		this.setFont(ParseUtils.getFont((String) parameters.get(DataField.FONT), this.dataField.getFont()));
		this.labelComponent
		.setFont(ParseUtils.getFont((String) parameters.get(DataField.LABELFONT), this.labelComponent.getFont()));
		if (parameters.containsKey("opaque")
				&& !ApplicationManager.parseStringValue(parameters.get("opaque").toString())) {
			DataField.changeOpacity(this, false);
		}
		this.requiredBorder = ParseUtils.getString((String) parameters.get(DataField.REQUIREDBORDER), null);
		if ("yes".equals(this.requiredBorder) || DataField.BORDER_REQUIRED_STYLE) {
			this.requiredBorder = BorderManager.DEFAULT_DATA_FIELD_REQUIRED_BORDER;
		}

		this.x = ParseUtils.getInteger((String) parameters.get(DataField.X), Integer.MAX_VALUE);
		this.y = ParseUtils.getInteger((String) parameters.get(DataField.Y), Integer.MAX_VALUE);
		this.width = ParseUtils.getInteger((String) parameters.get(DataField.WIDTH), Integer.MAX_VALUE);
		this.height = ParseUtils.getInteger((String) parameters.get(DataField.HEIGHT), Integer.MAX_VALUE);

		this.prefWidth = ParseUtils.getInteger((String) parameters.get(DataField.PREF_WIDTH), -1);

		this.defaultValue = ParseUtils.getString((String) parameters.get(DataField.DEFAULT_VALUE), null);
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelAlignBackgroundParameter(final Map parameters) {
		final Object labelalign = parameters.get(DataField.LABELALIGN);
		if (labelalign != null) {
			if (labelalign.equals(DataField.RIGHT)) {
				this.labelComponent.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				if (labelalign.equals(DataField.CENTER)) {
					this.labelComponent.setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					this.labelComponent.setHorizontalAlignment(SwingConstants.LEFT);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDisabledBackgroundColorParameter(final Map parameters) {
		final Object disableBgColor = parameters.get(DataField.DISABLEDBGCOLOR);
		if (disableBgColor != null) {
			try {
				this.disabledbgcolor = ColorConstants.parseColor(disableBgColor.toString());
			} catch (final Exception e) {
				DataField.logger.error("Error 'disabledbgcolor' parameter: ", e);
			}
		}
		if (this.disabledbgcolor == null) {
			this.disabledbgcolor = DataField.defaultDisableBackgroundColor;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBackgroundColorParameter(final Map parameters) {
		final Object bgcolor = parameters.get(DataField.BGCOLOR);
		if (bgcolor != null) {
			try {
				this.backgroundColor = ColorConstants.parseColor(bgcolor.toString());
				this.dataField.setBackground(this.backgroundColor);
			} catch (final Exception e) {
				DataField.logger.error(" Error 'bgcolor' parameter: ", e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontColorParameter(final Map parameters) {
		final Object fontcolor = parameters.get(DataField.FONTCOLOR);
		if (fontcolor != null) {
			try {
				this.fontColor = ColorConstants.parseColor(fontcolor.toString());
				this.setFontColor(this.fontColor);
			} catch (final Exception e) {
				DataField.logger.error("Error 'fontcolor' parameter: ", e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontSizeParameter(final Map parameters) {
		final Object fontsize = parameters.get(DataField.FONTSIZE);
		if (fontsize != null) {
			if (fontsize.toString().charAt(0) == '+') {
				this.incrementalFont = true;
				try {
					this.fontSize = Integer.parseInt(fontsize.toString().substring(1));
					this.setFontSize(this.fontSize);
				} catch (final Exception e) {
					DataField.logger.error(" Error 'fontsize' parameter : ", e);
				}
			} else {
				this.incrementalFont = false;
				try {
					this.fontSize = Integer.parseInt(fontsize.toString());
					this.setFontSize(this.fontSize);
				} catch (final Exception e) {
					DataField.logger.error("Error 'fontsize' parameter ", e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOutBorderParameter(final Map parameters) {
		final Object outborder = parameters.get(DataField.OUTBORDER);
		if (outborder != null) {
			if (outborder.equals(DataField.RAISED)) {
				this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (outborder.equals(DataField.LOWERED)) {
				this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				try {
					final Color c = ColorConstants.colorNameToColor(outborder.toString());
					this.setBorder(new LineBorder(c));
				} catch (final Exception e) {
					DataField.logger.error("Error 'outborder' parameter: ", e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderParameter(final Map parameters) {
		Object border = parameters.get(DataField.BORDER);
		if (border == null) {
			border = DataField.DEFAULT_BORDER;
		}
		if (border != null) {
			if (border.equals(DataField.NONE)) {
				this.borderText = border.toString();
				this.dataField.setBorder(new EmptyBorder(0, 0, 0, 0));
				this.dataField.setOpaque(false);
			} else if (border.equals(DataField.RAISED)) {
				this.dataField.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals(DataField.LOWERED)) {
				this.dataField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				try {
					final Color c = ColorConstants.colorNameToColor(border.toString());
					this.dataField.setBorder(new LineBorder(c));
				} catch (final Exception e) {
					DataField.logger.trace(null, e);
					this.dataField.setBorder(ParseUtils.getBorder((String) border, this.dataField.getBorder()));
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBoldParameter(final Map parameters) {
		final Object bold = parameters.get(DataField.BOLD);
		if (bold != null) {
			if (bold.equals(DataField.YES_STR)) {
				this.bold = true;
			} else {
				this.bold = false;
			}
		}
		if (this.bold) {
			this.setBold(this.bold);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setRequiredParameter(final Map parameters) {
		final Object required = parameters.get(DataField.REQUIRED);
		if (required != null) {
			if (required.equals(DataField.YES_STR)) {
				this.required = true;
			} else {
				this.required = false;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelVisibleParameter(final Map parameters) {
		final Object labelvisible = parameters.get(DataField.LABELVISIBLE);
		if (labelvisible != null) {
			if (labelvisible.equals(DataField.NO_STR)) {
				this.showLabel = false;
				this.labelComponent.setVisible(false);
			} else {
				this.showLabel = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextParameter(final Map parameters) {
		final Object text = parameters.get(DataField.TEXT_STR);
		if (text != null) {
			this.labelText = text.toString();
			this.labelComponent.setText(this.labelText);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setEnabledParameter(final Map parameters) {
		final Object enabled = parameters.get(DataField.ENABLED);
		if ((enabled != null) && (enabled instanceof String)) {
			final String sEnabled = enabled.toString();
			if (sEnabled.equalsIgnoreCase("no")) {
				this.isEnabled = false;
				this.setEnabled(false);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setVisibleParameter(final Map parameters) {
		final Object visible = parameters.get(DataField.VISIBLE);
		if (visible != null) {
			if (visible.equals(DataField.NO_STR)) {
				this.show = false;
			} else {
				this.show = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelPositionParameter(final Map parameters) {
		final Object posLabel = parameters.get(DataField.LABELPOSITION);
		if (posLabel != null) {
			if (posLabel.equals(DataField.LEFT)) {
				this.labelPosition = SwingConstants.LEFT;
			} else if (posLabel.equals(DataField.RIGHT)) {
				this.labelPosition = SwingConstants.RIGHT;
			} else if (posLabel.equals(DataField.TOP)) {
				this.labelPosition = SwingConstants.TOP;
			} else if (posLabel.equals(DataField.BOTTOM)) {
				this.labelPosition = SwingConstants.BOTTOM;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelSizeParameter(final Map parameters) {
		final Object oLabelSize = parameters.get(DataField.LABELSIZE);
		if (oLabelSize != null) {
			try {
				final Integer integerLabelSizeValue = new Integer(oLabelSize.toString());
				this.labelComponent.setLabelSize(integerLabelSizeValue.intValue());
			} catch (final Exception e) {
				DataField.logger.error("Error 'labelsize' parameter: {}", oLabelSize.toString(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setVerticalalignParameter(final Map parameters) {
		final Object oValign = parameters.get(DataField.VALIGN);
		if (oValign != null) {
			if (oValign.equals("center")) {
				this.Valignment = GridBagConstraints.CENTER;
			} else {
				if (oValign.equals("bottom")) {
					this.Valignment = GridBagConstraints.SOUTH;
				} else {
					this.Valignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextAlignParameter(final Map parameters) {
		final Object oAlignText = parameters.get(DataField.TEXTALIGN);
		if (oAlignText != null) {
			if (oAlignText.equals(DataField.RIGHT)) {
				this.textAlignment = DataField.ALIGN_RIGHT;
			} else {
				if (oAlignText.equals(DataField.CENTER)) {
					this.textAlignment = DataField.ALIGN_CENTER;
				} else {
					this.textAlignment = DataField.ALIGN_LEFT;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAlignParameter(final Map parameters) {
		final Object oAlign = parameters.get(DataField.ALIGN);
		if (oAlign != null) {
			if (oAlign.equals(DataField.RIGHT)) {
				this.alignment = GridBagConstraints.NORTHEAST;
			} else {
				if (oAlign.equals(DataField.LEFT)) {
					this.alignment = GridBagConstraints.NORTHWEST;
				} else {
					this.alignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param oSize
	 */
	protected void setSizeParameter(final Object oSize) {
		if (oSize != null) {
			try {
				final Integer integerSizeValue = new Integer(oSize.toString());
				this.fieldSize = integerSizeValue.intValue();
			} catch (final Exception e) {
				DataField.logger.error("Error 'size' parameter: {}", oSize.toString(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTipParameter(final Map parameters) {
		final Object tip = parameters.get(DataField.TIP);
		if (tip != null) {
			this.tipKey = tip.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @return
	 */
	protected String setAttributeParameterAndLabelText(final Map parameters) {
		String labelText;
		final Object oAttribute = parameters.get(DataField.ATTR);
		if (oAttribute == null) {
			labelText = "NULL";
		} else {
			labelText = oAttribute.toString();
			this.attribute = oAttribute.toString();
		}
		return labelText;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setResizingParameters(final Map parameters) {
		final Object resized = parameters.get(DataField.DIM);
		if (resized == null) {
			this.redimensionPanel = GridBagConstraints.NONE;
			this.redimensJTextField = GridBagConstraints.NONE;
			this.dim = DataField.NO;
		} else {
			if (resized.equals(DataField.YES_STR)) {
				this.redimensionPanel = GridBagConstraints.HORIZONTAL;
				this.redimensJTextField = GridBagConstraints.NONE;
				this.weightPanelH = 1;
				this.weightDataFieldH = 0;
				this.dim = DataField.YES;
			} else {
				if (resized.equals(DataField.TEXT_STR)) {
					this.redimensionPanel = GridBagConstraints.NONE;
					this.redimensJTextField = GridBagConstraints.HORIZONTAL;
					this.weightPanelH = 0;
					this.weightDataFieldH = 1;
					this.dim = DataField.TEXT;
				} else {
					this.redimensionPanel = GridBagConstraints.NONE;
					this.redimensJTextField = GridBagConstraints.NONE;
					this.weightPanelH = 0;
					this.weightDataFieldH = 0;
					this.dim = DataField.NO;
				}
			}
		}
	}

	/**
	 * Installs the preferences for help listener.
	 */
	protected void installPreferenceHelpListener() {
		if (this.dataField != null) {
			this.dataField.addMouseListener(new InfoMouseListener(this));
		}
	}

	/**
	 * Validates the component positions.
	 */
	protected void validateComponentPositions() {
		try {
			if ((this.labelComponent == null) || (this.panel == null) || (this.dataField == null)) {
				return;
			}
			final GridBagLayout l = (GridBagLayout) this.getLayout();
			final GridBagConstraints cons = l.getConstraints(this.labelComponent);
			final GridBagConstraints cons2 = l.getConstraints(this.panel);
			final GridBagConstraints cons3 = l.getConstraints(this.dataField);
			if ((cons == null) || (cons2 == null) || (cons3 == null)) {
				return;
			}
			switch (this.labelPosition) {
			case SwingConstants.LEFT:
				this.setLabelPositionConstraintsOnLeft(l, cons, cons2, cons3);
				break;

			case SwingConstants.RIGHT:
				this.setLabelPositionConstraintsOnRight(l, cons, cons2, cons3);
				break;
			case SwingConstants.TOP:
				this.setLabelPositionConstraintsOnTop(l, cons, cons2, cons3);
				break;
			case SwingConstants.BOTTOM:
				this.setLabelPositionConstraintOnBottom(l, cons, cons2, cons3);
				break;

			}
		} catch (final Exception e) {
			DataField.logger.debug("Error validating component positions", e);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #validateComponentPositions()}
	 * @param l
	 * @param cons
	 * @param cons2
	 * @param cons3
	 */
	protected void setLabelPositionConstraintOnBottom(final GridBagLayout l, final GridBagConstraints cons,
			final GridBagConstraints cons2, final GridBagConstraints cons3) {
		DataField.logger.trace("labelposition bottom");
		this.labelComponent.setHorizontalTextPosition(SwingConstants.LEFT);
		this.labelComponent.setHorizontalAlignment(SwingConstants.LEFT);

		cons.gridx = 0;
		cons2.gridx = 0;
		cons2.weightx = 0;
		cons3.gridx = 3;
		cons3.anchor = GridBagConstraints.NORTHWEST;
		cons.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_LABEL_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_LABEL_RIGHT_MARGIN);
		cons3.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_LABEL_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_FIELD_RIGHT_MARGIN);
		cons3.anchor = GridBagConstraints.NORTHEAST;
		cons2.gridy = 1;
		cons3.gridy = 0;
		cons.gridy = 1;
		cons.gridwidth = this.getComponentCount() + 1;
		this.remove(this.labelComponent);
		this.add(this.labelComponent, cons);
		this.remove(this.panel);
		this.add(this.panel, cons2);
		this.remove(this.dataField);
		this.add(this.dataField, cons3);
		final Object[] constraints = new Object[this.getComponentCount()];
		final Component[] components = new Component[this.getComponentCount()];
		for (int i = 0; i < this.getComponentCount(); i++) {
			final Component c = this.getComponent(i);
			if (c == this.labelComponent) {
				continue;
			}
			if (c == this.panel) {
				continue;
			}
			if (c == this.dataField) {
				continue;
			}
			final GridBagConstraints cAux = l.getConstraints(c);
			cAux.gridy = 2;
			constraints[i] = cAux;
			components[i] = c;
		}
		for (int i = 0; i < components.length; i++) {
			final Object cAux = constraints[i];
			final Component c = components[i];
			if ((cAux != null) && (c != null)) {
				this.remove(c);
				this.add(c, cAux);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #validateComponentPositions()}
	 * @param l
	 * @param cons
	 * @param cons2
	 * @param cons3
	 */
	protected void setLabelPositionConstraintsOnTop(final GridBagLayout l, final GridBagConstraints cons, final GridBagConstraints cons2,
			final GridBagConstraints cons3) {
		DataField.logger.trace("labelposition top");
		this.labelComponent.setHorizontalTextPosition(SwingConstants.LEFT);
		this.labelComponent.setHorizontalAlignment(SwingConstants.LEFT);
		cons.gridx = 0;
		cons2.gridx = 0;
		cons2.weightx = 0;
		cons3.gridx = 1;
		cons2.gridy = 2;
		cons3.gridy = 2;
		cons3.anchor = GridBagConstraints.NORTHWEST;
		cons.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_LABEL_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_LABEL_RIGHT_MARGIN);
		cons3.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_FIELD_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_FIELD_RIGHT_MARGIN);
		cons3.anchor = GridBagConstraints.NORTHEAST;

		cons.gridy = 0;
		cons.gridwidth = this.getComponentCount() + 1;
		this.remove(this.labelComponent);
		this.add(this.labelComponent, cons);
		this.remove(this.panel);
		this.add(this.panel, cons2);
		this.remove(this.dataField);
		this.add(this.dataField, cons3);
		final Object[] constraints = new Object[this.getComponentCount()];
		final Component[] components = new Component[this.getComponentCount()];
		for (int i = 0; i < this.getComponentCount(); i++) {
			final Component c = this.getComponent(i);
			if (c == this.labelComponent) {
				continue;
			}
			if (c == this.panel) {
				continue;
			}
			if (c == this.dataField) {
				continue;
			}
			final GridBagConstraints cAux = l.getConstraints(c);
			cAux.gridy = 2;
			constraints[i] = cAux;
			components[i] = c;
		}
		for (int i = 0; i < components.length; i++) {
			final Object cAux = constraints[i];
			final Component c = components[i];
			if ((cAux != null) && (c != null)) {
				this.remove(c);
				this.add(c, cAux);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #validateComponentPositions()}
	 * @param l
	 * @param cons
	 * @param cons2
	 * @param cons3
	 */
	protected void setLabelPositionConstraintsOnRight(final GridBagLayout l, final GridBagConstraints cons,
			final GridBagConstraints cons2, final GridBagConstraints cons3) {
		DataField.logger.trace("labelposition right");
		this.labelComponent.setHorizontalTextPosition(SwingConstants.LEFT);
		this.labelComponent.setHorizontalAlignment(SwingConstants.LEFT);
		cons.gridx = this.getComponentCount() + 1;
		cons.gridwidth = 1;
		cons2.gridx = 1;
		cons2.weightx = this.weightPanelH;
		cons3.gridx = 0;
		cons.gridy = 0;
		cons2.gridy = 0;
		cons3.gridy = 0;
		cons3.anchor = GridBagConstraints.NORTHWEST;

		cons.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_FIELD_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_LABEL_RIGHT_MARGIN);
		cons3.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_FIELD_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN, DataField.DEFAULT_FIELD_RIGHT_MARGIN);
		cons3.anchor = GridBagConstraints.NORTHEAST;

		this.remove(this.labelComponent);
		this.add(this.labelComponent, cons);
		this.remove(this.panel);
		this.add(this.panel, cons2);
		this.remove(this.dataField);
		this.add(this.dataField, cons3);
		final Object[] constraints = new Object[this.getComponentCount()];
		final Component[] components = new Component[this.getComponentCount()];
		for (int i = 0; i < this.getComponentCount(); i++) {
			final Component c = this.getComponent(i);
			if (c == this.labelComponent) {
				continue;
			}
			if (c == this.panel) {
				continue;
			}
			if (c == this.dataField) {
				continue;
			}
			final GridBagConstraints cAux = l.getConstraints(c);
			cAux.gridy = 2;
			constraints[i] = cAux;
			components[i] = c;
		}
		for (int i = 0; i < components.length; i++) {
			final Object cAux = constraints[i];
			final Component c = components[i];
			if ((cAux != null) && (c != null)) {
				this.remove(c);
				this.add(c, cAux);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #validateComponentPositions()}
	 * @param l
	 * @param cons
	 * @param cons2
	 * @param cons3
	 */
	protected void setLabelPositionConstraintsOnLeft(final GridBagLayout l, final GridBagConstraints cons, final GridBagConstraints cons2,
			final GridBagConstraints cons3) {
		DataField.logger.trace("labelposition left");
		this.labelComponent.setHorizontalTextPosition(SwingConstants.LEFT);
		this.labelComponent.setHorizontalAlignment(SwingConstants.LEFT);
		cons.gridx = 0;
		cons.gridwidth = 1;
		cons2.gridx = 1;
		cons2.weightx = this.weightPanelH;
		cons3.gridx = 3;
		cons.gridy = 0;
		cons2.gridy = 0;
		cons3.gridy = 0;
		cons.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_LABEL_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN,
				DataField.DEFAULT_LABEL_RIGHT_MARGIN);
		cons3.insets = new Insets(DataField.DEFAULT_TOP_MARGIN, DataField.DEFAULT_FIELD_LEFT_MARGIN,
				DataField.DEFAULT_BOTTOM_MARGIN,
				DataField.DEFAULT_FIELD_RIGHT_MARGIN);
		cons3.anchor = GridBagConstraints.NORTHEAST;
		this.remove(this.labelComponent);
		this.add(this.labelComponent, cons);
		this.remove(this.panel);
		this.add(this.panel, cons2);
		this.remove(this.dataField);
		this.add(this.dataField, cons3);
		final Object[] constraints = new Object[this.getComponentCount()];
		final Component[] components = new Component[this.getComponentCount()];
		for (int i = 0; i < this.getComponentCount(); i++) {
			final Component c = this.getComponent(i);
			if (c == this.labelComponent) {
				continue;
			}
			if (c == this.panel) {
				continue;
			}
			if (c == this.dataField) {
				continue;
			}
			final GridBagConstraints cAux = l.getConstraints(c);
			cAux.gridy = 2;
			constraints[i] = cAux;
			components[i] = c;
		}
		for (int i = 0; i < components.length; i++) {
			final Object cAux = constraints[i];
			final Component c = components[i];
			if ((cAux != null) && (c != null)) {
				this.remove(c);
				this.add(c, cAux);
			}
		}
	}

	/**
	 * Gets the constraints needed to adapt the field to the container from <code>init</code>
	 * <p>
	 * @param parentLayout the parent layout
	 */
	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		int totalAlignment = this.alignment;
		switch (this.Valignment) {
		case GridBagConstraints.NORTH:
			totalAlignment = this.alignment;
			break;
		case GridBagConstraints.CENTER:
			switch (this.alignment) {
			case GridBagConstraints.NORTH:
				totalAlignment = GridBagConstraints.CENTER;
				break;
			case GridBagConstraints.NORTHEAST:
				totalAlignment = GridBagConstraints.EAST;
				break;
			case GridBagConstraints.NORTHWEST:
				totalAlignment = GridBagConstraints.WEST;
				break;
			default:
				break;
			}
			break;
		case GridBagConstraints.SOUTH:
			switch (this.alignment) {
			case GridBagConstraints.NORTH:
				totalAlignment = GridBagConstraints.SOUTH;
				break;
			case GridBagConstraints.NORTHEAST:
				totalAlignment = GridBagConstraints.SOUTHEAST;
				break;
			case GridBagConstraints.NORTHWEST:
				totalAlignment = GridBagConstraints.SOUTHWEST;
				break;
			default:
				break;
			}
			break;
		default:
			totalAlignment = this.alignment;
			break;
		}

		if (parentLayout instanceof GridBagLayout) {
			if (this.dim == DataField.NO) {
				return new GridBagConstraints(0, 0, 1, 1, 0.0001, 0, totalAlignment, GridBagConstraints.NONE,
						new Insets(DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN,
								DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN),
						0, 0);
			} else {
				if ((this.dim == DataField.YES) || (this.dim == DataField.TEXT)) {
					return new GridBagConstraints(0, 0, 1, 1, 1, 0, totalAlignment, GridBagConstraints.HORIZONTAL,
							new Insets(DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN,
									DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN),
							0, 0);
				} else {
					return new GridBagConstraints(0, 0, 1, 1, 1, 0, totalAlignment, GridBagConstraints.NONE,
							new Insets(DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN,
									DataField.DEFAULT_PARENT_MARGIN, DataField.DEFAULT_PARENT_MARGIN),
							0, 0);
				}
			}
		} else if (parentLayout instanceof AbsoluteLayout) {
			final Dimension dimension = this.getPreferredSize();
			// TODO Check using getWidth or preferredSize.getWidth
			return new AbsoluteConstraints(this.x != Integer.MAX_VALUE ? this.x : this.getX(),
					this.y != Integer.MAX_VALUE ? this.y : this.getY(),
							this.width != Integer.MAX_VALUE ? this.width : dimension.width,
									this.height != Integer.MAX_VALUE ? this.height : dimension.height);
		} else {
			return null;
		}
	}

	/**
	 * Gets text.
	 * <p>
	 * @return the text in this implementation returns null.
	 */
	public String getText() {
		return null;
	}

	/**
	 * Gets the label text.
	 * <p>
	 * @return the label text
	 */
	public String getLabelText() {
		return this.labelText;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
		this.locale = l;
		if (this.dataField != null) {
			this.dataField.setLocale(l);
		}
		if ((this.dataField != null) && (this.dataField instanceof Internationalization)) {
			((Internationalization) this.dataField).setComponentLocale(l);
		}
		if ((this.dataField != null) && (this.dataField instanceof JTextComponent)) {
			final Document d = ((JTextComponent) this.dataField).getDocument();
			if ((d != null) && (d instanceof Internationalization)) {
				((Internationalization) d).setComponentLocale(l);
			}
		}
	}

	/**
	 * Returns true when field is enabled. False in other case.
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Returns true when field is hidden. False in other case.
	 */
	@Override
	public boolean isHidden() {
		return !this.show;
	}

	/**
	 * Returns true when field is required. False in other case.
	 */
	@Override
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Returns true when field is modifiable for this form. False in other case.
	 */
	@Override
	public boolean isModifiable() {
		return this.modifiable;
	}

	/**
	 * Sets modifiable the field.
	 * <p>
	 * @param modifiable modif the modifiable condition
	 */
	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	/**
	 * Enables the field to insert data.
	 * <p>
	 * @param enabled the condition to set enable
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if ((!enabled) && (this instanceof AdvancedDataComponent)) {
			((AdvancedDataComponent) this).setAdvancedQueryMode(false);
		}
		super.setEnabled(enabled);
		if (!enabled) {
			if (this.conditions != null) {
				this.conditions.setVisible(false);
			}
			this.advancedQueryMode = false;
		}
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				this.dataField.setEnabled(enabled);
				this.enabled = enabled;
				this.updateBackgroundColor();
			} else {
				this.setEnabled(false);
			}
		} else {
			if ((!enabled) && this.dataField.hasFocus()) {
				this.dataField.transferFocus();
			}
			this.dataField.setEnabled(enabled);
			this.enabled = enabled;
			this.updateBackgroundColor();
		}
	}

	/**
	 * Returns a reference for data field.
	 * <p>
	 * @return the data field reference.
	 */
	public JComponent getDataField() {
		return this.dataField;
	}

	/**
	 * Returns the label text.
	 * <p>
	 * @return the label component text
	 */
	@Override
	public String getLabelComponentText() {
		return this.labelComponent.getText();
	}

	/**
	 * Gets the label component.
	 * <p>
	 * @return the label component
	 */
	public JLabel getLabelComponent() {
		return this.labelComponent;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void requestFocus() {
		if (this.dataField != null) {
			this.dataField.requestFocus();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.labelComponent = null;
		this.panel = null;
		this.dataField = null;
		DataField.logger.debug("Finalized instance class");
		super.finalize();
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension prefSize = super.getPreferredSize();
		if (this.prefWidth != -1) {
			prefSize.width = this.prefWidth;
		}
		return prefSize;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			DataField.logger.debug("Component: {} Modified: Previos value = {}  New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			DataField.logger.debug("Component: {} Modified: Previos value = {}  New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			DataField.logger.debug("Component: {} Modified: Previos value = {}  New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Updates font size.
	 */
	public void updateFont() {
		if ((this.fontSize != -1) && (this.fontSize > 0)) {
			this.setFontSize(this.fontSize);
		}
	}

	/**
	 * Sets font size to size parameter.
	 * @param fontSize the font size parameter
	 */
	public void setFontSize(final int fontSize) {
		if (this.labelComponent == null) {
			return;
		}
		int iSize = fontSize;
		if (this.incrementalFont) {
			if (this.originalSize == -1) {
				this.originalSize = this.labelComponent.getFont().getSize();
			}
			iSize = this.originalSize + iSize;
		}

		DataField.logger.debug("Setting font size: {}", iSize);

		try {
			if (this.labelComponent != null) {
				this.labelComponent.setFont(this.labelComponent.getFont().deriveFont((float) iSize));
			}
			if (this.dataField != null) {
				this.dataField.setFont(this.dataField.getFont().deriveFont((float) iSize));
			}
		} catch (final Exception e) {
			DataField.logger.error("Error setting font size", e);
		}
	}

	/**
	 * Sets the font color.
	 * <p>
	 * @param fontColor the color parameter
	 */
	public void setFontColor(final Color fontColor) {
		try {
			if (this.labelComponent.getForeground().equals(this.dataField.getForeground())) {
				this.labelComponent.setForeground(fontColor);
			}
			this.dataField.setForeground(fontColor);
			this.fontColor = fontColor;
		} catch (final Exception e) {
			DataField.logger.error("Error setting font color", e);
		}
	}

	public void setLabelFontColor(final Color fontColor) {
		this.labelComponent.setForeground(fontColor);
	}

	/**
	 * Gets the font color.
	 * <p>
	 * @return the font color
	 */
	public Color getFontColor() {
		return this.fontColor;
	}

	public Color getLabelFontColor() {
		return this.labelComponent.getForeground();
	}

	/**
	 * Sets bold writing.
	 * <p>
	 * @param bold the condition to set bold writing
	 */
	public void setBold(final boolean bold) {
		try {
			if (bold) {
				if (this.labelComponent != null) {
					this.labelComponent.setFont(this.labelComponent.getFont().deriveFont(Font.BOLD));
				}
				if (this.dataField != null) {
					this.dataField.setFont(this.dataField.getFont().deriveFont(Font.BOLD));
				}
			} else {
				if (this.labelComponent != null) {
					this.labelComponent.setFont(this.labelComponent.getFont().deriveFont(Font.PLAIN));
				}
				if (this.dataField != null) {
					this.dataField.setFont(this.dataField.getFont().deriveFont(Font.PLAIN));
				}
			}
			this.bold = bold;
		} catch (final Exception e) {
			DataField.logger.error("Error setting bold", e);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		this.resources = resource;
		try {
			final String labelTextKey = this.labelText == null ? this.attribute.toString() : this.labelText;
			this.labelComponent.setText(ApplicationManager.getTranslation(labelTextKey, resource));
		} catch (final Exception e) {
			DataField.logger.debug(null, e);
		}
		this.updateTip();

		if (this.menuCopy != null) {
			String copyText = DataField.CLIPBOARD_COPY_es_ES;
			try {
				if (this.resources != null) {
					copyText = this.resources.getString(DataField.CLIPBOARD_COPY);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}
			this.menuCopy.setText(copyText);
		}

		if (this.menuCut != null) {
			String cutText = DataField.CLIPBOARD_CUT_es_ES;
			try {
				if (this.resources != null) {
					cutText = this.resources.getString(DataField.CLIPBOARD_CUT);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}
			this.menuCut.setText(cutText);
		}

		if (this.menuPaste != null) {
			String pasteText = DataField.CLIPBOARD_PASTE_es_ES;
			try {
				if (this.resources != null) {
					pasteText = this.resources.getString(DataField.CLIPBOARD_PASTE);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}
			this.menuPaste.setText(pasteText);
		}
		if (this.menuHelpPreferences != null) {
			this.menuHelpPreferences
			.setText(ApplicationManager.getTranslation(DataField.VISUALIZE_HELP_FIELD_PREFERENCE, this.resources));
		}
		if (this.menuDefineHelpPreference != null) {
			this.menuDefineHelpPreference
			.setText(ApplicationManager.getTranslation(DataField.DEFINE_HELP_FIELD_PREFERENCE, this.resources));
		}
	}

	@Override
	public void addValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && !this.valueListener.contains(l)) {
			this.valueListener.add(l);
		}
	}

	@Override
	public void addFocusListener(final FocusListener l) {
		if (this.dataField != null) {
			this.dataField.addFocusListener(l);
		}
	}

	@Override
	public void removeFocusListener(final FocusListener l) {
		if (this.dataField != null) {
			this.dataField.removeFocusListener(l);
		}
	}

	@Override
	public void removeValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && this.valueListener.contains(l)) {
			this.valueListener.remove(l);
		}
	}

	public List getValueChangeListeners() {
		return this.valueListener;
	}

	/**
	 * Detects fire value events.
	 * <p>
	 * @param newValue the new value
	 * @param oldValue the previous value
	 * @param type the type of value event
	 */
	protected void fireValueChanged(final Object newValue, final Object oldValue, final int type) {
		if (!this.fireValueEvents) {
			return;
		}
		for (int i = 0; i < this.valueListener.size(); i++) {
			((ValueChangeListener) this.valueListener.get(i))
			.valueChanged(new ValueEvent(this, newValue, oldValue, type));
		}
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		if (this.dataField != null) {
			this.dataField.setFont(font);
		}
		if ((this.labelComponent != null) && (!ApplicationManager.useOntimizePlaf)) {
			this.labelComponent.setFont(font);
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector(1);
		if (this.attribute instanceof String) {
			v.add(this.attribute);
		}
		if (this.tipKey != null) {
			v.add(this.tipKey);
		}
		if (this.labelText != null) {
			v.add(this.labelText);
		}
		return v;
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (permission) {
				super.setVisible(visible);
				if ((!this.showLabel) && this.labelComponent.isVisible()) {
					this.labelComponent.setVisible(false);
				}
			}
		} else {
			super.setVisible(visible);
			if ((!this.showLabel) && this.labelComponent.isVisible()) {
				this.labelComponent.setVisible(false);
			}
		}
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
		this.updateBackgroundColor();
	}

	/**
	 * Updates the background color.
	 */
	protected void updateBackgroundColor() {
		try {

			if (this.requiredBorder != null) {
				if (this.noRequiredBorder == null) {
					this.noRequiredBorder = this.dataField.getBorder();
				}
				if (!this.enabled) {
					this.dataField.setForeground(this.fontColor);
					this.dataField.setBackground(this.disabledbgcolor);
				} else {
					this.dataField.setBorder(
							this.required ? BorderManager.getBorder(this.requiredBorder) : this.noRequiredBorder);
					this.dataField.setBackground(this.backgroundColor);
					this.dataField.setForeground(this.fontColor);
				}
			} else if (DataField.ASTERISK_REQUIRED_STYLE) {
				if (this.enabled) {
					((AuxPanel) this.panel).setAsteriskVisible(this.isRequired());
					this.dataField.setForeground(this.fontColor);
					this.dataField.setBackground(this.backgroundColor);
				} else {
					((AuxPanel) this.panel).setAsteriskVisible(false);
					this.dataField.setForeground(this.fontColor);
					this.dataField.setBackground(this.disabledbgcolor);
				}
			} else {
				if (!this.enabled) {
					this.dataField.setForeground(this.fontColor);
					this.dataField.setBackground(this.disabledbgcolor);
				} else {
					if (this.required) {
						this.dataField.setBackground(DataField.requiredFieldBackgroundColor);
						this.dataField.setForeground(DataField.requiredFieldForegroundColor != null
								? DataField.requiredFieldForegroundColor : this.fontColor);
					} else {
						this.dataField.setBackground(this.backgroundColor);
						this.dataField.setForeground(this.fontColor);
					}
				}
			}
		} catch (final Exception e) {
			DataField.logger.error(null, e);
		}
	}

	/**
	 * Updates the background color according to the condition.
	 * <p>
	 * @param enabled the condition to update the background color
	 */
	protected void updateBackgroundColor(final boolean enabled) {
		try {
			if (!enabled) {
				this.dataField.setForeground(this.fontColor);
				this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
			} else {
				if (this.required) {
					this.dataField.setBackground(DataField.requiredFieldBackgroundColor);
					this.dataField.setForeground(DataField.requiredFieldForegroundColor != null
							? DataField.requiredFieldForegroundColor : this.fontColor);
				} else {
					this.dataField.setBackground(this.backgroundColor);
					this.dataField.setForeground(this.fontColor);
				}
				// dataField.setForeground(this.fontColor);
			}
		} catch (final Exception e) {
			DataField.logger.error(null, e);
		}
	}

	/**
	 * Creates a conditional(<,>,=,>=...) combo
	 */
	protected void createInstanceConditionCombo() {
		if (this.conditions == null) {
			this.conditions = new JComboBox();
			this.add(this.conditions, new GridBagConstraints(2, 0, 1, 1, 0, 0.0, GridBagConstraints.NORTHEAST,
					GridBagConstraints.NONE, new Insets(2, 2, 0, 0), 0, 0));
			this.conditions.addItem("<");
			this.conditions.addItem("<=");
			this.conditions.addItem("=");
			this.conditions.addItem(">=");
			this.conditions.addItem(">");
			this.conditions.addItem("NULL");
			this.conditions.setVisible(false);
			this.conditions.setSelectedIndex(2);
		}
	}

	/**
	 * Updates the tip for field.
	 */
	protected void updateTip() {
		if (this.tipKey != null) {
			if (this.dataField != null) {
				try {
					if (this.resources != null) {
						this.dataField.setToolTipText(this.resources.getString(this.tipKey));
					}
				} catch (final Exception e) {
					this.dataField.setToolTipText(this.tipKey);
					DataField.logger.debug(null, e);
				}
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (this.bold) {
			this.setBold(this.bold);
		}
		this.updateFont();
		if (this.dataField != null) {
			if ((this.borderText != null) && this.borderText.equals(DataField.NONE)) {
				this.dataField.setOpaque(false);
			}
		}
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Gets the parent form.
	 * <p>
	 * @return the parent form
	 */
	public Form getParentForm() {
		return this.parentForm;
	}

	/**
	 * Return the visible permission condition.
	 * <p>
	 * @return the visible condition
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionVisible == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionVisible = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.permissionVisible != null) {
					manager.checkPermission(this.permissionVisible);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					DataField.logger.error(null, e);
				} else {
					DataField.logger.debug(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Check enabled permission.
	 * <p>
	 * @return the enabled permission
	 */
	protected boolean checkEnabledPermission() {
		if (!this.isEnabled) {
			return false;
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionActivate == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionActivate = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.permissionActivate != null) {
					manager.checkPermission(this.permissionActivate);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					DataField.logger.error(null, e);
				} else {
					DataField.logger.debug(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the definition help permission.
	 * <p>
	 * @return the define help permission
	 */
	protected boolean checkDefineHelpPermission() {
		if (!this.isEnabled) {
			return false;
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			final ApplicationPermission perm = new ApplicationPermission("definefieldshelp", false);
			try {
				// Check to show
				manager.checkPermission(perm);
				return true;
			} catch (final Exception e) {
				if (e instanceof NullPointerException) {
					DataField.logger.error(null, e);
				} else {
					DataField.logger.debug(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			final Component[] cs = new Component[2];
			cs[0] = this.labelComponent;
			cs[1] = this.dataField;
			ClientSecurityManager.registerSecuredElement(this, cs);

		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}

	}

	/**
	 * Creates a pop-up menu.
	 */
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			this.popupMenu = new ExtendedJPopupMenu();
			this.popupMenu.addSeparator();
			String sCopyText = DataField.CLIPBOARD_COPY_es_ES;
			try {
				if (this.resources != null) {
					sCopyText = this.resources.getString(DataField.CLIPBOARD_COPY);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}
			this.menuCopy = new JMenuItem(sCopyText);
			this.menuCopy.addActionListener(new CopyActionListener());

			String sCutText = DataField.CLIPBOARD_CUT_es_ES;
			try {
				if (this.resources != null) {
					sCutText = this.resources.getString(DataField.CLIPBOARD_CUT);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}

			this.menuCut = new JMenuItem(sCutText);
			this.menuCut.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						((JTextComponent) DataField.this.dataField).cut();
					} catch (final Exception ex) {
						DataField.logger.trace(null, ex);
					}
				}
			});

			String sPasteText = DataField.CLIPBOARD_PASTE_es_ES;
			try {
				if (this.resources != null) {
					sPasteText = this.resources.getString(DataField.CLIPBOARD_PASTE);
				}
			} catch (final Exception e) {
				DataField.logger.debug(null, e);
			}
			this.menuPaste = new JMenuItem(sPasteText);

			this.menuPaste.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						((JTextComponent) DataField.this.dataField).paste();
					} catch (final Exception ex) {
						DataField.logger.trace(null, ex);
					}
				}
			});

			this.popupMenu.add(this.menuCopy);
			this.popupMenu.add(this.menuCut);
			this.popupMenu.add(this.menuPaste);
			this.addHelpMenuPopup(this.popupMenu);
		}
	}

	/**
	 * Shows the help field dialog.
	 */
	protected void seeHelpField() {
		ApplicationManager.showHelpDialog(SwingUtilities.getWindowAncestor(this), "Help", this.getPreferenceHelpText(),
				false);
	}

	/**
	 * Defines the help field.
	 */
	protected void defineHelpField() {
		final String sHelp = ApplicationManager.showHelpDialog(SwingUtilities.getWindowAncestor(this), "Help",
				this.getPreferenceHelpText(), true);
		if (sHelp != null) {
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			if ((prefs != null) && (prefs.getRemoteApplicationPreferences() != null)) {
				try {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					if (sHelp.length() > 0) {
						prefs.getRemoteApplicationPreferences()
						.setRemotePreference(sessionId, null, this.getHelpPreferenceKey(), sHelp);
					} else {
						prefs.getRemoteApplicationPreferences()
						.setRemotePreference(sessionId, null, this.getHelpPreferenceKey(), null);
					}
					this.textHelpPreference = sHelp;
					prefs.getRemoteApplicationPreferences().saveRemotePreferences(sessionId);
				} catch (final Exception ex) {
					DataField.logger.trace(null, ex);
				}
			}
		}
	}

	protected String getPreferenceHelpText() {
		if (!this.initPreferenceHelpText) {
			this.textHelpPreference = null;
			try {
				final ApplicationPreferences aPrefs = ApplicationManager.getApplication().getPreferences();
				if (aPrefs.getRemoteApplicationPreferences() != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					this.textHelpPreference = aPrefs.getRemoteApplicationPreferences()
							.getRemotePreference(sessionId, null, this.getHelpPreferenceKey());
				}
			} catch (final Exception ex1) {
				DataField.logger.trace(null, ex1);
			} finally {
				this.initPreferenceHelpText = true;
			}
		}
		return this.textHelpPreference;
	}

	/**
	 * Checks whether help exists in preferences.
	 * <p>
	 * @return the help in preferences condition
	 */
	protected boolean hasHelpInPreferences() {
		return (this.getPreferenceHelpText() != null) && (this.getPreferenceHelpText().length() > 0);
	}

	/**
	 * Gets the help preference key.
	 * <p>
	 * @return the help preference key.
	 */
	protected String getHelpPreferenceKey() {
		if (this.parentForm != null) {
			return DataField.FIELD_HELP_TIP + "_" + this.parentForm.getArchiveName() + "_" + this.attribute;
		} else {
			return DataField.FIELD_HELP_TIP + "_" + this.attribute;
		}
	}

	/**
	 * Adds the help menu pop-up.
	 * <p>
	 * @param m the pop-up menu
	 */
	protected void addHelpMenuPopup(final JPopupMenu m) {
		this.menuHelpPreferences = new JMenuItem(
				ApplicationManager.getTranslation(DataField.VISUALIZE_HELP_FIELD_PREFERENCE, this.resources));
		this.menuDefineHelpPreference = new JMenuItem(
				ApplicationManager.getTranslation(DataField.DEFINE_HELP_FIELD_PREFERENCE, this.resources));
		this.menuHelpPreferences.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DataField.this.seeHelpField();
			}
		});
		this.menuDefineHelpPreference.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DataField.this.defineHelpField();
			}
		});
		m.addSeparator();
		m.add(this.menuHelpPreferences);
		m.add(this.menuDefineHelpPreference);
	}

	/**
	 * Configures the pop-up menu help.
	 */
	protected void configurePopupMenuHelp() {
		if (ApplicationManager.getApplication() == null) {
			return;
		}
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		boolean remotePreferencesExist = false;
		if (prefs != null) {
			remotePreferencesExist = prefs.getRemoteApplicationPreferences() != null;
		}
		this.menuHelpPreferences.setVisible(this.hasHelpInPreferences() && remotePreferencesExist);
		this.menuDefineHelpPreference
		.setVisible(this.checkDefineHelpPermission() && remotePreferencesExist && (this.parentForm != null));
	}

	/**
	 * Shows the pop-up menu.
	 * <p>
	 * @param source the component
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void showPopupMenu(final Component source, final int x, final int y) {
		if ((this.dataField instanceof JTextComponent) && (this.popupMenu == null)) {
			this.createPopupMenu();
		}

		// When this data field is used as a CellEditor in a table this
		// component does not receive the changes in application bundle
		// Using this code it is possible to use at least the application locale
		// in the popup
		final ResourceBundle oldResources = this.resources;
		this.setResourceBundle(this.resources != null ? this.resources : ApplicationManager.getApplicationBundle());
		this.resources = oldResources;

		if (!this.dataField.isEnabled()) {
			if (this.popupMenu != null) {
				this.configurePopupMenuHelp();
				this.menuPaste.setEnabled(false);
				this.menuCut.setEnabled(false);
				this.popupMenu.show(this.dataField, x, y);
			}
			return;
		}

		if (this.popupMenu != null) {
			this.menuPaste.setEnabled(true);
			if ((((JTextComponent) this.dataField).getSelectedText() != null)
					&& (((JTextComponent) this.dataField).getSelectedText().length() > 0)) {
				this.menuCopy.setEnabled(true);
				this.menuCut.setEnabled(true);
			} else {
				this.menuCopy.setEnabled(false);
				this.menuCut.setEnabled(false);
			}
			this.configurePopupMenuHelp();
			this.popupMenu.show(source, x, y);
		}
	}

	/**
	 * Installs a pop-up menu listener.
	 */
	protected void installPopupMenuListener() {
		this.dataField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getModifiers() == InputEvent.META_MASK) {
					DataField.this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * Creates a field focus listener.
	 * <p>
	 * @return the field focus listener
	 */
	protected FieldFocusListener createFocusListener() {
		return new FieldFocusListener();
	}

	/**
	 * Adds the focus listener to the data field.
	 */
	protected void installFocusListener() {
		if ((this.dataField != null) && !ApplicationManager.useOntimizePlaf) {
			this.dataField.addFocusListener(this.fieldlistenerFocus);
		}
	}

	/**
	 * Gets the help identifier string.
	 * <p>
	 * @return the help identifier
	 */
	@Override
	public String getHelpIdString() {
		String sClassName = this.getClass().getName();
		sClassName = sClassName.substring(sClassName.lastIndexOf(".") + 1);
		return sClassName + "HelpId";
	}

	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();

			HelpUtilities.setHelpIdString(this, helpId);
			HelpUtilities.setHelpIdString(this.labelComponent, helpId);
			HelpUtilities.setHelpIdString(this.dataField, helpId);
		} catch (final Exception e) {
			DataField.logger.debug(null, e);
			return;
		}
	}

	/**
	 * Checks whether data field label is visible.
	 * <p>
	 * @return the visibility condition
	 */
	public boolean isLabelVisible() {
		return this.showLabel;
	}

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Returns <code>true</code> whether advanced query is enabled for component.
	 * @return advanced query condition.
	 */
	public boolean isAdvancedQueryMode() {
		return this.advancedQueryMode;
	}

	/**
	 * This class creates a field button.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class FieldButton extends RolloverButton {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		public static final String FIELDBUTTON = "FieldButton";

		public static Boolean defaultPaintFocus;

		public static Boolean defaultContentAreaFilled;

		public static Boolean defaultCapable;

		/**
		 * The reference to high. By default, 22.
		 */
		protected int high = DataField.defaultFieldButtonHigh;

		public int getHigh() {
			return high;
		}

		public void setHigh(final int high) {
			this.high = high;
		}

		/**
		 * Class constructor. Calls to <code>super()</code> with <code>text</code> parameter.
		 * <p>
		 * @param text the name of button
		 */
		public FieldButton(final String text) {
			super(text);
		}

		@Override
		public boolean isDefaultCapable() {
			if (FieldButton.defaultCapable != null) {
				return FieldButton.defaultCapable.booleanValue();
			}
			return false;
		}

		/**
		 * Class constructor. Calls to <code>super()</code> and sets the border.
		 */
		public FieldButton() {
			super();
			this.setBorder(new SoftButtonBorder());
		}

		/**
		 * Class constructor. Calls to <code>super()</code> with <code>icon</code> parameter and sets the
		 * border.
		 */
		public FieldButton(final Icon icon) {
			super(icon);
			this.setBorder(new SoftButtonBorder());
		}

		@Override
		public Dimension getMinimumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			d.height = this.high;
			return d;
		}

		@Override
		public String getName() {
			return FieldButton.FIELDBUTTON;
		}

		@Override
		public void setOpaque(final boolean isOpaque) {
			super.setOpaque(isOpaque);
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (FieldButton.defaultContentAreaFilled != null) {
				super.setContentAreaFilled(FieldButton.defaultContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

		@Override
		public void setFocusPainted(final boolean focusable) {
			if (FieldButton.defaultPaintFocus != null) {
				super.setFocusable(FieldButton.defaultPaintFocus.booleanValue());
				return;
			}
			super.setFocusable(focusable);
		}

	}

	/**
	 * This class creates a toggle field button.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class ToggleButton extends JToggleButton {

		public static Boolean defaultPaintFocus;

		public static Boolean defaultContentAreaFilled;

		public static Boolean defaultCapable;

		/**
		 * The reference to high. By default, 22.
		 */
		protected int high = DataField.defaultFieldButtonHigh;

		/**
		 * Class constructor. Calls to <code>super()</code> with <code>text</code> parameter.
		 * <p>
		 * @param text the name of button
		 */
		public ToggleButton(final String text) {
			super(text);
		}

		public boolean isDefaultCapable() {
			if (ToggleButton.defaultCapable != null) {
				return ToggleButton.defaultCapable.booleanValue();
			}
			return false;
		}

		/**
		 * Class constructor. Calls to <code>super()</code> and sets the border.
		 */
		public ToggleButton() {
			super();
			this.setBorder(new SoftButtonBorder());
		}

		/**
		 * Class constructor. Calls to <code>super()</code> with <code>icon</code> parameter and sets the
		 * border.
		 */
		public ToggleButton(final Icon icon) {
			super(icon);
			this.setBorder(new SoftButtonBorder());
		}

		@Override
		public Dimension getMinimumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			d.height = this.high;
			return d;
		}

		@Override
		public String getName() {
			return "ToggleButton";
		}

		@Override
		public void setOpaque(final boolean isOpaque) {
			super.setOpaque(isOpaque);
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (ToggleButton.defaultContentAreaFilled != null) {
				super.setContentAreaFilled(ToggleButton.defaultContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

	}

	/**
	 * Parses strings to boolean conditions converting yes/no or true/false strings to true/false
	 * boolean values.
	 * @param s the string to parse
	 * @param defaultValue the default value to return no successfully
	 * @return the parsed condition
	 */
	public static boolean parseBoolean(final String s, final boolean defaultValue) {
		if (s == null) {
			return defaultValue;
		}
		if (s.equals("yes") || s.equals("true")) {
			return true;
		}
		if (s.equals("no") || s.equals("false")) {
			return false;
		}
		return defaultValue;
	}

	/**
	 * Sets required the background color to field.
	 * @param c the color to set
	 */
	public static void setRequiredFieldBackground(final Color c) {
		DataField.logger.debug("Required field background color: {}", c);
		DataField.requiredFieldBackgroundColor = c;
	}

	public Color getDisabledbgcolor() {
		return this.disabledbgcolor;
	}

	public void setDisabledbgcolor(final Color disabledbgcolor) {
		this.disabledbgcolor = disabledbgcolor;
	}

	@Override
	public void preferenceChanged(final PreferenceEvent e) {
		final String pref = e.getPreference();
		if (pref.equals(BasicApplicationPreferences.REQUIRED_FIELDS_BG_COLOR)) {
			if (this.isRequired() && this.isEnabled()) {
				final String sValue = e.getValue();
				if (sValue != null) {
					try {
						final Color c = ColorConstants.parseColor(sValue);
						if (DataField.requiredFieldBackgroundColor != c) {
							DataField.requiredFieldBackgroundColor = c;
						}
						if (this.dataField != null) {
							this.dataField.setBackground(c);
							this.dataField.repaint();
						}
					} catch (final Exception ex) {
						DataField.logger.error(null, ex);
					}
				}
			}
		} else if (pref.equals(BasicApplicationPreferences.FOCUSED_FIELD_BG_COLOR)) {
			final String sValue = e.getValue();
			if (sValue != null) {
				try {
					final Color c = ColorConstants.parseColor(sValue);
					if (DataField.FOCUS_BACKGROUNDCOLOR != c) {
						DataField.FOCUS_BACKGROUNDCOLOR = c;
					}
					if ((this.dataField != null) && this.dataField.hasFocus()) {
						this.dataField.setBackground(c);
						this.dataField.repaint();
					}
					if (this.fieldlistenerFocus != null) {
						this.fieldlistenerFocus.setSourceBackgroundColor(null);
					}
				} catch (final Exception ex) {
					DataField.logger.error(null, ex);
				}
			}
		}
	}

	@Override
	public void initPreferences(final ApplicationPreferences aPrefs, final String user) {
		try {
			// Background
			String pref = aPrefs.getPreference(user, BasicApplicationPreferences.REQUIRED_FIELDS_BG_COLOR);

			if (pref != null) {
				final Color c = ColorConstants.parseColor(pref);
				if (DataField.requiredFieldBackgroundColor != c) {
					DataField.requiredFieldBackgroundColor = c;
				}
			}

			pref = aPrefs.getPreference(user, BasicApplicationPreferences.FOCUSED_FIELD_BG_COLOR);

			if (pref != null) {
				final Color c = ColorConstants.parseColor(pref);
				if (DataField.FOCUS_BACKGROUNDCOLOR != c) {
					DataField.FOCUS_BACKGROUNDCOLOR = c;
				}
			}
		} catch (final Exception ex) {
			DataField.logger.debug(null, ex);
		}
	}

	/**
	 * Gets the border.
	 * <p>
	 * @param border the string with border keys (NONE, RAISED, ...)
	 * @return the border specification
	 */
	protected Border getBorder(final String border) {
		if (border.equals(DataField.NONE)) {
			return new EmptyBorder(0, 0, 0, 0);
		} else if (border.equals(DataField.RAISED)) {
			return new EtchedBorder(EtchedBorder.RAISED);
		} else if (border.equals(DataField.LOWERED)) {
			return new EtchedBorder(EtchedBorder.LOWERED);
		} else {
			try {
				final Color c = ColorConstants.colorNameToColor(border.toString());
				return new LineBorder(c);
			} catch (final Exception e) {
				final Border bmBorder = BorderManager.getBorder(border);
				if (bmBorder != null) {
					return bmBorder;
				}
				DataField.logger.error("Error getBoder", e);
				return null;
			}
		}

	}

	public static void changeOpacity(final JComponent c, final boolean opaque) {
		c.setOpaque(opaque);
		final Component[] components = c.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComponent) {
				DataField.changeOpacity((JComponent) components[i], opaque);
			}
		}
	}

	protected void changeButton(final AbstractButton button, final boolean borderbuttons, final boolean opaquebuttons,
			final MouseListener listenerHighlightButtons) {
		if (button != null) {
			button.setFocusPainted(false);
			if (!borderbuttons) {
				button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
			if (!opaquebuttons) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (listenerHighlightButtons != null) {
				button.addMouseListener(listenerHighlightButtons);
			}
		}
	}

	// @Override
	// public void setDefaultValue() {
	// if (this.defaultValue != null) {
	// Object oPreviousValue = this.getValue();
	// try {
	// Document document = ((JTextField) this.dataField).getDocument();
	// document.remove(0, document.getLength());
	// document.insertString(0, this.defaultValue, null);
	// } catch (Exception e) {
	// DataField.logger.debug("set DefaultValue.", e);
	// }
	// this.valueSave = this.getValue();
	// this.fireValueChanged(this.valueSave, oPreviousValue,
	// ValueEvent.PROGRAMMATIC_CHANGE);
	// }
	// }

	@Override
	public void setDefaultValue() {
		if (this.defaultValue != null) {
			this.setValue(this.defaultValue);
		}
	}

	@Override
	public void free() {
		DataField.logger.trace("Dispose {} {}", this.getClass().getName(), this.attribute);
		this.popupMenu = null;
		this.menuCopy = null;
		this.menuPaste = null;
		this.menuCut = null;
		this.menuHelpPreferences = null;
		this.menuDefineHelpPreference = null;
		this.textHelpPreference = null;
		this.valueListener = null;
		this.parentForm = null;
		this.noRequiredBorder = null;
		this.attribute = null;
	}

}
