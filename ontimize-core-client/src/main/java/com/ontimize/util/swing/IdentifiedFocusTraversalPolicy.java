package com.ontimize.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.LayoutFocusTraversalPolicy;

import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

public class IdentifiedFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

	public List queryPolicy;

	public List insertPolicy;

	public List updatePolicy;

	public List defaultPolicy;

	public Form form;

	public IdentifiedFocusTraversalPolicy() {
		// super();
		this.setComparator(new PriorComparator());
	}

	public IdentifiedFocusTraversalPolicy(final Form form) {
		this();
		this.form = form;
	}

	public Form getForm() {
		return this.form;
	}

	public void setForm(final Form form) {
		this.form = form;

		if ((this.getComparator() != null) && (this.getComparator() instanceof PriorComparator)) {
			((PriorComparator) this.getComparator()).setForm(this.form);
			((PriorComparator) this.getComparator()).setPolicy(this);
		}
	}

	public void parseFocusMode(final String customfocus) {
		final StringTokenizer sT = new StringTokenizer(customfocus, "-");
		while (sT.hasMoreTokens()) {
			final String token = sT.nextToken();
			if (token.indexOf("{") >= 0) {
				final String mode = token.substring(0, token.indexOf("{"));

				boolean validDefinedMode = false;
				if ("QUERY".equals(mode) || "INSERT".equals(mode) || "UPDATE".equals(mode)) {
					validDefinedMode = true;
				}
				if (validDefinedMode) {
					final String str = token.substring(token.indexOf("{") + 1, token.indexOf("}"));
					final List comp = this.getPolicyModeComponents(str);
					if ("QUERY".equals(mode)) {
						this.queryPolicy = new Vector(comp.size());
						this.queryPolicy.addAll(comp);
					}
					if ("INSERT".equals(mode)) {
						this.insertPolicy = new Vector(comp.size());
						this.insertPolicy.addAll(comp);
					}
					if ("UPDATE".equals(mode)) {
						this.updatePolicy = new Vector(comp.size());
						this.updatePolicy.addAll(comp);
					}
				}
			} else {
				final List comp = this.getPolicyModeComponents(token);
				if (this.queryPolicy == null) {
					this.queryPolicy = new Vector(comp.size());
					this.queryPolicy.addAll(comp);
				}
				if (this.insertPolicy == null) {
					this.insertPolicy = new Vector(comp.size());
					this.insertPolicy.addAll(comp);
				}
				if (this.updatePolicy == null) {
					this.updatePolicy = new Vector(comp.size());
					this.updatePolicy.addAll(comp);
				}
				if (this.defaultPolicy == null) {
					this.defaultPolicy = new Vector(comp.size());
					this.defaultPolicy.addAll(comp);
				}
			}
		}
	}

	@Override
	public Component getFirstComponent(final Container focusCycleRoot) {

		if (focusCycleRoot instanceof Form) {
			final Form form = (Form) focusCycleRoot;
			if ((form.getDetailComponent() != null) || (form.getSubForm() != null)) {
				final int mode = form.getInteractionManager().getCurrentMode();
				Component c = null;
				switch (mode) {
				case InteractionManager.INSERT:
					if ((this.insertPolicy != null) && (this.insertPolicy.size() > 0)) {
						c = (Component) form.getDataFieldReference((String) this.insertPolicy.get(0));
					}
					break;
				case InteractionManager.QUERY:
					if ((this.queryPolicy != null) && (this.queryPolicy.size() > 0)) {
						c = (Component) form.getDataFieldReference((String) this.queryPolicy.get(0));
					}
					break;
				case InteractionManager.UPDATE:
					if ((this.updatePolicy != null) && (this.updatePolicy.size() > 0)) {
						c = (Component) form.getDataFieldReference((String) this.updatePolicy.get(0));
					}
					break;

				default:
					break;
				}

				if (c != null) {
					return c;
				}

			}
		}

		return super.getFirstComponent(focusCycleRoot);
	}

	protected List getPolicyModeComponents(final String strComponents) {

		if (strComponents != null) {
			final List comp = new Vector();
			final StringTokenizer sT = new StringTokenizer(strComponents, ";");
			while (sT.hasMoreTokens()) {
				final String token = sT.nextToken();
				if (token != null) {
					comp.add(token);
				}
			}
			return comp;
		}
		return null;
	}

	static class PriorComparator implements Comparator, java.io.Serializable {

		private static final int ROW_TOLERANCE = 10;

		public boolean DEBUG = false;

		private Form form = null;

		private int mode = -1;

		private List orderVector = null;

		private IdentifiedFocusTraversalPolicy policy = null;

		public PriorComparator() {

		}

		public PriorComparator(final Form form) {
			super();
			this.form = form;
		}

		public Form getForm() {
			return this.form;
		}

		public void setForm(final Form form) {
			this.form = form;
		}

		public IdentifiedFocusTraversalPolicy getPolicy() {
			return this.policy;
		}

		public void setPolicy(final IdentifiedFocusTraversalPolicy policy) {
			this.policy = policy;
		}

		@Override
		public int compare(final Object o1, final Object o2) {

			final int currentMode = this.form.getInteractionManager().getCurrentMode();
			if ((this.mode == -1) || (this.mode != currentMode)) {
				this.mode = currentMode;
				if (this.orderVector == null) {
					this.orderVector = new Vector();
				}
				this.orderVector.clear();
				switch (this.mode) {
				case InteractionManager.INSERT:
					this.orderVector.addAll(this.policy.insertPolicy);
					break;
				case InteractionManager.QUERY:
					this.orderVector.addAll(this.policy.queryPolicy);
					break;
				case InteractionManager.UPDATE:
					this.orderVector.addAll(this.policy.updatePolicy);
					break;
				default:
					if (this.policy.defaultPolicy != null) {
						this.orderVector.addAll(this.policy.defaultPolicy);
					}
					break;
				}
			}

			final Component a = (Component) o1;
			final Component b = (Component) o2;

			final Container aFormComponent = PriorComparator.getFormComponentAncestorOfClass(FormComponent.class, a);
			Object aAttr = null;
			if (aFormComponent instanceof IdentifiedElement) {
				aAttr = ((IdentifiedElement) aFormComponent).getAttribute();
			}
			final Container bFormComponent = PriorComparator.getFormComponentAncestorOfClass(FormComponent.class, b);
			Object bAttr = null;
			if (bFormComponent instanceof IdentifiedElement) {
				bAttr = ((IdentifiedElement) bFormComponent).getAttribute();
			}

			/*
			 * Compare Function compare(a,b) if a < b => return -1 if a = b => return 0 if a > b => return 1
			 */

			if ((aAttr == null) && (bAttr == null)) {
				// The components are not
				// IdentifiedElements.
				// They are
				// ordered by position.
				return PriorComparator.comparePosition(a, b);
			} else if ((aAttr != null) && (bAttr == null)) {
				// Just component
				// "a" is
				// IdentifiedElement.
				// The
				// element "a"
				// have to be
				// placed
				// before.
				return -1;
			} else if ((aAttr == null) && (bAttr != null)) {
				// Just component
				// "b" is
				// IdentifiedElement.
				// The
				// element "b"
				// have to be
				// placed
				// before.
				return 1;
			} else { // The components are IdentifiedElements.
				int indA = -1;
				if (this.orderVector.contains(aAttr.toString())) {
					indA = this.orderVector.indexOf(aAttr.toString());
				}
				int indB = -1;
				if (this.orderVector.contains(bAttr.toString())) {
					indB = this.orderVector.indexOf(bAttr.toString());
				}

				if ((indA != -1) && (indB != -1)) { // If both components are
					// included
					// into orderVector...
					if (indA < indB) {
						return -1;
					} else if (indA > indB) {
						return 1;
					} else {
						// The components belong to the same FormComponent.
						// The Focus is given by position to the first left
						// element.
						// Component aOntComp = null;
						// if (aAttr != null) {
						// aOntComp = (Component)
						// this.form.getElementReference(aAttr.toString());
						// }
						// Component bOntComp = null;
						// if (bAttr != null) {
						// bOntComp = (Component)
						// this.form.getElementReference(bAttr.toString());
						// }
						final int result = PriorComparator.comparePosition(a, b);

						return result;
					}
				} else if ((indA != -1) && (indB == -1)) { // Just the element
					// "a"
					// belongs to the
					// orderVector
					return -1;
				} else if ((indA == -1) && (indB != -1)) { // Just the element
					// "b"
					// belongs to the
					// orderVector
					return 1;
				} else {
					return PriorComparator.comparePosition(a, b);
				}
			}

		}

		public static int getX(final Container commonParent, Component c) {
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

		public static int getY(final Container commonParent, Component c) {
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

		public static Container getFormComponentAncestorOfClass(final Class c, final Component comp) {
			if ((comp == null) || (c == null)) {
				return null;
			}

			Container parent = comp.getParent();
			while ((parent != null) && !c.isInstance(parent)) {
				parent = parent.getParent();
			}
			return parent;
		}

		// void setComponentOrientation(ComponentOrientation orientation) {
		// horizontal = orientation.isHorizontal();
		// leftToRight = orientation.isLeftToRight();
		// }

		public static int comparePosition(final Object o1, final Object o2) {
			final boolean horizontal = true;

			final boolean leftToRight = true;

			final Component a = (Component) o1;
			final Component b = (Component) o2;

			if (a == b) {
				return 0;
			}

			Container commonParent = null;
			final ArrayList aParents = new ArrayList();
			Container parent = a.getParent();
			while (parent != null) {
				aParents.add(parent);
				parent = parent.getParent();
			}
			final ArrayList bParents = new ArrayList();
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

			final int ax = PriorComparator.getX(commonParent, a);
			final int ay = PriorComparator.getY(commonParent, a);
			final int bx = PriorComparator.getX(commonParent, b);
			final int by = PriorComparator.getY(commonParent, b);

			if (horizontal) {
				if (leftToRight) {

					// LT - Western Europe (optional for Japanese, Chinese,
					// Korean)
					return compareHorizontalLeftToRight(ax, ay, bx, by);
				} else { // !leftToRight

					// RT - Middle East (Arabic, Hebrew)
					return compareHorizontalRightToLeft(ax, ay, bx, by);
				}
			} else { // !horizontal
				if (leftToRight) {

					// TL - Mongolian
					// TODO This method maybe has to be different
					return compareHorizontalLeftToRight(ay, ax, by, bx);
				} else { // !leftToRight

					// TR - Japanese, Chinese, Korean
					return comparteVerticalRightToLeft(ax, ay, bx, by);
				}
			}
		}

		protected static int comparteVerticalRightToLeft(final int ax, final int ay, final int bx, final int by) {
			if (Math.abs(ax - bx) < PriorComparator.ROW_TOLERANCE) {
				if (ay == by) {
					if (ax == bx) {
						return 0;
					}
					return ax < bx ? -1 : 1;
				} else {
					return ay < by ? -1 : 1;
				}
			} else {
				return ax > bx ? -1 : 1;
			}
		}

		protected static int compareHorizontalRightToLeft(final int ax, final int ay, final int bx, final int by) {
			if (Math.abs(ay - by) < PriorComparator.ROW_TOLERANCE) {
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

		protected static int compareHorizontalLeftToRight(final int ax, final int ay, final int bx, final int by) {
			if (Math.abs(ay - by) < PriorComparator.ROW_TOLERANCE) {
				if (ax == bx) {
					if (ay == by) {
						return 0;
					}
					return ay < by ? -1 : 1;

				} else {
					return ax < bx ? -1 : 1;
				}
			} else {
				return ay < by ? -1 : 1;
			}
		}

	}

}
