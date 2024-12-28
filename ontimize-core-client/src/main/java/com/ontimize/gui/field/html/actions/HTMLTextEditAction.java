package com.ontimize.gui.field.html.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JEditorPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.utils.ActionPerformedListener;
import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.i18n.Internationalization;

/**
 * @author Imatia S.L.
 *
 */
public abstract class HTMLTextEditAction extends DefaultAction implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(HTMLTextEditAction.class);

	public static final String EDITOR = "editor";

	public static final int DISABLED = -1;

	public static final int WYSIWYG = 0;

	static final I18n i18n = I18n.getInstance();

	protected List actionListeners = new Vector(2);

	protected ResourceBundle resourceBundle;

	public HTMLTextEditAction(final String name) {
		super(name);
		this.updateEnabledState();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void execute(final ActionEvent e) throws Exception {
		this.firePreviousActionPerformed(e);
		this.editPerformed(e, this.getCurrentEditor());
		this.firePostActionPerformed(e);
	}

	protected JEditorPane getCurrentEditor() {
		try {
			final JEditorPane ep = (JEditorPane) this.getContextValue(HTMLTextEditAction.EDITOR);
			return ep;
		} catch (final ClassCastException cce) {
			HTMLTextEditAction.logger.error(null, cce);
		}
		return null;
	}

	@Override
	protected void actionPerformedCatch(final Throwable t) {
		HTMLTextEditAction.logger.debug(null, t);
	}

	@Override
	protected void contextChanged() {
		this.updateContextState(this.getCurrentEditor());
	}

	protected void updateContextState(final JEditorPane editor) {

	}

	protected abstract void editPerformed(ActionEvent e, JEditorPane editor);

	public void addActionPerformedListener(final ActionPerformedListener listener) {
		this.actionListeners.add(listener);
	}

	public void firePreviousActionPerformed(final ActionEvent e) {
		for (int i = 0; i < this.actionListeners.size(); i++) {
			((ActionPerformedListener) this.actionListeners.get(i)).previousActionPerformed(e);
		}
	}

	public void firePostActionPerformed(final ActionEvent e) {
		for (int i = 0; i < this.actionListeners.size(); i++) {
			((ActionPerformedListener) this.actionListeners.get(i)).postActionPerformed(e);
		}
	}

	public void removeActionPerformedListener(final ActionPerformedListener listener) {
		this.actionListeners.remove(listener);
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

}
