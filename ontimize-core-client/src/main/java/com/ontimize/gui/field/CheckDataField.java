package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.templates.ITemplateField;

/**
 * This class create a checkbox field where methods <code>getValue</code> and <code>setValue</code>
 * use <code>Integer</code> with '1' for selected value and '0' for deselected value.
 * <p>
 *
 * @author Imatia Innovation
 */
public class CheckDataField extends DataField implements AdvancedDataComponent, ITemplateField {

	private static final Logger logger = LoggerFactory.getLogger(CheckDataField.class);

	/**
	 * The activated value (1).
	 * @deprecated 5.2059EN
	 * @see selectedValue
	 */
	@Deprecated
	public static final Integer UNO = new Integer(1);

	/**
	 * The disable value (0).
	 * @deprecated 5.2059EN
	 * @see deselectedValue
	 */
	@Deprecated
	public static final Integer ZERO = new Integer(0);

	/**
	 * Key for selected icon.
	 */
	public static final String SELECT_ICON = "selecticon";

	/**
	 * Key for unselected icon.
	 */
	public static final String DESELECT_ICON = "deselecticon";

	/**
	 * Key for return string parameter.
	 */
	public static final String RETURNSTRING = "returnstring";

	/**
	 * Key for return boolean parameter.
	 */
	public static final String RETURNBOOLEAN = "returnboolean";

	/**
	 * Numeric value that indicates the value to store when check is selected.
	 *
	 * @since 5.2059EN
	 */
	public static Integer selectedValue = CheckDataField.UNO;

	/**
	 * Numeric value that indicates the value to store when check is deselected. By default 0.
	 *
	 * @since 5.2059EN
	 */
	public static Integer deselectedValue = CheckDataField.ZERO;

	/**
	 * The 'yes' condition.
	 */
	public static String YES = "S";

	/**
	 * The 'no' condition.
	 */
	public static String NO = "N";

	/**
	 * This class implements an inner listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerListener implements ItemListener {

		/**
		 * The inner listener condition. By default, true.
		 */
		protected boolean isInnerListenerEnabled = true;

		protected Object innerValue = CheckDataField.this.valueSave;

		/**
		 * Sets enable the inner listener according to the condition.
		 * <p>
		 * @param act the condition
		 */
		public void setInnerListenerEnabled(final boolean act) {
			this.isInnerListenerEnabled = act;
		}

		@Override
		public void itemStateChanged(final ItemEvent e) {
			if (this.isInnerListenerEnabled) {
				if (((AbstractButton) CheckDataField.this.dataField).isSelected()) {
					// When is field is selected, innerValue should store the
					// contrary state for throwing the correct change event
					if (CheckDataField.this.returnString) {
						this.innerValue = CheckDataField.NO;
					} else if (CheckDataField.this.returnBoolean) {
						this.innerValue = Boolean.FALSE;
					} else {
						this.innerValue = new Integer(0);
					}
				} else {
					if (CheckDataField.this.returnString) {
						this.innerValue = CheckDataField.YES;
					} else if (CheckDataField.this.returnBoolean) {
						this.innerValue = Boolean.TRUE;
					} else {
						this.innerValue = new Integer(1);
					}
				}
				CheckDataField.this.fireValueChanged(CheckDataField.this.getValue(), this.innerValue,
						ValueEvent.USER_CHANGE);
			}
		}

