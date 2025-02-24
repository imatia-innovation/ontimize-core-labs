package com.ontimize.gui.style;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Menu;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.PlafPreferences;

public class StyleMenu extends Menu {

	private static final Logger logger = LoggerFactory.getLogger(StyleMenu.class);

	protected ButtonGroup buttonGroup = new ButtonGroup();

	ItemListener listener = new ItemListener() {

		@Override
		public void itemStateChanged(final ItemEvent ev) {
			// Event source
			final Object oSource = ev.getSource();
			if (oSource instanceof StyleMenuItem) {
				final StyleMenuItem styleMenuItem = (StyleMenuItem) oSource;
				final Application application = ApplicationManager.getApplication();

				if (styleMenuItem.isSelected()){
					final boolean bApply = MessageDialog.showQuestionMessage(application.getFrame(), "apply_style_configuration",
							application.getResourceBundle());
					if (bApply){
						final String style = styleMenuItem.getStyle();
						final PlafPreferences instance = PlafPreferences.getInstance();
						if (instance!=null){
							instance.setStylePreference(style);
						}
					}
				}
			}
		}
	};

	public StyleMenu(final Map parameters) {
		super(parameters);
	}

	@Override
	public JMenuItem add(final JMenuItem menuItem) {

		if (menuItem instanceof StyleMenuItem) {
			final StyleMenuItem styleMenuItem = (StyleMenuItem)menuItem;
			this.buttonGroup.add(menuItem);
			final String selectedStyle = System.getProperty("com.ontimize.gui.lafstyle");
			if (selectedStyle!=null){
				if (selectedStyle.equals(styleMenuItem.getStyle())){
					styleMenuItem.setSelected(true);
				}
			}
			styleMenuItem.addItemListener(this.listener);
		}
		return super.add(menuItem);
	}
}
