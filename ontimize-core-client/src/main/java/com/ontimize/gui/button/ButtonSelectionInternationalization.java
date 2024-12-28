package com.ontimize.gui.button;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.swing.ButtonSelection;

public class ButtonSelectionInternationalization extends ButtonSelection implements Internationalization, AccessForm {

	protected String originalButtonText;

	protected String originalButtonTip;

	protected ResourceBundle resource;

	protected Locale locale;

	protected Form parentForm;

	public ButtonSelectionInternationalization(final Icon attachmentIcon, final boolean b) {
		super(attachmentIcon, b);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		if (this.originalButtonText != null) {
			v.add(this.originalButtonText);
		}
		if (this.originalButtonTip != null) {
			v.add(this.originalButtonTip);
		}
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		if ((this.menuList != null) && (this.menuList instanceof Internationalization)) {
			((Internationalization) this.menuList).setComponentLocale(this.locale);
		}
	}

	@Override
	public void setToolTipText(final String text) {
		this.originalButtonTip = text;
		if (this.resource != null) {
			this.button.setToolTipText(ApplicationManager.getTranslation(text, this.resource));
		}
	}

	@Override
	public void setText(final String text) {
		this.originalButtonText = text;
		if (this.resource != null) {
			this.button.setText(ApplicationManager.getTranslation(text, this.resource));
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resource = resourceBundle;
		if (this.originalButtonText != null) {
			this.button.setText(ApplicationManager.getTranslation(this.originalButtonText, this.resource));
		}
		if (this.originalButtonTip != null) {
			this.button.setToolTipText(ApplicationManager.getTranslation(this.originalButtonTip, this.resource));
		}

		if ((this.menuList != null) && (this.menuList instanceof Internationalization)) {
			((Internationalization) this.menuList).setResourceBundle(this.resource);
		}
	}

	@Override
	public void setMenuList(final JList menuList) {
		super.setMenuList(menuList);
		if ((this.menuList != null) && (this.menuList instanceof Internationalization)) {
			((Internationalization) this.menuList).setComponentLocale(this.locale);
			((Internationalization) this.menuList).setResourceBundle(this.resource);
		}
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

}
