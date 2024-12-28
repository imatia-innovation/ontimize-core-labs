package com.ontimize.gui.field;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.LimitedTextDocument;
import com.ontimize.gui.field.document.TextDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.multilanguage.FormMultilanguageTable;

/**
 * This class is the implementation for a text field.
 * <p>
 *
 * @author Imatia Innovation
 */
public class TextDataField extends TextFieldDataField implements InteractionManagerModeListener {

	private static final Logger logger = LoggerFactory.getLogger(TextDataField.class);

	/**
	 * The parameter name for trimming trailing white spaces in field. By default, false.
	 */
	public static final String TRIM = "trim";

	/**
	 * The default value for uppercase condition. By default, false.
	 */
	public static boolean UPPERCASE_DEFAULT_VALUE = false;

	/**
	 * A reference to uppercase condition. By default, false.
	 */
	protected boolean uppercase = false;

	/**
	 * The name of the parameter that indicates whether multi-language mode is activated for the
	 * selected field
	 */
	private static final String MULTILANGUAGE_STR = "multilanguage";

	/**
	 * Condition about whether field value must contain leading and trailing whitespace omitted or not.
	 *
	 * @since 5.2068EN
	 */
	// 5.2067EN-0.1
	protected boolean trim = true;

	/**
	 * The button that shows the translation table
	 */
	protected JButton multilanguageButton = null;

	/**
	 * Indicates whether the field is configured as multi-language or not
	 */
	protected boolean multilanguage = false;

	/**
	 * Variable where the local reference is stored.
	 */
	protected transient EntityReferenceLocator locator = null;

	public TextDataField(final Map parameters) {
		super();
		this.init(parameters);
		if (this.multilanguage) {
			this.createMultilanguageButton();
		}
	}

