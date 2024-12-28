package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableAttributesPanel extends HTMLAttributeEditorPanel {

	private static final Logger logger = LoggerFactory.getLogger(TableAttributesPanel.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String ALIGNMENTS[] = { "left", "center", "right" };

	private static final String MEASUREMENTS[] = { "percent", "pixels" };

	protected JCheckBox widthCB = null;

	protected JSpinner widthField = null;

	protected JComboBox widthCombo = null;

	protected JCheckBox alignCB = null;

	protected JCheckBox cellSpacingCB = null;

	protected JSpinner cellSpacingField = null;

	protected JCheckBox borderCB = null;

	protected JSpinner borderField = null;

	protected JCheckBox cellPaddingCB = null;

	protected JSpinner cellPaddingField = null;

	protected JComboBox alignCombo = null;

	protected BGColorPanel bgPanel = null;

	protected JPanel expansionPanel = null;

	protected TitledBorder propertiesBorder;

	/**
	 * This is the default constructor
	 */
	public TableAttributesPanel() {
		this(new Hashtable());
	}

	public TableAttributesPanel(final Map attribs) {
		super(attribs);
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
				this.widthCombo.setSelectedIndex(1);
			}
			this.widthField.setEnabled(true);

			try {
				this.widthField.getModel().setValue(new Integer(w));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.widthCB.setSelected(false);
			this.widthField.setEnabled(false);
			this.widthCombo.setEnabled(false);
		}

		if (this.attribs.containsKey("align")) {
			this.alignCB.setSelected(true);
			this.alignCombo.setEnabled(true);
			this.alignCombo.setSelectedItem(this.attribs.get("align"));
		} else {
			this.alignCB.setSelected(false);
			this.alignCombo.setEnabled(false);
		}

		if (this.attribs.containsKey("border")) {
			this.borderCB.setSelected(true);
			this.borderField.setEnabled(true);
			try {
				this.borderField.getModel().setValue(new Integer(this.attribs.get("border").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.borderCB.setSelected(false);
			this.borderField.setEnabled(false);
		}

		if (this.attribs.containsKey("cellpadding")) {
			this.cellPaddingCB.setSelected(true);
			this.cellPaddingField.setEnabled(true);
			try {
				this.cellPaddingField.getModel().setValue(new Integer(this.attribs.get("cellpadding").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.cellPaddingCB.setSelected(false);
			this.cellPaddingField.setEnabled(false);
		}

		if (this.attribs.containsKey("cellspacing")) {
			this.cellSpacingCB.setSelected(true);
			this.cellSpacingField.setEnabled(true);
			try {
				this.cellSpacingField.getModel().setValue(new Integer(this.attribs.get("cellspacing").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.cellSpacingCB.setSelected(false);
			this.cellSpacingField.setEnabled(false);
		}

		if (this.attribs.containsKey("bgcolor")) {
			this.bgPanel.setSelected(true);
			this.bgPanel.setColor(this.attribs.get("bgcolor").toString());
		} else {
			this.bgPanel.setSelected(false);
		}
	}

	@Override
	public void updateAttribsFromComponents() {
		if (this.widthCB.isSelected()) {
			String w = this.widthField.getModel().getValue().toString();
			if (this.widthCombo.getSelectedIndex() == 0) {
				w += "%";
			}
			this.attribs.put("width", w);
		} else {
			this.attribs.remove("width");
		}

		if (this.alignCB.isSelected()) {
			this.attribs.put("align", this.alignCombo.getSelectedItem().toString());
		} else {
			this.attribs.remove("align");
		}

		if (this.borderCB.isSelected()) {
			this.attribs.put("border", this.borderField.getModel().getValue().toString());
		} else {
			this.attribs.remove("border");
		}

		if (this.cellSpacingCB.isSelected()) {
			this.attribs.put("cellspacing", this.cellSpacingField.getModel().getValue().toString());
		} else {
			this.attribs.remove("cellspacing");
		}

		if (this.cellPaddingCB.isSelected()) {
			this.attribs.put("cellpadding", this.cellPaddingField.getModel().getValue().toString());
		} else {
			this.attribs.remove("cellpadding");
		}

		if (this.bgPanel.isSelected()) {
			this.attribs.put("bgcolor", this.bgPanel.getColor());
		} else {
			this.attribs.remove("bgcolor");
		}
	}

	public void setComponentStates(final Map attribs) {
		if (attribs.containsKey("width")) {
			this.widthCB.setSelected(true);
			String w = attribs.get("width").toString();
			if (w.endsWith("%")) {
				w = w.substring(0, w.length() - 1);
			} else {
				this.widthCombo.setSelectedIndex(1);
			}
			try {
				this.widthField.getModel().setValue(new Integer(w));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.widthCB.setSelected(false);
			this.widthField.setEnabled(false);
			this.widthCombo.setEnabled(false);
		}

		if (attribs.containsKey("align")) {
			this.alignCB.setSelected(true);
			this.alignCombo.setSelectedItem(attribs.get("align"));
		} else {
			this.alignCB.setSelected(false);
			this.alignCombo.setEnabled(false);
		}

		if (attribs.containsKey("border")) {
			this.borderCB.setSelected(true);
			try {
				this.borderField.getModel().setValue(new Integer(attribs.get("border").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.borderCB.setSelected(false);
			this.borderField.setEnabled(false);
		}

		if (attribs.containsKey("cellpadding")) {
			this.cellPaddingCB.setSelected(true);
			try {
				this.cellPaddingField.getModel().setValue(new Integer(attribs.get("cellpadding").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.cellPaddingCB.setSelected(false);
			this.cellPaddingField.setEnabled(false);
		}

		if (attribs.containsKey("cellspacing")) {
			this.cellSpacingCB.setSelected(true);
			try {
				this.cellSpacingField.getModel().setValue(new Integer(attribs.get("cellspacing").toString()));
			} catch (final Exception ex) {
				TableAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.cellSpacingCB.setSelected(false);
			this.cellSpacingField.setEnabled(false);
		}

		if (attribs.containsKey("bgcolor")) {
			this.bgPanel.setSelected(true);
			this.bgPanel.setColor(attribs.get("bgcolor").toString());
		} else {
			this.bgPanel.setSelected(false);
		}

	}

	/**
	 * This method initializes this
	 * @return void
	 */
	protected void initialize() {
		final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints12.gridwidth = 4;
		gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.weightx = 0.0;
		gridBagConstraints12.weighty = 1.0;
		gridBagConstraints12.gridy = 4;
		final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridwidth = 4;
		gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints11.weighty = 0.0;
		gridBagConstraints11.gridy = 3;
		final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 1;
		gridBagConstraints4.weightx = 0.0;
		gridBagConstraints4.gridwidth = 2;
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints4.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints4.gridx = 1;
		final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints10.gridy = 2;
		gridBagConstraints10.weightx = 1.0;
		gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints10.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints10.gridx = 4;
		final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 3;
		gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints9.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints9.gridy = 2;
		final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridy = 2;
		gridBagConstraints8.weightx = 0.0;
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints8.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints8.gridx = 1;
		final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints7.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints7.gridy = 2;
		final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints6.gridy = 1;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints6.insets = new java.awt.Insets(0, 0, 2, 0);
		gridBagConstraints6.gridx = 4;
		final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 3;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.insets = new java.awt.Insets(0, 0, 2, 3);
		gridBagConstraints5.gridy = 1;
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.insets = new java.awt.Insets(0, 0, 2, 3);
		gridBagConstraints3.gridy = 1;
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 0.0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.gridwidth = 2;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints2.gridx = 3;
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 0.0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints1.gridx = 1;
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 3);
		gridBagConstraints.gridy = 0;

		this.setLayout(new GridBagLayout());
		this.setSize(420, 180);
		this.setPreferredSize(new java.awt.Dimension(420, 180));
		this.setBorder(this.getPropertiesBorder());

		this.add(this.getWidthCB(), gridBagConstraints);
		this.add(this.getWidthField(), gridBagConstraints1);
		this.add(this.getWidthCombo(), gridBagConstraints2);
		this.add(this.getAlignCB(), gridBagConstraints3);
		this.add(this.getCellSpacingCB(), gridBagConstraints5);
		this.add(this.getCellSpacingField(), gridBagConstraints6);
		this.add(this.getBorderCB(), gridBagConstraints7);
		this.add(this.getBorderField(), gridBagConstraints8);
		this.add(this.getCellPaddingCB(), gridBagConstraints9);
		this.add(this.getCellPaddingField(), gridBagConstraints10);
		this.add(this.getAlignCombo(), gridBagConstraints4);
		this.add(this.getBGPanel(), gridBagConstraints11);
		this.add(this.getExpansionPanel(), gridBagConstraints12);

	}

	protected Border getPropertiesBorder() {
		this.propertiesBorder = BorderFactory
				.createTitledBorder(HTMLAttributeEditorPanel.i18n.str("HTMLShef.properties"));
		return BorderFactory.createCompoundBorder(this.propertiesBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
					TableAttributesPanel.this.widthField.setEnabled(TableAttributesPanel.this.widthCB.isSelected());
					TableAttributesPanel.this.widthCombo.setEnabled(TableAttributesPanel.this.widthCB.isSelected());
				}
			});
		}
		return this.widthCB;
	}

	/**
	 * This method initializes widthField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getWidthField() {
		if (this.widthField == null) {
			this.widthField = new JSpinner(new SpinnerNumberModel(100, 1, 999, 1));

		}
		return this.widthField;
	}

	/**
	 * This method initializes widthCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getWidthCombo() {
		if (this.widthCombo == null) {
			this.widthCombo = new JComboBox(TableAttributesPanel.MEASUREMENTS);
		}
		return this.widthCombo;
	}

	/**
	 * This method initializes alignCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getAlignCB() {
		if (this.alignCB == null) {
			this.alignCB = new JCheckBox();
			this.alignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.align"));
			this.alignCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					TableAttributesPanel.this.alignCombo.setEnabled(TableAttributesPanel.this.alignCB.isSelected());
				}
			});
		}
		return this.alignCB;
	}

	/**
	 * This method initializes cellSpacingCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getCellSpacingCB() {
		if (this.cellSpacingCB == null) {
			this.cellSpacingCB = new JCheckBox();
			this.cellSpacingCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.cellspacing"));
			this.cellSpacingCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					TableAttributesPanel.this.cellSpacingField
					.setEnabled(TableAttributesPanel.this.cellSpacingCB.isSelected());
				}
			});
		}
		return this.cellSpacingCB;
	}

	/**
	 * This method initializes cellSpacingField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getCellSpacingField() {
		if (this.cellSpacingField == null) {
			this.cellSpacingField = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

		}
		return this.cellSpacingField;
	}

	/**
	 * This method initializes borderCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getBorderCB() {
		if (this.borderCB == null) {
			this.borderCB = new JCheckBox();
			this.borderCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.border"));
			this.borderCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					TableAttributesPanel.this.borderField.setEnabled(TableAttributesPanel.this.borderCB.isSelected());
				}
			});
		}
		return this.borderCB;
	}

	/**
	 * This method initializes borderField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getBorderField() {
		if (this.borderField == null) {
			this.borderField = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

		}
		return this.borderField;
	}

	/**
	 * This method initializes cellPaddingCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getCellPaddingCB() {
		if (this.cellPaddingCB == null) {
			this.cellPaddingCB = new JCheckBox();
			this.cellPaddingCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.cellpadding"));
			this.cellPaddingCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					TableAttributesPanel.this.cellPaddingField
					.setEnabled(TableAttributesPanel.this.cellPaddingCB.isSelected());
				}
			});
		}
		return this.cellPaddingCB;
	}

	/**
	 * This method initializes cellPaddingField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getCellPaddingField() {
		if (this.cellPaddingField == null) {
			this.cellPaddingField = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

		}
		return this.cellPaddingField;
	}

	/**
	 * This method initializes alignCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getAlignCombo() {
		if (this.alignCombo == null) {
			this.alignCombo = new JComboBox(TableAttributesPanel.ALIGNMENTS);
		}
		return this.alignCombo;
	}

	/**
	 * This method initializes tempPanel
	 * @return javax.swing.JPanel
	 */
	protected JPanel getBGPanel() {
		if (this.bgPanel == null) {
			this.bgPanel = new BGColorPanel();

		}
		return this.bgPanel;
	}

	/**
	 * This method initializes expansionPanel
	 * @return javax.swing.JPanel
	 */
	protected JPanel getExpansionPanel() {
		if (this.expansionPanel == null) {
			this.expansionPanel = new JPanel();
		}
		return this.expansionPanel;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);

		if (this.propertiesBorder != null) {
			this.propertiesBorder.setTitle(HTMLAttributeEditorPanel.i18n.str("HTMLShef.properties"));
		}
		if (this.widthCB != null) {
			this.widthCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.width"));
		}
		if (this.alignCB != null) {
			this.alignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.align"));
		}
		if (this.cellPaddingCB != null) {
			this.cellPaddingCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.cellpadding"));
		}
		if (this.cellSpacingCB != null) {
			this.cellSpacingCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.cellspacing"));
		}
		if (this.borderCB != null) {
			this.borderCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.border"));
		}
		if (this.bgPanel != null) {
			this.bgPanel.setResourceBundle(resourceBundle);
		}
	}

}
