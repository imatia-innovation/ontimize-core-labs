package com.ontimize.jee.desktopclient.components.messaging;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.WindowTools;
import com.ontimize.jee.desktopclient.tools.ParseUtilsExtended;

/**
 * The Class UToastMessage.
 */
public class ToastMessage {

	private static final Logger logger = LoggerFactory.getLogger(ToastNoblockingPanel.class);

	/** The icon warning. */
	public static ImageIcon ICON_WARNING = ParseUtilsExtended.getImageIcon("com/ontimize/gui/images/warning.png", null);

	/** The icon error. */
	public static ImageIcon ICON_ERROR = ParseUtilsExtended.getImageIcon("com/ontimize/gui/images/error.png", null);

	/** The icon information. */
	public static ImageIcon ICON_INFORMATION = ParseUtilsExtended.getImageIcon("com/ontimize/gui/images/info_16.png",
			null);

	/** The message time short. */
	public static int MESSAGE_TIME_SHORT = 1200;

	/** The message time long. */
	public static int MESSAGE_TIME_LONG = 3000;

	/** The message. */
	protected String message;

	/** The description. */
	protected String description;

	/** The icon. */
	protected ImageIcon icon;

	/** The parent window. */
	protected Window parentWindow;

	/** The animation time. */
	protected int animationTime;

	/** The window bounds. */
	protected Rectangle windowBounds;

	/** The other data. */
	protected Map<?, ?> otherData;

	/** The panel class. */
	protected Class<? extends JPanel> panelClass;

	/** The blocking. */
	protected boolean blocking;

	/** The bundle message params. */
	protected Object[] bundleMessageParams;

	/** The bundle description params. */
	protected Object[] bundleDescriptionParams;

	/**
	 * Instantiates a new u toast message.
	 * @param parent the parent
	 * @param blocking the blocking
	 * @param message the message
	 * @param description the description
	 * @param icon the icon
	 * @param animationTime the animation time
	 * @param windowBounds the window bounds
	 * @param panelClass the panel class
	 * @param otherData the other data
	 * @param bundleMessageParams the bundle message params
	 * @param bundleDescriptionParams the bundle description params
	 */
	public ToastMessage(final Component parent, final boolean blocking, final String message, final String description, final ImageIcon icon,
			final int animationTime, final Rectangle windowBounds,
			final Class<? extends JPanel> panelClass, final Hashtable<?, ?> otherData, final Object[] bundleMessageParams,
			final Object[] bundleDescriptionParams) {
		super();
		this.parentWindow = (Window) (parent == null ? null
				: parent instanceof Window ? parent : WindowTools.getWindowAncestor(parent));
		this.blocking = blocking;
		this.message = message;
		this.description = description;
		this.icon = icon;
		this.animationTime = animationTime;
		this.windowBounds = windowBounds;
		this.panelClass = panelClass;
		this.otherData = otherData;
		this.bundleMessageParams = bundleMessageParams == null ? null
				: Arrays.copyOf(bundleMessageParams, bundleMessageParams.length);
		this.bundleDescriptionParams = bundleDescriptionParams == null ? null
				: Arrays.copyOf(bundleDescriptionParams, bundleDescriptionParams.length);
	}

	/**
	 * Instantiates a new u toast message.
	 * @param msg the msg
	 * @param msgType the msg type
	 * @param blocking the blocking
	 */
	public ToastMessage(final String msg, final MessageType msgType, final boolean blocking) {
		this(null, blocking, msg, null, null, ToastMessage.MESSAGE_TIME_SHORT, null,
				blocking ? ToastBlockingPanel.class : ToastNoblockingPanel.class, null, null, null);
		this.setIconByType(msgType);
	}

	/**
	 * Instantiates a new u toast message.
	 * @param parent the parent
	 * @param msg the msg
	 * @param msgType the msg type
	 * @param blocking the blocking
	 */
	public ToastMessage(final Component parent, final String msg, final MessageType msgType, final boolean blocking) {
		this(parent, blocking, msg, null, null, ToastMessage.MESSAGE_TIME_SHORT, null,
				blocking ? ToastBlockingPanel.class : ToastNoblockingPanel.class, null, null, null);
		this.setIconByType(msgType);
	}

