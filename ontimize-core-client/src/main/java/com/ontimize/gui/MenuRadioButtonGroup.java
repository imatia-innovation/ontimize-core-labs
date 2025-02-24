package com.ontimize.gui;

import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;

public class MenuRadioButtonGroup extends Menu {

	protected ButtonGroup buttonGroup = new ButtonGroup();

	public MenuRadioButtonGroup(final Map parameters) {
		super(parameters);
	}

	@Override
	public JMenuItem add(final JMenuItem itemMenu) {
		if (itemMenu instanceof RadioMenuItem) {
			this.buttonGroup.add(itemMenu);
		}
		return super.add(itemMenu);
	}

}
