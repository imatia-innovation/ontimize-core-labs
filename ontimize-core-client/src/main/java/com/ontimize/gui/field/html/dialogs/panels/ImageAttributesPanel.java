package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ImagePreview;
import com.ontimize.gui.field.html.HTMLUtils.Base64;
import com.ontimize.gui.field.html.utils.TextEditPopupManager;
import com.ontimize.gui.images.ImageManager;

public class ImageAttributesPanel extends HTMLAttributeEditorPanel {

	private static final Logger logger = LoggerFactory.getLogger(ImageAttributesPanel.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String ALIGNMENTS[] = { "top", "middle", "bottom", "left", "right" };

	protected JLabel imgUrlLabel = null;

	protected JCheckBox altTextCB = null;

	protected JCheckBox widthCB = null;

	protected JCheckBox heightCB = null;

	protected JCheckBox borderCB = null;

	protected JSpinner widthField = null;

	protected JSpinner heightField = null;

	protected JSpinner borderField = null;

	protected JCheckBox vSpaceCB = null;

	protected JCheckBox hSpaceCB = null;

	protected JCheckBox alignCB = null;

	protected JSpinner vSpaceField = null;

	protected JSpinner hSpaceField = null;

	protected JComboBox alignCombo = null;

	protected JTextField imgUrlField = null;

	protected JTextField altTextField = null;

	protected JPanel attribPanel = null;

	protected JButton imgFileChooserB;

	protected JTextField imgFileField = null;

	protected JCheckBox embedImgCB = null;

	protected JPanel spacerPanel = null;

	/**
	 * The last path for file. By default, null.
	 */
	protected File lastPath = null;

	/**
	 * The reference for the bytes of image.
	 */
	protected byte[] bytesImage = null;

	/**
	 * This is the default constructor
	 */
	public ImageAttributesPanel() {
		super();
		this.initialize();
		this.updateComponentsFromAttribs();
	}

	@Override
	public void updateComponentsFromAttribs() {
		if (this.attribs.containsKey("src")) {
			final String src = this.attribs.get("src").toString();
			if (!src.startsWith("data:image/png;base64,")) {
				this.imgUrlField.setText(src);
				this.embedImgCB.setSelected(false);
				this.imgFileField.setText(null);
			} else {
				this.embedImgCB.setSelected(true);
			}
		}

		if (this.attribs.containsKey("alt")) {
			this.altTextCB.setSelected(true);
			this.altTextField.setEditable(true);
			this.altTextField.setText(this.attribs.get("alt").toString());
		} else {
			this.altTextCB.setSelected(false);
			this.altTextField.setEditable(false);
		}

		if (this.attribs.containsKey("width")) {
			this.widthCB.setSelected(true);
			this.widthField.setEnabled(true);
			try {
				this.widthField.getModel().setValue(new Integer(this.attribs.get("width").toString()));
			} catch (final Exception ex) {
				ImageAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.widthCB.setSelected(false);
			this.widthField.setEnabled(false);
		}

		if (this.attribs.containsKey("height")) {
			this.heightCB.setSelected(true);
			this.heightField.setEnabled(true);
			try {
				this.heightField.getModel().setValue(new Integer(this.attribs.get("height").toString()));
			} catch (final Exception ex) {
				ImageAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.heightCB.setSelected(false);
			this.heightField.setEnabled(false);
		}

		if (this.attribs.containsKey("hspace")) {
			this.hSpaceCB.setSelected(true);
			this.hSpaceField.setEnabled(true);
			try {
				this.hSpaceField.getModel().setValue(new Integer(this.attribs.get("hspace").toString()));
			} catch (final Exception ex) {
				ImageAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.hSpaceCB.setSelected(false);
			this.hSpaceField.setEnabled(false);
		}

		if (this.attribs.containsKey("vspace")) {
			this.vSpaceCB.setSelected(true);
			this.vSpaceField.setEnabled(true);
			try {
				this.vSpaceField.getModel().setValue(new Integer(this.attribs.get("vspace").toString()));
			} catch (final Exception ex) {
				ImageAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.vSpaceCB.setSelected(false);
			this.vSpaceField.setEnabled(false);
		}

		if (this.attribs.containsKey("border")) {
			this.borderCB.setSelected(true);
			this.borderField.setEnabled(true);
			try {
				this.borderField.getModel().setValue(new Integer(this.attribs.get("border").toString()));
			} catch (final Exception ex) {
				ImageAttributesPanel.logger.error(null, ex);
			}
		} else {
			this.borderCB.setSelected(false);
			this.borderField.setEnabled(false);
		}

		if (this.attribs.containsKey("align")) {
			this.alignCB.setSelected(true);
			this.alignCombo.setEnabled(true);
			this.alignCombo.setSelectedItem(this.attribs.get("align"));
		} else {
			this.alignCB.setSelected(false);
			this.alignCombo.setEnabled(false);
		}
	}

	@Override
	public void updateAttribsFromComponents() {
		if ((this.imgUrlField.getText() != null) && !this.embedImgCB.isSelected()) {
			this.attribs.put("src", this.imgUrlField.getText());
			this.attribs.remove("embed");
		} else if (this.embedImgCB.isSelected() && (this.bytesImage != null)) {
			this.attribs.put("src", Base64.encodeBytes(this.bytesImage));
			this.attribs.put("embed", "yes");
		}

		if (this.altTextCB.isSelected()) {
			this.attribs.put("alt", this.altTextField.getText());
		} else {
			this.attribs.remove("alt");
		}

		if (this.widthCB.isSelected()) {
			this.attribs.put("width", this.widthField.getModel().getValue().toString());
		} else {
			this.attribs.remove("width");
		}

		if (this.heightCB.isSelected()) {
			this.attribs.put("height", this.heightField.getModel().getValue().toString());
		} else {
			this.attribs.remove("height");
		}

		if (this.vSpaceCB.isSelected()) {
			this.attribs.put("vspace", this.vSpaceField.getModel().getValue().toString());
		} else {
			this.attribs.remove("vspace");
		}

		if (this.hSpaceCB.isSelected()) {
			this.attribs.put("hspace", this.hSpaceField.getModel().getValue().toString());
		} else {
			this.attribs.remove("hspace");
		}

		if (this.borderCB.isSelected()) {
			this.attribs.put("border", this.borderField.getModel().getValue().toString());
		} else {
			this.attribs.remove("border");
		}

		if (this.alignCB.isSelected()) {
			this.attribs.put("align", this.alignCombo.getSelectedItem().toString());
		} else {
			this.attribs.remove("align");
		}
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	protected void initialize() {
		final GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 0;
		gridBagConstraints41.gridwidth = 3;
		gridBagConstraints41.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints41.weighty = 1.0;
		gridBagConstraints41.gridy = 4;
		final GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.gridwidth = 3;
		gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints31.gridy = 3;
		final GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
		gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints22.gridy = 2;
		gridBagConstraints22.weightx = 1.0;
		gridBagConstraints22.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints22.gridwidth = 2;
		gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints22.gridx = 1;
		final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints12.gridwidth = 1;
		gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints12.gridx = 1;
		final GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints13.gridy = 1;
		gridBagConstraints13.weightx = 0.0;
		gridBagConstraints13.insets = new java.awt.Insets(0, 0, 10, 0);
		gridBagConstraints13.gridwidth = 1;
		gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints13.gridx = 2;
		final GridBagConstraints gridBagConstraints01 = new GridBagConstraints();
		gridBagConstraints01.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints01.gridy = 0;
		gridBagConstraints01.weightx = 1.0;
		gridBagConstraints01.insets = new java.awt.Insets(0, 0, 5, 0);
		gridBagConstraints01.gridwidth = 2;
		gridBagConstraints01.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints01.gridx = 1;
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.insets = new java.awt.Insets(0, 0, 10, 5);
		gridBagConstraints2.gridy = 2;
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 10, 5);
		gridBagConstraints1.gridy = 1;
		final GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
		gridBagConstraints0.gridx = 0;
		gridBagConstraints0.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints0.insets = new java.awt.Insets(0, 0, 5, 5);
		gridBagConstraints0.gridy = 0;
		this.imgUrlLabel = new JLabel();
		this.imgUrlLabel.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.image_url"));
		this.setLayout(new GridBagLayout());
		this.setSize(365, 188);

		this.add(this.imgUrlLabel, gridBagConstraints0);
		this.add(this.getImgUrlField(), gridBagConstraints01);

		this.add(this.getEmbedImgCB(), gridBagConstraints1);
		this.add(this.getImgFileField(), gridBagConstraints12);
		this.add(this.getImgFileButton(), gridBagConstraints13);

		this.add(this.getAltTextCB(), gridBagConstraints2);
		this.add(this.getAltTextField(), gridBagConstraints22);

		this.add(this.getAttribPanel(), gridBagConstraints31);
		this.add(this.getSpacerPanel(), gridBagConstraints41);

		this.getAltTextCB();
		this.getAltTextField();
		final TextEditPopupManager popupMan = TextEditPopupManager.getInstance();
		popupMan.registerJTextComponent(this.imgUrlField);
		popupMan.registerJTextComponent(this.altTextField);
	}

	/**
	 * This method initializes altTextCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getAltTextCB() {
		if (this.altTextCB == null) {
			this.altTextCB = new JCheckBox();
			this.altTextCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.alt_text"));
			this.altTextCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ImageAttributesPanel.this.altTextField
					.setEditable(ImageAttributesPanel.this.altTextCB.isSelected());
				}
			});
		}
		return this.altTextCB;
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
					ImageAttributesPanel.this.widthField.setEnabled(ImageAttributesPanel.this.widthCB.isSelected());
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
					ImageAttributesPanel.this.heightField.setEnabled(ImageAttributesPanel.this.heightCB.isSelected());
				}
			});
		}
		return this.heightCB;
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
					ImageAttributesPanel.this.borderField.setEnabled(ImageAttributesPanel.this.borderCB.isSelected());
				}
			});
		}
		return this.borderCB;
	}

	/**
	 * This method initializes widthField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getWidthField() {
		if (this.widthField == null) {
			this.widthField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
			// widthField.setColumns(4);
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
			// heightField.setColumns(4);
		}
		return this.heightField;
	}

	/**
	 * This method initializes borderField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getBorderField() {
		if (this.borderField == null) {
			this.borderField = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
			// borderField.setColumns(4);
		}
		return this.borderField;
	}

	/**
	 * This method initializes vSpaceCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getVSpaceCB() {
		if (this.vSpaceCB == null) {
			this.vSpaceCB = new JCheckBox();
			this.vSpaceCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.vspace"));
			this.vSpaceCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ImageAttributesPanel.this.vSpaceField.setEnabled(ImageAttributesPanel.this.vSpaceCB.isSelected());
				}
			});
		}
		return this.vSpaceCB;
	}

	/**
	 * This method initializes hSpaceCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getHSpaceCB() {
		if (this.hSpaceCB == null) {
			this.hSpaceCB = new JCheckBox();
			this.hSpaceCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.hspace"));
			this.hSpaceCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ImageAttributesPanel.this.hSpaceField.setEnabled(ImageAttributesPanel.this.hSpaceCB.isSelected());
				}
			});
		}
		return this.hSpaceCB;
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
					ImageAttributesPanel.this.alignCombo.setEnabled(ImageAttributesPanel.this.alignCB.isSelected());
				}
			});
		}
		return this.alignCB;
	}

	/**
	 * This method initializes vSpaceField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getVSpaceField() {
		if (this.vSpaceField == null) {
			this.vSpaceField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
			// vSpaceField.setColumns(4);
		}
		return this.vSpaceField;
	}

	/**
	 * This method initializes hSpaceField
	 * @return javax.swing.JSpinner
	 */
	protected JSpinner getHSpaceField() {
		if (this.hSpaceField == null) {
			this.hSpaceField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
			// hSpaceField.setColumns(4);
		}
		return this.hSpaceField;
	}

	/**
	 * This method initializes alignCombo
	 * @return javax.swing.JComboBox
	 */
	protected JComboBox getAlignCombo() {
		if (this.alignCombo == null) {
			this.alignCombo = new JComboBox(ImageAttributesPanel.ALIGNMENTS);
		}
		return this.alignCombo;
	}

	/**
	 * This method initializes imgUrlField
	 * @return javax.swing.JTextField
	 */
	protected JTextField getImgUrlField() {
		if (this.imgUrlField == null) {
			this.imgUrlField = new JTextField();
		}
		return this.imgUrlField;
	}

	/**
	 * This method initializes imgFileField
	 * @return javax.swing.JTextField
	 */
	protected JTextField getImgFileField() {
		if (this.imgFileField == null) {
			this.imgFileField = new JTextField();
			this.imgFileField.setEnabled(false);
		}
		return this.imgFileField;
	}

	/**
	 * This method initializes imgFileChooserB
	 * @return javax.swing.JButton
	 */
	protected JButton getImgFileButton() {
		if (this.imgFileChooserB == null) {
			this.imgFileChooserB = new JButton();
			this.imgFileChooserB.setIcon(ImageManager.getIcon(ImageManager.EXPLORE));

			this.imgFileChooserB.addActionListener(new LoadActionListener());
			this.imgFileChooserB.setEnabled(false);
		}
		return this.imgFileChooserB;
	}

	/**
	 * This method initializes embedImgCB
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getEmbedImgCB() {
		if (this.embedImgCB == null) {
			this.embedImgCB = new JCheckBox();
			this.embedImgCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.embed"));
			this.embedImgCB.addItemListener(new java.awt.event.ItemListener() {

				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					ImageAttributesPanel.this.imgFileChooserB
					.setEnabled(ImageAttributesPanel.this.embedImgCB.isSelected());
					ImageAttributesPanel.this.imgFileField
					.setEnabled(ImageAttributesPanel.this.embedImgCB.isSelected());
					ImageAttributesPanel.this.imgUrlField
					.setEnabled(!ImageAttributesPanel.this.embedImgCB.isSelected());
				}
			});
		}
		return this.embedImgCB;
	}

	/**
	 * This method initializes altTextField
	 * @return javax.swing.JTextField
	 */
	protected JTextField getAltTextField() {
		if (this.altTextField == null) {
			this.altTextField = new JTextField();
		}
		return this.altTextField;
	}

	/**
	 * This method initializes attribPanel
	 * @return javax.swing.JPanel
	 */
	protected JPanel getAttribPanel() {
		if (this.attribPanel == null) {
			final GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints13.insets = new Insets(0, 0, 10, 0);
			gridBagConstraints13.gridx = 3;
			gridBagConstraints13.gridy = 2;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.fill = java.awt.GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.insets = new java.awt.Insets(0, 0, 10, 0);
			gridBagConstraints12.gridx = 3;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 0.0;
			gridBagConstraints12.fill = java.awt.GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.insets = new java.awt.Insets(0, 0, 5, 0);
			gridBagConstraints11.gridx = 3;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.weightx = 0.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.insets = new java.awt.Insets(0, 0, 10, 5);
			final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.insets = new java.awt.Insets(0, 0, 10, 5);
			final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.insets = new java.awt.Insets(0, 0, 5, 5);
			final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.insets = new java.awt.Insets(0, 0, 10, 10);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 0.0;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(0, 0, 10, 10);
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 0.0;
			gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(0, 0, 5, 10);
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 0.0;
			gridBagConstraints5.fill = GridBagConstraints.NONE;
			final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.insets = new java.awt.Insets(0, 0, 10, 5);
			final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(0, 0, 10, 5);
			final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.insets = new java.awt.Insets(0, 0, 5, 5);
			this.attribPanel = new JPanel();
			this.attribPanel.setLayout(new GridBagLayout());
			this.attribPanel.add(this.getWidthCB(), gridBagConstraints2);
			this.attribPanel.add(this.getHeightCB(), gridBagConstraints3);
			this.attribPanel.add(this.getBorderCB(), gridBagConstraints4);
			this.attribPanel.add(this.getWidthField(), gridBagConstraints5);
			this.attribPanel.add(this.getHeightField(), gridBagConstraints6);
			this.attribPanel.add(this.getBorderField(), gridBagConstraints7);
			this.attribPanel.add(this.getVSpaceCB(), gridBagConstraints8);
			this.attribPanel.add(this.getHSpaceCB(), gridBagConstraints9);
			this.attribPanel.add(this.getAlignCB(), gridBagConstraints10);
			this.attribPanel.add(this.getVSpaceField(), gridBagConstraints11);
			this.attribPanel.add(this.getHSpaceField(), gridBagConstraints12);
			this.attribPanel.add(this.getAlignCombo(), gridBagConstraints13);
		}
		return this.attribPanel;
	}

	/**
	 * This method initializes spacerPanel
	 * @return javax.swing.JPanel
	 */
	protected JPanel getSpacerPanel() {
		if (this.spacerPanel == null) {
			this.spacerPanel = new JPanel();
		}
		return this.spacerPanel;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);

		if (this.imgUrlLabel != null) {
			this.imgUrlLabel.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.image_url"));
		}
		if (this.altTextCB != null) {
			this.altTextCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.alt_text"));
		}
		if (this.widthCB != null) {
			this.widthCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.width"));
		}
		if (this.heightCB != null) {
			this.heightCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.height"));
		}
		if (this.borderCB != null) {
			this.borderCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.border"));
		}
		if (this.vSpaceCB != null) {
			this.vSpaceCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.vspace"));
		}
		if (this.hSpaceCB != null) {
			this.hSpaceCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.hspace"));
		}
		if (this.alignCB != null) {
			this.alignCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.align"));
		}
		if (this.embedImgCB != null) {
			this.embedImgCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.embed"));
		}
	}

	protected class LoadActionListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			// Open a dialog to select the images
			JFileChooser chooser = null;
			if (ImageAttributesPanel.this.lastPath != null) {
				chooser = new JFileChooser(ImageAttributesPanel.this.lastPath);
			} else {
				chooser = new JFileChooser();
			}
			final ExtensionFileFilter filter = new ExtensionFileFilter();
			filter.addExtension("jpg");
			filter.addExtension("gif");
			filter.addExtension("png");
			filter.addExtension("JPG");
			filter.addExtension("GIF");
			filter.addExtension("PNG");
			filter.setDescription("Images");
			chooser.setAccessory(new ImagePreview(chooser));
			chooser.setFileFilter(filter);
			final int selection = chooser.showOpenDialog(SwingUtilities.getWindowAncestor(ImageAttributesPanel.this));
			if (selection == JFileChooser.APPROVE_OPTION) {
				// Update the image
				final File selectedFile = chooser.getSelectedFile();
				ImageAttributesPanel.this.lastPath = selectedFile;
				ImageAttributesPanel.this.imgFileField.setText(ImageAttributesPanel.this.lastPath.getAbsolutePath());
				if (!selectedFile.isDirectory()) {
					FileInputStream fileInputStream = null;
					try {
						fileInputStream = new FileInputStream(selectedFile);
						ImageAttributesPanel.this.bytesImage = new byte[(int) selectedFile.length()];
						int read = 0;
						while (read < ImageAttributesPanel.this.bytesImage.length) {
							read += fileInputStream.read(ImageAttributesPanel.this.bytesImage, read,
									ImageAttributesPanel.this.bytesImage.length - read);
							if (read == -1) {
								break;
							}
						}
						fileInputStream.close();
					} catch (final Exception ex) {
						ImageAttributesPanel.logger.error(null, ex);
					} finally {
						if (fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (final Exception ex) {
								ImageAttributesPanel.logger.trace(null, ex);
							}
						}
					}
				}
			}
		}

	}

	/**
	 * Class to filter files by extension. For example, "jpeg" or "gif".
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {

		List extensions = new Vector();

		String description = "";

		/**
		 * Class constructor. Supports a list of accepted extensions.
		 * <p>
		 * @param acceptedExtensions accepted extension list
		 * @param description the description
		 */
		public ExtensionFileFilter(final Vector acceptedExtensions, final String description) {
			this.extensions = acceptedExtensions;
			this.description = description;
		}

		/**
		 * Class constructor. By default, null.
		 */
		public ExtensionFileFilter() {
		}

		/**
		 * Adds an extension.
		 * <p>
		 * @param extension the extension
		 */
		public void addExtension(final String extension) {
			this.extensions.add(extension);
		}

		/**
		 * Gets the description.
		 * <p>
		 * @return the description
		 */
		@Override
		public String getDescription() {
			return this.description;
		}

		/**
		 * Sets description.
		 * <p>
		 * @param description the string with description
		 */
		public void setDescription(final String description) {
			this.description = description;
		}

		/**
		 * Condition to accept the file.
		 * <p>
		 * @param file the file
		 * @return the condition
		 */
		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				final int dotIndex = file.getPath().lastIndexOf(".");
				final String extension = file.getPath().substring(dotIndex + 1);
				if (this.extensions.contains(extension)) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

}
