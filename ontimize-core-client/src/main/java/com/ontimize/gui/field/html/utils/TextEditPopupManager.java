package com.ontimize.gui.field.html.utils;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages an application-wide popup menu for JTextComponents. Any JTextComponent registered with
 * the manager will have a right-click invokable popup menu, which provides options to undo, redo,
 * cut, copy, paste, and select-all. The popup manager is a singleton and must be retrieved with the
 * getInstance() method:
 *
 * <pre>
 * <code>
 * JTextField textField = new JTextField(20);
 * TextEditPopupManager.getInstance().registerJTextComponent(textField);
 * </code>
 * </pre>
 *
 * @author Bob Tantlinger TODO Internationalize, add mnemonics, etc
 */

public class TextEditPopupManager {

	private static final Logger logger = LoggerFactory.getLogger(TextEditPopupManager.class);

	private static final I18n i18n = I18n.getInstance();

	private static TextEditPopupManager singleton = null;

	public static final String CUT = "cut";

	public static final String COPY = "copy";

	public static final String PASTE = "paste";

	public static final String SELECT_ALL = "selectAll";

	public static final String UNDO = "undo";

	public static final String REDO = "redo";

	protected HashMap actions = new HashMap();

	// The actions we add to the popup menu
	protected Action cut = new DefaultEditorKit.CutAction();

	protected Action copy = new DefaultEditorKit.CopyAction();

	protected Action paste = new DefaultEditorKit.PasteAction();

	protected Action selectAll = new NSelectAllAction();

	protected Action undo = new UndoAction();

	protected Action redo = new RedoAction();

	// maintains a list of the currently registered JTextComponents
	protected List textComps = new Vector();

	protected List undoers = new Vector();

	protected JTextComponent focusedComp;// the registered JTextComponent that
	// is
	// focused

	protected UndoManager undoer; // The undomanager for the focused
	// JTextComponent

	// Listeners for the JTextComponents
	protected FocusListener focusHandler = new PopupFocusHandler();

	protected MouseListener popupHandler = new PopupHandler();

	protected UndoListener undoHandler = new UndoListener();

	protected CaretListener caretHandler = new CaretHandler();

	protected JPopupMenu popup = new JPopupMenu();// The one and only popup menu

	private TextEditPopupManager() {
		this.cut.putValue(Action.NAME, TextEditPopupManager.i18n.str("cut"));
		// cut.putValue(Action.SMALL_ICON, UIUtils.getIcon(UIUtils.X16,
		// "cut.png"));
		this.copy.putValue(Action.NAME, TextEditPopupManager.i18n.str("copy"));
		// copy.putValue(Action.SMALL_ICON, UIUtils.getIcon(UIUtils.X16,
		// "copy.png"));
		this.paste.putValue(Action.NAME, TextEditPopupManager.i18n.str("paste"));
		// paste.putValue(Action.SMALL_ICON, UIUtils.getIcon(UIUtils.X16,
		// "paste.png"));
		this.selectAll.putValue(Action.ACCELERATOR_KEY, null);

		this.popup.add(this.undo);
		this.popup.add(this.redo);
		this.popup.addSeparator();
		this.popup.add(this.cut);
		this.popup.add(this.copy);
		this.popup.add(this.paste);
		this.popup.addSeparator();
		this.popup.add(this.selectAll);

		this.actions.put(TextEditPopupManager.CUT, this.cut);
		this.actions.put(TextEditPopupManager.COPY, this.copy);
		this.actions.put(TextEditPopupManager.PASTE, this.paste);
		this.actions.put(TextEditPopupManager.SELECT_ALL, this.selectAll);
		this.actions.put(TextEditPopupManager.UNDO, this.undo);
		this.actions.put(TextEditPopupManager.REDO, this.redo);

	}

	/**
	 * Gets the singleton instance of TextEditPopupManager
	 * @return The one and only TextEditPopupManager
	 */
	public static TextEditPopupManager getInstance() {
		if (TextEditPopupManager.singleton == null) {
			TextEditPopupManager.singleton = new TextEditPopupManager();
		}
		return TextEditPopupManager.singleton;
	}

