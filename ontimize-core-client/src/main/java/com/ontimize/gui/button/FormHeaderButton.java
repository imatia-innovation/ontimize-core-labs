package com.ontimize.gui.button;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ontimize.gui.images.ImageManager;

/**
 * An simple implementation of a form header button only with a fixed <code>margin</code> parameter.
 * <p>
 *
 * @author Imatia Innovation
 */
public class FormHeaderButton extends Button {

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String FORMHEADERBUTTON = "FormHeaderButton";

	public static Boolean defaultPaintFocus;

	public static Boolean defaultContentAreaFilled;

	public static Boolean defaultCapable;

	public static boolean createRolloverIcon = false;

	/**
	 * The class constructor. Calls to <code>super</code> with parameters.
	 * <p>
	 * @param p0 the <code>Hashtable</code> with parameters.
	 */
	public FormHeaderButton(final Map p0) {
		super(p0);
	}

	/**
	 * Inits parameters.
	 * <p>
	 * @param h the <code>Hashtable</code> with parameters. Adds a fixed <code>margin</code> parameter.
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>margin</td>
	 *        <td></td>
	 *        <td>3;3;3;3</td>
	 *        <td>no</td>
	 *        <td>The margin.</td>
	 *        </tr>
	 *
	 *        </Table>
	 */
	@Override
	public void init(final Map h) {
		if (!h.containsKey("margin")) {
			h.put("margin", "3;3;3;3");
		}
		super.init(h);
	}

	@Override
	public String getName() {
		return FormHeaderButton.FORMHEADERBUTTON;
	}

	@Override
	public void setIcon(final Icon defaultIcon) {
		super.setIcon(defaultIcon);
		if (FormHeaderButton.createRolloverIcon && (defaultIcon instanceof ImageIcon)) {
			final ImageIcon rollOverIcon = ImageManager.transparent((ImageIcon) defaultIcon, 0.5f);
			this.setRolloverIcon(rollOverIcon);
		}
	}

	@Override
	public void setContentAreaFilled(final boolean b) {
		if (FormHeaderButton.defaultContentAreaFilled != null) {
			super.setContentAreaFilled(FormHeaderButton.defaultContentAreaFilled.booleanValue());
			return;
		}
		super.setContentAreaFilled(b);
	}

	@Override
	public boolean isDefaultCapable() {
		if (FormHeaderButton.defaultCapable != null) {
			return FormHeaderButton.defaultCapable.booleanValue();
		}
		return false;
	}

	@Override
	public void setFocusPainted(final boolean b) {
		if (FormHeaderButton.defaultPaintFocus != null) {
			super.setFocusPainted(FormHeaderButton.defaultPaintFocus.booleanValue());
			return;
		}
		super.setFocusPainted(b);
	}

}
