package com.ontimize.gui.field;

import java.awt.Color;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.MaskDocument;
import com.ontimize.jee.common.db.NullValue;

/**
 * This class implements a field for introducing data based in a mask.
 * <p>
 *
 * @author Imatia Innovation
 */
public class MaskDataField extends TextFieldDataField {

	private static final Logger logger = LoggerFactory.getLogger(MaskDataField.class);

	/**
	 * The mask reference. By default, "".
	 */
	protected String mask = new String("");

	/**
	 * The exact match condition. By default, false.
	 */
	protected boolean exactMatch = false;

	private Color originalFontColor = Color.black;

	/**
	 * A reference to a color listener. By default, null.
	 */
	protected ColorListener colorListener = null;

	class ColorListener implements DocumentListener {

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (!MaskDataField.this.exactMatch) {
				return;
			}
			if (!((MaskDocument) e.getDocument()).isRight()) {
				MaskDataField.this.dataField.setForeground(Color.red);
				MaskDataField.this.fontColor = Color.red;
			} else {
				MaskDataField.this.dataField.setForeground(MaskDataField.this.originalFontColor);
				MaskDataField.this.fontColor = MaskDataField.this.originalFontColor;
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (!MaskDataField.this.exactMatch) {
				return;
			}
			if (!((MaskDocument) e.getDocument()).isRight()) {
				MaskDataField.this.dataField.setForeground(Color.red);
				MaskDataField.this.fontColor = Color.red;
			} else {
				MaskDataField.this.dataField.setForeground(MaskDataField.this.originalFontColor);
				MaskDataField.this.fontColor = MaskDataField.this.originalFontColor;
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

	}

	/**
	 * The class constructor. Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public MaskDataField(final Map parameters) {
		this.init(parameters);
		((JTextField) this.dataField).setDocument(new MaskDocument(this.mask, this.exactMatch));
		this.originalFontColor = this.fontColor;
		this.installColorListener();

	}

	/**
	 * Initializes parameters calling to <code>super()</code>.
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
	 *        <tr>
	 *        <td>mask</td>
	 *        <td><b>* </b>: any character<br>
	 *        <b># </b>: alphabetical character (capital letter or lower case letter)<br>
	 *        <b>A </b>: alphabetical character (capital letter)<br>
	 *        <b>a </b>: alphabetical character (lower case letter)<br>
	 *        <b>% </b>: alphanumeric character<br>
	 *        <b>0 </b>: numeric characters</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The characters supported in each position of field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>exactmatch</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>With 'yes' value indicates that field must be filled completely, otherwise, the field
	 *        value will be considered <code>null</code> although it contains any data.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final Object oMaskParameter = parameters.get("mask");
		if (oMaskParameter == null) {
			MaskDataField.logger.debug("Parameter 'mask' not found.");
		} else {
			this.mask = oMaskParameter.toString();
		}
		if (parameters.get(DataField.SIZE) == null) {
			((JTextField) this.dataField).setColumns(this.mask.length());
		}
		final Object exactMatch = parameters.get("exactmatch");
		if (exactMatch != null) {
			if (exactMatch.toString().equalsIgnoreCase("yes")) {
				this.exactMatch = true;
			} else {
				this.exactMatch = false;
			}
		}

		if (this.tipKey == null) {
			this.tipKey = this.mask;
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final MaskDocument document = (MaskDocument) ((JTextField) this.dataField).getDocument();
		try {
			return document.getValue();
		} catch (final Exception e) {
			MaskDataField.logger.error(this.getClass().toString() + ": " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			this.valueSave = this.getValue();
			this.setInnerListenerEnabled(true);
			return;
		} else {
			final MaskDocument document = (MaskDocument) ((JTextField) this.dataField).getDocument();
			try {
				document.setValue(value.toString(), true);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					MaskDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
			this.valueSave = this.getValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

	/**
	 * Gets the mask.
	 * <p>
	 * @return the mask
	 */
	public String getMask() {
		return this.mask;
	}

	/**
	 * Fixes the exact match to parameter condition.
	 * <p>
	 * @param exactMatch the boolean condition
	 */
	public void setExactMatch(final boolean exactMatch) {
		this.exactMatch = exactMatch;
		((MaskDocument) ((JTextField) this.dataField).getDocument()).setExactMatch(this.exactMatch);
	}

	@Override
	public boolean isEmpty() {
		if (!this.exactMatch) {
			return super.isEmpty();
		} else {
			if (((JTextField) this.dataField).getText().length() != this.mask.length()) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Installs a color listener.
	 */
	protected void installColorListener() {
		if (this.colorListener == null) {
			this.colorListener = new ColorListener();
			((JTextField) this.dataField).getDocument().addDocumentListener(this.colorListener);
		}
	}

}
