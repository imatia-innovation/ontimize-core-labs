package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.ValueEvent;

/**
 * This class creates a slider field with a specified orientation and size.
 * <p>
 *
 * @author Imatia Innovation
 */
public class SliderDataField extends DataField implements DataComponent, AdvancedDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(SliderDataField.class);

	/**
	 * The default value. By default, 0.
	 */
	protected int defaultValue = 0;

	/**
	 * An instance of a listener list.
	 */
	protected List listenersList = new Vector();

	/**
	 * A reference for an inner document listener. By default, null.
	 */
	protected InnerDocumentListener innerListener = null;

	/**
	 * The scale. By default, 1.0.
	 */
	protected double scale = 1.0d;

	/**
	 * The key for including menu in search.
	 */
	public static String INCLUDE_MENU_KEY = "datafield.include_in_search";

	/**
	 * A checkbox menu item for including menu in search.
	 */
	protected JCheckBoxMenuItem includeMenu = new JCheckBoxMenuItem(SliderDataField.INCLUDE_MENU_KEY, true);

	;

	/**
	 * An instance for not including borders.
	 */
	protected Border notIncludeBorder = new CompoundBorder(new LineBorder(Color.red, 2),
			new BevelBorder(BevelBorder.LOWERED));

	/**
	 * This class implements a inner document listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerDocumentListener implements javax.swing.event.ChangeListener {

		protected boolean activateInnerListener = true;

		public void setActiveInnerListener(final boolean act) {
			this.activateInnerListener = act;
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (this.activateInnerListener && (e.getSource() instanceof JSlider)
					&& !((JSlider) e.getSource()).getValueIsAdjusting()) {
				SliderDataField.this.fireValueChanged(SliderDataField.this.getValue(), SliderDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
		}

	}

	/**
	 * The class constructor. Inits parameters and installs inner listeners.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public SliderDataField(final Map parameters) {
		this.init(parameters);
		final Object or = parameters.get("orientation");
		if (or != null) {
			if ("v".equalsIgnoreCase(or.toString())) {
				((JSlider) this.dataField).setOrientation(SwingConstants.VERTICAL);
			}
		}

		final Object attr = parameters.get("attr");
		if (attr == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " parameter 'attr' is required");
		} else {
			this.attribute = attr;
		}

		final Object label = parameters.get("label");
		if (label != null) {
			if (label.toString().equalsIgnoreCase("no")) {
				((JSlider) this.dataField).setPaintLabels(false);
				((JSlider) this.dataField).setPaintTicks(false);
			} else {
				((JSlider) this.dataField).setPaintTicks(true);
				((JSlider) this.dataField).setPaintLabels(true);
			}
		} else {
			((JSlider) this.dataField).setPaintTicks(true);
			((JSlider) this.dataField).setPaintLabels(true);
		}

		final Object max = parameters.get("max");
		if (max != null) {
			try {
				final int m = Integer.parseInt(max.toString());
				((JSlider) this.dataField).setMaximum(m);
			} catch (final Exception ex) {
				SliderDataField.logger.error("Error in parameter 'max'", ex);
			}
		}

		final Object min = parameters.get("min");
		if (min != null) {
			try {
				final int m = Integer.parseInt(min.toString());
				((JSlider) this.dataField).setMinimum(m);
			} catch (final Exception ex) {
				SliderDataField.logger.error("Error in parameter 'min'", ex);
			}
		}

		final Object scale = parameters.get("scale");
		if (scale != null) {
			try {
				this.scale = Double.parseDouble(scale.toString());
			} catch (final Exception ex) {
				SliderDataField.logger.error("Error in parameter 'scale'", ex);
			}
		}

		this.installInnerListener();

	}

	/**
	 * Installs inner listener.
	 */
	protected void installInnerListener() {
		this.innerListener = new InnerDocumentListener();
		((JSlider) this.dataField).addChangeListener(this.innerListener);
	}

	/**
	 * Returns the internal value or returns <code>scale</code> internal value when <code>scale</code>
	 * is distinct to 1.0.
	 * <p>
	 * @param innerValue the internal value
	 * @return the value
	 */
	public Number internal2ExternalValue(final int innerValue) {
		if (Double.compare(this.scale, 1.0d) == 0) {
			return new Integer(innerValue);
		} else {
			return new Double(this.scale * innerValue);
		}
	}

	/**
	 * Returns <code>d</code> divided by <code>scale</code> value.
	 * <p>
	 * @param d the value
	 * @return the converted value
	 */
	protected int externalToInnerValue(final double d) {
		return (int) (d / this.scale);
	}

	/**
	 * Creates component and initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>orientation</td>
	 *        <td><i>h/v</td>
	 *        <td>h</td>
	 *        <td>no</td>
	 *        <td>Indicates the orientation slider.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>label</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether labels and separators are used.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>max</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The maximum value for slider.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>min</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The minimum value for slider.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>scale</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The scale for internal component value.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.createDataField();
		super.init(parameters);
	}

	/**
	 * Creates the slider.
	 */
	protected void createDataField() {
		this.dataField = new JSlider() {

			@Override
			public String getToolTipText() {
				return String.valueOf(SliderDataField.this
						.internal2ExternalValue(((JSlider) SliderDataField.this.dataField).getValue()));
			}
		};
		javax.swing.ToolTipManager.sharedInstance().registerComponent(this.dataField);
		this.backgroundColor = this.dataField.getBackground();
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.INTEGER;
	}

	@Override
	public void deleteData() {
		this.setValue(new Integer(this.defaultValue));
	}

	@Override
	public void setValue(final Object value) {
		try {
			this.enabledInnerListener(false);
			if (value instanceof Number) {
				((JSlider) this.dataField).setValue(this.externalToInnerValue(((Number) value).doubleValue()));
			} else {
				this.deleteData();
			}
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
			this.valueSave = this.getValue();
		} catch (final Exception ex) {
			SliderDataField.logger.error(null, ex);
		} finally {
			this.enabledInnerListener(true);
		}

	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this.internal2ExternalValue(((JSlider) this.dataField).getValue());
		}
	}

	@Override
	public boolean isEmpty() {
		this.empty = false;
		if (this.advancedQueryMode) {
			// The item ZERO is not include.
			if (this.includeMenu.isSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return this.empty;
		}
	}

	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			this.popupMenu = new ExtendedJPopupMenu();
			this.addHelpMenuPopup(this.popupMenu);
			this.popupMenu.addSeparator();
			String sMenuText = SliderDataField.INCLUDE_MENU_KEY;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(SliderDataField.INCLUDE_MENU_KEY);
				}
			} catch (final Exception e) {
				SliderDataField.logger.trace(null, e);
			}
			this.includeMenu.setText(sMenuText);
			this.popupMenu.add(this.includeMenu);
			this.includeMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (!SliderDataField.this.includeMenu.isSelected()) {
						// Update the Checkbox
						((AbstractButton) SliderDataField.this.dataField).setEnabled(false);
					} else {
						((AbstractButton) SliderDataField.this.dataField).setEnabled(true);
					}
				}
			});

		}
	}

	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		if (this.popupMenu == null) {
			this.createPopupMenu();
		}
		if (this.popupMenu != null) {
			this.includeMenu.setVisible(this.advancedQueryMode);
			this.configurePopupMenuHelp();
			this.popupMenu.show(c, x, y);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		// Set the condition texts
		try {
			if (resources != null) {
				if (this.includeMenu != null) {
					this.includeMenu.setText(this.resources.getString(SliderDataField.INCLUDE_MENU_KEY));
				}
			} else {
				if (this.includeMenu != null) {
					this.includeMenu.setText(SliderDataField.INCLUDE_MENU_KEY);
				}
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				SliderDataField.logger.debug(null, e);
			} else {
				SliderDataField.logger.trace(null, e);
			}
		}

	}

	@Override
	public void setAdvancedQueryMode(final boolean enabled) {
		this.advancedQueryMode = enabled;
		if (!this.advancedQueryMode) {
			this.dataField.setEnabled(this.isEnabled());
		} else {
			// Default --> not include in query
			this.includeMenu.setSelected(false);
			this.dataField.setEnabled(false);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (!enabled) {
			this.setAdvancedQueryMode(false);
		}

		if (!enabled) {
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
			super.setEnabled(enabled);
		}
	}

	@Override
	public boolean isModified() {
		final boolean res = super.isModified();
		return res;
	}

	/**
	 * Enables/disables the inner listener according the condition.
	 * <p>
	 * @param enable the enabled condition
	 */
	protected void enabledInnerListener(final boolean enable) {
		this.innerListener.setActiveInnerListener(enable);
	}

}
