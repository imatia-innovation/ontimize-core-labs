package com.ontimize.gui.manager;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.ontimize.gui.Form;

public interface ITabbedFormManager extends IFormManager {

	public int indexOfKeys(Map keyValues);

	public int indexOfComponent(Component component);

	public void removeTab(int index);

	public void showTab(int index);

	public void setTitleAt(int index, String text);

	public Form getMainForm();

	public List<JFrame> getFrameList();

}
