package com.ontimize.gui.container;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.JComponent;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

/**
 * This class implements a card panel to put many graphical components above layout. For example,
 * two forms or a form and a tree or other combinations.
 * <p>
 *
 * @author Imatia Innovation
 */
public class CardPanel extends Column implements FormComponent {

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2074EN
	 */
	public static final String CARDPANEL_NAME = "CardPanel";

	/**
	 * The class constructor. Calls to <code>super()</code>
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public CardPanel(final Map parameters) {
		super(parameters);
	}

	/**
	 * Calls to <code>super()</code> to initialize parameters and set layout.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		this.setLayout(new CardLayout());
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			if (this.horizontalExpand) {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			} else {
				return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
						GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
			}
		} else {
			return null;
		}
	}

	@Override
	public void add(final Component comp, final Object constraints) {
		if (comp instanceof IdentifiedElement) {
			if (((IdentifiedElement) comp).getAttribute() == null) {
				throw new IllegalArgumentException(this.getClass().toString() + " -> Element attribute is null");
			}

			// TODO Check it's necessary. In the FormEditor, a CardPanel with
			// two IntegerDataField
			if (comp instanceof JComponent) {
				if (!ApplicationManager.useOntimizePlaf) {
					((JComponent) comp).setOpaque(true);
				}
			}
			super.add(comp, ((IdentifiedElement) comp).getAttribute().toString());
			this.show(((IdentifiedElement) comp).getAttribute().toString());
		} else {
			super.add(comp, constraints);
		}

	}

	/**
	 * Shows the component.
	 * <p>
	 *
	 * @see CardLayout#show(Container, String)
	 * @param at the reference to component.
	 */
	public void show(final String at) {
		((CardLayout) this.getLayout()).show(this, at);
	}

	@Override
	public String getName() {
		return CardPanel.CARDPANEL_NAME;
	}

}
