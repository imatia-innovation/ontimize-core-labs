package com.ontimize.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.IdentifiedElement;

/**
 * Default swing focus manager implementation.
 */
public class FixedFocusManager extends FocusManager {

	private static final Logger logger = LoggerFactory.getLogger(FixedFocusManager.class);

	public static boolean DEBUG = false;

	public static boolean DEBUG2 = false;

	/**
	 * This method is called by JComponents when a key event occurs. JComponent gives key events to the
	 * focus manager first, then to key listeners, then to the keyboard UI dispatcher. This method
	 * should look at the key event and change the focused component if the key event matches the
	 * receiver's focus manager hot keys. For example the default focus manager will change the focus if
	 * the key event matches TAB or Shift + TAB. The focus manager should call consume() on
	 * <b>anEvent</b> if <code>anEvent</code> has been processed. <code>focusedComponent</code> is the
	 * component that currently has the focus. Note: FocusManager will receive KEY_PRESSED, KEY_RELEASED
	 * and KEY_TYPED key events. If one event is consumed, all other events type should be consumed.
	 */
	Stack history = new Stack();

	@Override
	public void processKeyEvent(final Component focusedComponent, final KeyEvent anEvent) {
		if ((anEvent.getKeyCode() == KeyEvent.VK_TAB) || (anEvent.getKeyChar() == '\t')) {
			/**
			 * If the focused component manages focus, let it do so if control is not pressed
			 */
			if (focusedComponent instanceof JComponent) {
				final JComponent fc = (JComponent) focusedComponent;
				if (fc.isManagingFocus()) {
					final int isctrl = anEvent.getModifiers() & ActionEvent.CTRL_MASK;
					if ((isctrl != ActionEvent.CTRL_MASK) || (anEvent.getKeyCode() == KeyEvent.VK_I)) {
						return;
					}
				}
			}

			/** If this is not a key press, consume and return **/
			if (anEvent.getID() != KeyEvent.KEY_PRESSED) {
				anEvent.consume();
				return;
			}

			if ((anEvent.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) {
				this.focusPreviousComponent(focusedComponent);
			} else {
				this.focusNextComponent(focusedComponent);
			}

			anEvent.consume();
		}
	}

	/**
	 * Cause the focus manager to set the focus on the next focusable component
	 */
	@Override
	public void focusNextComponent(final Component aComponent) {
		if (aComponent instanceof JComponent) {
			final JComponent fc = (JComponent) aComponent;
			Component nc;
			final Container root = this.getFocusRoot(fc);
			if (!this.history.empty() && (this.history.peek() != aComponent)) {
				this.history.removeAllElements();
			}
			if (root != null) {
				nc = this.getFocusableComponentAfter(fc, root, true);
				if (nc != null) {
					if (this.history.empty() || (this.history.peek() == aComponent)) {
						this.history.push(nc);
					}
					if (nc instanceof JComponent) {
						((JComponent) nc).grabFocus();
					} else {
						nc.requestFocus();
					}

					if (FixedFocusManager.DEBUG) {
						FixedFocusManager.logger.debug(nc.getClass().toString() + " get focus");
						if (!nc.isEnabled()) {
							FixedFocusManager.logger.debug("Component is disabled");
						}
						if (!nc.isVisible()) {
							FixedFocusManager.logger.debug("Component is not visible");
						}
						if (!nc.isShowing()) {
							FixedFocusManager.logger.debug("The component is hide");
							nc.isShowing();
							nc.isVisible();
						}
						final Component com = nc;
						final Container cont = com.getParent();

						if (cont instanceof IdentifiedElement) {
							FixedFocusManager.logger.debug(
									((IdentifiedElement) cont).getAttribute()
									+ " is an identified element detected as father of component focused, visible "
									+ cont.isVisible());
						}

						final java.awt.Color cAnt = com.getBackground();
						final java.awt.Color cAntCont = cont.getBackground();

						com.setBackground(java.awt.Color.red);
						cont.setBackground(java.awt.Color.red);
						final Thread t = new Thread() {

							@Override
							public void run() {
								try {
									Thread.sleep(2000);
								} catch (final Exception e) {
									FixedFocusManager.logger.trace(null, e);
								}
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										com.setBackground(cAnt);
										cont.setBackground(cAntCont);
									}
								});
							}
						};
						t.start();
					}

				}
			}
		}
	}

	/**
	 * Cause the focus manager to set the focus on the previous focusable component
	 **/
	@Override
	public void focusPreviousComponent(final Component aComponent) {

		if (aComponent instanceof JComponent) {
			final JComponent fc = (JComponent) aComponent;
			Component nc;
			final Container root = this.getFocusRoot(fc);

			if (!this.history.empty() && (this.history.peek() == aComponent)) {
				this.history.pop();
				if (!this.history.empty()) {
					nc = (Component) this.history.peek();
					if (nc instanceof JComponent) {
						((JComponent) nc).grabFocus();
					} else {
						nc.requestFocus();
					}
					return;
				}
			}

			this.history.removeAllElements();

			if (root != null) {
				nc = this.getFocusableComponentAfter(fc, root, false);
				if (nc != null) {
					if (nc instanceof JComponent) {
						((JComponent) nc).grabFocus();
					} else {
						nc.requestFocus();
					}

					if (FixedFocusManager.DEBUG) {
						FixedFocusManager.logger.debug(nc.getClass().toString() + " get focus");
						if (!nc.isEnabled()) {
							FixedFocusManager.logger.debug("Component is disabled");
						}
						if (!nc.isVisible()) {
							FixedFocusManager.logger.debug("Component is not visible");
						}
						if (!nc.isShowing()) {
							FixedFocusManager.logger.debug("Component is hide");
							nc.isShowing();
						}
						final Component com = nc;
						final java.awt.Color cAnt = com.getBackground();
						com.setBackground(java.awt.Color.red);
						final Thread t = new Thread() {

							@Override
							public void run() {
								try {
									Thread.sleep(2000);
								} catch (final Exception e) {
									FixedFocusManager.logger.trace(null, e);
								}
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										com.setBackground(cAnt);
									}
								});
							}
						};
						t.start();
					}

				}
			}
		}
	}

	Container getFocusRoot(final Component c) {
		Container p;
		for (p = c.getParent(); p != null; p = p.getParent()) {
			if (((p instanceof JComponent) && p.isFocusCycleRoot()) || (p instanceof Window)
					|| (p instanceof Dialog)) {
				return p;
			}
		}
		return null;
	}

	private Component getFocusableComponentAfter(final Component focusedComponent, final Container rootContainer,
			final boolean moveForward) {
		Component nextComponent = focusedComponent;
		final Map visitedComps = new Hashtable();
		final Object nonNull = new Object();

		if (focusedComponent != null) {
			visitedComps.put(focusedComponent, nonNull);
		}

		do {
			nextComponent = moveForward ? this.getNextComponent(nextComponent, rootContainer, true)
					: this.getPreviousComponent(nextComponent, rootContainer);
			if (nextComponent == null) {
				break;
			}
			if (visitedComps.put(nextComponent, nonNull) != null) {
				// cycle detected, get out
				nextComponent = null;
				break;
			}
		} while (!(nextComponent.isVisible() && nextComponent.isFocusTraversable() && nextComponent.isEnabled()));
		visitedComps.clear();
		return nextComponent;
	}

	private Component getNextComponent(final Component component, final Container root, final boolean canGoDown) {
		Component nsv = null;
		if (root instanceof Form) {
			if (FixedFocusManager.DEBUG2) {
				System.out.println(this.getClass().toString() + " detected form");
			}
			if (((Form) root).isCustomFocusEnabled()) {
				final java.util.List l = ((Form) root).getComponentsInOrderFocus();
				if (FixedFocusManager.DEBUG2) {
					System.out.println("Component: " + component);
				}
				if (FixedFocusManager.DEBUG2) {
					System.out.println(l);
				}
				final int i = l.indexOf(component);
				if ((i >= 0) && (i < (l.size() - 1))) {
					if (FixedFocusManager.DEBUG2) {
						System.out.println(this.getClass().toString() + " return component");
					}
					return (Component) l.get(i + 1);
				}
				if (i == (l.size() - 1)) {
					this.focusPreviousComponent(root);
				}
			}
		}

		if (canGoDown && component.isVisible()
				&& (!(component instanceof JComponent) || !(((JComponent) component).isManagingFocus()))
				&& ((component instanceof Container) && (((Container) component).getComponentCount() > 0))) {
			return this.getFirstComponent((Container) component);
		} else {
			final Container parent = component.getParent();
			nsv = this.getComponentAfter(parent, component);
			if (nsv != null) {
				return nsv;
			}
			if (parent == root) {
				return root;
			} else {
				return this.getNextComponent(parent, root, false);
			}
		}
	}

	private Component getPreviousComponent(final Component component, final Container root) {
		if (root instanceof Form) {
			if (FixedFocusManager.DEBUG2) {
				System.out.println(this.getClass().toString() + " detected form");
			}
			if (((Form) root).isCustomFocusEnabled()) {
				final java.util.List l = ((Form) root).getComponentsInOrderFocus();
				if (FixedFocusManager.DEBUG2) {
					System.out.println("Component: " + component);
				}
				if (FixedFocusManager.DEBUG2) {
					System.out.println(l);
				}
				final int i = l.indexOf(component);
				if ((i >= 1) && (i <= (l.size() - 1))) {
					if (FixedFocusManager.DEBUG2) {
						System.out.println(this.getClass().toString() + " devolviendo componente");
					}
					return (Component) l.get(i - 1);
				}
				if (i == 0) {
					this.focusPreviousComponent(root);
				}
			}
		}

		final Container parent = component.getParent();

		if (component == root) {
			return this.getDeepestLastComponent(root);
		} else {
			final Component nsv = this.getComponentBefore(parent, component);
			if (nsv != null) {
				return this.getDeepestLastComponent(nsv);
			} else {
				return parent;
			}
		}
	}

	private Component getDeepestLastComponent(final Component component) {
		if (component.isVisible()
				&& (((component instanceof JComponent) && !((JComponent) component).isManagingFocus())
						|| !(component instanceof JComponent))
				&& ((component instanceof Container) && (((Container) component).getComponentCount() > 0))) {
			return this.getDeepestLastComponent(this.getLastComponent((Container) component));
		} else {
			return component;
		}
	}

	/** Return the first component that should receive the focus **/
	public Component getFirstComponent(final Container aContainer) {
		final Component orderedChildren[] = this.getChildrenTabOrder(aContainer);
		if (orderedChildren.length > 0) {
			return orderedChildren[0];
		} else {
			return null;
		}
	}

	/** Return the last component that should receive the focus **/
	public Component getLastComponent(final Container aContainer) {
		final Component orderedChildren[] = this.getChildrenTabOrder(aContainer);
		if (orderedChildren.length > 0) {
			return orderedChildren[orderedChildren.length - 1];
		} else {
			return null;
		}
	}

	/** Return the component that should receive the focus before aComponent **/
	public Component getComponentBefore(final Container aContainer, final Component aComponent) {
		Component comp;
		if ((comp = this.getPreviousFocusable(aContainer, aComponent)) != null) {
			return comp;
		}
		return this.tabOrderPreviousComponent(aContainer, aComponent);
	}

	/** Return the component the should receive the focus after aComponent **/
	public Component getComponentAfter(final Container aContainer, final Component aComponent) {
		Component nc;
		if ((aComponent instanceof JComponent)
				&& ((nc = ((JComponent) aComponent).getNextFocusableComponent()) != null)) {
			return nc;
		}
		return this.tabOrderNextComponent(aContainer, aComponent);
	}

	/**
	 * Return true if <code>a</code> should be before <code>b</code> in the "tab" order. Override this
	 * method if you want to change the automatic "tab" order. The default implementation will order tab
	 * to give a left to right, top down order. Override this method if another order is required.
	 */
	public boolean compareTabOrder(final Component a, final Component b) {
		Rectangle bounds;
		int ay, by;
		int ax, bx;
		if (a instanceof JComponent) {
			ay = ((JComponent) a).getY();
			ax = ((JComponent) a).getX();
		} else {
			bounds = a.getBounds();
			ay = bounds.y;
			ax = bounds.x;
		}

		if (b instanceof JComponent) {
			by = ((JComponent) b).getY();
			bx = ((JComponent) b).getX();
		} else {
			bounds = b.getBounds();
			by = bounds.y;
			bx = bounds.x;
		}

		if (Math.abs(ay - by) < 10) {
			return (ax < bx);
		}
		return (ay < by);
	}

	/**
	 * Return the component after comp according to compareTabOrder. If comp is the last component
	 * according to that order, return null.
	 */

	private Component tabOrderNextComponent(final Container cont, final Component cmp) {
		final Component orderedChildren[] = this.getChildrenTabOrder(cont);
		int i;
		final int c = orderedChildren.length;

		/* since cmp is a child of cont, we know cont has at least one child */
		if (c == 1) {
			return null;
		}

		for (i = 0; i < (c - 1); i++) {
			if (orderedChildren[i] == cmp) {
				return orderedChildren[i + 1];
			}
		}
		return null;
	}

	/**
	 * Return the component before comp according to compareTabOrder. If comp is the first component
	 * according to that order, return null.
	 */

	private Component tabOrderPreviousComponent(final Container cont, final Component cmp) {
		final Component orderedChildren[] = this.getChildrenTabOrder(cont);
		int i;
		final int c = orderedChildren.length;

		/* since cmp is a child of cont, we know cont has at least one child */
		if (c == 1) {
			return null;
		}

		for (i = 1; i < c; i++) {
			if (orderedChildren[i] == cmp) {
				return orderedChildren[i - 1];
			}
		}
		return null;
	}

	/**
	 * If there is a child c of Cont such that c.getNextFocusableComponent == comp, return that
	 * component. Otherwise, return null.
	 */

	private Component getPreviousFocusable(final Container cont, final Component comp) {
		final Component children[] = cont.getComponents();
		int i;
		int c;

		for (i = 0, c = children.length; i < c; i++) {
			if ((children[i] instanceof JComponent)
					&& ((((JComponent) children[i]).getNextFocusableComponent()) == comp)) {
				return children[i];
			}
		}
		return null;
	}

	Component[] getChildrenTabOrder(final Container co) {
		final Component children[] = co.getComponents();
		Component tmp;
		int i, j, c;

		/** Get the tab order from the geometry **/
		for (i = 0, c = children.length; i < c; i++) {
			for (j = i; j < c; j++) {
				if (i == j) {
					continue;
				}
				if (this.compareTabOrder(children[j], children[i])) {
					tmp = children[i];
					children[i] = children[j];
					children[j] = tmp;
				}
			}
		}
		return children;
	}

	void clearHistory() {
		this.history.removeAllElements();
	}

}
