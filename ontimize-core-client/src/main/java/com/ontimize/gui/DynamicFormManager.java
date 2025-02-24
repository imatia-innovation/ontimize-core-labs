package com.ontimize.gui;

import java.util.Map;

import com.ontimize.gui.manager.IFormManager;

public interface DynamicFormManager {

	public String getForm(Map data);

	public String getFormInteractionManagerClass(String formName);

	public void setBaseName(String baseName);

	public void setFormManager(IFormManager formManager);

}