	/**
	 * Instantiates a new u toast message.
	 * @param msg the msg
	 * @param msgParameters the msg parameters
	 * @param msgType the msg type
	 * @param blocking the blocking
	 */
	public ToastMessage(final String msg, final Object[] msgParameters, final MessageType msgType, final boolean blocking) {
		this(null, blocking, msg, null, null, ToastMessage.MESSAGE_TIME_SHORT, null,
				blocking ? ToastBlockingPanel.class : ToastNoblockingPanel.class, null, msgParameters, null);
		this.setIconByType(msgType);
	}

	/**
	 * Instantiates a new u toast message.
	 * @param msg the msg
	 * @param ico the ico
	 * @param blocking the blocking
	 */
	public ToastMessage(final String msg, final ImageIcon ico, final boolean blocking) {
		this(null, blocking, msg, null, ico, ToastMessage.MESSAGE_TIME_SHORT, null,
				blocking ? ToastBlockingPanel.class : ToastNoblockingPanel.class, null, null, null);
	}

	/**
	 * Sets the icon by type.
	 * @param msgType the new icon by type
	 */
	protected void setIconByType(final MessageType msgType) {
		if (msgType != null) {
			switch (msgType) {
			case INFORMATION:
				this.icon = ToastMessage.ICON_INFORMATION;
				break;
			case WARNING:
				this.icon = ToastMessage.ICON_WARNING;
				break;
			case ERROR:
				this.icon = ToastMessage.ICON_ERROR;
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Gets the panel class.
	 * @return the panel class
	 */
	public Class<? extends JPanel> getPanelClass() {
		return this.panelClass;
	}

	/**
	 * Gets the other data.
	 * @return the other data
	 */
	public Map<?, ?> getOtherData() {
		return this.otherData;
	}

	/**
	 * Gets the message.
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the bundle description params.
	 * @return the bundle description params
	 */
	public Object[] getBundleDescriptionParams() {
		return this.bundleDescriptionParams == null ? null
				: Arrays.copyOf(this.bundleDescriptionParams, this.bundleDescriptionParams.length);
	}

	/**
	 * Gets the description.
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Gets the bundle message params.
	 * @return the bundle message params
	 */
	public Object[] getBundleMessageParams() {
		return this.bundleMessageParams == null ? null
				: Arrays.copyOf(this.bundleMessageParams, this.bundleMessageParams.length);
	}

	/**
	 * Gets the translated message.
	 * @param bundle the bundle
	 * @return the translated message
	 */
	public String getTranslatedMessage(final ResourceBundle bundle) {
		try {
			return ApplicationManager.getTranslation(this.getMessage(),
					bundle != null ? bundle : ApplicationManager.getApplicationBundle(), this.getBundleMessageParams());
		} catch (final Exception e) {
			ToastMessage.logger.trace(null, e);
			return this.getMessage();
		}
	}

	/**
	 * Gets the translated description.
	 * @param bundle the bundle
	 * @return the translated description
	 */
	public String getTranslatedDescription(final ResourceBundle bundle) {
		try {
			return ApplicationManager.getTranslation(this.getDescription(),
					bundle != null ? bundle : ApplicationManager.getApplicationBundle(),
							this.getBundleDescriptionParams());
		} catch (final Exception e) {
			ToastMessage.logger.trace(null, e);
			return this.getDescription();
		}

	}

	/**
	 * Checks if is blocking.
	 * @return true, if is blocking
	 */
	public boolean isBlocking() {
		return this.blocking;
	}

	/**
	 * Gets the icon.
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return this.icon;
	}

	/**
	 * Gets the parent window.
	 * @return the parent window
	 */
	public Window getParentWindow() {
		return this.parentWindow;
	}

	/**
	 * Gets the animation time.
	 * @return the animation time
	 */
	public int getAnimationTime() {
		return this.animationTime;
	}

	/**
	 * Gets the window bounds.
	 * @return the window bounds
	 */
	public Rectangle getWindowBounds() {
		return this.windowBounds;
	}

}
