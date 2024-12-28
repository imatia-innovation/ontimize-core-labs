package com.ontimize.gui.field;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.CollectionTools;
import com.ontimize.util.swing.BasicHTMLViewer;
import com.ontimize.util.swing.XMLTextPane;
import com.ontimize.windows.office.WindowsUtils;

/**
 *
 * The initializes parameters can be seen in {@link #init(Map)}
 *
 * @author Imatia Innovation S.L.
 * @since 5.2057EN-0.5
 */

public class Form2HTMLDataField extends IdentifiedAbstractFormComponent
implements DataComponent, ActionListener, Internationalization, ValueChangeDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(Form2HTMLDataField.class);

	public static final String XSL_LIST_TAG = "xsllist";

	public static final String XSL_PARAMETERS_TAG = "xslparameters";

	public static final String XML_FIELD_ATTR_TAG = "xmlfieldattr";

	public static final String HTML_VIEWER_TAG = "htmlviewer";

	public static final String CSS_TAG = "css";

	public static final String PATTERN_TAG = "pattern";

	public static final String HTML_TITLE_TAG = "htmltitle";

	public static final String HTML_VIEWER_TITLE_TAG = "htmlviewertitle";

	public static final String XSL_LABEL_TAG = "xsllabel";

	public static final String PATTERN_STYLE_TAG = "<!-- style -->";

	public static final String PATTERN_BODY_TAG = "<!-- body -->";

	public static final String DEFAULT_HTML_PATTERN = "<html> \n" + " <head> \n" + "  <style type=\"text/css\"> \n"
			+ "    " + Form2HTMLDataField.PATTERN_STYLE_TAG + "  </style> \n" + " </head> \n" + " <body> \n" + "   "
			+ Form2HTMLDataField.PATTERN_BODY_TAG + " </body> \n" + "</html>";

	public static final String RELOAD_TEXT = "Form2HTMLDataField.RELOAD_TEXT";

	public static final String APPLY_TEXT = "Form2HTMLDataField.APPLY_TEXT";

	public static final String NAVIGATOR_TEXT = "Form2HTMLDataField.NAVIGATOR_TEXT";

	public static final String ZOOM_IN_TEXT = "Form2HTMLDataField.ZOOM_IN_TEXT";

	public static final String ZOOM_OUT_TEXT = "Form2HTMLDataField.ZOOM_OUT_TEXT";

	public static final String SHOW_RIGHT_TEXT = "Form2HTMLDataField.SHOW_RIGHT";

	public static final String ERROR_XSL_NOT_VALID = "Form2HTMLDataField.ERROR_XSL_NOT_VALID";

	public static final String ERROR_APPLYING_XSL = "Form2HTMLDataField.ERROR_APPLYING_XSL";

	protected boolean modifiable = true;

	protected boolean required = false;

	protected boolean fireValueEvents = true;

	protected String valueSave = "";

	protected List valueListener = new ArrayList();

	protected JButton reload = new JButton(ImageManager.getIcon(ImageManager.REFRESH));

	protected JButton apply = new JButton(ImageManager.getIcon(ImageManager.GEAR));

	protected JButton navigator = new JButton(ImageManager.getIcon(ImageManager.INTERNET));

	protected JButton zoomIn = new JButton(ImageManager.getIcon(ImageManager.ZOOM_IN));

	protected JButton zoomOut = new JButton(ImageManager.getIcon(ImageManager.ZOOM_OUT));

	protected JButton right = new JButton(ImageManager.getIcon(ImageManager.RIGHT));

	protected BasicHTMLViewer viewer = null;

	protected JComboBox xslList = null;

	protected Map xslParameters = new Hashtable();

	protected XMLTextPane html = new XMLTextPane();

	protected String xmlFieldAttr = "";

	protected String cssContentFile = null;

	protected String patternContentFile = null;

	protected String htmlTitle = null;

	protected String htmlViewerTitle = null;

	protected String xslLabel = null;

	protected JComponent menuComponent = null;

	protected JLabel jlXSLLabel = new JLabel("");

	;
	protected JPanel htmlEditorPanel = new JPanel();

	protected ResourceBundle resourceBundle = ApplicationManager.getApplicationBundle();

	protected Vector				xslListValues			= new Vector();

	protected TransformerFactory tFactory = TransformerFactory.newInstance();

	protected Transformer transformer = null;

	private class CustomDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(final DocumentEvent e) {
			Form2HTMLDataField.this.htmlTextModified();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			Form2HTMLDataField.this.htmlTextModified();
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			Form2HTMLDataField.this.htmlTextModified();
		}

	}

	/**
	 * This field transform an Ontimize Form in a HTML Form that can be view in a web browser.<BR>
	 * It contains basically a HTML Editor, a HTML Viewer and a combo with the transform
	 * style-sheets.<BR>
	 * This field takes the source from another field, refenreced by the <I>xmlFieldAttr</I>
	 * attribute,<BR>
	 * then applies the <code>xsl</code> selected to the content, and stores the result in the component
	 * or in the <BR>
	 * database. The component can also show the result of the transformation using the HTML viewer<BR>
	 * @param parameters The <code>Hashtable</code> with parameters
	 */
	public Form2HTMLDataField(final Map parameters) {
		super();
		this.init(parameters);

		this.xslList = new JComboBox(this.xslListValues);
		this.initButtons();
		this.setText();
		this.html.getDocument().addDocumentListener(new CustomDocumentListener());

		this.createMenuComponent();
		final JComponent viewer = this.createViewerComponent();

		this.setLayout(new GridBagLayout());
		this.add(this.menuComponent, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));

		this.add(viewer, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 2, 2));
	}

	@Override
	public Object getConstraints(final LayoutManager arg0) {
		return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0);
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Adds the next parameters:
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td><I>attr</I> of field</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>xmlFieldAttr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td><I>attr</I> of the field where is stored the XML Form and which will be used for the
	 *        transformations</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>htmlTitle</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text that will appear in the border of the HTML Field</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>htmlViewerTitle</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text that will appear in the border of the HTML Viewer Field</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>xslLabel</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text that will appear in the component</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>xslList</td>
	 *        <td><I>package.filename1.xsl;package.filename2.xsl;...</I></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>List of the XSL files that can be used form transform the XML File</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>xslParameters</td>
	 *        <td><I>param1=value1;param2=value2;...</I></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>List of <I>parameters=values</I> that will be applied to all XSL transformations</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>htmlViewer</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Name of the class that will be used as a HTML Viewer</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>css</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Name of the css file that will used by the viewer</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>pattern</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Name of the pattern file that will used by the viewer</td>
	 *        </tr>
	 *
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		this.attribute = parameters.get("attr");
		Object o = parameters.get(Form2HTMLDataField.XSL_LIST_TAG);
		if (o != null) {
			this.xslListValues = new Vector(ApplicationManager.getTokensAt(o.toString(), ";"));
		}

		o = parameters.get(Form2HTMLDataField.HTML_TITLE_TAG);
		if (o != null) {
			this.htmlTitle = (String) o;
		}

		o = parameters.get(Form2HTMLDataField.HTML_VIEWER_TITLE_TAG);
		if (o != null) {
			this.htmlViewerTitle = (String) o;
		}

		o = parameters.get(Form2HTMLDataField.CSS_TAG);
		if (o != null) {
			this.cssContentFile = Form2HTMLDataField
					.fileToString(Form2HTMLDataField.class.getClassLoader().getResourceAsStream(o.toString()));
		}

		o = parameters.get(Form2HTMLDataField.PATTERN_TAG);
		if (o != null) {
			this.patternContentFile = Form2HTMLDataField
					.fileToString(Form2HTMLDataField.class.getClassLoader().getResourceAsStream(o.toString()));
		} else {
			this.patternContentFile = Form2HTMLDataField.DEFAULT_HTML_PATTERN;
		}

		o = parameters.get(Form2HTMLDataField.XML_FIELD_ATTR_TAG);
		if (o != null) {
			this.xmlFieldAttr = (String) o;
		}

		o = parameters.get(Form2HTMLDataField.XSL_LABEL_TAG);
		if (o != null) {
			this.xslLabel = (String) o;
		}

		o = parameters.get(Form2HTMLDataField.XSL_PARAMETERS_TAG);
		if (o != null) {
			final List vP = ApplicationManager.getTokensAt(o.toString(), ";");
			for (int i = 0, a = vP.size(); i < a; i++) {
				final List vp2 = ApplicationManager.getTokensAt(vP.get(i).toString(), "=");
				if (vp2.size() == 2) {
					this.xslParameters.put(CollectionTools.firstElement(vp2), CollectionTools.lastElement(vp2));
				}
			}
		}

		o = parameters.get(Form2HTMLDataField.HTML_VIEWER_TAG);
		if (o != null) {
			try {
				final Class c = Class.forName(o.toString());
				final Object obj = c.newInstance();
				if (obj instanceof BasicHTMLViewer) {
					this.viewer = (BasicHTMLViewer) obj;
				}
			} catch (final Exception ex) {
				Form2HTMLDataField.logger.error(null, ex);
			}
		}
	}

	private void initButtons() {
		this.reload.setActionCommand("reload");
		this.apply.setActionCommand("apply");
		this.navigator.setActionCommand("navigator");
		this.zoomIn.setActionCommand("zoomIn");
		this.zoomOut.setActionCommand("zoomOut");
		this.right.setActionCommand("right");

		this.reload.setPreferredSize(new Dimension(20, 20));
		this.apply.setPreferredSize(new Dimension(20, 20));
		this.navigator.setPreferredSize(new Dimension(20, 20));
		this.zoomIn.setPreferredSize(new Dimension(20, 20));
		this.zoomOut.setPreferredSize(new Dimension(20, 20));
		this.right.setPreferredSize(new Dimension(20, 20));

		this.reload.addActionListener(this);
		this.apply.addActionListener(this);
		this.navigator.addActionListener(this);
		this.zoomIn.addActionListener(this);
		this.zoomOut.addActionListener(this);
		this.right.addActionListener(this);

		this.reload.setEnabled(false);
		this.apply.setEnabled(false);
		this.navigator.setEnabled(false);
		this.zoomIn.setEnabled(false);
		this.zoomOut.setEnabled(false);
		this.right.setEnabled(false);
	}

	private void setText() {
		this.reload
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.RELOAD_TEXT, this.resourceBundle));
		this.apply
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.APPLY_TEXT, this.resourceBundle));
		this.navigator
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.NAVIGATOR_TEXT, this.resourceBundle));
		this.zoomIn
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.ZOOM_IN_TEXT, this.resourceBundle));
		this.zoomOut
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.ZOOM_OUT_TEXT, this.resourceBundle));
		this.right
		.setToolTipText(ApplicationManager.getTranslation(Form2HTMLDataField.SHOW_RIGHT_TEXT, this.resourceBundle));

		if (this.htmlTitle != null) {
			this.htmlEditorPanel
			.setBorder(new TitledBorder(ApplicationManager.getTranslation(this.htmlTitle, this.resourceBundle)));
		}
		if ((this.htmlViewerTitle != null) && (this.viewer != null)) {
			this.viewer.setBorder(
					new TitledBorder(ApplicationManager.getTranslation(this.htmlViewerTitle, this.resourceBundle)));
		}
		if (this.xslLabel != null) {
			this.jlXSLLabel.setText(ApplicationManager.getTranslation(this.xslLabel, this.resourceBundle));
		}
	}

	private void createMenuComponent() {
		this.menuComponent = new JPanel();
		this.menuComponent.setLayout(new GridBagLayout());

		int i = 0;

		if (this.xslLabel != null) {
			this.menuComponent.add(this.jlXSLLabel, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
			i += 1;
		}

		this.menuComponent.add(this.xslList, new GridBagConstraints(i, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
		i += 1;
		this.menuComponent.add(this.apply, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		i += 1;
		this.menuComponent.add(this.navigator, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		i += 1;
		this.menuComponent.add(this.reload, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
	}

	private JComponent createViewerComponent() {
		this.createHTMLEditorPanel();
		if (this.viewer != null) {
			final JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			sp.setLeftComponent(this.htmlEditorPanel);
			sp.setRightComponent(this.viewer);
			sp.setDividerSize(10);
			sp.setOneTouchExpandable(true);
			sp.setResizeWeight(0.5);
			return sp;
		} else {
			return this.htmlEditorPanel;
		}
	}

	private void createHTMLEditorPanel() {
		this.htmlEditorPanel.setLayout(new GridBagLayout());
		final JScrollPane js = new JScrollPane(this.html);
		this.htmlEditorPanel.add(js, new GridBagConstraints(0, 0, 1, 3, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
		this.htmlEditorPanel.add(this.zoomIn, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		this.htmlEditorPanel.add(this.zoomOut, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		this.htmlEditorPanel.add(this.right, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		js.getViewport().setPreferredSize(new Dimension(200, 400));
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		this.reload.setEnabled(enabled);
		this.apply.setEnabled(enabled);
		this.navigator.setEnabled(enabled);
		this.xslList.setEnabled(enabled);
		this.html.setEnabled(enabled);
		this.zoomIn.setEnabled(enabled);
		this.zoomOut.setEnabled(enabled);

		if (this.viewer != null) {
			this.viewer.setEnabled(enabled);
			this.right.setEnabled(enabled);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);
		this.resourceBundle = resourceBundle;
		this.setText();
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public int getSQLDataType() {
		return Types.VARCHAR;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return this.modifiable;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public void setValue(final Object value) {
		this.fireValueEvents = false;
		this.html.setText(value != null ? value.toString() : "");
		this.valueSave = value != null ? value.toString() : "";
		if (this.viewer != null) {
			if (value != null) {
				final String htmlString = this.createHTML();
				if (htmlString != null) {
					this.viewer.setHTML(htmlString);
				}
			} else {
				this.viewer.setHTML(this.patternContentFile);
			}
		}
		this.fireValueEvents = true;
	}

	@Override
	public Object getValue() {
		return this.html.getText();
	}

	@Override
	public void deleteData() {
		this.fireValueEvents = false;
		this.html.setText("");
		this.valueSave = "";
		if (this.viewer != null) {
			this.viewer.setHTML(this.patternContentFile);
		}
		this.fireValueEvents = true;
	}

	private void htmlTextModified() {
		this.fireValueChanged(null, this.html.getText(), ValueEvent.USER_CHANGE);
	}

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
	public void addValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && !this.valueListener.contains(l)) {
			this.valueListener.add(l);
		}
	}

	@Override
	public void removeValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && this.valueListener.contains(l)) {
			this.valueListener.remove(l);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		if (ApplicationManager.DEBUG) {
			Form2HTMLDataField.logger.debug("command = " + e.getActionCommand());
		}

		if (e.getActionCommand().equals("reload")) {
			this.fireValueEvents = false;
			this.html.setText(this.valueSave);
			if (this.viewer != null) {
				this.viewer.setHTML(this.patternContentFile);
			}
			this.fireValueEvents = true;
		}
		if ("apply".equals(e.getActionCommand())) {
			this.applyXSL();
		}
		if ("navigator".equals(e.getActionCommand())) {
			this.openInBrowser();
		}

		if ("right".equals(e.getActionCommand())) {
			if (this.viewer == null) {
				return;
			}
			final String htmlString = this.createHTML();
			if (htmlString != null) {
				this.viewer.setHTML(htmlString);
			}
		}

		if ("zoomIn".equals(e.getActionCommand())) {
			final Font f = this.html.getFont();
			this.html.setFont(new Font(f.getFontName(), f.getStyle(), f.getSize() + 1));
		}
		if ("zoomOut".equals(e.getActionCommand())) {
			final Font f = this.html.getFont();
			this.html.setFont(new Font(f.getFontName(), f.getStyle(), f.getSize() - 1));
		}
	}

	protected String createHTML() {
		String htmlString = this.patternContentFile;
		final String style = this.cssContentFile;
		if (htmlString == null) {
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("No Pattern Content File Found");
			}
			return null;
		}
		htmlString = this.appendString(htmlString, Form2HTMLDataField.PATTERN_STYLE_TAG, style);
		htmlString = this.appendString(htmlString, Form2HTMLDataField.PATTERN_BODY_TAG, this.html.getText());
		return htmlString;
	}

	protected void openInBrowser() {

		final String htmlString = this.createHTML();
		try {
			final File f = File.createTempFile("wfv", ".html");
			final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			bos.write(htmlString.getBytes());
			bos.flush();
			bos.close();

			WindowsUtils.openFile(f);

			f.deleteOnExit();
		} catch (final Exception ex) {
			Form2HTMLDataField.logger.error(null, ex);
		}
	}

	protected void applyXSL() {
		final Object selectedItem = this.xslList.getSelectedItem();
		Object htmlValue = null;
		if (this.xmlFieldAttr != null) {
			if ((this.getParentForm().getElementReference(this.xmlFieldAttr) != null)
					&& (this.getParentForm().getElementReference(this.xmlFieldAttr) instanceof DataComponent)) {
				htmlValue = ((DataComponent) this.getParentForm().getElementReference(this.xmlFieldAttr)).getValue();
			}
		}

		if (ApplicationManager.DEBUG) {
			Form2HTMLDataField.logger.debug("htmlValue = " + htmlValue + " selectedItem = " + selectedItem);
		}
		if ((selectedItem == null) || (htmlValue == null)) {
			return;
		}
		try {

			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("Transformer " + selectedItem.toString());
			}
			this.transformer = this.tFactory.newTransformer(new StreamSource(
					Form2HTMLDataField.class.getClassLoader().getResourceAsStream(selectedItem.toString())));
		} catch (final Exception ex) {
			Form2HTMLDataField.logger.error(null, ex);
			this.getParentForm().message(Form2HTMLDataField.ERROR_XSL_NOT_VALID, Form.ERROR_MESSAGE, ex);
			return;
		}

		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			final ByteArrayInputStream bais = new ByteArrayInputStream(htmlValue.toString().getBytes());
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("Parameters");
			}
			if (!this.xslParameters.isEmpty()) {
				for (final Entry<?, ?> entry : ((Map<?, ?>) this.xslParameters).entrySet()) {
					final Object key = entry.getKey();
					final Object value = entry.getValue();
					if (ApplicationManager.DEBUG) {
						Form2HTMLDataField.logger.debug(key.toString() + " " + value.toString());
					}
					this.transformer.setParameter(key.toString(), value.toString());
				}
			}
			this.transformer.transform(new StreamSource(bais), new StreamResult(bos));
			bos.flush();
			baos.flush();

			final String s = new String(baos.toByteArray());

			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("HTML: " + s);
			}
			this.html.setText(s);
			final String htmlString = this.createHTML();
			if (ApplicationManager.DEBUG) {
				Form2HTMLDataField.logger.debug("HTML TOTAL: " + htmlString);
			}
			if ((htmlString != null) && (this.viewer != null)) {
				this.viewer.setHTML(htmlString);
			}

		} catch (final Exception ex) {
			this.getParentForm().message(Form2HTMLDataField.ERROR_APPLYING_XSL, Form.ERROR_MESSAGE);
			Form2HTMLDataField.logger.error(null, ex);
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (final Exception ex) {
				Form2HTMLDataField.logger.error(null, ex);
			}
		}
	}

	protected String appendString(final String base, final String toFind, String toADD) {

		final int i = base.indexOf(toFind);
		if (i == -1) {
			return base;
		}
		if (toADD == null) {
			toADD = "";
		}
		return base.substring(0, i + toFind.length()) + toADD + base.substring(i + toFind.length());
	}

	public static String fileToString(final InputStream is) {
		try {
			final BufferedInputStream fb = new BufferedInputStream(is);
			final ByteArrayOutputStream bA = new ByteArrayOutputStream();
			int car = -1;
			while ((car = fb.read()) != -1) {
				bA.write(car);
			}
			bA.close();
			return bA.toString();
		} catch (final Exception ex) {
			Form2HTMLDataField.logger.error(null, ex);
		}
		return null;
	}

}
