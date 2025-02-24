package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListAttributesPanel extends HTMLAttributeEditorPanel {

	private static final Logger logger = LoggerFactory.getLogger(ListAttributesPanel.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final int UL_LIST = 0;

	public static final int OL_LIST = 1;

	private static final String UL = "HTMLShef.unordered_list";

	private static final String OL = "HTMLShef.ordered_list";

	private static final String LIST_TYPES[] = { ListAttributesPanel.UL, ListAttributesPanel.OL };

	private static final String OL_TYPES[] = { "1", "a", "A", "i", "I" };

	private static final String OL_TYPE_LABELS[] = { "1, 2, 3, ...", "a, b, c, ...", "A, B, C, ...", "i, ii, iii, ...",
	"I, II, III, ..." };

	private static final String UL_TYPES[] = { "disc", "square", "circle" };

	private static final String UL_TYPE_LABELS[] = { "HTMLShef.solid_circle", "HTMLShef.solid_square",
	"HTMLShef.open_circle" };

	protected JLabel typeLabel = null;

	protected JComboBox typeCombo = null;

	protected JComboBox styleCombo = null;

	protected JSpinner startAtField = null;

	protected JCheckBox styleCB = null;

	protected JCheckBox startAtCB = null;

	/**
	 * This method initializes
	 *
	 */
	public ListAttributesPanel() {
		this(new Hashtable());
	}

	public ListAttributesPanel(final Map ht) {
		super();
		this.initialize();
		this.setAttributes(ht);
		this.updateComponentsFromAttribs();
	}

	public void setListType(final int t) {
		this.typeCombo.setSelectedIndex(t);
		this.updateForType();
	}

	public int getListType() {
		return this.typeCombo.getSelectedIndex();
	}

	protected void updateForType() {
		this.styleCombo.removeAllItems();
		if (this.typeCombo.getSelectedItem().equals(ListAttributesPanel.UL)) {
			for (int i = 0; i < ListAttributesPanel.UL_TYPE_LABELS.length; i++) {
				this.styleCombo.addItem(ListAttributesPanel.UL_TYPE_LABELS[i]);
			}
			this.startAtCB.setEnabled(false);
			this.startAtField.setEnabled(false);
		} else {
			for (int i = 0; i < ListAttributesPanel.OL_TYPE_LABELS.length; i++) {
				this.styleCombo.addItem(ListAttributesPanel.OL_TYPE_LABELS[i]);
			}
			this.startAtCB.setEnabled(true);
			this.startAtField.setEnabled(this.startAtCB.isSelected());
		}
	}

	protected int getIndexForStyle(final String s) {
		if (this.typeCombo.getSelectedIndex() == ListAttributesPanel.UL_LIST) {
			for (int i = 0; i < ListAttributesPanel.UL_TYPES.length; i++) {
				if (ListAttributesPanel.UL_TYPES[i].equals(s)) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < ListAttributesPanel.OL_TYPES.length; i++) {
				if (ListAttributesPanel.OL_TYPES[i].equals(s)) {
					return i;
				}
			}
		}

		return 0;
	}

	/**
	 * This method initializes this
	 *
	 */
	protected void initialize() {
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 2;
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints1.gridy = 1;
		final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.gridx = 1;
		final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 1;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
		gridBagConstraints4.gridx = 1;
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.insets = new java.awt.Insets(0, 0, 5, 0);
		gridBagConstraints3.gridx = 1;
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints.gridy = 0;
		this.typeLabel = new JLabel();
		this.typeLabel.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.list_type"));
		this.setLayout(new GridBagLayout());
		this.setSize(new java.awt.Dimension(234, 159));
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(this.typeLabel, gridBagConstraints);
		this.add(this.getTypeCombo(), gridBagConstraints3);
		this.add(this.getStyleCombo(), gridBagConstraints4);
		this.add(this.getStartAtField(), gridBagConstraints5);
		this.add(this.getStyleCB(), gridBagConstraints1);
		this.add(this.getStartAtCB(), gridBagConstraints2);
	}

	@Override
	public void updateComponentsFromAttribs() {
		// updateForType();
		if (this.attribs.containsKey("type")) {
			this.styleCB.setSelected(true);
			this.styleCombo.setEnabled(true);
			final int i = this.getIndexForStyle(this.attribs.get("type").toString());
			this.styleCombo.setSelectedIndex(i);
		} else {
			this.styleCB.setSelected(false);
			this.styleCombo.setEnabled(false);
		}

		if (this.attribs.containsKey("start")) {
			this.startAtCB.setSelected(true);
			this.startAtField.setEnabled(true);
			try {
				final int n = Integer.parseInt(this.attribs.get("start").toString());
				this.startAtField.getModel().setValue(new Integer(n));
			} catch (final Exception ex) {
				ListAttributesPanel.logger.trace(null, ex);
			}
		} else {
			this.startAtCB.setSelected(false);
			this.startAtField.setEnabled(false);
		}
	}

	@Override
	public void updateAttribsFromComponents() {
		if (this.styleCB.isSelected()) {
			if (this.typeCombo.getSelectedIndex() == ListAttributesPanel.UL_LIST) {
				this.attribs.put("type", ListAttributesPanel.UL_TYPES[this.styleCombo.getSelectedIndex()]);
			} else {
				this.attribs.put("type", ListAttributesPanel.OL_TYPES[this.styleCombo.getSelectedIndex()]);
			}
		} else {
			this.attribs.remove("type");
		}

		if (this.startAtCB.isSelected()) {
			this.attribs.put("start", this.startAtField.getModel().getValue().toString());
		} else {
			this.attribs.remove("start");
		}
	}

	/**
	 * This method initializes typeCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getTypeCombo() {
		if (this.typeCombo == null) {
			this.typeCombo = new JComboBox(ListAttributesPanel.LIST_TYPES);
			this.typeCombo.setRenderer(new BundleComboRenderer());
			this.typeCombo.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ListAttributesPanel.this.updateForType();
				}
			});
		}
		return this.typeCombo;
	}

	/**
	 * This method initializes styleCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getStyleCombo() {
		if (this.styleCombo == null) {
			this.styleCombo = new JComboBox(ListAttributesPanel.UL_TYPE_LABELS);
			this.styleCombo.setRenderer(new BundleComboRenderer());
		}
		return this.styleCombo;
	}

	/**
	 * This method initializes startAtField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getStartAtField() {
		if (this.startAtField == null) {
			this.startAtField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

		}
		return this.startAtField;
	}

	/**
	 * This method initializes styleCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getStyleCB() {
		if (this.styleCB == null) {
			this.styleCB = new JCheckBox();
			this.styleCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.style"));
			this.styleCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ListAttributesPanel.this.styleCombo.setEnabled(ListAttributesPanel.this.styleCB.isSelected());
				}
			});
		}
		return this.styleCB;
	}

	/**
	 * This method initializes startAtCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getStartAtCB() {
		if (this.startAtCB == null) {
			this.startAtCB = new JCheckBox();
			this.startAtCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.start_at"));
			this.startAtCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ListAttributesPanel.this.startAtField.setEnabled(ListAttributesPanel.this.startAtCB.isSelected());
				}
			});
		}
		return this.startAtCB;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);

		if (this.typeLabel != null) {
			this.typeLabel.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.list_type"));
		}
		if (this.styleCB != null) {
			this.styleCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.style"));
		}
		if (this.startAtCB != null) {
			this.startAtCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.start_at"));
		}
	}

	protected class BundleComboRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			if (value instanceof String) {
				value = HTMLAttributeEditorPanel.i18n.str((String) value);
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}

	}

}
