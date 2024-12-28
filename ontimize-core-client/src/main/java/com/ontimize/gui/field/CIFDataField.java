package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.CIFDocument;
import com.ontimize.jee.common.db.NullValue;

/**
 * The class to create the CIF (Código de Identificación Fiscal). The control letter is
 * automatically calculated.
 *
 * @author Imatia Innovation
 */
public class CIFDataField extends TextFieldDataField {

	private static final Logger logger = LoggerFactory.getLogger(CIFDataField.class);

	/**
	 * The mask that contains a valid CIF format.
	 */
	static final String mask = "A0000000%";

	/**
	 * The class constructor. Initializes parameters and adds a
	 * <code>Document Listener<code> to the field. <p>
	 *
	 * &#64;param parameters
	 *            the <code>Hashtable</code> with parameters
	 */
	public CIFDataField(final Map parameters) {
		this.init(parameters);
		((JTextField) this.dataField).setDocument(new CIFDocument());
		final Document doc = ((JTextField) this.dataField).getDocument();
		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {
				CIFDataField.this.checkCIF();
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				CIFDataField.this.checkCIF();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				CIFDataField.this.checkCIF();
			}
		});

		this.dataField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent evento) {
				CIFDataField.this.checkCIF();
			}

			public void focusGain(final FocusEvent event) {
				CIFDataField.this.checkCIF();
			}
		});
		((JTextField) this.dataField).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CIFDataField.this.transferFocus();
			}
		});
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		if (parameters.get(DataField.SIZE) == null) {
			((JTextField) this.dataField).setColumns(CIFDataField.mask.length());
		}
	}

	/**
	 * Checks whether the letter is correct. In correct case sets color to black, and in other case
	 * changes to red.
	 *
	 * @see CIFDocument#isCIFWellFormed(String)
	 */
	protected void checkCIF() {
		final CIFDocument doc = (CIFDocument) ((JTextField) this.dataField).getDocument();
		try {
			final int length = doc.getLength();
			if (length > 8) {
				final boolean correct = CIFDocument.isCIFWellFormed(doc.getText(0, length));
				if (!correct) {
					this.dataField.setForeground(Color.red);
				} else {
					if (this.isRequired()) {
						this.dataField.setForeground(DataField.requiredFieldForegroundColor);
					} else {
						this.dataField.setForeground(this.fontColor);
					}
				}
			} else {
				this.dataField.setForeground(Color.red);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				CIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns a string with the CIF.
	 * <p>
	 * @return a string with values
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}

		final CIFDocument document = (CIFDocument) ((JTextField) this.dataField).getDocument();
		String sValue = null;
		try {
			sValue = document.getText(0, document.getLength());
			return sValue;
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				CIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * Sets the CIF value to TextField.
	 * <p>
	 * @param value the string object with CIF value
	 */
	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oldValue = this.getValue();
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
		} else {
			final CIFDocument document = (CIFDocument) ((JTextField) this.dataField).getDocument();
			try {
				document.remove(0, document.getLength());
				document.insertString(0, (String) value, null);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					CIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oldValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		}
		this.valueSave = this.getValue();
	}

	/**
	 * Calculates the CIF number.
	 * <p>
	 * @param number the CIF number
	 * @return the calculated letter
	 */
	protected char calculateLetter(final int number) {
		return CIFDocument.calculateLetter(number);
	}

	/**
	 * Calculates the CIF number.
	 * <p>
	 *
	 * @see CIFDocument#isCIFWellFormed(String)
	 * @param number the CIF number
	 * @return the calculated number
	 */
	protected char calculateNumber(final int number) {
		return CIFDocument.calculateNumber(number);
	}

	/**
	 * Gets the SQL data type.
	 * <p>
	 *
	 * @see java.sql.Types#VARCHAR
	 * @return the SQL Type for VARCHAR
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

	/**
	 * Checks whether CIF is well formed.
	 * <p>
	 * @param CIF the CIf in a String
	 * @return the well-formed condition
	 */
	public static boolean isCIFWellFormed(final String CIF) {
		return CIFDocument.isCIFWellFormed(CIF);
	}

}
