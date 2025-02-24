package com.ontimize.gui.container;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.button.FormHeaderButton;
import com.ontimize.gui.field.AbstractFormComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

/**
 * This class implements the standard form header buttons (query, insert, delete...)
 * <p>
 *
 * @author Imatia Innovation
 */
public class FormHeader extends AbstractFormComponent {

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String FORMHEADER = "FormHeader";

	public static boolean useTextInButtons = false;

	/**
	 * The key for standard buttons.
	 */
	public static final String STANDARD_BUTTONS = "standardbuttons";

	/**
	 * The key for query icon.
	 */
	protected static final String QUERY_ICON = ImageManager.SEARCH;

	/**
	 * The key for confirm query icon.
	 */
	protected static final String CONFIRM_QUERY_ICON = ImageManager.CONFIRM_QUERY;

	public static String queryIcon = FormHeader.QUERY_ICON;

	public static String confirmQueryIcon = FormHeader.CONFIRM_QUERY_ICON;

	/**
	 * The key for insert icon.
	 */
	protected static final String INSERT_ICON = ImageManager.INSERT;

	/**
	 * The key for confirm insert icon.
	 */
	protected static final String CONFIRM_INSERT_ICON = ImageManager.CONFIRM_INSERT;

	public static String insertIcon = FormHeader.INSERT_ICON;

	public static String confirmInsertIcon = FormHeader.CONFIRM_INSERT_ICON;

	/**
	 * The key for update icon.
	 */
	protected static final String UPDATE_ICON = ImageManager.UPDATE;

	public static String updateIcon = FormHeader.UPDATE_ICON;

	/**
	 * The key for delete icon.
	 */
	protected static final String DELETE_ICON = ImageManager.DELETE_DOCUMENT;

	private static final String POSITION = "position";

	public static final String TEXT = "text";

	public static String deleteIcon = FormHeader.DELETE_ICON;

	public static String separatorIcon = ImageManager.SEPARATOR_ICON_16;

	protected MouseListener listenerHighlightButtons;

	protected boolean borderbuttons;

	protected boolean opaquebuttons;

	protected boolean highlightButtons;

	protected boolean labelTextButtons;

	/**
	 * The class constructor. Initializes parameters.
	 * <p>
	 * @param params the <code>Hashtable</code> with parameters
	 */
	public FormHeader(final Map params) {
		super();
		this.init(params);
	}

