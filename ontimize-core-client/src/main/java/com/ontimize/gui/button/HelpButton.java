package com.ontimize.gui.button;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.help.HelpUtilities;

/**
 * Button to show the help window with the associated help identifier.
 * <p>
 *
 * @author Imatia Innovation
 */
public class HelpButton extends Button {

	/**
	 * This class implements a help listener.
	 * <p>
	 *
	 * @see HelpUtilities#showHelp(Window, String)
	 * @author Imatia Innovation
	 */
	protected static class HelpListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() instanceof HelpButton) {
				if (HelpUtilities.isHelpEnabled()) {
					final Window w = SwingUtilities.getWindowAncestor((HelpButton) e.getSource());
					final String helpId = ((HelpButton) e.getSource()).getHelpIdString();
					HelpUtilities.showHelp(w, helpId);
				} else {
					((HelpButton) e.getSource()).parentForm.message("M_AYUDA_NO_DISPONIBLE", Form.ERROR_MESSAGE);
				}
			}
		}

	}

	/**
	 * An instance of a help listener.
	 */
	protected static HelpListener helpListener = new HelpListener();

	/**
	 * Calls super(), adds an ActionListener and sets an icon.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public HelpButton(final Map parameters) {
		super(parameters);
		if (this.getIcon() == null) {
			this.setIcon(ApplicationManager.getDefaultHelpIcon());
		}
		super.addActionListener(HelpButton.helpListener);
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
