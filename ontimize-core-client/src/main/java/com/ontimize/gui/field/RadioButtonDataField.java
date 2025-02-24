package com.ontimize.gui.field;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * This class implements a radio button field with {@link CheckDataField} behaviour.
 * <p>
 *
 * @author Imatia Innovation
 */
public class RadioButtonDataField extends CheckDataField {

	/**
	 * The key for 1 value.
	 */
	public static final Short ONE = new Short((short) 1);

	/**
	 * The key for 0 value.
	 */
	public static final Short ZERO = new Short((short) 0);

	/**
	 * This class creates a graphical adaptation field for Windows.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class RadioButtonUIE extends com.sun.java.swing.plaf.windows.WindowsRadioButtonUI {

		private static RadioButtonUIE ui = new RadioButtonUIE();

		public static ComponentUI createUI(final JComponent b) {
			return RadioButtonUIE.ui;
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
	 * The class constructor. Calls to <code>super()</code> with parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public RadioButtonDataField(final Map parameters) {
		super(parameters);
	}

	@Override
	public Object getValue() {
		// Needed for compatibility
		final Object oValue = super.getValue();
		if (oValue instanceof Number) {
			if (((Number) oValue).intValue() != 0) {
				return RadioButtonDataField.ONE;
			} else {
				return RadioButtonDataField.ZERO;
			}
		}
		return oValue;
	}

	@Override
	protected void createDataField() {
		this.dataField = new JRadioButton() {

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
						this.setUI(RadioButtonUIE.createUI(this));
					}
				} catch (final ClassNotFoundException e) {
				}
				this.setOpaque(op);
			}
		};
	}

}
