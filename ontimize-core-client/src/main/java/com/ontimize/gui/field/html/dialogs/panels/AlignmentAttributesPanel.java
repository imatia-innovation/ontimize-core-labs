package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * A panel for editing table alignment attributes
 *
 * @author Imatia S.L.
 *
 */
public class AlignmentAttributesPanel extends HTMLAttributeEditorPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String VERT_ALIGNMENTS[] = { "top", "middle", "bottom" };

	private static final String HORIZ_ALIGNMENTS[] = { "left", "center", "right", "justify" };

	protected JCheckBox vAlignCB = null;

	protected JCheckBox hAlignCB = null;

	protected JComboBox vLocCombo = null;

	protected JComboBox hLocCombo = null;

	protected TitledBorder externalBorder;

	public AlignmentAttributesPanel() {
		this(new Hashtable());
	}

	public AlignmentAttributesPanel(final Map attr) {
		super(attr);
		this.initialize();
		this.updateComponentsFromAttribs();
	}

	@Override
	public void updateComponentsFromAttribs() {
		if (this.attribs.containsKey("align")) {
			this.hAlignCB.setSelected(true);
			this.hLocCombo.setEnabled(true);
			this.hLocCombo.setSelectedItem(this.attribs.get("align"));
		} else {
			this.hAlignCB.setSelected(false);
			this.hLocCombo.setEnabled(false);
		}

		if (this.attribs.containsKey("valign")) {
			this.vAlignCB.setSelected(true);
			this.vLocCombo.setEnabled(true);
			this.vLocCombo.setSelectedItem(this.attribs.get("valign"));
		} else {
			this.vAlignCB.setSelected(false);
			this.vLocCombo.setEnabled(false);
		}
	}

	@Override
	public void updateAttribsFromComponents() {
		if (this.vAlignCB.isSelected()) {
			this.attribs.put("valign", this.vLocCombo.getSelectedItem().toString());
		} else {
			this.attribs.remove("valign");
		}

		if (this.hAlignCB.isSelected()) {
			this.attribs.put("align", this.hLocCombo.getSelectedItem().toString());
		} else {
			this.attribs.remove("align");
		}
	}

	public void setComponentStates(final Map attribs) {
		if (attribs.containsKey("align")) {
			this.hAlignCB.setSelected(true);
			this.hLocCombo.setEnabled(true);
			this.hLocCombo.setSelectedItem(attribs.get("align"));
		} else {
			this.hAlignCB.setSelected(false);
			this.hLocCombo.setEnabled(false);
		}

		if (attribs.containsKey("valign")) {
			this.vAlignCB.setSelected(true);
			this.vLocCombo.setEnabled(true);
			this.vLocCombo.setSelectedItem(attribs.get("valign"));
		} else {
			this.vAlignCB.setSelected(false);
			this.vLocCombo.setEnabled(false);
		}
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	protected void initialize() {
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.gridx = 1;
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 5, 0);
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
		this.setSize(185, 95);
		this.setBorder(this.getPanelBorder());

		this.setPreferredSize(new java.awt.Dimension(185, 95));
		this.setMaximumSize(this.getPreferredSize());
		this.setMinimumSize(this.getPreferredSize());
		this.add(this.getVAlignCB(), gridBagConstraints);
		this.add(this.getHAlignCB(), gridBagConstraints1);
		this.add(this.getVLocCombo(), gridBagConstraints2);
		this.add(this.getHLocCombo(), gridBagConstraints3);
	}

	protected Border getPanelBorder() {
		final Border internal = javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2);
		this.externalBorder = javax.swing.BorderFactory
				.createTitledBorder(HTMLAttributeEditorPanel.i18n.str("HTMLShef.content_alignment"));
		return BorderFactory.createCompoundBorder(this.externalBorder, internal);
	}

	/**
	 * This method initializes vAlignCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getVAlignCB() {
		if (this.vAlignCB == null) {
			this.vAlignCB = new JCheckBox();
			this.vAlignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.vertical"));

			this.vAlignCB.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					AlignmentAttributesPanel.this.vLocCombo
					.setEnabled(AlignmentAttributesPanel.this.vAlignCB.isSelected());
				}
			});
		}
		return this.vAlignCB;
	}

	/**
	 * This method initializes hAlignCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getHAlignCB() {
		if (this.hAlignCB == null) {
			this.hAlignCB = new JCheckBox();
			this.hAlignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.horizontal"));

			this.hAlignCB.addActionListener(new java.awt.event.ActionListener() {

				@Override
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					AlignmentAttributesPanel.this.hLocCombo
					.setEnabled(AlignmentAttributesPanel.this.hAlignCB.isSelected());
				}
			});
		}
		return this.hAlignCB;
	}

	/**
	 * This method initializes vLocCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getVLocCombo() {
		if (this.vLocCombo == null) {
			this.vLocCombo = new JComboBox(AlignmentAttributesPanel.VERT_ALIGNMENTS);
		}
		return this.vLocCombo;
	}

	/**
	 * This method initializes hLocCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getHLocCombo() {
		if (this.hLocCombo == null) {
			this.hLocCombo = new JComboBox(AlignmentAttributesPanel.HORIZ_ALIGNMENTS);

		}
		return this.hLocCombo;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);

		if (this.vAlignCB != null) {
			this.vAlignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.vertical"));
		}
		if (this.hAlignCB != null) {
			this.hAlignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.horizontal"));
		}
		if (this.externalBorder != null) {
			this.externalBorder.setTitle(HTMLAttributeEditorPanel.i18n.str("HTMLShef.content_alignment"));
		}

	}

}
