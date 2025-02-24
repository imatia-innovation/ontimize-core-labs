package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.cache.ImageRepository;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * This class implements a component with two states. For each state an image is associated. Size
 * for both images must be the same. Moreover, component allows resizing (setSize(), setBounds(),
 * resize()) and internal status ('0'->OFF or '1'->ON) could be obtained from getStatus().
 *
 * @author Imatia Innovation S.L.
 */

public class ToggleImage extends JPanel implements FormComponent, AccessForm, IdentifiedElement, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ToggleImage.class);

	public static final int ON = 1;

	public static final int OFF = 0;

	public static final int ALARM = -1;

	protected static final int TOP = 10;

	protected static final int LEFT = 11;

	protected static final int BOTTOM = 12;

	protected static final int RIGHT = 13;

	public static final String RIGHT_STR = "right";

	public static final String LEFT_STR = "left";

	public static final String CENTER_STR = "center";

	public static final String LABELVISIBLE = "labelvisible";

	public static final String VISIBLE = "visible";

	public static final int NO = 0;

	public static final int YES = 1;

	public static final int TEXT = 2;

	public static final String DIM = "dim";

	public static final String YES_STR = "yes";

	public static final String TEXT_STR = "text";

	public static final String NO_STR = "no";

	public static final String ALIGN = "align";

	public static final String VALIGN = "valign";

	protected int alignment = GridBagConstraints.NORTH;

	protected int valignment = GridBagConstraints.NORTH;

	protected int labelPosition = ToggleImage.BOTTOM;

	protected int status = ToggleImage.ON;

	protected int preferredStatus = ToggleImage.OFF;

	protected int loadStatusOn = ImageObserver.SOMEBITS;

	protected int loadStatusOff = ImageObserver.SOMEBITS;

	protected int loadStatusAlarm = ImageObserver.SOMEBITS;

	protected int loadStatusDisabled = ImageObserver.SOMEBITS;

	protected ImageField onImage = null;

	protected ImageField offImage = null;

	protected ImageField alarmImage = null;

	protected ImageField disabledImage = null;

	protected URL onImageURL = null;

	protected URL offImageURL = null;

	protected URL alarmImageURL = null;

	protected URL disabledImageURL = null;

	protected String attribute = null;

	protected String labelText = null;

	protected int width = -1;

	protected int high = -1;

	protected int dim = ToggleImage.NO;

	protected JLabel attributeLabel = new JLabel();

	protected JPanel labelPanel = new JPanel();

	protected JImage panelImage = null;

	AlarmThread alarmThread = null;

	protected boolean isAlarmImage = false;

	protected Form parentForm = null;

	private boolean callForm = true;

	protected FormPermission visiblePermission = null;

	protected FormPermission enabledPermission = null;

	protected boolean showLabel = true;

	protected boolean show = true;

	class AlarmThread extends Thread {

		boolean stopThread = false;

		Component c = null;

		public AlarmThread(final Component comp) {
			this.c = comp;
		}

		@Override
		public void start() {
			super.start();
			if (ToggleImage.this.callForm) {
				if (ToggleImage.this.parentForm != null) {
					ToggleImage.this.parentForm.showMe(this.c);
				}
			}
		}

		@Override
		public void run() {
			while (!this.stopThread) {
				ToggleImage.this.isAlarmImage = !ToggleImage.this.isAlarmImage;
				if (ToggleImage.this.isAlarmImage) {
					ToggleImage.this.panelImage.setImage(ToggleImage.this.alarmImageURL);
				} else {
					ToggleImage.this.panelImage.setImage(ToggleImage.this.offImageURL);
				}
				if (this.c.isShowing()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							AlarmThread.this.c.repaint();
						}
					});
					// } else {
					}
				try {
					Thread.sleep(500);
				} catch (final Exception e) {
					ToggleImage.logger.trace(null, e);
				}
			}
		}

		public void interruptAlarm() {
			ToggleImage.this.isAlarmImage = false;
			this.stopThread = true;
		}

	}

	class OnImageObserver implements ImageObserver {

		@Override
		public boolean imageUpdate(final java.awt.Image im, final int info, final int x, final int y, final int width, final int height) {
			ToggleImage.this.loadStatusOn = info;
			ToggleImage.this.revalidate();
			ToggleImage.this.repaint();
			return true;
		}

	}

	class OffImageObserver implements ImageObserver {

		@Override
		public boolean imageUpdate(final java.awt.Image im, final int info, final int x, final int y, final int width, final int height) {
			ToggleImage.this.loadStatusOff = info;
			ToggleImage.this.revalidate();
			ToggleImage.this.repaint();
			return true;
		}

	}

	class AlarmImageObserver implements ImageObserver {

		@Override
		public boolean imageUpdate(final java.awt.Image im, final int info, final int x, final int y, final int width, final int height) {
			ToggleImage.this.loadStatusAlarm = info;
			ToggleImage.this.revalidate();
			ToggleImage.this.repaint();
			return true;
		}

	}

	class DisabledImageObserver implements ImageObserver {

		@Override
		public boolean imageUpdate(final java.awt.Image im, final int info, final int x, final int y, final int width, final int height) {
			ToggleImage.this.loadStatusDisabled = info;
			ToggleImage.this.revalidate();
			ToggleImage.this.repaint();
			return true;
		}

	}

	protected OnImageObserver onImageObserver = new OnImageObserver();

	protected OffImageObserver offImageObserver = new OffImageObserver();

	protected AlarmImageObserver alarmImageObserver = new AlarmImageObserver();

	protected DisabledImageObserver disabledImageObserver = new DisabledImageObserver();

	public ToggleImage(final Map parameters) {
		this.init(parameters);
		this.setToolTipText("");
		this.setLayout(new GridBagLayout());
		switch (this.labelPosition) {
		case TOP:
			this.add(this.panelImage, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.labelPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			break;
		case LEFT:
			this.add(this.panelImage, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.labelPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			break;
		case RIGHT:
			this.add(this.panelImage, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.labelPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			break;
		default:
			this.add(this.panelImage, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.labelPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			break;
		}
		this.labelPanel.add(this.attributeLabel);

		try {
			this.attributeLabel.setFont(this.attributeLabel.getFont().deriveFont(Font.BOLD));
		} catch (final Exception e) {
			ToggleImage.logger.error("Error changing attribute text font: ", e);
		}

		this.panelImage.setOpaque(false);
		this.labelPanel.setOpaque(false);
		this.attributeLabel.setOpaque(false);
		this.setOpaque(false);
		this.setSize(this.getPreferredSize());
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
	 * <td></td>
	 * <td>yes</td>
	 * <td>The field attribute.</td>
	 * </tr>
	 * <tr>
	 * <td>alarm</td>
	 * <td><i>on/off</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>When enabled image blinks between state image and alarm image.</td>
	 * </tr>
	 * <tr>
	 * <td>align</td>
	 * <td><i>center/left/right</td>
	 * <td>center</td>
	 * <td>no</td>
	 * <td>The alignment for field.</td>
	 * </tr>
	 * <tr>
	 * <td>valign</td>
	 * <td><i>center/top/bottom</td>
	 * <td>top</td>
	 * <td>no</td>
	 * <td>The vertical alignment for field.</td>
	 * </tr>
	 * <tr>
	 * <td>dim</td>
	 * <td><i>no/text/yes</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>The resize possibilities (no resize, resize the insertion space, resize the space between
	 * label and text ).</td>
	 * </tr>
	 * <tr>
	 * <td>imgOn</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The image for on state.</td>
	 * </tr>
	 * <tr>
	 * <td>imgOff</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The image for off state</td>
	 * </tr>
	 * <tr>
	 * <td>imgAlarm</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The image for alarm state.</td>
	 * </tr>
	 * <tr>
	 * <td>imgDisabled</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The image for disabled state.</td>
	 * </tr>
	 * <tr>
	 * <td>rotate</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Rotation of field. When this feature is enabled, height and width for image are
	 * interchanged.</td>
	 * </tr>
	 * <tr>
	 * <td>status</td>
	 * <td><i>on/off</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Initial state for component.</td>
	 * </tr>
	 * <tr>
	 * <td>width</td>
	 * <td><i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Width of field.</td>
	 * </tr>
	 * <tr>
	 * <td>height</td>
	 * <td><i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Height of field.</td>
	 * </tr>
	 * <tr>
	 * <td>label</td>
	 * <td><i>left/right/top/bottom</td>
	 * <td></td>
	 * <td>bottom</td>
	 * <td>Position of label respect of text.</td>
	 * </tr>
	 * <tr>
	 * <td>labelvisible</td>
	 * <td><i>yes/no</td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The label visibility.</td>
	 * </tr>
	 * <tr>
	 * <td>text</td>
	 * <td><i>yes/no</td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Alternative for attr text.</td>
	 * </tr>
	 * </Table>
	 */
	@Override
	public void init(final Map parameters) {
		final Object oText = this.setTextParameters(parameters);
		this.setAttributeParameters(parameters, oText);
		this.setLabelVisibleParameters(parameters);
		this.setResizedParameters(parameters);
		this.setAlignParameters(parameters);
		this.setVerticalAlignParameters(parameters);
		this.setVisibleParameters(parameters);
		final Object imageOn = this.setImageONParameters(parameters);
		this.setImageOffParameters(parameters);
		this.setImageAlarmaParameters(parameters);
		this.setDisableImageParameters(parameters);
		final Map param = new Hashtable();
		this.setPanelParameters(parameters, imageOn, param);
		this.panelImage = new JImage(param);
		this.setStatusPanelParameters(parameters);
		this.setStatusAlarmParameters(parameters);
		this.setLabelPositionParameters(parameters);
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @param imageOn
	 * @param param
	 */
	protected void setPanelParameters(final Map parameters, final Object imageOn, final Map param) {
		if (imageOn != null) {
			param.put("img", imageOn); // Default status
		}
		final Object rotate = parameters.get("rotate");
		if (rotate != null) {
			param.put("rotate", parameters.get("rotate"));
		}
		final Object width = parameters.get("width");
		if (width != null) {
			param.put("width", parameters.get("width"));
		}
		final Object height = parameters.get("height");
		if (height != null) {
			param.put("height", parameters.get("height"));
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setStatusPanelParameters(final Map parameters) {
		final Object statusPanel = parameters.get("status");
		if (statusPanel == null) {
			this.setStatus(ToggleImage.ON);
			this.preferredStatus = ToggleImage.ON;
		} else {
			if (statusPanel.toString().equalsIgnoreCase("on")) {
				this.setStatus(ToggleImage.ON);
				this.preferredStatus = ToggleImage.ON;
			} else {
				this.setStatus(ToggleImage.OFF);
				this.preferredStatus = ToggleImage.OFF;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setStatusAlarmParameters(final Map parameters) {
		final Object statusAlarm = parameters.get("alarm");
		if (statusAlarm != null) {
			if (statusAlarm.toString().equalsIgnoreCase("on")) {
				this.setStatus(ToggleImage.ALARM);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelPositionParameters(final Map parameters) {
		final Object posLabel = parameters.get("label");
		if (posLabel != null) {
			if (posLabel.toString().equalsIgnoreCase("top")) {
				this.labelPosition = ToggleImage.TOP;
			} else if (posLabel.toString().equalsIgnoreCase("left")) {
				this.labelPosition = ToggleImage.LEFT;
			} else if (posLabel.toString().equalsIgnoreCase("right")) {
				this.labelPosition = ToggleImage.RIGHT;
			} else {
				this.labelPosition = ToggleImage.BOTTOM;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDisableImageParameters(final Map parameters) {
		final Object oDisableImage = parameters.get("imgDisabled");
		if (oDisableImage == null) {
			ToggleImage.logger.debug(this.getClass().toString() + " parameter 'imgDisabled' not found");
		} else {
			this.disabledImageURL = this.getClass().getClassLoader().getResource(oDisableImage.toString());
			if (this.disabledImageURL == null) {
				ToggleImage.logger.debug(this.getClass().toString() + " " + oDisableImage.toString() + " not found.");
			} else {
				ImageRepository.getImage(this.disabledImageURL, this.disabledImageObserver);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setImageAlarmaParameters(final Map parameters) {
		final Object oAlarmImage = parameters.get("imgAlarm");
		if (oAlarmImage == null) {
			ToggleImage.logger.debug(this.getClass().toString() + " parameter 'imgAlarm' not found");
		} else {
			this.alarmImageURL = this.getClass().getClassLoader().getResource(oAlarmImage.toString());
			if (this.alarmImageURL == null) {
				ToggleImage.logger.debug(this.getClass().toString() + " " + oAlarmImage.toString() + " not found.");
			} else {
				ImageRepository.getImage(this.alarmImageURL, this.alarmImageObserver);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setImageOffParameters(final Map parameters) {
		final Object imageOff = parameters.get("imgOff");
		if (imageOff == null) {
			ToggleImage.logger.debug(this.getClass().toString() + " parameter 'imgOff' not found");
		} else {
			this.offImageURL = this.getClass().getClassLoader().getResource(imageOff.toString());
			if (this.offImageURL == null) {
				ToggleImage.logger.debug(this.getClass().toString() + " " + imageOff.toString() + " not found.");
			} else {
				ImageRepository.getImage(this.offImageURL, this.offImageObserver);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @return
	 */
	protected Object setImageONParameters(final Map parameters) {
		final Object imageOn = parameters.get("imgOn");
		if (imageOn == null) {
			ToggleImage.logger.debug(this.getClass().toString() + " parameter 'imgOn' not found");
		} else {
			this.onImageURL = this.getClass().getClassLoader().getResource(imageOn.toString());
			if (this.onImageURL == null) {
				ToggleImage.logger.debug(this.getClass().toString() + " " + imageOn.toString() + " not found.");
			} else {
				ImageRepository.getImage(this.onImageURL, this.onImageObserver);
			}
		}
		return imageOn;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setVisibleParameters(final Map parameters) {
		final Object visible = parameters.get(ToggleImage.VISIBLE);
		if (visible != null) {
			final String no = (String) visible;
			if (no.equalsIgnoreCase(ToggleImage.NO_STR) || no.equalsIgnoreCase("false")) {
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
	protected void setVerticalAlignParameters(final Map parameters) {
		final Object oValign = parameters.get(ToggleImage.VALIGN);
		if (oValign != null) {
			if (oValign.equals("center")) {
				this.valignment = GridBagConstraints.CENTER;
			} else {
				if (oValign.equals("bottom")) {
					this.valignment = GridBagConstraints.SOUTH;
				} else {
					this.valignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAlignParameters(final Map parameters) {
		final Object oAlign = parameters.get(ToggleImage.ALIGN);
		if (oAlign != null) {
			if (oAlign.equals(ToggleImage.RIGHT_STR)) {
				this.alignment = GridBagConstraints.NORTHEAST;
			} else if (oAlign.equals(ToggleImage.LEFT_STR)) {
				this.alignment = GridBagConstraints.NORTHWEST;
			} else if (oAlign.equals(ToggleImage.CENTER_STR)) {
				this.alignment = GridBagConstraints.CENTER;
			} else {
				this.alignment = GridBagConstraints.CENTER;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setResizedParameters(final Map parameters) {
		final Object resized = parameters.get(ToggleImage.DIM);
		if (resized == null) {
			this.dim = ToggleImage.NO;
		} else {
			if (resized.equals(ToggleImage.YES_STR)) {
				this.dim = ToggleImage.YES;
			} else {
				if (resized.equals(ToggleImage.TEXT_STR)) {
					this.dim = ToggleImage.TEXT;
				} else {
					this.dim = ToggleImage.NO;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelVisibleParameters(final Map parameters) {
		final Object labelvisible = parameters.get(ToggleImage.LABELVISIBLE);
		if (labelvisible != null) {
			if (labelvisible.equals(ToggleImage.NO_STR)) {
				this.showLabel = false;
				this.attributeLabel.setVisible(false);
			} else {
				this.showLabel = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @param oText
	 */
	protected void setAttributeParameters(final Map parameters, final Object oText) {
		final Object oAttribute = parameters.get("attr");
		if (oAttribute == null) {
		} else {
			this.attribute = oAttribute.toString();
			if (oText == null) {
				this.labelText = this.attribute;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 * @return
	 */
	protected Object setTextParameters(final Map parameters) {
		final Object oText = parameters.get("text");
		if (oText != null) {
			this.labelText = (String) oText;
		}
		return oText;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			final String labelTextKey = this.labelText == null ? this.attribute.toString() : this.labelText;
			this.attributeLabel.setText(ApplicationManager.getTranslation(labelTextKey, resources));
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				ToggleImage.logger.error(null, e);
			} else {
				ToggleImage.logger.trace(null, e);
			}
		}
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		int totalAlignment = this.alignment;
		switch (this.valignment) {
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

		if (layout instanceof GridBagLayout) {
			if (this.dim == ToggleImage.NO) {
				return new GridBagConstraints(0, 0, 1, 1, 0.0001, 0, totalAlignment, GridBagConstraints.NONE,
						new Insets(1, 1, 1, 1), 0, 0);
			} else {
				if ((this.dim == ToggleImage.YES) || (this.dim == ToggleImage.TEXT)) {
					return new GridBagConstraints(0, 0, 1, 1, 1, 0, totalAlignment, GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1), 0, 0);
				} else {
					return new GridBagConstraints(0, 0, 1, 1, 1, 0, totalAlignment, GridBagConstraints.NONE,
							new Insets(1, 1, 1, 1), 0, 0);
				}
			}
		} else {
			return null;
		}
	}

	/*
	 * public String getToolTipText() { return this.getConstraints(null).toString(); } public String
	 * getToolTipText(MouseEvent e) { return this.getConstraints(null).toString(); }
	 */

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public Insets getInsets() {
		return new Insets(0, 0, 0, 0);
	}

	/**
	 * This method sets the component status and update the image
	 * @param status
	 */
	public void setStatus(final int status) {
		if ((status != ToggleImage.ON) && (status != ToggleImage.OFF) && (status != ToggleImage.ALARM)) {
			ToggleImage.logger.debug("Estado no es ON,OFF,ALARM: " + status);
			return;
		}

		if (this.panelImage == null) {
			this.status = status;
		}
		if (this.panelImage != null) {
			this.status = status;
			switch (status) {
			case ON:
				this.panelImage.setImage(this.onImageURL);
				break;
			case OFF:
				this.panelImage.setImage(this.offImageURL);
				break;
			case ALARM:
				this.panelImage.setImage(this.alarmImageURL);
				break;
			default:
				this.panelImage.setImage(this.offImageURL);
				break;
			}

			if (status == ToggleImage.ALARM) {
				if ((this.alarmThread != null) && this.alarmThread.isAlive()) {
				} else {
					this.alarmThread = new AlarmThread(this);
					this.alarmThread.start();
				}
			} else {
				if ((this.alarmThread != null) && this.alarmThread.isAlive()) {
					this.alarmThread.interruptAlarm();
					this.alarmThread = null;
				}
			}
		}
		this.revalidate();
		this.repaint();
	}

	public int getStatus() {
		return this.status;
	}

	@Override
	public void setParentForm(final Form parentForm) {
		this.parentForm = parentForm;
	}

	public void setCallFormAlarm(final boolean callAlarmForma) {
		this.callForm = callAlarmForma;
	}

	@Override
	protected void finalize() throws Throwable {
		this.alarmThread.interruptAlarm();
		super.finalize();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		super.setEnabled(enabled);
		if (!enabled) {
			this.panelImage.setImage(this.disabledImageURL);
		} else {
			switch (this.status) {
			case ON:
				this.panelImage.setImage(this.onImageURL);
				break;
			case OFF:
				this.panelImage.setImage(this.offImageURL);
				break;
			case ALARM:
				this.panelImage.setImage(this.alarmImageURL);
				break;
			default:
				this.panelImage.setImage(this.offImageURL);
				break;
			}
		}
		this.revalidate();
		this.repaint();
	}

	public Point getImageOffset() {
		int xPixels = 0;
		int yPixels = 0;
		switch (this.labelPosition) {
		case TOP: // If label is on the top then offset is positive
			xPixels = (this.getSize().width - this.panelImage.getPreferredSize().width) / 2;
			yPixels = this.getSize().height - this.panelImage.getPreferredSize().height;
			if ((xPixels >= 0) && (yPixels >= 0)) {
				return new Point(xPixels, yPixels);
			} else {
				return new Point(0, 0);
			}
		case LEFT:
			xPixels = this.getSize().width - this.panelImage.getPreferredSize().width;
			yPixels = (this.getSize().height - this.panelImage.getPreferredSize().height) / 2;
			if ((xPixels >= 0) && (yPixels >= 0)) {
				return new Point(xPixels, yPixels);
			} else {
				return new Point(0, 0);
			}
		case RIGHT:
			xPixels = 0;
			yPixels = this.getSize().height - this.panelImage.getPreferredSize().height;
			if ((xPixels >= 0) && (yPixels >= 0)) {
				return new Point(xPixels, yPixels);
			} else {
				return new Point(0, 0);
			}
		default:
			xPixels = (this.getSize().width - this.panelImage.getPreferredSize().width) / 2;
			yPixels = 0;
			if (xPixels > 0) {
				return new Point(xPixels, 0);
			} else {
				return new Point(0, 0);
			}
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		// if (visible) {
		// boolean permission = this.checkVisiblePermission();
		// if (!permission) {
		// return;
		// }
		// }
		// super.setVisible(visible);

		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (permission) {
				super.setVisible(visible);
				if (!this.showLabel && this.attributeLabel.isVisible()) {
					this.attributeLabel.setVisible(false);
				}
			}
		} else {
			super.setVisible(visible);
			if (!this.showLabel && this.attributeLabel.isVisible()) {
				this.attributeLabel.setVisible(false);
			}
		}

	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
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

	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ToggleImage.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					ToggleImage.logger.debug(null, e);
				} else {
					ToggleImage.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ToggleImage.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					ToggleImage.logger.debug(null, e);
				} else {
					ToggleImage.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	public boolean isLabelVisible() {
		return this.showLabel;
	}

	public boolean isHidden() {
		return !this.show;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