	public Action getAction(final String name) {
		return (Action) this.actions.get(name);
	}

	/**
	 * Registers a JTextComponent with the manager. Note that if you change the document of the
	 * JTextComponent, you should unregister it with method unregisterJTextComponent, and then
	 * re-register it with this method. e.g...
	 *
	 * <pre>
	 * <code>
	 * TextEditPopupManager.getInstance().registerJTextComponent(comp);
	 * ...
	 * ...
	 * TextEditPopupManager.getInstance().unregisterJTextComponent(comp);
	 * comp.setDocument(new PlainDocument());
	 * TextEditPopupManager.getInstance().registerJTextComponent(comp);
	 * </code>
	 * </pre>
	 *
	 * @param tc The JTextComponent to register
	 * @throws IllegalArgumentException If the component is null, or already registered
	 */
	public void registerJTextComponent(final JTextComponent tc) throws IllegalArgumentException {
		this.registerJTextComponent(tc, new UndoManager());
	}

	/**
	 * Registers a JTextComponent and UndoManager with the manager. This is useful if you wish to supply
	 * a custom UndoManager
	 * @param tc The JTextComponent to register
	 * @param um The UndoManger to register
	 * @throws IllegalArgumentException If the component is null, or already registered
	 */
	public void registerJTextComponent(final JTextComponent tc, final UndoManager um) throws IllegalArgumentException {
		if ((tc == null) || (um == null)) {
			throw new IllegalArgumentException("null arguments aren't allowed");
		}

		if (this.getIndexOfJTextComponent(tc) != -1) {
			throw new IllegalArgumentException("Component already registered");
		}

		tc.addFocusListener(this.focusHandler);
		tc.addCaretListener(this.caretHandler);
		tc.addMouseListener(this.popupHandler);
		tc.getDocument().addUndoableEditListener(this.undoHandler);

		this.textComps.add(new WeakReference(tc));
		this.undoers.add(um);
	}

	/**
	 * Unregisters a JTextComponent from the manager.
	 * @param tc The JTextComponent to unregister
	 */
	public void unregisterJTextComponent(final JTextComponent tc) {
		final int index = this.getIndexOfJTextComponent(tc);
		if (index != -1) {
			tc.removeFocusListener(this.focusHandler);
			tc.removeCaretListener(this.caretHandler);
			tc.removeMouseListener(this.popupHandler);
			tc.getDocument().removeUndoableEditListener(this.undoHandler);

			this.textComps.remove(index);
			this.undoers.remove(index);
		}
	}

	/**
	 * Gets the index of a registered JTextComponent
	 * @param tc
	 * @return
	 */
	protected int getIndexOfJTextComponent(final JTextComponent tc) {
		for (int i = 0; i < this.textComps.size(); i++) {
			final WeakReference wr = (WeakReference) this.textComps.get(i);
			if (wr.get() == tc) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Clears any JTextComponent references from the manager that have been garbage collected.
	 */
	protected void clearEmptyReferences() {
		for (int i = 0; i < this.textComps.size(); i++) {
			final WeakReference wr = (WeakReference) this.textComps.get(i);
			if (wr.get() == null) {
				this.undoers.set(i, null);
			}
		}

		for (final Iterator it = this.textComps.iterator(); it.hasNext();) {
			final WeakReference w = (WeakReference) it.next();
			if (w.get() == null) {
				it.remove();
			}
		}

		for (final Iterator it = this.undoers.iterator(); it.hasNext();) {
			if (it.next() == null) {
				it.remove();
			}
		}
	}

	/**
	 * Updates the enabled state of the actions
	 */
	protected void updateActions() {
		if ((this.focusedComp != null) && this.focusedComp.hasFocus()) {
			this.undo.setEnabled(this.undoer.canUndo());
			this.redo.setEnabled(this.undoer.canRedo());
			final boolean hasSel = this.focusedComp.getSelectedText() != null;
			this.copy.setEnabled(hasSel);
			this.cut.setEnabled(hasSel);
		}
	}

	/*
	 * Listens for undoable edits on the documents of registered JTextComponents
	 */
	protected class UndoListener implements UndoableEditListener {

		@Override
		public void undoableEditHappened(final UndoableEditEvent e) {
			final UndoableEdit edit = e.getEdit();
			if (TextEditPopupManager.this.undoer != null) {
				TextEditPopupManager.this.undoer.addEdit(edit);
				TextEditPopupManager.this.updateActions();
			}
		}

	}

	/*
	 * Undo and redo actions
	 */
	protected class RedoAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RedoAction() {
			super(TextEditPopupManager.i18n.str("redo"));
			// super(i18n.str("redo"), UIUtils.getIcon(UIUtils.X16,
			// "redo.png"));
			this.putValue(Action.MNEMONIC_KEY, new Integer(TextEditPopupManager.i18n.mnem("redo")));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				if (TextEditPopupManager.this.undoer != null) {
					TextEditPopupManager.this.undoer.redo();
					TextEditPopupManager.this.updateActions();
				}
			} catch (final Exception ex) {
				TextEditPopupManager.logger.error("Cannot Redo", ex);
			}
		}

	}

