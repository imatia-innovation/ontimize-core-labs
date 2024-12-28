package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Freeable;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.icon.StretchIcon;

/**
 * The main class to load an .jpg or .gif image from file.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ImageField extends JPanel implements FormComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ImageField.class);

	protected ImageIcon image = null;

	protected JLabel labelCenter;

	protected String attribute = null;

	protected boolean opaque;

	protected boolean expand;

	protected String dim;

	protected Insets insets;

	/**
	 * The class constructor. Calls to JPanel <code>constructor</code> and inits parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public ImageField(final Map parameters) {
		super();
		this.setLayout(new BorderLayout());
		this.init(parameters);
	}

	/**
	 * Gets the attribute parameter.
	 * @return the <code>attribute</code> variable
	 */
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		final GridBagConstraints constraints = new GridBagConstraints(GridBagConstraints.RELATIVE,
				GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, this.insets, 0, 0);
		if (!this.expand) {
			constraints.weighty = 0.0;
		}

		if ("NO".equalsIgnoreCase(this.dim)) {
			constraints.weightx = 0.0;
			constraints.fill = GridBagConstraints.VERTICAL;
		} else if ("YES".equalsIgnoreCase(this.dim)) {
			constraints.fill = GridBagConstraints.VERTICAL;
		}

		return constraints;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
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
	 *        <td>src</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The image location.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Field opacity condition</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>width</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Sets image width</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>height</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Sets image height</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>expand</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Sets vertical redimension</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>dim</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Sets horizontal redimension</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>insets</td>
	 *        <td></td>
	 *        <td>0;0;0;0</td>
	 *        <td>no</td>
	 *        <td>Sets insets</td>
	 *        </tr>
	 *
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		final Object src = parameters.get("src");
		int width = -1;
		int height = -1;

		if (src != null) {
			final ImageIcon icon = new StretchIcon(ImageManager.getIconURL(src.toString()));
			if (icon != null) {
				this.image = icon;
				this.labelCenter = new JLabel(this.image);
				this.add(this.labelCenter, BorderLayout.CENTER);
				width = this.image.getImage().getWidth(null);
				height = this.image.getImage().getHeight(null);
			} else {
				ImageField.logger.error("File not found: {}", src.toString());
			}
		} else {
			ImageField.logger.debug("Parameter 'src' not found in component ImageField");
		}
		this.opaque = ParseUtils.getBoolean((String) parameters.get(DataField.OPAQUE), true);
		this.setOpaque(this.opaque);

		width = ParseUtils.getInteger((String) parameters.get(DataField.WIDTH), width);
		height = ParseUtils.getInteger((String) parameters.get(DataField.HEIGHT), height);
		if (this.labelCenter != null) {
			final Dimension currentDimension = this.labelCenter.getPreferredSize();
			final Dimension extD = this.getPreferredSize();
			if (width >= 0) {
				currentDimension.width = width;
			}

			if (height >= 0) {
				currentDimension.height = height;
			}
			this.setPreferredSize(currentDimension);
		}

		this.expand = ParseUtils.getBoolean((String) parameters.get(DataField.EXPAND), false);
		this.dim = ParseUtils.getString((String) parameters.get(DataField.DIM), "no");
		this.attribute = ParseUtils.getString((String) parameters.get(DataField.ATTR), null);
		this.insets = ParseUtils.getMargin((String) parameters.get(DataField.INSETS), new Insets(0, 0, 0, 0));
	}

	/**
	 * Adds the attribute parameter to the List to translate.
	 * <p>
	 * @return the List with attribute parameter.
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
	}

	/**
	 * Sets the component locale. Empty method.
	 * <p>
	 * @param locale the locale to set
	 */
	@Override
	public void setComponentLocale(final Locale locale) {
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
