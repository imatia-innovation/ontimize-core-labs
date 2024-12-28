package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * Class to implement a component for signature introduction. It is formed by a label (which shows
 * the title), a SignLabel (JLabel which stores the sign) and a JButton to clean the signature.
 *
 * @author Imatia Innovation
 */
public class SignDataField extends JPanel implements DataComponent, AccessForm, ValueChangeDataComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(SignDataField.class);

	/**
	 * The reference to label.
	 */
	protected JLabel dataLabel;

	/**
	 * The reference to sign component.
	 */
	protected SignLabel dataField;

	/**
	 * The reference to JButton "Clean" component.
	 */
	protected JButton dataClean;

	/**
	 * The attribute key.
	 */
	public static final String ATTR = "attr";

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The alternative text for attr.
	 */
	public static final String TEXT_STR = "text";

	/**
	 * The enabled key.
	 */
	public static final String ENABLED = "enabled";

	/**
	 * The enabled key.
	 */
	public static final String VISIBLE = "visible";

	/**
	 * The reference for enabled the component (still visible).
	 */
	protected boolean enabledXML = true;

	/**
	 * The reference for visualize the component
	 */
	protected boolean visible = true;

	/**
	 * The reference for resources file. By default, null.
	 */
	protected ResourceBundle bundle = null;

	/**
	 * The default parent margin. By default, 1.
	 */
	public static int DEFAULT_PARENT_MARGIN = 1;

	/**
	 * The key to specify the label position.
	 */
	public static final String LABELPOSITION = "labelposition";

	/**
	 * The label position. By default, top.
	 * <p>
	 * {@value BorderLayout#NORTH}
	 */
	protected String labelPosition = BorderLayout.NORTH;

	/**
	 * The key to specify the label visibility.
	 */
	public static final String LABELVISIBILITY = "labelvisible";

	/**
	 * The label view. By default, true.
	 */
	protected boolean labelView = true;

	/**
	 * The reference for text label. By default, null.
	 */
	protected String labelText = null;

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
	 * The reference for restricted view
	 */
	protected boolean restricted = false;

	/**
	 * The reference to enabled permission. By default, null.
	 */
	protected FormPermission permissionEnabled = null;

	/**
	 * The reference to visible permission. By default, null.
	 */
	protected FormPermission permissionVisible = null;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The condition to indicate when field is required. By default, false.
	 */
	protected boolean required = false;

	/**
	 * The condition to indicate when field is editable. By default, true.
	 */
	protected boolean modifiable = true;

	/**
	 * Object to save the signature bytes array
	 */
	protected byte[] bytesImage;

	/**
	 * Object to save the bufferedSignImage
	 */
	protected Object bufferedSign;

	/**
	 * Object to save the signature
	 */
	protected Object valueSave = null;

	/**
	 * List for listeners. By default, empty
	 */
	protected List valueListener = new Vector();

	/**
	 * Notify the value change events. By default, null
	 */
	protected boolean fireValueEvents = true;

	/**
	 * Constructor of SignDatafield
	 * @param params A Map which contains the initialize parameters
	 * @throws Exception
	 */
	public SignDataField(final Map param) throws Exception {
		this.init(param);
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the resources bundle
	 * @param res This resource bundle contains all application translations
	 */
	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.bundle = res;
		this.dataClean.setText(ParseUtils.getString(ApplicationManager.getTranslation("Clean", this.bundle), "Clean"));
		this.dataLabel.setText(ParseUtils.getString(ApplicationManager.getTranslation(this.labelText), this.labelText));
	}

	@Override
	public List getTextsToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates the label, signature area and clean button
	 */
	public void createDataFields() {
		this.dataLabel = new JLabel();
		this.dataField = new SignLabel();
		this.dataClean = new JButton();
	}

	/**
	 * Initialize the SignDataField component with the parameters in *.xml form
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
	 *        <td>null</td>
	 *        <td>yes</td>
	 *        <td>The reference to attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>enabled</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The reference for enabled the component (still visible).</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelposition</td>
	 *        <td><i>left/right/top/bottom</td>
	 *        <td>top</td>
	 *        <td>no</td>
	 *        <td>The label position.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelvisible</td>
	 *        <td><i>true/false</td>
	 *        <td>true</td>
	 *        <td>no</td>
	 *        <td>The label view.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td>null</td>
	 *        <td>no</td>
	 *        <td>The alternative text for attr, and a reference for the text label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>visible</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The reference for visualize the component.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map params) throws Exception {
		this.createDataFields();

		final Object oAttribute = params.get(SignDataField.ATTR);
		if (oAttribute != null) {
			this.attribute = oAttribute.toString();
		} else {
			SignDataField.logger.error("{}: 'attr' parameter is required", this.getClass().toString());
			throw new IllegalArgumentException();
		}

		final Object enabled = params.get(SignDataField.ENABLED);
		if ((enabled != null) && (enabled instanceof String)) {
			final String sEnabled = enabled.toString();
			if (sEnabled.equalsIgnoreCase("no")) {
				this.enabledXML = false;
				this.setEnabled(false);
			}
		}

		final Object visible = params.get(SignDataField.VISIBLE);
		if ((visible != null) && (visible instanceof String)) {
			final String sVisible = visible.toString();
			if (sVisible.equalsIgnoreCase("no")) {
				this.visible = false;
				this.setVisible(false);
			}
		}

		final Object labelvw = params.get(SignDataField.LABELVISIBILITY);
		if ((labelvw != null) && (labelvw instanceof String)) {
			final String sLabelvw = labelvw.toString();
			if (sLabelvw.equalsIgnoreCase("false")) {
				this.labelView = false;
			}
		}

		final Object posLabel = params.get(SignDataField.LABELPOSITION);
		if ((posLabel != null) && (posLabel instanceof String)) {
			final String sPosLabel = posLabel.toString();
			if (sPosLabel.equalsIgnoreCase(SignDataField.LEFT)) {
				this.labelPosition = BorderLayout.WEST;
			} else if (sPosLabel.equalsIgnoreCase(SignDataField.RIGHT)) {
				this.labelPosition = BorderLayout.EAST;
			} else if (sPosLabel.equalsIgnoreCase(SignDataField.TOP)) {
				this.labelPosition = BorderLayout.NORTH;
			} else if (sPosLabel.equalsIgnoreCase(SignDataField.BOTTOM)) {
				this.labelPosition = BorderLayout.SOUTH;
			}
		}

		if (this.labelView) {
			if (this.labelPosition != BorderLayout.EAST) {
				this.setLayout(new BorderLayout());
				this.add(this.dataLabel, this.labelPosition);
				final JPanel panel = new JPanel(new GridBagLayout());
				panel.add(this.dataField, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
				this.add(panel, BorderLayout.CENTER);
				this.add(this.dataClean, BorderLayout.EAST);

			} else {
				this.setLayout(new BorderLayout());
				this.add(this.dataLabel, this.labelPosition);
				final JPanel panel = new JPanel(new GridBagLayout());
				panel.add(this.dataField, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
				this.add(panel, BorderLayout.CENTER);
				this.add(this.dataClean, BorderLayout.WEST);
			}
		} else {
			this.setLayout(new BorderLayout());
			final JPanel panel = new JPanel(new GridBagLayout());
			panel.add(this.dataField, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
			this.add(panel, BorderLayout.CENTER);
			this.add(this.dataClean, BorderLayout.EAST);
		}

		this.labelText = ParseUtils.getString((String) params.get(SignDataField.TEXT_STR),
				(String) this.getAttribute());

		this.dataClean.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object oPreviousValue = SignDataField.this.getValue();
				SignDataField.this.deleteData();
				SignDataField.this.fireValueChanged(SignDataField.this.getValue(), oPreviousValue,
						ValueEvent.USER_CHANGE);
			}
		});

		this.dataField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (SignDataField.this.dataField.isEnabled()) {
					final Object oPreviousValue = SignDataField.this.getValue();
					SignDataField.this.dataField.getArrayPoints().add(null);
					try {
						SignDataField.this.dataField.bufferedSignLabel = SignLabel
								.jcomponentToImage(SignDataField.this.dataField);
						final ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(SignDataField.this.dataField.bufferedSignLabel, "png", baos);
						baos.flush();
						SignDataField.this.dataField.bytesImageLabel = baos.toByteArray();
						baos.close();

						SignDataField.this.fireValueChanged(SignDataField.this.getValue(), oPreviousValue,
								ValueEvent.USER_CHANGE);
					} catch (final IOException ex) {
						SignDataField.logger.error(null, ex);
					}
				}
			}
		});

		this.dataField.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(final MouseEvent e) {
				if (SignDataField.this.dataField.isEnabled()) {
					SignDataField.this.dataField.getArrayPoints().add(e.getPoint());
					SignDataField.this.dataField.repaint();
				}
			}
		});
	}

	/**
	 * Initialize the component's permissions, setting the interaction and visibility, checking with the
	 * users permission
	 */
	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			final Component[] cs = new Component[3];
			cs[0] = this.dataLabel;
			cs[1] = this.dataField;
			cs[2] = this.dataClean;
			ClientSecurityManager.registerSecuredElement(this, cs);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}

		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

	}

	/**
	 * Check if the user has permissions to interact with the component
	 * @return <code>true</code> The user has permission to interact with the component
	 *         <code>false</code> Otherwise
	 */
	protected boolean checkEnabledPermission() {
		if (!this.enabledXML) {
			return false;
		}

		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionEnabled == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionEnabled = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				if (this.permissionEnabled != null) {
					manager.checkPermission(this.permissionEnabled);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					SignDataField.logger.debug("{}: {}", this.getClass().toString(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Check if the user has permission to view with the component
	 * @return <code>true</code> The user has permission to view the component <code>false</code>
	 *         Otherwise
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
				if (this.permissionVisible != null) {
					manager.checkPermission(this.permissionVisible);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					SignDataField.logger.debug("{}: {}", this.getClass().toString(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Set the enabled/disabled state of the component
	 */
	@Override
	public void setEnabled(final boolean enabled) {

		if (enabled) {
			if (!this.checkEnabledPermission()) {
				return;
			}
		}

		super.setEnabled(enabled);

		this.dataLabel.setEnabled(enabled);
		this.dataClean.setEnabled(enabled);
		this.dataField.setEnabled(enabled);

		if (!enabled) {
			this.dataField.setOpaque(false);
		} else {
			this.dataField.setOpaque(true);
		}

	}

	/**
	 * Set the visibility state of the component
	 */
	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (permission) {
				super.setVisible(visible);
			}
		} else {
			super.setVisible(false);
		}
	}

	/**
	 * Return a new GridBagConstraints
	 * @param parentLayout The LayoutManager of the parentLayout
	 * @return A new GridBagContraints
	 */
	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		return new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(SignDataField.DEFAULT_PARENT_MARGIN, SignDataField.DEFAULT_PARENT_MARGIN,
						SignDataField.DEFAULT_PARENT_MARGIN, SignDataField.DEFAULT_PARENT_MARGIN),
				0,
				0);
	}

	/**
	 * Check if the component is restricted
	 * @return <code>true</code> The component is restricted <code>false</code> The component is not
	 *         restricted
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Obtain the component's attributte
	 * @return The component's attribute
	 */
	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Class to create the signature panel and paints the signature.
	 *
	 * @author Imatia Innovation
	 *
	 */
	public static class SignLabel extends JLabel {

		/**
		 * ArrayList of Points of signature
		 */
		protected ArrayList<Point> points = new ArrayList<Point>();

		/**
		 * BufferedImage of signature
		 */
		protected BufferedImage bufferedSignLabel = null;

		/**
		 * Bytes array of signature
		 */
		protected byte[] bytesImageLabel = null;

		/**
		 * Converts the bytes array to a BufferedImage
		 * @param ba The bytes array of the signature
		 * @return The BufferedImage of signature
		 */
		public static BufferedImage bytesArrayToBufferedImage(final byte[] ba) {

			final InputStream in = new ByteArrayInputStream(ba);
			try {
				final BufferedImage bi = ImageIO.read(in);
				return bi;
			} catch (final IOException e) {
				SignDataField.logger.error(null, e);
				return null;
			}
		}

		/**
		 * Get the ArrayPoint of the signature
		 * @return the array points of signature
		 */
		public ArrayList<Point> getArrayPoints() {
			return this.points;
		}

		/**
		 * The constructor of the signature panel
		 */
		public SignLabel() {
			this.setBackground(Color.WHITE);
			this.setOpaque(true);
		}

		/**
		 * Paint the array points of signature
		 * @param g Graphic
		 */
		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			g.drawImage(this.bufferedSignLabel, 0, 0, null);

			for (int i = 0; i < (this.points.size() - 2); i++) {

				final Point p1 = this.points.get(i);
				final Point p2 = this.points.get(i + 1);

				if ((p1 != null) && (p2 != null)) {
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}

			}
		}

		/**
		 * Set the border of signature panel
		 * @param border Set the component's border
		 */
		@Override
		public void setBorder(final Border border) {
			super.setBorder(border);
		}

		/**
		 * Converts the JComponent signature in an image
		 * @param component The component of signature field
		 * @return A BufferedImage signature
		 * @throws IOException
		 */
		public static BufferedImage jcomponentToImage(final Component component) throws IOException {
			final BufferedImage img = new BufferedImage(component.getWidth(), component.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			final Graphics g = img.getGraphics();
			g.setColor(component.getForeground());
			g.setFont(component.getFont());
			component.paintAll(g);

			return img.getSubimage(0, 0, component.getWidth() - 1, component.getHeight() - 1);

		}

	}

	/**
	 * Sets the parent form of SignDataField
	 * @param f The parent form
	 */
	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Obtains the label of SignDataField
	 * @return The label of SignDataField
	 */
	@Override
	public String getLabelComponentText() {
		return this.dataLabel.getText();
	}

	/**
	 * Check if the SignDataField is modifiable
	 * @return <code>true</code> The field is modifiable
	 */
	@Override
	public boolean isModifiable() {
		return true;
	}

	/**
	 * Check the visibility of SignDataField
	 * @return <code>true</code> The field is not visible <code>false</code> The field is visible
	 */
	@Override
	public boolean isHidden() {
		return !this.visible;
	}

	/**
	 * Obtain the SQL Data type for SignDataField
	 * @return Type LONGVARBINARY
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.LONGVARBINARY;
	}

	/**
	 * Check if the component is required or optional
	 * @return <code>false</code> The field is optional
	 */
	@Override
	public boolean isRequired() {
		return false;
	}

	/**
	 * Gets the value of SignLabel field
	 * @return A signature BytesBlock
	 */
	@Override
	public Object getValue() {
		this.bytesImage = this.dataField.bytesImageLabel;

		if (this.isEmpty()) {
			return null;
		}
		final BytesBlock bytesBlock = new BytesBlock(this.dataField.bytesImageLabel, BytesBlock.NO_COMPRESSION);
		return bytesBlock;
	}

	/**
	 * Check if the signature field has changed
	 * @param newValue the new value of SignLabel
	 * @param oldValue the old value of SignLabel
	 * @param type Integer indicating who made the changes , if the user or application
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

	/**
	 * Set the value of SignDataField
	 * @param value The value to set.
	 */
	@Override
	public void setValue(final Object value) {
		if ((value != null) && (value instanceof BytesBlock)) {
			// Cache:
			final Object oCurrentValue = this.getValue();
			if (!this.isEmpty()) {
				if (oCurrentValue.equals(value)) {
					this.valueSave = this.getValue();
					return;
				}
			}

			this.bytesImage = null;
			this.bytesImage = ((BytesBlock) value).getBytes();
			this.dataField.bytesImageLabel = this.bytesImage;
			this.update();
			this.valueSave = this.getValue();
		} else {
			this.deleteData();
		}

	}

	/**
	 * Updates the SignLabel
	 */
	protected void update() {
		if (this.bytesImage == null) {
			this.dataField.bufferedSignLabel = null;
			this.dataField.repaint();
		} else {

			this.dataField.bufferedSignLabel = SignLabel.bytesArrayToBufferedImage(this.bytesImage);
			this.dataField.repaint();
		}
	}

	/**
	 * Delete the SignLabel
	 */
	@Override
	public void deleteData() {
		SignDataField.this.dataField.getArrayPoints().clear();
		SignDataField.this.dataField.bufferedSignLabel = null;
		SignDataField.this.dataField.bytesImageLabel = null;

		SignDataField.this.dataField.repaint();
	}

	/**
	 * Check if the SignLabel is empty
	 * @return <code>true</code> The field is empty <code>false</code> The field is not empty
	 */
	@Override
	public boolean isEmpty() {
		if (this.bytesImage == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the modifiable behavior
	 * @param modibiable
	 */
	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	/**
	 * Check if the SignLabel was modified
	 * @return <code>true</code>The component was modified <code>false</code>The component was not
	 *         modified
	 */
	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			SignDataField.logger.debug("Component: {} Modified. Previous value = {} New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			SignDataField.logger.debug("Component: {} Modified. Previous value = {} New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			SignDataField.logger.debug("Component: {} Modified. Previous value = {} New value = {}", this.attribute,
					this.valueSave, oValue);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set if component is required
	 * @param required Boolean
	 */
	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * Adds a value change listener
	 * @param l The ValueChangeLitener
	 */
	@Override
	public void addValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && !this.valueListener.contains(l)) {
			this.valueListener.add(l);
		}
	}

	/**
	 * Remove the value change listener
	 * @param l The ValueChangeListener
	 */
	@Override
	public void removeValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && (this.valueListener.contains(l))) {
			this.valueListener.remove(l);
		}

	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