	protected class UndoAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public UndoAction() {
			super(TextEditPopupManager.i18n.str("undo"));
			// super(i18n.str("undo"), UIUtils.getIcon(UIUtils.X16,
			// "undo.png"));
			this.putValue(Action.MNEMONIC_KEY, new Integer(TextEditPopupManager.i18n.mnem("undo")));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				if (TextEditPopupManager.this.undoer != null) {
					TextEditPopupManager.this.undoer.undo();
					TextEditPopupManager.this.updateActions();
				}
			} catch (final Exception ex) {
				TextEditPopupManager.logger.error("Cannot Undo", ex);
			}
		}

	}

	/*
	 * Select all action for the registered JTextComponents
	 */
	protected class NSelectAllAction extends TextAction {

		private static final long serialVersionUID = 1L;

		public NSelectAllAction() {
			super(TextEditPopupManager.i18n.str("select_all"));
			this.putValue(Action.MNEMONIC_KEY, new Integer(TextEditPopupManager.i18n.mnem("select_all")));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			this.getTextComponent(e).selectAll();
		}

	}

	/*
	 * Listens for focus changes on the registered components and updates the UndoManager accordingly
	 */
	protected class PopupFocusHandler implements FocusListener {

		@Override
		public void focusGained(final FocusEvent e) {
			if (!e.isTemporary()) {
				final JTextComponent tc = (JTextComponent) e.getComponent();
				final int index = TextEditPopupManager.this.getIndexOfJTextComponent(tc);
				if (index != -1) {
					// set the current UndoManager for the currently focused
					// JTextComponent
					TextEditPopupManager.this.undoer = (UndoManager) TextEditPopupManager.this.undoers.get(index);
					TextEditPopupManager.this.focusedComp = tc;
					TextEditPopupManager.this.updateActions();
				}

				// clean up any dead refs that have been garbage collected
				TextEditPopupManager.this.clearEmptyReferences();
			}
		}

		@Override
		public void focusLost(final FocusEvent e) {
		}

	}

	/*
	 * Listens for caret changes on the registered JTextComponents
	 */
	protected class CaretHandler implements CaretListener {

		@Override
		public void caretUpdate(final CaretEvent e) {
			TextEditPopupManager.this.updateActions();
		}

	}

	/*
	 * Handles right clicks on the component to popup the menu
	 */
	protected class PopupHandler extends MouseAdapter {

		@Override
		public void mousePressed(final MouseEvent e) {
			this.checkForPopupTrigger(e);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			this.checkForPopupTrigger(e);
		}

		protected void checkForPopupTrigger(final MouseEvent e) {
			final JTextComponent tc = (JTextComponent) e.getComponent();
			if (e.isPopupTrigger() && tc.isEditable()) {
				if (!tc.isFocusOwner()) {
					tc.requestFocusInWindow();
				}

				TextEditPopupManager.this.popup.show(tc, e.getX(), e.getY());
			}
		}

	}

}
