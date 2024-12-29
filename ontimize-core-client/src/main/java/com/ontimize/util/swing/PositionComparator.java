package com.ontimize.util.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionComparator implements Comparator, java.io.Serializable {

	private static final Logger logger = LoggerFactory.getLogger(PositionComparator.class);

	private static final int ROW_TOLERANCE = 15;

	private boolean horizontal = true;

	private boolean leftToRight = true;

	void setComponentOrientation(final ComponentOrientation orientation) {
		this.horizontal = orientation.isHorizontal();
		this.leftToRight = orientation.isLeftToRight();
	}

	@Override
	public int compare(final Object o1, final Object o2) {
		final Component a = (Component) o1;
		final Component b = (Component) o2;

		if (a == b) {
			return 0;
		}

		Container commonParent = null;
		final List aParents = new ArrayList();
		Container parent = a.getParent();
		while (parent != null) {
			aParents.add(parent);
			parent = parent.getParent();
		}
		final List bParents = new ArrayList();
		parent = b.getParent();
		while (parent != null) {
			bParents.add(parent);
			parent = parent.getParent();
		}
		for (int i = 0; i < aParents.size(); i++) {
			for (int j = 0; j < bParents.size(); j++) {
				if ((aParents.get(i) == bParents.get(j)) && ((Container) aParents.get(i)).isFocusCycleRoot()) {
					commonParent = (Container) aParents.get(i);
					break;
				}
			}
			if (commonParent != null) {
				break;
			}
		}

		final int ax = this.getX(commonParent, a), ay = this.getY(commonParent, a), bx = this.getX(commonParent, b),
				by = this.getY(commonParent, b);
		if ((a instanceof com.ontimize.gui.field.IdentifiedElement)
				&& (b instanceof com.ontimize.gui.field.IdentifiedElement)) {
			PositionComparator.logger.debug("a: {}, b: {}",
					((com.ontimize.gui.field.IdentifiedElement) a).getAttribute(),
					((com.ontimize.gui.field.IdentifiedElement) b).getAttribute());
		}

		if (this.horizontal) {
			if (this.leftToRight) {

				// LT - Western Europe (optional for Japanese, Chinese,
						// Korean)
				return compareHorizontalLeftToRight(a, b, commonParent, ax, ay, bx, by);
			} else { // !leftToRight

				// RT - Middle East (Arabic, Hebrew)
				return compareHorizontalRightToLeft(ax, ay, bx, by);
			}
		} else { // !horizontal
			if (this.leftToRight) {

				// TL - Mongolian
				return compareVerticalLeftToRight(a, b, commonParent, ax, ay, bx, by);
			} else { // !leftToRight

				// TR - Japanese, Chinese, Korean

				return compareVerticalRightToLeft(a, b, commonParent, ax, ay, bx, by);
			}
		}
	}

	protected int compareVerticalRightToLeft(final Component a, final Component b, final Container commonParent, final int ax, final int ay, final int bx,
			final int by) {
		if (Math.abs(ax - bx) < PositionComparator.ROW_TOLERANCE) {
			if (ay == by) {
				if (ax == bx) {
					if (a.equals(b)) {
						return 0;
					} else {
						final int az = this.getZ(commonParent, a);
						final int bz = this.getZ(commonParent, b);
						if (az == bz) {
							return 0;
						}
						return az < bz ? -1 : 1;
					}
				}
				return ax < bx ? -1 : 1;
			} else {
				return ay < by ? -1 : 1;
			}
		} else {
			return ax > bx ? -1 : 1;
		}
	}

	protected int compareHorizontalRightToLeft(final int ax, final int ay, final int bx, final int by) {
		if (Math.abs(ay - by) < PositionComparator.ROW_TOLERANCE) {
			if (ax == bx) {
				if (ay == by) {
					return 0;
				}
				return ay > by ? -1 : 1;
			} else {
				return ax > bx ? -1 : 1;
			}
		} else {
			return ay < by ? -1 : 1;
		}
	}

	protected int compareVerticalLeftToRight(final Component a, final Component b, final Container commonParent, final int ax, final int ay, final int bx,
			final int by) {
		if (Math.abs(ax - bx) < PositionComparator.ROW_TOLERANCE) {
			if (ay == by) {
				if (ax == bx) {
					if (a.equals(b)) {
						return 0;
					} else {
						final int az = this.getZ(commonParent, a);
						final int bz = this.getZ(commonParent, b);
						if (az == bz) {
							return 0;
						}
						return az < bz ? -1 : 1;
					}
				}
				return ax < bx ? -1 : 1;
			} else {
				return ay < by ? -1 : 1;
			}
		} else {
			return ax < bx ? -1 : 1;
		}
	}

	protected int compareHorizontalLeftToRight(final Component a, final Component b, final Container commonParent, final int ax, final int ay,
			final int bx, final int by) {
		PositionComparator.logger.debug("ax : {} ay : {} bx : {} by : {}", ax, ay, bx, by);
		if (Math.abs(ay - by) < PositionComparator.ROW_TOLERANCE) {
			if (ax == bx) {
				if (ay == by) {
					PositionFocusTraversalPolicy.logger.trace("ay==by -> 0");
					if (a.equals(b)) {
						return 0;
					} else {
						final int az = this.getZ(commonParent, a);
						final int bz = this.getZ(commonParent, b);
						if (az == bz) {
							return a.getWidth() < b.getWidth() ? -1 : 1;
						}
						return az < bz ? -1 : 1;
					}
				}
				PositionComparator.logger.trace("(ay < by) ? -1 : 1; -> {}", ay < by ? -1 : 1);
				return ay < by ? -1 : 1;
			} else {
				PositionComparator.logger.trace("(ax < bx) ? -1 : 1; -> {}", ax < bx ? -1 : 1);
				return ax < bx ? -1 : 1;
			}
		} else {
			PositionComparator.logger.trace("(ay < by) ? -1 : 1; -> {}", ay < by ? -1 : 1);
			return ay < by ? -1 : 1;
		}
	}

	public int getX(final Container commonParent, Component c) {
		if (commonParent == null) {
			return c.getX();
		} else {
			int x = c.getX();
			while ((c.getParent() != commonParent) && (c.getParent() != null)) {
				c = c.getParent();
				x = x + c.getX();
			}
			return x;
		}
	}

	public int getY(final Container commonParent, Component c) {
		if (commonParent == null) {
			return c.getX();
		} else {
			int y = c.getY();
			while ((c.getParent() != commonParent) && (c.getParent() != null)) {
				c = c.getParent();
				y = y + c.getY();
			}
			return y;
		}
	}

	public int getZ(final Container commonParent, final Component c) {
		if (commonParent == null) {
			return 0;
		} else {
			int z = 0;
			if (commonParent.equals(c)) {
				return z;
			}
			Component parent = c.getParent();
			while (parent != null) {
				if (commonParent.equals(parent)) {
					return z;
				}
				parent = parent.getParent();
				z++;
			}
			return z;
		}
	}

}
