package com.ontimize.gui.login;

import java.awt.Point;
import java.awt.Window;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;

/**
 * Class that provides an abstract implementation of the change password window when the
 * applications has a periodic check of the password age.
 *
 * @author Imatia Innovation S.L.
 */
public abstract class AbstractChangePasswordDialog extends JDialog implements IChangePasswordDialog {

	/**
	 * {@link Logger} for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractChangePasswordDialog.class);

	/**
	 * {@link String} to set {@link PasswordDataField} "currentPassword" configuration in the
	 * defaultXMLConfigurationParameters.
	 */
	public static final String CHANGE_PASSWORD_DIALOG_CURRENT_PASSWORD = "ChangePasswordDialog.CurrentPassword";

	/**
	 * {@link String} to set {@link PasswordDataField} "newPassword" configuration in the
	 * defaultXMLConfigurationParameters.
	 */
	public static final String CHANGE_PASSWORD_DIALOG_NEW_PASSWORD = "ChangePasswordDialog.NewPassword";

	/**
	 * {@link String} to set {@link PasswordDataField} "repeatNewPassword" configuration in the
	 * defaultXMLConfigurationParameters.
	 */
	public static final String CHANGE_PASSWORD_DIALOG_REPEAT_NEW_PASSWORD = "ChangePasswordDialog.RepeatNewPassword";

	/**
	 * {@link String} to set {@link Button} "acceptButton" configuration in the
	 * defaultXMLConfigurationParameters.
	 */
	public static final String CHANGE_PASSWORD_DIALOG_BUTTON = "ChangePasswordDialog.Button";

	/**
	 * {@link ResourceBundle} of application.
	 */
	protected ResourceBundle bundle = null;

	/**
	 * {@link JLabel} to show messages about the status.
	 */
	protected JLabel securityLabel = null;

	/**
	 * {@link PasswordDataField} for "newPassword".
	 */
	protected PasswordDataField newPassword = null;

	/**
	 * {@link PasswordDataField} for "currentPassword".
	 */
	protected PasswordDataField currentPassword = null;

	/**
	 * {@link PasswordDataField} for "repeatNewPassword".
	 */
	protected PasswordDataField repeatNewPassword = null;

	/**
	 * {@link Button} for "acceptButton".
	 */
	protected Button acceptButton = null;

	/**
	 * {@link EntityReferenceLocator} used to call the method
	 * {@link UtilReferenceLocator#changePassword(String, int, Hashtable, Hashtable)} to modify the
	 * current password of the user who try to login and its period of validity has expired (Difference
	 * bettwen lastUpdatedDate and actuale date is greater than period).
	 */
	protected EntityReferenceLocator locator = null;

	/**
	 * {@link Application} for obtain the parent frame.
	 */
	protected Application application = null;

	/**
	 * {@link Hashtable} with the configuration of main application.
	 */
	protected Map params = new Hashtable();

	/**
	 * {@link String} with the actual user which password validity has expired.
	 */
	protected String user;

	/**
	 * {@link String} of the old password of actual user.
	 */
	protected String oldPassword;