		public void setInnerValue(final Object innerValue) {
			this.innerValue = innerValue;
		}

	};

	/**
	 * The returned boolean. By default, false.
	 */
	protected boolean returnBoolean = false;

	/**
	 * The returned string. By default, false.
	 */
	protected boolean returnString = false;

	/**
	 * The key to include the menu key.
	 */
	public static String INCLUDE_MENU_KEY = "datafield.include_in_search";

	/**
	 * An instance of checkbox menu item.
	 */
	protected JCheckBoxMenuItem includeMenu = new JCheckBoxMenuItem(CheckDataField.INCLUDE_MENU_KEY, true);

	;

	/**
	 * An instance of a line and a bevel border definition.
	 */
	protected Border borderNotIncluded = new CompoundBorder(new LineBorder(Color.red, 2),
			new BevelBorder(BevelBorder.LOWERED));

	/**
	 * An instance for a inner listener.
	 */
	protected InnerListener innerListener = new InnerListener();

	public static ImageIcon defaultSelectIcon = null;

	public static ImageIcon defaultDeselectIcon = null;

	protected ImageIcon selectIcon = null;

	protected ImageIcon deselectIcon = null;

	/**
	 * This class provides a <CODE>Windows</CODE> checkbox.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class CheckBoxUIE extends com.sun.java.swing.plaf.windows.WindowsCheckBoxUI {

		private static CheckBoxUIE ui = new CheckBoxUIE();

		public static ComponentUI createUI(final JComponent b) {
			return CheckBoxUIE.ui;
		}

		@Override
		public synchronized void paint(final Graphics g, final JComponent c) {
			super.paint(g, c);
			final AbstractButton b = (AbstractButton) c;
			if (b.hasFocus() && b.isFocusPainted()) {
				this.paintFocus(g, null, b.getSize());
			}
		}

		@Override
		protected void paintFocus(final Graphics g, final Rectangle t, final Dimension d) {
			// super.paintFocus(g,t,d);
			g.setColor(this.getFocusColor());
			javax.swing.plaf.basic.BasicGraphicsUtils.drawDashedRect(g, 0, 0, d.width - 1, d.height - 1);

		}

	}

	/**
	 * The class constructor. Creates the field, inits parameters, sets transparent and adds listeners.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public CheckDataField(final Map parameters) {
		this.createDataField();

		this.init(parameters);
		this.dataField.setOpaque(false);

		this.addItemListener(this.innerListener);

		this.installLabelListener();

	}

	/**
	 * Installs the label listener.
	 */
	protected void installLabelListener() {
		if (this.labelComponent != null) {
			this.labelComponent.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 1) {
						if (CheckDataField.this.dataField.isEnabled()) {
							final AbstractButton b = (AbstractButton) CheckDataField.this.dataField;
							b.requestFocus();
							b.setSelected(!b.isSelected());
						}
					}
				}
			});
		}
	}

	/**
	 * Creates the checkbox.
	 */
	protected void createDataField() {
		this.dataField = new JCheckBox() {

			@Override
			public void setOpaque(final boolean opaque) {
				super.setOpaque(false);
			}

			@Override
			public void updateUI() {
				final boolean op = this.isOpaque();
				super.updateUI();
				try {
					if (Class.forName("com.sun.java.swing.plaf.windows.WindowsLookAndFeel").isInstance(UIManager.getLookAndFeel())) {
						this.setUI(CheckBoxUIE.createUI(this));
					}
				} catch (final ClassNotFoundException e) {
				}
				this.setOpaque(op);
			}
		};
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (!enabled) {
			this.setAdvancedQueryMode(false);
		}

		if (!enabled) {
			if (this.conditions != null) {
				this.conditions.setVisible(false);
			}
			this.advancedQueryMode = false;
		}
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				if (!this.advancedQueryMode) {
					this.dataField.setEnabled(enabled);
				} else {
					if (this.includeMenu.isSelected()) {
						this.dataField.setEnabled(true);
					} else {
						this.dataField.setEnabled(false);
					}
				}
				this.enabled = enabled;
				this.updateBackgroundColor();
			}
		} else {
			this.dataField.setEnabled(enabled);
			this.enabled = enabled;
		}
	}

	/**
	 * Initializes parameters and selects the bundle equals to class {@link ResourceBundle}, adding a
	 * suffix to basic file name.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td>returnboolean</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether <CODE>getValue()</CODE> returns a <CODE>boolean</CODE>. By default,
	 *        returns an <code>Integer</code>.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>returnstring</td>
	 *        <td><i>yes/no</i></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether returns the value in <code>String</code> format.(S->True,
	 *        N->False)</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>selecticon</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to icon used when check is selected.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>deselecticon</td>
	 *        <td><i></i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to icon used when check is unselected.</td>
	 *        </tr>
	 *        </TABLE>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		this.returnBoolean = ParseUtils.getBoolean((String) parameters.get(CheckDataField.RETURNBOOLEAN), false);
		this.returnString = ParseUtils.getBoolean((String) parameters.get(CheckDataField.RETURNSTRING), false);

		this.selectIcon = ParseUtils.getImageIcon((String) parameters.get(CheckDataField.SELECT_ICON),
				CheckDataField.defaultSelectIcon);
		this.deselectIcon = ParseUtils.getImageIcon((String) parameters.get(CheckDataField.DESELECT_ICON),
				CheckDataField.defaultDeselectIcon);
		if (this.dataField instanceof JCheckBox) {
			if (this.selectIcon != null) {
				((JCheckBox) this.dataField).setSelectedIcon(this.selectIcon);
			}
			if (this.deselectIcon != null) {
				((JCheckBox) this.dataField).setIcon(this.deselectIcon);
			}
		}
	}

	@Override
	public Object getValue() {
		if (((AbstractButton) this.dataField).isSelected()) {
			if (this.returnString) {
				return CheckDataField.YES;
			}
			if (this.returnBoolean) {
				return Boolean.TRUE;
			}
			return CheckDataField.selectedValue;
		} else {
			if (this.returnString) {
				return CheckDataField.NO;
			}
			if (this.returnBoolean) {
				return Boolean.FALSE;
			}
			return CheckDataField.deselectedValue;
		}
	}

	/**
	 * Enables the inner listener.
	 * <p>
	 * @param enable the condition
	 */
	protected void setInnerListenerEnabled(final boolean enable) {
		if (this.innerListener != null) {
			this.innerListener.setInnerListenerEnabled(enable);
		}
	}

	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object previousValue = this.getValue();
		if ((value != null) && (!(value instanceof NullValue))) {
			if (value instanceof Number) {
				if (((Number) value).intValue() != 0) {
					((AbstractButton) this.dataField).setSelected(true);
				} else {
					((AbstractButton) this.dataField).setSelected(false);
				}
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else {
				// If value is a Boolean Object it is admitted
				if (value instanceof Boolean) {
					if (((Boolean) value).booleanValue()) {
						((AbstractButton) this.dataField).setSelected(true);
					} else {
						((AbstractButton) this.dataField).setSelected(false);
					}
					this.valueSave = this.getValue();
					this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				} else if (value instanceof String) {
					if (value.equals(CheckDataField.YES)) {
						((AbstractButton) this.dataField).setSelected(true);
					} else {
						((AbstractButton) this.dataField).setSelected(false);
					}
					this.valueSave = this.getValue();
					this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				} else {
					this.deleteData();
				}
			}
		} else {
			this.deleteData();
		}
		this.setInnerListenerEnabled(true);
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public void deleteData() {
		this.setInnerListenerEnabled(false);
		final Object previousValue = this.getValue();
		((AbstractButton) this.dataField).setSelected(false);
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public boolean isEmpty() {
		this.empty = false;
		if (this.advancedQueryMode) {
			// Item 0 is not include in the query
			if (this.includeMenu.isSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return this.empty;
		}
	}

	/**
	 * Return a boolean to check if the field is in Query Mode and is included in the search
	 * @return <code>true</code> if the field is in query mode and the field is included in the
	 *         search,<code>false</code> otherwise.
	 */
	public boolean isIncluded() {
		if (this.getParentForm().getInteractionManager().getCurrentMode() == InteractionManager.QUERY) {
			return this.includeMenu.isSelected();
		}
		return false;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public int getSQLDataType() {
		if (this.returnBoolean) {
			return java.sql.Types.BIT;
		} else if (this.returnString) {
			return java.sql.Types.VARCHAR;
		}
		return java.sql.Types.INTEGER;
	}

	/**
	 * Adds an ItemListener to the checkbox.
	 * @param cl the item listener to be added
	 */
	public void addItemListener(final ItemListener cl) {
		((AbstractButton) this.dataField).addItemListener(cl);
	}

	/**
	 * Removes an ItemListener to the checkbox.
	 * @param cl the item listener to be removed
	 */
	public void removeItemListener(final ItemListener cl) {
		((AbstractButton) this.dataField).removeItemListener(cl);
	}

	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			// this.popupMenu = new ExtendedJPopupMenu();
			super.createPopupMenu();
			this.popupMenu.addSeparator();
			String sMenuText = CheckDataField.INCLUDE_MENU_KEY;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(CheckDataField.INCLUDE_MENU_KEY);
				}
			} catch (final Exception e) {
				CheckDataField.logger.trace(null, e);
			}

			this.includeMenu.setText(sMenuText);
			this.popupMenu.add(this.includeMenu);
			this.includeMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (!CheckDataField.this.includeMenu.isSelected()) {
						// Update the checkbox
						((AbstractButton) CheckDataField.this.dataField).setEnabled(false);
					} else {
						((AbstractButton) CheckDataField.this.dataField).setEnabled(true);
					}
				}
			});
		}
	}

	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		// if(this.isEnabled()==false) return;
		if (this.popupMenu == null) {
			this.createPopupMenu();
		}
		if (this.popupMenu != null) {
			this.configurePopupMenuHelp();
			this.includeMenu.setVisible(this.advancedQueryMode && this.isEnabled());
			this.popupMenu.show(c, x, y);
		}
	}

	/**
	 * Checks the selected condition.
	 * <p>
	 * @return the selected condition
	 */
	public boolean isSelected() {
		return ((AbstractButton) this.dataField).isSelected();
	}

	@Override
	public void setAdvancedQueryMode(final boolean enabled) {
		this.advancedQueryMode = enabled;
		if (!this.advancedQueryMode) {
			this.dataField.setEnabled(this.isEnabled());
		} else {
			// By default it is not included in the query
			this.includeMenu.setSelected(false);
			this.dataField.setEnabled(false);
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		v.add(CheckDataField.INCLUDE_MENU_KEY);
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		// Set the conditions text
		try {
			if (resources != null) {
				if (this.includeMenu != null) {
					this.includeMenu.setText(this.resources.getString(CheckDataField.INCLUDE_MENU_KEY));
				}
			} else {
				if (this.includeMenu != null) {
					this.includeMenu.setText(CheckDataField.INCLUDE_MENU_KEY);
				}
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CheckDataField.logger.debug(null, e);
			} else {
				CheckDataField.logger.trace(null, e);
			}
		}
	}

	@Override
	public String getText() {
		if (this.isSelected()) {
			return "Yes";
		} else {
			return "No";
		}
	}

	/**
	 * Gets a reference of an <code>AbstractButton</code> of this field.
	 * @return
	 */
	public AbstractButton getAbstractButton() {
		return (AbstractButton) this.dataField;
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_FIELD;
	}

	@Override
	public Object getTemplateDataValue() {
		if (this.getValue() instanceof Boolean) {
			return ApplicationManager.getTranslation(this.getValue().toString(),
					ApplicationManager.getApplicationBundle());
		} else {
			return this.getValue();
		}
	}

}
