package com.ontimize.gui.attachment;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

public class JDescriptionPanel extends JPanel implements Internationalization {

	protected static final String ATTACH_DESCRIPTION = "form.description";

	protected static final String ATTACH_PRIVATE = "form.private";

	protected JLabel lPrivate = null;

	protected JLabel lDescription = null;

	public JLabel getTitle() {
		return this.lDescription;
	}

	protected JTextArea tDescription = null;

	protected JCheckBox tPrivate = null;

	protected ResourceBundle bundle = null;

	public JDescriptionPanel(final ResourceBundle bundle) {
		super();
		this.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		this.setLayout(new GridBagLayout());

		this.lPrivate = new JLabel(ApplicationManager.getTranslation(JDescriptionPanel.ATTACH_PRIVATE, bundle)) {

			int width = 225;

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.width = this.width;
				return d;
			}
		};

		this.lDescription = new JLabel(ApplicationManager.getTranslation(JDescriptionPanel.ATTACH_DESCRIPTION, bundle));
		this.lDescription.setHorizontalAlignment(java.awt.Label.LEFT);
		this.tDescription = new JTextArea();
		this.tPrivate = new JCheckBox();

		this.add(this.lDescription, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(new JScrollPane(this.tDescription),
				new GridBagConstraints(0, 1, 2, GridBagConstraints.RELATIVE, 1, 1, GridBagConstraints.WEST,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		this.add(this.tPrivate, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.lPrivate,
				new GridBagConstraints(1, 2, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	public void clearValues() {
		this.tDescription.setText("");
		this.tPrivate.setSelected(false);
	}

	public boolean getPrivate() {
		return this.tPrivate.getModel().isSelected();
	}

	public String getDescription() {
		return this.tDescription.getText();
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.bundle = resources;
		this.lPrivate.setText(ApplicationManager.getTranslation(JDescriptionPanel.ATTACH_PRIVATE, this.bundle));
		this.lDescription.setText(ApplicationManager.getTranslation(JDescriptionPanel.ATTACH_DESCRIPTION, this.bundle));

	}

	@Override
	public List getTextsToTranslate() {
		return new Vector(
				Arrays.asList(new String[] { JDescriptionPanel.ATTACH_DESCRIPTION, JDescriptionPanel.ATTACH_PRIVATE }));
	}

	public JCheckBox gePrivateField() {
		return this.tPrivate;
	}

}
