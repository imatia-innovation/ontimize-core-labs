package com.ontimize.gui.tree;

import java.util.Map;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.ReferenceComboDataField;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class ReferenceTreeComponent {

	protected ReferenceComboDataField comboReferenceDataField = null;

	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.comboReferenceDataField.setReferenceLocator(locator);
		this.comboReferenceDataField.initCache();
	}

	public void setParentForm(final Form form) {
		this.comboReferenceDataField.setParentForm(form);
	}

	public String getAttribute() {
		return this.comboReferenceDataField.getAttribute().toString();
	}

	public String getDescriptionForCode(final Object code) {
		return this.comboReferenceDataField.getCodeDescription(code);
	}

	public ReferenceTreeComponent(final Map parameters) {
		parameters.remove("cachetime");
		this.comboReferenceDataField = new ReferenceComboDataField(parameters);
		this.comboReferenceDataField.setUseCacheManager(false);
	}

}
