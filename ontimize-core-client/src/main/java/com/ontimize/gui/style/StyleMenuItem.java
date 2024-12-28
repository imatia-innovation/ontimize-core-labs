package com.ontimize.gui.style;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.RadioMenuItem;

public class StyleMenuItem extends RadioMenuItem {

	private static final Logger logger = LoggerFactory.getLogger(StyleMenuItem.class);

	private final String style;
	/**
	 * The class constructor. Calls to super, initializes parameters and permissions and sets margin.
	 * <p>
	 *
	 * @param parameters the Map with parameters
	 */
	public StyleMenuItem(final Map parameters) {
		super(parameters);

		style = (String) parameters.get("style");
		if (style == null) {
			StyleMenuItem.logger.debug("'style' parameter is mandatory");
			throw new RuntimeException("'style' parameter is mandatory");
		}
	}

	public String getStyle(){
		return this.style;
	}
}
