package com.ontimize.util.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Map;

public class AbsoluteLayout implements LayoutManager2 {

	/**
	 * This Map maintains the association between a component and its gridbag constraints. The
	 * Keys in <code>comptable</code> are the components and the values are the instances of
	 * <code>GridBagConstraints</code>.
	 *
	 */
	protected Map comptable;

	protected AbsoluteConstraints defaultConstraints;

	public AbsoluteLayout() {
		this.defaultConstraints = new AbsoluteConstraints();
		this.comptable = new Hashtable();
	}

	@Override
	public void addLayoutComponent(final Component comp, final Object constraints) {
		if (constraints instanceof AbsoluteConstraints) {
			this.comptable.put(comp, ((AbsoluteConstraints) constraints).clone());
		} else if (constraints != null) {
			throw new IllegalArgumentException("cannot add to layout: constraints must be a AbsoluteConstraints");
		}
	}

	@Override
	public float getLayoutAlignmentX(final Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(final Container target) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(final Container target) {
	}

	@Override
	public Dimension maximumLayoutSize(final Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public void addLayoutComponent(final String name, final Component comp) {
	}

	@Override
	public void layoutContainer(final Container parent) {
		final Component components[] = parent.getComponents();
		int compindex;
		Component comp;
		AbsoluteConstraints constraints;
		final Rectangle r = new Rectangle();

		for (compindex = 0; compindex < components.length; compindex++) {
			comp = components[compindex];
			constraints = this.lookupConstraints(comp);

			r.x = constraints.x;
			r.y = constraints.y;
			r.width = constraints.width;
			r.height = constraints.height;
			comp.setBounds(r);
		}
	}

	protected AbsoluteConstraints lookupConstraints(final Component comp) {
		AbsoluteConstraints constraints = (AbsoluteConstraints) this.comptable.get(comp);
		if (constraints == null) {
			final Dimension preferredSize = comp.getPreferredSize();
			this.defaultConstraints.height = preferredSize.height;
			this.defaultConstraints.width = preferredSize.width;

			this.comptable.put(comp, this.defaultConstraints);

			constraints = (AbsoluteConstraints) this.comptable.get(comp);
		}
		return constraints;
	}

	@Override
	public Dimension minimumLayoutSize(final Container parent) {
		return this.preferredLayoutSize(parent);
	}

	@Override
	public Dimension preferredLayoutSize(final Container parent) {
		final Component components[] = parent.getComponents();
		int xMaxArray = 0;
		int yMaxArray = 0;
		int compindex;
		Component comp;
		AbsoluteConstraints constraints;

		for (compindex = 0; compindex < components.length; compindex++) {
			comp = components[compindex];
			constraints = this.lookupConstraints(comp);
			xMaxArray = Math.max(constraints.x + constraints.width, xMaxArray);
			yMaxArray = Math.max(constraints.y + constraints.height, yMaxArray);
		}
		return new Dimension(xMaxArray, yMaxArray);
	}

	@Override
	public void removeLayoutComponent(final Component comp) {
		this.comptable.remove(comp);
	}

}
