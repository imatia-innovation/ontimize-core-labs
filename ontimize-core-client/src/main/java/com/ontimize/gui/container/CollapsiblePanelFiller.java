package com.ontimize.gui.container;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.FormComponent;

public class CollapsiblePanelFiller extends JComponent implements FormComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(CollapsiblePanelFiller.class);

	protected boolean deployed = true;

	public CollapsiblePanelFiller(final Map h) {
	}

	@Override
	public void validate() {
		super.validate();
		if (this.changeState()) {
			final LayoutManager manager = this.getParent().getLayout();
			if (manager instanceof GridBagLayout) {
				final GridBagConstraints currentConstraints = ((GridBagLayout) manager).getConstraints(this);
				if (this.deployed) {
					currentConstraints.weightx = 1.0;
					currentConstraints.weighty = 1.0;
				} else {
					currentConstraints.weightx = 0.0;
					currentConstraints.weighty = 0.0;
				}
				((GridBagLayout) manager).setConstraints(this, currentConstraints);
			}
			this.getParent().validate();
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		return new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0);
	}

	protected boolean isExpanded(final CollapsiblePanel cPanel) {
		try {
			final LayoutManager manager = this.getParent().getLayout();
			if (manager instanceof GridBagLayout) {
				final GridBagConstraints panelConstraints = ((GridBagLayout) manager).getConstraints(cPanel);
				if (panelConstraints.weighty > 0) {
					return true;
				}
				return false;
			}
		} catch (final Exception e) {
			CollapsiblePanelFiller.logger.error(null, e);
		}
		return true;
	}

	protected boolean changeState() {
		final Container container = this.getParent();
		final int count = container.getComponentCount();

		boolean allCollapsed = true;

		for (int i = 0; i < count; i++) {
			if (container.getComponent(i) instanceof CollapsiblePanel) {
				final CollapsiblePanel cPanel = (CollapsiblePanel) container.getComponent(i);
				if (cPanel.isDeploy() && this.isExpanded(cPanel)) {
					allCollapsed = false;
					if (this.deployed) {
						this.deployed = false;
						return true;
					}
				}
			}
		}

		if (allCollapsed && !this.deployed) {
			this.deployed = true;
			return true;
		}

		return false;
	}

	@Override
	public void init(final Map parameters) throws Exception {
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