	/**
	 * Initializes parameters.
	 *
	 *
	 * the <code>Hashtable</code> with parameters
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 * <tr>
	 * <td>standardbuttons</td>
	 * <td><i>yes/no or a combination of letters:<br>
	 *
	 * <b>q</b>(query),<b>u</b>(update),<b>i</b>(insert),<b>d</b>(delete ),<b>a</b>(advanced query) <br>
	 * <br>
	 * For example: <b>"qui"</b> -> only query,update and insert buttons will be showed</td>
	 * <td>yes</td>
	 * <td>yes</td>
	 * <td>The standard buttons definition. Default value could be changed with:
	 * {@link Form#STANDARD_BUTTONS}</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>separator</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Creates a separator for this component.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>borderbuttons</td>
	 * <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover, it is
	 * also allowed a border defined in #BorderManager</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The border for buttons in Form</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>highlightbuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Sets the highlight in button property when mouse is entered. See
	 * {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires opaque='no'.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>opaquebuttons</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Data field opacity condition for Form buttons</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>text</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Add label text to the FormHeader buttons</td>
	 * </tr>
	 *
	 * </table>
	 * @param params The <code>Hashtable</code> with parameters
	 */
	@Override
	public void init(final Map params) {
		this.setName(FormHeader.FORMHEADER);
		this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

		this.borderbuttons = ParseUtils.getBoolean((String) params.get("borderbuttons"), true);
		this.opaquebuttons = ParseUtils.getBoolean((String) params.get("opaquebuttons"), true);
		this.highlightButtons = ParseUtils.getBoolean((String) params.get("highlightbuttons"), false);
		this.labelTextButtons = ParseUtils.getBoolean((String) params.get(FormHeader.TEXT),
				FormHeader.useTextInButtons);
		if (this.highlightButtons) {
			this.listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}
		// Buttons
		final JButton[] buttons = new JButton[4];
		final Object standardbuttons = params.get(FormHeader.STANDARD_BUTTONS);
		if (standardbuttons != null) {
			// Put buttons
			if (standardbuttons.equals("yes")) {
				buttons[0] = this.createQueryButton();
				buttons[1] = this.createInsertButton();
				buttons[2] = this.createUpdateButton();
				buttons[3] = this.createDeleteButton();
			} else {
				String b = standardbuttons.toString();
				if (b.length() > 4) {
					b = b.substring(0, 4);
				}
				// Check the letters
				if (b.indexOf('q') >= 0) {
					buttons[b.indexOf('q')] = this.createQueryButton();
				}
				if (b.indexOf('i') >= 0) {
					buttons[b.indexOf('i')] = this.createInsertButton();
				}
				if (b.indexOf('u') >= 0) {
					buttons[b.indexOf('u')] = this.createUpdateButton();
				}
				if (b.indexOf('d') >= 0) {
					buttons[b.indexOf('d')] = this.createDeleteButton();
				}
			}

			// add

			if (ParseUtils.getBoolean((String) params.get("separator"), false)) {
				final JLabel bt = new JLabel(ImageManager.getIcon(FormHeader.separatorIcon));
				this.add(bt);
			}

			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] != null) {
					this.add(buttons[i]);
				}
			}
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Creates the advanced query button. Adds rollover, icon and tip properties.
	 * <p>
	 * @return the button
	 */
	protected FormHeaderButton createAvancedQueryButton() {
		final Map bP = new Hashtable();
		bP.put("key", InteractionManager.ADVANCED_QUERY_KEY);
		bP.put("tip", "advanced_search");
		bP.put("rollover", "yes");
		bP.put("icon", ImageManager.ADVANCE_SEARCH);

		final FormHeaderButton button = new FormHeaderButton(bP);
		return button;
	}

	/**
	 * Creates the query button. Adds rollover, icon and tip properties.
	 * <p>
	 * @return the button
	 */
	protected FormHeaderButton createQueryButton() {
		final Map bP = new Hashtable();
		bP.put("key", InteractionManager.QUERY_KEY);
		bP.put("tip", "query");
		bP.put("rollover", "yes");
		bP.put("icon", FormHeader.queryIcon);
		bP.put("alttip", "formheader.enter_query_conditions_start_searching");
		bP.put("alticon", FormHeader.confirmQueryIcon);
		if (this.labelTextButtons) {
			bP.put("text", "query");
		}
		final FormHeaderButton button = new FormHeaderButton(bP);
		this.changeButton(button);
		return button;
	}

	/**
	 * Creates the insert button. Adds rollover, icon, tip and alttip properties.
	 * <p>
	 * @return the button
	 */
	protected FormHeaderButton createInsertButton() {
		final Map bP = new Hashtable();
		bP.put("key", InteractionManager.INSERT_KEY);
		bP.put("tip", "insert");
		bP.put("rollover", "yes");
		bP.put("icon", FormHeader.insertIcon);
		bP.put("alttip", "formheader.enter_data_click_confirm_insertion");
		bP.put("alticon", FormHeader.confirmInsertIcon);
		if (this.labelTextButtons) {
			bP.put("text", "insert");
		}
		final FormHeaderButton button = new FormHeaderButton(bP);
		this.changeButton(button);
		return button;
	}

	/**
	 * Creates the update button. Adds rollover, icon and tip properties.
	 * <p>
	 * @return the button
	 */
	protected FormHeaderButton createUpdateButton() {
		final Map bP = new Hashtable();
		bP.put("key", InteractionManager.UPDATE_KEY);
		bP.put("tip", "application.update");
		bP.put("rollover", "yes");
		bP.put("icon", FormHeader.updateIcon);
		if (this.labelTextButtons) {
			bP.put("text", "application.update");
		}
		final FormHeaderButton button = new FormHeaderButton(bP);
		this.changeButton(button);
		return button;
	}

	/**
	 * Creates the delete button. Adds rollover, icon and tip properties.
	 * <p>
	 * @return the button
	 */
	protected FormHeaderButton createDeleteButton() {
		final Map bP = new Hashtable();
		bP.put("key", InteractionManager.DELETE_KEY);
		bP.put("tip", "delete");
		bP.put("rollover", "yes");
		bP.put("icon", FormHeader.deleteIcon);
		if (this.labelTextButtons) {
			bP.put("text", "delete");
		}

		final FormHeaderButton button = new FormHeaderButton(bP);
		this.changeButton(button);
		return button;
	}

	protected void changeButton(final JButton button) {
		if (button != null) {
			button.setFocusPainted(false);
			if (!this.borderbuttons) {
				button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
			if (!this.opaquebuttons) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (this.highlightButtons) {
				button.addMouseListener(this.listenerHighlightButtons);
			}
		}
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