	/**
	 * The class constructor.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Adds the next parameters:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>maxlength</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The maximum number of characters supported for introducing.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>uppercase</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Shows the characters always in uppercase.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>trim</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether field value must contain leading and trailing whitespace omitted or
	 *        not.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>multilanguage</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether the field should display the multi-language configuration button
	 *        when this option is enabled in the form. (Only allowed when the parameter 'multilanguage'
	 *        in the {@link Form} is set to true)</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {

		super.init(parameters);
		final Object uppercase = parameters.get("uppercase");
		if (uppercase != null) {
			if (uppercase.equals("yes")) {
				this.uppercase = true;
			}
		} else {
			this.uppercase = TextDataField.UPPERCASE_DEFAULT_VALUE;
		}
		final Object oMaxlength = parameters.get("maxlength");
		int iMaxLength = 0;
		if (oMaxlength != null) {
			try {
				iMaxLength = Integer.parseInt(oMaxlength.toString());
			} catch (final Exception e) {
				TextDataField.logger
				.error(this.getClass().toString() + " : Error in parameter 'maxlength'." + e.getMessage(), e);
			}
		}
		if (iMaxLength != 0) {
			((JTextField) this.dataField).setDocument(new LimitedTextDocument(iMaxLength, this.uppercase));
		} else {
			((JTextField) this.dataField).setDocument(new TextDocument(this.uppercase));
		}
		this.trim = ParseUtils.getBoolean((String) parameters.get(TextDataField.TRIM), true);

		this.multilanguage = ParseUtils.getBoolean((String) parameters.get(TextDataField.MULTILANGUAGE_STR), false);
		if (this.multilanguage) {
			try {
				this.locator = ApplicationManager.getApplication().getReferenceLocator();
			} catch (final Exception e) {
				TextDataField.logger.error("Error when obtaining the reference locator", e);
			}
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return ((JTextField) this.dataField).getText();
	}

	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}

		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		if (value != null) {
			final Document doc = ((JTextField) this.dataField).getDocument();
			if (doc instanceof LimitedTextDocument) {
				((LimitedTextDocument) doc).setActivatedLimited(false);
			}
			if (this.trim) {
				((JTextField) this.dataField).setText(value.toString().trim());
			} else {
				((JTextField) this.dataField).setText(value.toString());
			}
			if (doc instanceof LimitedTextDocument) {
				((LimitedTextDocument) doc).setActivatedLimited(true);
			}
			this.valueSave = this.getValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		} else {
			this.deleteData();
		}
	}

	/**
	 * Creates the button to display the multi-language translation button
	 */
	protected void createMultilanguageButton() {
		this.multilanguageButton = new FieldButton();
		this.multilanguageButton.setMargin(new Insets(0, 0, 0, 0));
		this.multilanguageButton.setIcon(ImageManager.getIcon(ImageManager.BUNDLE));
		this.multilanguageButton.setToolTipText(ApplicationManager.getTranslation("textdatafield.multilanguage",
				ApplicationManager.getApplicationBundle()));
		this.multilanguageButton.setFocusable(false);

		this.multilanguageButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				TextDataField.this.createAndShowDialogTranslation();
				TextDataField.this.getParentForm().refreshCurrentDataRecord();

			}
		});

		super.add(this.multilanguageButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


		if (this.labelPosition != SwingConstants.TOP) {
			super.add(this.multilanguageButton, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		} else {
			super.add(this.multilanguageButton, new GridBagConstraints(2, 2, 1, 1, 0, 0,
					GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

	}

	/**
	 * Creates and displays the translation dialog that is activated by clicking on the corresponding
	 * button
	 */
	protected void createAndShowDialogTranslation() {

		final Map formKeys = new Hashtable();
		for (final Object o : this.getParentForm().getKeys()) {
			formKeys.put(o, this.getParentForm().getDataFieldValue((String) o));
		}

		final Dialog owner = new FormMultilanguageTable(SwingUtilities.getWindowAncestor(this.getMultilanguageButton()), true,
				this.locator, this.getParentForm().getEntityName(),
				(String) this.getAttribute(), formKeys, false);
		if (((FormMultilanguageTable) owner).isCreated()) {
			ApplicationManager.center(owner);
			owner.setVisible(true);
		} else {
			owner.dispose();
		}
	}

	/**
	 * Checks if the field is multi-language or not
	 * @return <code>true</code> if the field is multi-language, <code>false</code> otherwise.
	 */
	public boolean isMultilanguage() {
		return this.multilanguage;
	}

	/**
	 * Sets if the field is multi-language or not
	 * @param multilanguage <code>true</code> to set the field as multi-language, <code>false</code>
	 *        otherwise
	 */
	public void setMultilanguage(final boolean multilanguage) {
		this.multilanguage = multilanguage;
	}

	/**
	 * Returns the button showing the multi-language translation dialog
	 * @return The multi-language button
	 */
	public JButton getMultilanguageButton() {
		return this.multilanguageButton;
	}

	/**
	 * Sets the button that displays the multi-language translation dialog
	 * @param multilanguageButton the multi-language button
	 */
	public void setMultilanguageButton(final JButton multilanguageButton) {
		this.multilanguageButton = multilanguageButton;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

	/**
	 * Gets the maximum length for field.
	 * <p>
	 * @return the max length
	 */
	public int getMaxLength() {
		final Document doc = ((JTextField) this.dataField).getDocument();
		if (doc instanceof LimitedTextDocument) {
			return ((LimitedTextDocument) doc).getMaxLength();
		} else {
			return -1;
		}
	}

	/**
	 * Sets the max length.
	 * <p>
	 * @param l the length to set
	 */
	public void setMaxLength(final int l) {
		final Document doc = ((JTextField) this.dataField).getDocument();
		if (doc instanceof LimitedTextDocument) {
			((LimitedTextDocument) doc).setMaxLength(l);
		} else {
			final String text = this.getText();
			((JTextComponent) this.dataField).setDocument(new LimitedTextDocument(l));
			((JTextComponent) this.dataField).setText(text);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (this.isMultilanguage() && (this.multilanguageButton != null)) {
			this.getMultilanguageButton().setEnabled(enabled);
		}
	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (this.getMultilanguageButton() != null) {
			if (e.getInteractionManagerMode() == InteractionManager.UPDATE) {
				this.getMultilanguageButton().setVisible(true);
				this.getMultilanguageButton().setEnabled(true);
			} else {
				this.getMultilanguageButton().setVisible(false);
				this.getMultilanguageButton().setEnabled(false);
			}
		}

	}

}