	/**
	 * Contructor for this abstract class.
	 * @param mainApplication The actual application.
	 * @param parameters The configuration of application in the clientapplication.xml.
	 * @param locator The entity reference locator.
	 */
	public AbstractChangePasswordDialog(final Application mainApplication, final Map parameters,
			final EntityReferenceLocator locator, final String user, final String password) {
		super(mainApplication.getFrame(), IChangePasswordDialog.WINDOW_TITLE, true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		this.locator = locator;
		this.application = mainApplication;
		this.params = parameters;
		this.user = user.toString();
		this.oldPassword = password.toString();
	}

	/**
	 * Returns the application core.
	 * @return The application core.
	 */
	@Override
	public Application getApplication() {
		return this.application;
	}

	/**
	 * Returns the entity reference locator.
	 * @return The entity reference locator.
	 */
	@Override
	public EntityReferenceLocator getEntityReferenceLocator() {
		return this.locator;
	}

	/**
	 * Returns the {@link PasswordDataField} which references to the "currentPassword".
	 * <p>
	 * This method check the defaultXMLParameters parameters for the tag
	 * {@code <Class name="ChangePasswordDialog.CurrentPassword">} and create the
	 * {@link PasswordDataField} with them. This {@link PasswordDataField} has as
	 * {@code attr="currentPassword"}.
	 * </p>
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 *        Does nothing, it is only for extension purposes, e.g., the {@code "encrypt"} attribute in
	 *        the configuration parameters of the application.
	 * @return The {@link PasswordDataField} which references to the "currentPassword".
	 */
	protected PasswordDataField createCurrentPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CURRENT_PASSWORD);
		h.put("attr", "currentPassword");
		return new PasswordDataField(h);
	}

	/**
	 * Returns the {@link PasswordDataField} which references to the "newPassword".
	 * <p>
	 * This method check the defaultXMLParameters parameters for the tag
	 * {@code <Class name="ChangePasswordDialog.NewPassword">} and create the {@link PasswordDataField}
	 * with them. This {@link PasswordDataField} has as {@code attr="newPassword"}.
	 * </p>
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 *        Does nothing, it is only for extension purposes, e.g., the {@code "encrypt"} attribute in
	 *        the configuration parameters of the application. It's recommended to check if in the xml
	 *        parameters of application exists parameters in:
	 *        {@code DefaultXMLParametersManager.getParameters(IChangePasswordDialog.SECURITY_PASSWORD_PARAMETERS);},
	 *        and add it to the hashtable.
	 * @return The {@link PasswordDataField} which references to the "newPassword".
	 */
	protected PasswordDataField createPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_NEW_PASSWORD);
		h.put("attr", "newPassword");
		return new PasswordDataField(h);
	}

	/**
	 * Returns the {@link PasswordDataField} which references to the "repeatNewPassword".
	 *
	 * <p>
	 * This method check the defaultXMLParameters parameters for the tag
	 * {@code <Class name="ChangePasswordDialog.RepeatNewPassword">} and create the
	 * {@link PasswordDataField} with them. This {@link PasswordDataField} has as
	 * {@code attr="repeatNewPassword"}.
	 * </p>
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 *        Does nothing, it is only for extension purposes, e.g., the {@code "encrypt"} attribute in
	 *        the configuration parameters of the application.
	 * @return The {@link PasswordDataField} which references to the "repeatNewPassword".
	 */
	protected PasswordDataField createRepeatPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_REPEAT_NEW_PASSWORD);
		h.put("attr", "repeatNewPassword");
		return new PasswordDataField(h);
	}

	/**
	 * Return an {@link ImageIcon} for the {@link AbstractChangePasswordDialog} window.
	 *
	 * <p>
	 * The path to the {@link ImageIcon} can be configured in the parameters of the application in the
	 * {@code "clientapplication.xml"} file. First, checks if exists in the parameters a value with the
	 * {@code key="changepasswordicon"}. If not, check if exists a value in parameters with the
	 * {@code key="loginicon"} (used for the login dialog). If not exists any of this values, returns
	 * the Ontimize Back Login {@link ImageIcon}.
	 * </p>
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 * @return An {@link ImageIcon}
	 */
	protected ImageIcon createImage(final Map parameters) {
		ImageIcon icon = null;
		if (parameters.containsKey(IChangePasswordDialog.CHANGE_PASSWORD_ICON)) {
			final String loginIcon = (String) parameters.get(IChangePasswordDialog.CHANGE_PASSWORD_ICON);
			icon = ImageManager.getIcon(loginIcon);
		} else if (parameters.containsKey(IChangePasswordDialog.LOGIN_ICON)) {
			final String loginIcon = (String) parameters.get(IChangePasswordDialog.LOGIN_ICON);
			icon = ImageManager.getIcon(loginIcon);
		}
		if (icon == null) {
			return ImageManager.getIcon(ImageManager.BACK_LOGIN);
		} else {
			return icon;
		}
	}

	/**
	 * Return a {@link Button} for the {@link AbstractChangePasswordDialog} window.
	 *
	 * <p>
	 * This method check the defaultXMLParameters parameters for the tag
	 * {@code <Class name="ChangePasswordDialog.Button">} and create the {@link Button} with them. This
	 * {@link Button} has as {@code attr="application.accept" text="application.accept"}.
	 * </p>
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 *        Does nothing, it is only for extension purposes.
	 * @return A {@link Button}
	 */
	protected Button createAcceptButton(final Map parameters) {
		final Map p = DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_BUTTON);
		p.put("key", "application.accept");
		p.put("text", "application.accept");
		return new Button(p);
	}

	/**
	 * Return a {@link JLabel}, where it shows messages about the password update.
	 * @param parameters The {@link Hashtable} with the configuration parameters of the application.
	 *        Does nothing, it is only for extension purposes.
	 * @return A {@link JLabel}
	 */
	protected JLabel createSecurityLabel(final Map parameters) {
		return new JLabel();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.bundle = resourceBundle;

		if (this.newPassword != null) {
			this.newPassword.setResourceBundle(this.bundle);
		}

		if (this.currentPassword != null) {
			this.currentPassword.setResourceBundle(this.bundle);
		}

		if (this.repeatNewPassword != null) {
			this.repeatNewPassword.setResourceBundle(this.bundle);
		}

		if (this.acceptButton != null) {
			this.acceptButton.setResourceBundle(this.bundle);
		}

		if (this.securityLabel != null) {
			this.securityLabel.setText(ApplicationManager.getTranslation("securityLabel.message", this.bundle));
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		this.addTextToTranslate(this.newPassword, v);
		this.addTextToTranslate(this.repeatNewPassword, v);
		this.addTextToTranslate(this.acceptButton, v);
		this.addTextToTranslate(this.currentPassword, v);
		return v;
	}

	/**
	 * Add the {@code component} to the {@link Vector} of texts to translate
	 *
	 * @see AbstractChangePasswordDialog#getTextsToTranslate()
	 * @param component The component to be translated
	 * @param texts The {@link Vector} of texts to be translated
	 */
	protected void addTextToTranslate(final Internationalization component, final List texts) {
		if (component != null) {
			final List textsToTranslate = component.getTextsToTranslate();
			if ((textsToTranslate != null) && (textsToTranslate.size() > 0)) {
				texts.addAll(textsToTranslate);
			}
		}
	}

	/**
	 * Return the {@link String} of the new password
	 *
	 * <p>
	 * Conditions to return the new password {@link String}:
	 * <ul>
	 * <li>{@link #newPassword} is not null</li>
	 * <li>{@link #repeatNewPassword} is not null</li>
	 * <li>{@link #newPassword} and {@link #repeatNewPassword} are equals</li>
	 * </ul>
	 * </p>
	 * @return The {@link String} of the new password, if all conditions are satisfied, otherwise
	 *         {@code null}
	 */
	@Override
	public String getPasswordValue() {
		if ((this.newPassword.getValue() != null) && (this.repeatNewPassword.getValue() != null)
				&& this.newPassword.getValue().equals(this.repeatNewPassword.getValue())) {
			return this.newPassword.getValue().toString();
		} else {
			return null;
		}
	}

	/**
	 * Set to the {@link #user}, who expires its password valid time, a new one.
	 *
	 * If {@link #locator} is an instance of {@link UtilReferenceLocator}, it creates a
	 * {@link Hashtable} with the {@code "password"}, and a {@link Hashtable} with the the {@link #user}
	 * and the {@link #oldPassword} , as AttributtesValue and KeyValues of the method
	 * {@link UtilReferenceLocator#changePassword(String, int, Hashtable, Hashtable)}
	 *
	 * @see UtilReferenceLocator#changePassword(String, int, Hashtable, Hashtable)
	 * @param password The new password
	 * @return The {@link EntityResult} of change the password if {@link #locator} is instance of
	 *         {@link UtilReferenceLocator}, {@code null} otherwise
	 */
	@Override
	public EntityResult setPassword(final String password) {
		if (this.locator instanceof UtilReferenceLocator) {
			try {
				final Map av = new Hashtable();
				av.put("Password", password);

				final Map kv = new Hashtable();
				kv.put("User_", this.user);
				kv.put("Password", this.oldPassword);

				return ((UtilReferenceLocator) this.locator).changePassword(password, this.locator.getSessionId(), av,
						kv);
			} catch (final Exception e) {
				AbstractChangePasswordDialog.logger.error(null, e);
			}
		}
		return null;
	}

	/**
	 * Center the change password dialog in the screen and change its visibility to true.
	 */
	@Override
	public void showChangePassword() {
		ApplicationManager.center(this);
		this.setVisible(true);

	}

	/**
	 * Center the change password dialog in the center of the window passed by parameter and change its
	 * visibility to true.
	 * <p>
	 * This method obtain the top-left point of the parent window and move it to the center of the
	 * parent window. Then, move the {@code x} and {@code y} coordinates so that their translation is
	 * half the height and width of the change password dialog, setting the point in the upper left
	 * corner, and using it to set the location of the change password dialog
	 * </p>
	 * @param w Parent window
	 */
	@Override
	public void showChangePasswordInParentLocation(final Window w) {
		final Point p = w.getLocation();
		p.x = p.x + (w.getBounds().width / 2);
		p.y = p.y + (w.getBounds().height / 2);
		p.x = p.x - (this.getBounds().width / 2);
		p.y = p.y - (this.getBounds().height / 2);
		this.setLocation(p.x, p.y);
		w.setVisible(false);
		this.setVisible(true);

	}

}
