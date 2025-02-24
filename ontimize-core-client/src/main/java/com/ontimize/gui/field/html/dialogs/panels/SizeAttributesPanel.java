package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for editing the size of a table cell
 *
 * @author Imatia S.L.
 *
 */
public class SizeAttributesPanel extends HTMLAttributeEditorPanel {

	private static final Logger logger = LoggerFactory.getLogger(SizeAttributesPanel.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String MEASUREMENTS[] = { "percent", "pixels" };

	protected JCheckBox widthCB = null;

	protected JCheckBox heightCB = null;

	protected JSpinner widthField = null;

	protected JSpinner heightField = null;

	protected JComboBox wMeasurementCombo = null;

	protected JComboBox hMeasurementCombo = null;

	protected TitledBorder sizePanelBorder;

	public SizeAttributesPanel() {
		this(new Hashtable());
	}

	public SizeAttributesPanel(final Map attr) {
		super(attr);
		this.initialize();
		this.updateComponentsFromAttribs();
	}

	@Override
	public void updateComponentsFromAttribs() {
		if (this.attribs.containsKey("width")) {
			this.widthCB.setSelected(true);
			String w = this.attribs.get("width").toString();
			if (w.endsWith("%")) {
				w = w.substring(0, w.length() - 1);
			} else {
				this.wMeasurementCombo.setSelectedIndex(1);
			}
			try {
				this.widthField.getModel().setValue(new Integer(w));
			} catch (final Exception ex) {
				SizeAttributesPanel.logger.error(null, ex);
			}
			this.wMeasurementCombo.setEnabled(true);
			this.widthField.setEnabled(true);
		} else {
			this.widthCB.setSelected(false);
			this.widthField.setEnabled(false);
			this.wMeasurementCombo.setEnabled(false);
		}

		if (this.attribs.containsKey("height")) {
			this.heightCB.setSelected(true);
			String h = this.attribs.get("height").toString();
			if (h.endsWith("%")) {
				h = h.substring(0, h.length() - 1);
			} else {
				this.hMeasurementCombo.setSelectedIndex(1);
			}
			try {
				this.heightField.getModel().setValue(new Integer(h));
			} catch (final Exception ex) {
				SizeAttributesPanel.logger.error(null, ex);
			}
			this.hMeasurementCombo.setEnabled(true);
			this.heightField.setEnabled(true);
		} else {
			this.heightCB.setSelected(false);
			this.heightField.setEnabled(false);
			this.hMeasurementCombo.setEnabled(false);
		}
	}

	@Override
	public void updateAttribsFromComponents() {
		if (this.widthCB.isSelected()) {
			String w = this.widthField.getModel().getValue().toString();
			if (this.wMeasurementCombo.getSelectedIndex() == 0) {
				w += "%";
			}
			this.attribs.put("width", w);
		} else {
			this.attribs.remove("width");
		}

		if (this.heightCB.isSelected()) {
			String h = this.heightField.getModel().getValue().toString();
			if (this.hMeasurementCombo.getSelectedIndex() == 0) {
				h += "%";
			}
			this.attribs.put("height", h);
		} else {
			this.attribs.remove("height");
		}
	}

	public void setComponentStates(final Map attribs) {
		if (attribs.containsKey("width")) {
			this.widthCB.setSelected(true);
			String w = attribs.get("width").toString();
			if (w.endsWith("%")) {
				w = w.substring(0, w.length() - 1);
			} else {
				this.wMeasurementCombo.setSelectedIndex(1);
			}
			try {
				this.widthField.getModel().setValue(new Integer(w));
			} catch (final Exception ex) {
				SizeAttributesPanel.logger.error(null, ex);
			}
			this.wMeasurementCombo.setEnabled(true);
			this.widthField.setEnabled(true);
		} else {
			this.widthCB.setSelected(false);
			this.widthField.setEnabled(false);
			this.wMeasurementCombo.setEnabled(false);
		}

		if (attribs.containsKey("height")) {
			this.heightCB.setSelected(true);
			String h = attribs.get("height").toString();
			if (h.endsWith("%")) {
				h = h.substring(0, h.length() - 1);
			} else {
				this.hMeasurementCombo.setSelectedIndex(1);
			}
			try {
				this.heightField.getModel().setValue(new Integer(h));
			} catch (final Exception ex) {
				SizeAttributesPanel.logger.error(null, ex);
			}
			this.hMeasurementCombo.setEnabled(true);
			this.heightField.setEnabled(true);
		} else {
			this.heightCB.setSelected(false);
			this.heightField.setEnabled(false);
			this.hMeasurementCombo.setEnabled(false);
		}
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	protected void initialize() {
		final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints5.gridy = 1;
		gridBagConstraints5.weightx = 0.0;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.ipadx = 0;
		gridBagConstraints5.gridx = 2;
		final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints4.gridy = 0;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
		gridBagConstraints4.gridx = 2;
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 0.0;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints3.ipadx = 0;
		gridBagConstraints3.gridx = 1;
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 0.0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints2.ipadx = 0;
		gridBagConstraints2.gridx = 1;
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints1.gridy = 1;
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints.gridy = 0;

		this.setLayout(new GridBagLayout());
		this.setSize(230, 95);
		this.setPreferredSize(new java.awt.Dimension(230, 95));
		this.setMaximumSize(this.getPreferredSize());
		this.setMinimumSize(this.getPreferredSize());
		this.setBorder(this.getPanelBorder());
		this.add(this.getWidthCB(), gridBagConstraints);
		this.add(this.getHeightCB(), gridBagConstraints1);
		this.add(this.getWidthField(), gridBagConstraints2);
		this.add(this.getHeightField(), gridBagConstraints3);
		this.add(this.getWMeasurementCombo(), gridBagConstraints4);
		this.add(this.getHMeasurementCombo(), gridBagConstraints5);
	}

	protected Border getPanelBorder() {

		this.sizePanelBorder = javax.swing.BorderFactory.createTitledBorder(null,
				HTMLAttributeEditorPanel.i18n.str("HTMLShef.size"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				null, null);
		return javax.swing.BorderFactory.createCompoundBorder(this.sizePanelBorder,
				javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
	}

	/**
	 * This method initializes widthCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getWidthCB() {
		if (this.widthCB == null) {
			this.widthCB = new JCheckBox();
			this.widthCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.width"));

			this.widthCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					SizeAttributesPanel.this.widthField.setEnabled(SizeAttributesPanel.this.widthCB.isSelected());
					SizeAttributesPanel.this.wMeasurementCombo
					.setEnabled(SizeAttributesPanel.this.widthCB.isSelected());
				}
			});
		}
		return this.widthCB;
	}

	/**
	 * This method initializes heightCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getHeightCB() {
		if (this.heightCB == null) {
			this.heightCB = new JCheckBox();
			this.heightCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.height"));

			this.heightCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					SizeAttributesPanel.this.heightField.setEnabled(SizeAttributesPanel.this.heightCB.isSelected());
					SizeAttributesPanel.this.hMeasurementCombo
					.setEnabled(SizeAttributesPanel.this.heightCB.isSelected());
				}
			});
		}
		return this.heightCB;
	}

	/**
	 * This method initializes widthField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getWidthField() {
		if (this.widthField == null) {

			this.widthField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

		}

		return this.widthField;
	}

	/**
	 * This method initializes heightField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getHeightField() {
		if (this.heightField == null) {

			this.heightField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

		}

		return this.heightField;
	}

	/**
	 * This method initializes wMeasurementCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getWMeasurementCombo() {
		if (this.wMeasurementCombo == null) {
			this.wMeasurementCombo = new JComboBox(SizeAttributesPanel.MEASUREMENTS);

		}
		return this.wMeasurementCombo;
	}

	/**
	 * This method initializes hMeasurementCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getHMeasurementCombo() {
		if (this.hMeasurementCombo == null) {
			this.hMeasurementCombo = new JComboBox(SizeAttributesPanel.MEASUREMENTS);

		}
		return this.hMeasurementCombo;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);

		if (this.sizePanelBorder != null) {
			this.sizePanelBorder.setTitle(HTMLAttributeEditorPanel.i18n.str("HTMLShef.size"));
		}
		if (this.widthCB != null) {
			this.widthCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.width"));
		}
		if (this.heightCB != null) {
			this.heightCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.height"));
		}
	}

}
