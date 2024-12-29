package com.ontimize.util.swing.popuplist;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.ontimize.gui.ApplicationManager;

public class PopupItem extends JLabel {

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2068EN
	 */
	public static final String POPUPITEM_NAME = "PopupItem";

	protected List listenerList = null;

	protected boolean paintBorder = true;

	protected MouseListener handler = null;

	protected ResourceBundle bundle = null;

	protected boolean isMouseOver = false;

	public PopupItem(final String text) {
		super(text);
		this.init();
	}

	public PopupItem(final String text, final Icon icon, final ResourceBundle bundle) {
		super(ApplicationManager.getTranslation(text, bundle), icon, SwingConstants.LEFT);
		this.bundle = bundle;
		this.init();
	}

	@Override
	public String getName() {
		return PopupItem.POPUPITEM_NAME;
	}

	public ResourceBundle getResourceBundle() {
		return this.bundle;
	}

	protected void init() {

		if (!ApplicationManager.useOntimizePlaf) {
			final CompoundBorder border = new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),
					new LineBorder(Color.RED));
			this.setBorder(border);
		}
		this.setPaintBorder(false);
		this.installHander();
	}

	public String getActionCommand() {
		return this.getText();
	}

	protected static class MouseListenerItemPopup extends MouseAdapter {

		@Override
		public void mouseEntered(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupItem) {
				((PopupItem) o).setPaintBorder(true);
				((PopupItem) o).setMouseOver(true);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupItem) {
				((PopupItem) o).setPaintBorder(false);
				((PopupItem) o).setMouseOver(false);
			}

		}

		@Override
		public void mousePressed(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupItem) {
				((PopupItem) o).setPaintBorder(false);
				((PopupItem) o).setMouseOver(false);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			final Object o = e.getSource();
			if (o instanceof PopupItem) {
				((PopupItem) o).setPaintBorder(true);
				((PopupItem) o).setMouseOver(true);
				((PopupItem) o).fireActionPerformed();
			}
		}

	}

	protected void installHander() {
		this.handler = new MouseListenerItemPopup();
		this.addMouseListener(this.handler);
	}

	public void setMouseOver(final boolean isMouseOver) {
		this.isMouseOver = isMouseOver;
	}

	public boolean isMouseOver() {
		return this.isMouseOver;
	}

	public void setPaintBorder(final boolean border) {
		this.paintBorder = border;
		this.repaint();
	}

	@Override
	public void paintBorder(final Graphics g) {
		if (this.paintBorder) {
			super.paintBorder(g);
		}
	}

	public void addActionListener(final ActionListener action) {
		if (this.listenerList == null) {
			this.listenerList = new ArrayList();
		}
		if (!this.listenerList.contains(action)) {
			this.listenerList.add(action);
		}
	}

	public void removeActionListener(final ActionListener action) {
		if (this.listenerList == null) {
			return;
		}
		if (this.listenerList.contains(action)) {
			this.listenerList.remove(action);
		}
	}

	protected void fireActionPerformed() {
		if (this.listenerList == null) {
			return;
		}

		final ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, this.getText());

		for (int i = this.listenerList.size() - 1; i >= 0; i--) {
			((ActionListener) this.listenerList.get(i)).actionPerformed(e);
		}
	}

}
