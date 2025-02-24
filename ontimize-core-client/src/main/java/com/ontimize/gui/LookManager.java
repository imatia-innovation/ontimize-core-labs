package com.ontimize.gui;

import java.awt.Color;
import java.awt.Window;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public abstract class LookManager {

	private static Map properties = new Hashtable();

	public static void setSelectionColor(final Color c) {
		LookManager.properties.put("Tree.selectionBackground", c);
		LookManager.properties.put("CheckBoxMenuItem.selectionBackground", c);
		LookManager.properties.put("Menu.selectionBackground", c);
		LookManager.properties.put("TextArea.selectionBackground", c);

		LookManager.properties.put("ComboBox.selectionBackground", c);
		LookManager.properties.put("List.selectionBackground", c);
		LookManager.properties.put("PasswordField.selectionBackground", c);
		LookManager.properties.put("TextPane.selectionBackground", c);
		LookManager.properties.put("MenuItem.selectionBackground", c);

		LookManager.properties.put("EditorPane.selectionBackground", c);

		LookManager.properties.put("TextField.selectionBackground", c);
		LookManager.properties.put("ProgressBar.selectionBackground", c);
		LookManager.properties.put("Table.selectionBackground", c);
		LookManager.properties.put("RadioButtonMenuItem.selectionBackground", c);
	}

	public static void setFocusColor(final Color c) {

		LookManager.properties.put("ToggleButton.focus", c);
		LookManager.properties.put("CheckBox.focus", c);
		LookManager.properties.put("Button.focus", c);
		LookManager.properties.put("RadioButton.focus", c);
		LookManager.properties.put("TabbedPane.focus", c);
	}

	public static void setButtonBackground(final Color c) {
		LookManager.properties.put("Button.background", c);
	}

	public static void setGeneralBackground(final Color c) {
		LookManager.properties.put("DesktopIcon.background", c);
		LookManager.properties.put("Button.background", c);
		LookManager.properties.put("EditorPane.background", c);
		LookManager.properties.put("ToggleButton.background", c);
		LookManager.properties.put("TabbedPane.background", c);
		LookManager.properties.put("PopupMenu.background", c);
		LookManager.properties.put("CheckBoxMenuItem.background", c);
		LookManager.properties.put("Viewport.background", c);
		LookManager.properties.put("MenuItem.background", c);
		LookManager.properties.put("Tree.background", c);
		LookManager.properties.put("SplitPane.background", c);
		LookManager.properties.put("Panel.background", c);
		LookManager.properties.put("RadioButtonMenuItem.background", c);
		LookManager.properties.put("MenuBar.background", c);
		LookManager.properties.put("TextPane.background", c);
		LookManager.properties.put("CheckBox.background", c);
		LookManager.properties.put("ProgressBar.backgroundHighlight", c);
		LookManager.properties.put("RadioButton.background", c);
		LookManager.properties.put("ScrollBar.background", c);
		LookManager.properties.put("TextArea.background", c);
		LookManager.properties.put("PasswordField.background", c);
		LookManager.properties.put("ProgressBar.background", c);
		LookManager.properties.put("Menu.background", c);
		LookManager.properties.put("TableHeader.background", c);
		LookManager.properties.put("TextField.background", c);
		LookManager.properties.put("OptionPane.background", c);
		LookManager.properties.put("ColorChooser.background", c);
		LookManager.properties.put("ToolBar.background", c);
		LookManager.properties.put("List.background", c);
		LookManager.properties.put("Slider.background", c);
		LookManager.properties.put("Desktop.background", c);
		LookManager.properties.put("ScrollPane.background", c);
		LookManager.properties.put("Table.background", c);
		LookManager.properties.put("Label.background", c);
		LookManager.properties.put("Separator.background", c);
		LookManager.properties.put("ToolTip.background", c);
		LookManager.properties.put("ComboBox.background", c);
	}

	public static void update(final Window w) {
		final Enumeration enumKeys = Collections.enumeration(LookManager.properties.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oValue = LookManager.properties.get(oKey);
			UIManager.put(oKey, oValue);
		}
		SwingUtilities.updateComponentTreeUI(w);
	}

	public static void reset(final Window w) {
		final UIDefaults defaults = UIManager.getDefaults();
		final Enumeration enumKeys = defaults.keys();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oValue = defaults.get(oKey);
			UIManager.put(oKey, oValue);
		}
		SwingUtilities.updateComponentTreeUI(w);
	}

}
