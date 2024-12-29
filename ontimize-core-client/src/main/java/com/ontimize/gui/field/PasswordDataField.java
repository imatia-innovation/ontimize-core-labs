package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.JTipWindow;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.PasswordDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.Toast;

public class PasswordDataField extends TextFieldDataField implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(PasswordDataField.class);

	protected static final String M_CAPS_LOCK = "datafield.caps_lock_is_on";

	protected char maskCharacter = '*';

	protected int maxLength;

	private boolean encrypt = false;

	public static final String ENCRYPT_PASSWORDS_ALGORITHM = "SHA";

	public static final String SECURITY_LEVEL = "securitylevel";

	public static final String SECURITY_BUTTON = "securitybutton";

	protected boolean passwordSecurity = false;

	protected JButton securityButton = null;

	protected int secureLevel = 0;

	protected Map<Integer, String>	patternLevels				= new Hashtable<Integer, String>();

	protected JToolTip tooltip = new JToolTip() {

		@Override
		public String getTipText() {
			return ApplicationManager.getTranslation(PasswordDataField.M_CAPS_LOCK);
		}
	};

	protected JToolTip tooltipSecurity = new JToolTip();

	protected JTipWindow popupTip = new JTipWindow();

	protected JTipWindow popupTipSecurity = new JTipWindow();

	protected boolean validPassword = false;

	protected class KeyboardListener extends KeyAdapter {

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
				if (PasswordDataField.this.isCapsLockOn()) {
					PasswordDataField.this.popupTip.pack();
					PasswordDataField.this.popupTip.show(e.getComponent(), 0, e.getComponent().getHeight());
				} else {
					PasswordDataField.this.popupTip.setVisible(false);
				}
			}
		}

	}

	protected class TipListener extends MouseAdapter {

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (PasswordDataField.this.isCapsLockOn() && !PasswordDataField.this.popupTip.isVisible()) {
				PasswordDataField.this.popupTip.pack();
				PasswordDataField.this.popupTip.show(e.getComponent(), e.getX(), e.getComponent().getHeight());
			} else {
				PasswordDataField.this.popupTip.setVisible(false);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			PasswordDataField.this.popupTip.setVisible(false);
		}

	}

	protected class TipFocusListener extends FocusAdapter {

		@Override
		public void focusGained(final FocusEvent e) {
			if (PasswordDataField.this.isCapsLockOn() && !PasswordDataField.this.popupTip.isVisible()) {
				PasswordDataField.this.popupTip.pack();
				PasswordDataField.this.popupTip.show(e.getComponent(), 0, e.getComponent().getHeight());
			} else {
				PasswordDataField.this.popupTip.setVisible(false);
			}
		}

		@Override
		public void focusLost(final FocusEvent e) {
			PasswordDataField.this.popupTip.setVisible(false);
		}

	}

	protected TipListener tipMouseListener = null;

	protected TipFocusListener tipFocusListener = null;

	protected KeyboardListener keyboardListener = null;

	protected DocumentListener passwordDocumentListener = new DocumentListener() {

		@Override
		public void insertUpdate(final DocumentEvent e) {
			PasswordDataField.this.checkPasswordSecurity(e);
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			PasswordDataField.this.checkPasswordSecurity(e);
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			PasswordDataField.this.checkPasswordSecurity(e);
		}

	};

	protected class SecurityKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(final KeyEvent e) {
			if (!((PasswordDocument) ((JTextField) PasswordDataField.this.dataField).getDocument())
					.isPatternMatches()) {
				PasswordDataField.this.popupTipSecurity.pack();
				PasswordDataField.this.popupTipSecurity.show(PasswordDataField.this.dataField,
						PasswordDataField.this.dataField.getWidth()
						- PasswordDataField.this.popupTipSecurity.getWidth(),
						PasswordDataField.this.dataField.getHeight());
				PasswordDataField.this.popupTipSecurity.setAlwaysOnTop(true);
			} else {
				PasswordDataField.this.popupTipSecurity.setVisible(false);
			}
		}

	}

	protected class SecurityFocusListener extends FocusAdapter {

		@Override
		public void focusLost(final FocusEvent e) {
			PasswordDataField.this.popupTipSecurity.setVisible(false);
		}

		@Override
		public void focusGained(final FocusEvent e) {
			if ((((JTextField) PasswordDataField.this.dataField).getDocument()
					.getLength() > 0)
					&& !((PasswordDocument) ((JTextField) PasswordDataField.this.dataField).getDocument())
					.isPatternMatches()) {
				PasswordDataField.this.popupTipSecurity.pack();
				PasswordDataField.this.popupTipSecurity.show(PasswordDataField.this.dataField,
						PasswordDataField.this.dataField.getWidth()
						- PasswordDataField.this.popupTipSecurity.getWidth(),
						PasswordDataField.this.dataField.getHeight());
				PasswordDataField.this.popupTipSecurity.setAlwaysOnTop(true);
			}
		}

	}

	protected SecurityFocusListener securityFocusListener = null;

	protected SecurityKeyListener securityKeyListener = null;

	public PasswordDataField(final Map parameters) {
		this.init(parameters);
		((JTextField) this.dataField).setDocument(new PasswordDocument(this.maskCharacter, this.maxLength));
		this.createPopup();
		this.addPatterns();
		if (this.isPasswordSecurity()) {
			final Document doc = ((JTextField) this.dataField).getDocument();
			this.createPopupSecurity();
			doc.addDocumentListener(this.passwordDocumentListener);

			this.securityFocusListener = new SecurityFocusListener();
			this.dataField.addFocusListener(this.securityFocusListener);
			this.securityKeyListener = new SecurityKeyListener();
			this.dataField.addKeyListener(this.securityKeyListener);
		}
	}

	protected void createPopupSecurity() {
		this.tooltipSecurity.setTipText(this.setTextSecurityTooltip());
		this.popupTipSecurity.getContentPane().add(this.tooltipSecurity);
		((JPanel) this.popupTipSecurity.getContentPane()).setBorder(new EmptyBorder(0, 0, 0, 0));
		this.popupTipSecurity.setSize(200, 50);

	}

	protected String setTextSecurityTooltip() {
		final int level = this.getSecureLevel();
		final ResourceBundle bundle = ApplicationManager.getApplicationBundle();
		final String security_tooltip_lv = "security_tooltip_lv" + level;
		final String tooltip = ApplicationManager.getTranslation(security_tooltip_lv, bundle,
				new String[] {
						ApplicationManager.getTranslation("security_tooltip.character", bundle,
								new String[] { "" + (level + 4) + "" }),
						ApplicationManager
						.getTranslation("security_tooltip.uppercase", bundle),
						ApplicationManager.getTranslation("security_tooltip.lowercase", bundle), ApplicationManager
						.getTranslation("security_tooltip.number", bundle),
						ApplicationManager.getTranslation("security_tooltip.symbol", bundle), });
		return tooltip;
	}

	protected void createPopup() {
		this.tooltip.setTipText(PasswordDataField.M_CAPS_LOCK);
		this.popupTip.getContentPane().add(this.tooltip);
		((JPanel) this.popupTip.getContentPane()).setBorder(new EmptyBorder(0, 0, 0, 0));
		this.popupTip.setSize(200, 50);
		this.tipMouseListener = new TipListener();
		this.tipFocusListener = new TipFocusListener();
		this.keyboardListener = new KeyboardListener();
		this.dataField.addMouseListener(this.tipMouseListener);
		this.dataField.addFocusListener(this.tipFocusListener);
		this.dataField.addKeyListener(this.keyboardListener);
	}

	/**
	 * <table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <th>attribute</th>
	 * <th>values</th>
	 * <th>default</th>
	 * <th>required</th>
	 * <th>meaning</th>
	 * </tr>
	 * <tr>
	 * <td>char</td>
	 * <td></td>
	 * <td>*</td>
	 * <td>no</td>
	 * <td>character to show in the field</td>
	 * </tr>
	 * <tr>
	 * <td>encrypt</td>
	 * <td><i>yes/no</td>
	 * <td><i>no</td>
	 * <td>no</td>
	 * <td>Indicates if the field value is the password encrypted or not</td>
	 * </tr>
	 * <tr>
	 * <td>maxlength</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>maximum number of characters allowed in this field.</td>
	 * </tr>
	 * <tr>
	 * <td>securitylevel</td>
	 * <td><i>0,1,2,3,4</td>
	 * <td><i>0</td>
	 * <td>no</td>
	 * <td>Level of password security:
	 * <table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <th>Level</th>
	 * <th>Restrictions</th>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td>No restrictions</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>Minimum 5 characters.</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>Minimum 6 characters, 1 lowercase, 1 uppercase.</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>Minimum 7 characters, 1 lowercase, 1 uppercase, 1 number.</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>Minimum 8 characters. 1 lowercase, 1 uppercase, 1 number, 1 symbol.</td>
	 * </tr>
	 * </table>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>securitybutton</td>
	 * <td><i>yes/no</td>
	 * <td><i>yes</td>
	 * <td>no</td>
	 * <td>Indicates if the field has a button to check the security of the password. Only available if
	 * 'securitylevel' is enable or greater than 0</td>
	 * </tr>
	 * </table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final Object chars = parameters.get("char");
		if (chars != null) {
			final String characteres = chars.toString();
			this.maskCharacter = characteres.charAt(0);
		}

		this.maxLength = ParseUtils.getInteger((String) parameters.get("maxlength"), -1);
		this.encrypt = ParseUtils.getBoolean((String) parameters.get("encrypt"), false);

		final Object oSecurityLevel = parameters.get(PasswordDataField.SECURITY_LEVEL);
		if (oSecurityLevel != null) {
			final int level = ParseUtils.getInteger(oSecurityLevel.toString(), 0);
			if ((level >= 0) && (level <= 4)) {
				this.secureLevel = level;
			} else {
				this.secureLevel = 0;
			}
			this.setPasswordSecurity(true);

			final Object oSecurityButton = parameters.get(PasswordDataField.SECURITY_BUTTON);
			if ((oSecurityButton != null) && (!DataField.parseBoolean(oSecurityButton.toString(), true))) {
				PasswordDataField.logger.debug("The check button is disabled.");

			} else {
				if (this.secureLevel > 0) {
					this.createSecurityButton();
				} else {
					PasswordDataField.logger.debug("Security level is {}. Security button is not enabled",
							this.secureLevel);
				}
			}
		} else {
			this.setPasswordSecurity(false);
		}
	}

	protected void createSecurityButton() {
		this.securityButton = new FieldButton();
		this.securityButton.setMargin(new Insets(0, 0, 0, 0));
		this.securityButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		this.securityButton.setToolTipText(ApplicationManager.getTranslation("passworddatafield.security_field_no",
				ApplicationManager.getApplicationBundle()));
		this.securityButton.setFocusable(false);

		this.securityButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				String message;
				if (PasswordDataField.this.validPassword) {
					message = ApplicationManager.getTranslation("passworddatafield.security_field_yes",
							ApplicationManager.getApplicationBundle());
				} else {
					message = ApplicationManager.getTranslation("passworddatafield.security_field_no",
							ApplicationManager.getApplicationBundle());
				}
				Toast.showMessage(((FieldButton) e.getSource()).getParent(), message,
						ApplicationManager.getApplicationBundle(), 1000);
			}
		});

		super.add(this.securityButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}

	}

	protected void checkPasswordSecurity(final DocumentEvent e) {
		final String password = ((PasswordDocument) e.getDocument()).getContain();

		this.validPassword = ((PasswordDocument) ((JTextField) this.dataField).getDocument()).checkPattern(password,
				this.patternLevels.get(this.getSecureLevel()));
		if (this.validPassword) {

			if (this.isRequired()) {
				this.dataField.setForeground(DataField.requiredFieldForegroundColor);
			} else {
				this.dataField.setForeground(this.fontColor);
			}

			if (this.securityButton != null) {
				this.securityButton.setIcon(ImageManager.getIcon(ImageManager.OK));
				this.securityButton.setToolTipText(ApplicationManager
						.getTranslation("passworddatafield.security_field_yes", ApplicationManager.getApplicationBundle()));
			}
		} else {
			this.dataField.setForeground(Color.RED);
			if (this.securityButton != null) {
				this.securityButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
				this.securityButton.setToolTipText(ApplicationManager
						.getTranslation("passworddatafield.security_field_no", ApplicationManager.getApplicationBundle()));
			}
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}

		if (!this.encrypt) {
			return ((PasswordDocument) ((JTextField) this.dataField).getDocument()).getContain();
		} else {
			return PasswordDataField
					.encryptPassword(((PasswordDocument) ((JTextField) this.dataField).getDocument()).getContain());
		}
	}

	@Override
	public boolean isEmpty() {
		if (this.isPasswordSecurity()
				&& !((PasswordDocument) ((JTextField) this.dataField).getDocument()).isPatternMatches()) {
			return true;
		} else {
			return super.isEmpty();
		}
	}

	@Override
	public void setValue(final Object value) {

		final Object oPreviousValue = this.getValue();
		if (value != null) {
			((JTextField) this.dataField).setText(value.toString());
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		} else {
			this.deleteData();
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

	public void setEncrypt(final boolean encrypt) {
		this.encrypt = encrypt;
	}

	public boolean getEncrypt() {
		return this.encrypt;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (this.securityButton != null) {
			this.securityButton.setEnabled(enabled);
		}
	}

	/**
	 * Encrypt the input parameter with ASCII codification
	 * @param password
	 * @return The encrypted password.
	 */
	public static final String encryptPassword(final String password) {
		try {
			PasswordDataField.logger.debug("Encripting: {}", password);
			final MessageDigest md = java.security.MessageDigest.getInstance(PasswordDataField.ENCRYPT_PASSWORDS_ALGORITHM);
			// Get the password bytes
			final byte[] bytes = password.getBytes();
			md.update(bytes);
			final byte[] encryptedBytes = md.digest();

			final byte[] encryptedBytesExt = new byte[encryptedBytes.length];

			for (int i = 0; i < encryptedBytes.length; i++) {
				byte b = encryptedBytes[i];
				final byte bAux = (byte) 127;
				b = (byte) (b & bAux);
				encryptedBytesExt[i] = b;
			}
			final String result = new String(encryptedBytesExt, "US-ASCII");
			PasswordDataField.logger.debug("Encripted: {}", result);
			return result;
		} catch (final Exception e) {
			PasswordDataField.logger.error(e.getMessage(), e);
			return null;
		}
	}

	private boolean isCapsLockOn() {
		boolean iscaplockon = false;
		try {
			iscaplockon = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
		} catch (final UnsupportedOperationException e) {
			PasswordDataField.logger.debug(null, e);
			iscaplockon = false;
		}
		return iscaplockon;
	}

	public int getPasswordLength() {
		if (this.isEmpty()) {
			return 0;
		} else {
			return ((PasswordDocument) ((JTextField) this.dataField).getDocument()).getContain().length();
		}
	}

	public String getPassword() {
		if (this.isEmpty()) {
			return null;
		} else {
			return ((PasswordDocument) ((JTextField) this.dataField).getDocument()).getContain();
		}
	}

	@Override
	public void free() {
		super.free();
		if (this.popupTip != null) {
			this.popupTip.dispose();
			this.popupTip = null;
		}
	}

	/**
	 * Check is the password field has a specific level security
	 * @return true if password field has a security level. Otherwise, false.
	 */
	public boolean isPasswordSecurity() {
		return this.passwordSecurity;
	}

	/**
	 * Set if password field has a security level
	 * @param boolean
	 *
	 */
	public void setPasswordSecurity(final boolean passwordSecurity) {
		this.passwordSecurity = passwordSecurity;
	}

	/**
	 * Return the security level of password field
	 * @return The security level of password field
	 */
	public int getSecureLevel() {
		return this.secureLevel;
	}

	/**
	 * Level of password:
	 *
	 * <table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <th>Level</th>
	 * <th>Restrictions</th>
	 * <th>Regexp</th>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td>No restrictions</td>
	 * <td>(.*)</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>Minimum 5 characters.</td>
	 * <td>(.{5,})</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>Minimum 6 characters, 1 lowercase, 1 uppercase.</td>
	 * <td>((?=.*\p{Ll})(?=.*\p{Lu}).{6,})</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>Minimum 7 characters, 1 lowercase, 1 uppercase, 1 number.</td>
	 * <td>((?=.*\p{Nd})(?=.*\p{Ll})(?=.*\p{Lu}).{7,})</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>Minimum 8 characters. 1 lowercase, 1 uppercase, 1 number, 1 symbol.</td>
	 * <td>((?=.*[\p{P}\p{S}])(?=.*\p{Nd})(?=.*\p{Ll})(?=.*\p{Lu}).{8,})</td>
	 * </tr>
	 * </table>
	 */
	protected void addPatterns() {
		this.patternLevels.put(0, "(.*)");
		this.patternLevels.put(1, "(.{5,})");
		this.patternLevels.put(2, "((?=.*\\p{Ll})(?=.*\\p{Lu}).{6,})");
		this.patternLevels.put(3, "((?=.*\\p{Nd})(?=.*\\p{Ll})(?=.*\\p{Lu}).{7,})");
		this.patternLevels.put(4, "((?=.*[\\p{P}\\p{S}])(?=.*\\p{Nd})(?=.*\\p{Ll})(?=.*\\p{Lu}).{8,})");
	}

	/**
	 * Check is security button is enabled
	 * @return true if securityButton is enabled. Otherwise, false.
	 */
	public boolean isSecurityButton() {
		if (this.securityButton == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
		if (this.isPasswordSecurity()) {
			this.createPopupSecurity();
		}
	}

}
