package com.ontimize.gui.button;

import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @version 1.0
 * @deprecated
 */
@Deprecated
public class OpenAppWindowsButton extends Button {

	protected String appName = null;

	public OpenAppWindowsButton(final Map parameters) throws Exception {
		super(parameters);
		final Object appName = parameters.get("appname");
		if (appName == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " : The 'appname' attibute is mandatory");
		}
		this.appName = appName.toString();
		super.addActionListener(new com.ontimize.gui.actions.WindowsOpenApplicationAction(this.appName));
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
