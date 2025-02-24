package com.ontimize.util.templates;

import java.util.Map;

import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;

/*
 * Parameters to configure the template 'fielddata' = List with the fields to get the data separated
 * with ;. 'imagedata' = Attributes of the images to use in the templates separated by ;.
 * 'tabledata' = Table data used to create the template separated by ;. 'templatepath' = Template
 * path
 */
public class OpenTemplateButton extends Button {

	protected OpenTemplateAction action;

	protected Map parameters;

	public OpenTemplateButton(final Map parameters) {
		super(parameters);
	}

	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		this.parameters = parameters;
	}

	@Override
	public void setParentForm(final Form f) {
		super.setParentForm(f);
		this.addActionListener(new OpenTemplateAction(f, this.parameters));
	}

}
