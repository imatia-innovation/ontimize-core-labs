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
import com.ontimize.gui.field.document.NIFDocument;
import com.ontimize.jee.common.db.NullValue;

/**
 * The class to create the NIF(Fiscal Identifier Number in Spain, 8 digits and a final letter
 * related to the previous 8 digits. ) By example, 11111111A is a valid NIF format.
 * <p>
 * The algorithm to assign the final letter consisted of:
 * <ul>
 * <li>Divide the 8 digit number by 23.
 * <li>Multiply the entire part of previous result by 23.
 * <li>Subtract the initial number from previous result.
 * <li>Apply the next relation to find the letter:
 * <p>
 * -0=T, 1=R, 2=W, 3=A, 4=G, 5=M, 6=Y, 7=F, 8=P, 9=D, 10=X, 11=B, 12=N, 13=J, 14=Z, 15=S, 16=Q,
 * 17=V, 18=H, 19=L, 20=C, 21=K, 22=E, 23=O
 * </ul>
 * <p>
 *
 * @autor Imatia Innovation
 */
public class NIFDataField extends TextFieldDataField {

	private static final Logger logger = LoggerFactory.getLogger(NIFDataField.class);

	/**
	 * The mask that contains a valid NIF format.
	 */
	static final String mask = "00000000A";

	/**
	 * The class constructor. Initializes parameters and adds a <code>Document Listener</code> to check
	 * the letter.
	 * @param parameters a Map with parameters
	 */
	public NIFDataField(final Map parameters) {
		this.init(parameters);
		((JTextField) this.dataField).setDocument(new NIFDocument());
		final Document doc = ((JTextField) this.dataField).getDocument();
		doc.addDocumentListener(new DocumentListener() {

			/**
			 * Only checks the letter.
			 */
			@Override
			public void changedUpdate(final DocumentEvent e) {
				NIFDataField.this.checkLetter();
			}

			/**
			 * Only checks the letter.
			 */
			@Override
			public void removeUpdate(final DocumentEvent e) {
				NIFDataField.this.checkLetter();
			}

			/**
			 * Only checks the letter.
			 */
			@Override
			public void insertUpdate(final DocumentEvent e) {
				NIFDataField.this.checkLetter();
			}
		});
		this.dataField.addFocusListener(new FocusAdapter() {

			/**
			 * Only checks the letter.
			 */
			@Override
			public void focusLost(final FocusEvent evento) {
				NIFDataField.this.checkLetter();
			}

			/**
			 * Only checks the letter.
			 */
			public void focusGain(final FocusEvent event) {
				NIFDataField.this.checkLetter();
			}
		});
		((JTextField) this.dataField).addActionListener(new ActionListener() {

			/**
			 * Only transfers the focus.
			 */
			@Override
			public void actionPerformed(final ActionEvent e) {
				NIFDataField.this.transferFocus();
			}
		});
	}

	/**
	 * Initializes the NIF data field. Calls to <code>super()</code> to init parameters.
	 * <p>
	 * @param parameters a Map with parameters.
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		if (parameters.get(DataField.SIZE) == null) {
			((JTextField) this.dataField).setColumns(NIFDataField.mask.length());
		}
	}

	/**
	 * Checks whether the letter is correct. In correct case sets color to black, and in other case
	 * changes to red.
	 */
	protected void checkLetter() {
		final NIFDocument doc = (NIFDocument) ((JTextField) this.dataField).getDocument();
		try {
			final int length = doc.getLength();
			char letter = ' ';
			if (length > 8) {
				final String DNI = ((JTextField) this.dataField).getText(0, 8);
				letter = NIFDocument.calculateLetter(DNI);
				final String text = ((JTextField) this.dataField).getText(0, length);
				if (text.charAt(8) != letter) {
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
				NIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Gets the NIF from field. The result is a String.
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final NIFDocument document = (NIFDocument) ((JTextField) this.dataField).getDocument();
		String sValue = null;
		try {
			sValue = document.getText(0, document.getLength());
			return sValue;
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				NIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * Sets the NIF into the field.
	 * <p>
	 * @param value the value. It must be a String
	 */
	@Override
	public void setValue(final Object value) {
		final Object oldValue = this.getValue();
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		} else {
			final NIFDocument document = (NIFDocument) ((JTextField) this.dataField).getDocument();
			try {
				document.remove(0, document.getLength());
				document.insertString(0, (String) value, null);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					NIFDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oldValue, ValueEvent.PROGRAMMATIC_CHANGE);
		}
	}

	/**
	 * Get the SQL data type.
	 * <p>
	 *
	 * @see java.sql.Types#VARCHAR
	 * @return the SQL Type for VARCHAR
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

}
