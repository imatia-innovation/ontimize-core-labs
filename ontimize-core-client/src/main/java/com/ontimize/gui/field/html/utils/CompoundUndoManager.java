package com.ontimize.gui.field.html.utils;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.bushe.swing.action.ActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;

/**
 * Manages compound undoable edits.
 *
 * Before an undoable edit happens on a particular document, you should call the static method
 * CompoundUndoManager.beginCompoundEdit(doc)
 *
 * Conversely after an undoable edit happens on a particular document, you shoulc call the static
 * method CompoundUndoManager.beginCompoundEdit(doc)
 *
 * For either of these methods to work, you must add an instance of CompoundUndoManager as a
 * document listener... e.g
 *
 * doc.addUndoableEditListener(new CompoundUndoManager(doc, new UndoManager());
 *
 * Note that each CompoundUndoManager should have its own UndoManager.
 *
 * @author Imatia S.L.
 */
public class CompoundUndoManager implements UndoableEditListener {

	private static final Logger logger = LoggerFactory.getLogger(CompoundUndoManager.class);

	private static final I18n i18n = I18n.getInstance();

	/**
	 * Static undo action that works across all documents with a CompoundUndoManager registered as an
	 * UndoableEditListener
	 */
	public static Action UNDO = new UndoAction();

	/**
	 * Static undo action that works across all documents with a CompoundUndoManager registered as an
	 * UndoableEditListener
	 */
	public static Action REDO = new RedoAction();

	protected UndoManager undoer;

	protected CompoundEdit compoundEdit = null;

	protected Document document = null;

	protected static List docs = new Vector();

	protected static List lsts = new Vector();

	protected static List undoers = new Vector();

	protected static void registerDocument(final Document doc, final CompoundUndoManager lst, final UndoManager um) {
		CompoundUndoManager.docs.add(doc);
		CompoundUndoManager.lsts.add(lst);
		CompoundUndoManager.undoers.add(um);
	}

	/**
	 * Gets the undo manager for a document that has a CompoundUndoManager as an UndoableEditListener
	 * @param doc
	 * @return The registed undomanger for the document
	 */
	public static UndoManager getUndoManagerForDocument(final Document doc) {
		for (int i = 0; i < CompoundUndoManager.docs.size(); i++) {
			if (CompoundUndoManager.docs.get(i) == doc) {
				return (UndoManager) CompoundUndoManager.undoers.get(i);
			}
		}

		return null;
	}

	/**
	 * Notifies the CompoundUndoManager for the specified Document that a compound edit is about to
	 * begin.
	 * @param doc
	 */
	public static void beginCompoundEdit(final Document doc) {
		for (int i = 0; i < CompoundUndoManager.docs.size(); i++) {
			if (CompoundUndoManager.docs.get(i) == doc) {
				final CompoundUndoManager l = (CompoundUndoManager) CompoundUndoManager.lsts.get(i);
				l.beginCompoundEdit();
				return;
			}
		}
	}

	/**
	 * Notifies the CompoundUndoManager for the specified Document that a compound edit is complete.
	 * @param doc
	 */
	public static void endCompoundEdit(final Document doc) {
		for (int i = 0; i < CompoundUndoManager.docs.size(); i++) {
			if (CompoundUndoManager.docs.get(i) == doc) {
				final CompoundUndoManager l = (CompoundUndoManager) CompoundUndoManager.lsts.get(i);
				l.endCompoundEdit();
				return;
			}
		}
	}

	/**
	 * Updates the enabled states of the UNDO and REDO actions for the specified document
	 * @param doc
	 */
	public static void updateUndo(final Document doc) {
		final UndoManager um = CompoundUndoManager.getUndoManagerForDocument(doc);
		if (um != null) {
			CompoundUndoManager.UNDO.setEnabled(um.canUndo());
			CompoundUndoManager.REDO.setEnabled(um.canRedo());
		}
	}

	/**
	 * Discards all edits for the specified Document
	 * @param doc
	 */
	public static void discardAllEdits(final Document doc) {
		final UndoManager um = CompoundUndoManager.getUndoManagerForDocument(doc);
		if (um != null) {
			um.discardAllEdits();
			CompoundUndoManager.UNDO.setEnabled(um.canUndo());
			CompoundUndoManager.REDO.setEnabled(um.canRedo());
		}
	}

	/**
	 * Creates a new CompoundUndoManager
	 * @param doc
	 * @param um The UndoManager to use for this document
	 */
	public CompoundUndoManager(final Document doc, final UndoManager um) {
		this.undoer = um;
		this.document = doc;
		CompoundUndoManager.registerDocument(this.document, this, this.undoer);
	}

	/**
	 * Creates a new CompoundUndoManager
	 * @param doc
	 */
	public CompoundUndoManager(final Document doc) {
		this(doc, new UndoManager());
	}

	@Override
	public void undoableEditHappened(final UndoableEditEvent evt) {
		final UndoableEdit edit = evt.getEdit();
		if (this.compoundEdit != null) {
			this.compoundEdit.addEdit(edit);
		} else {
			this.undoer.addEdit(edit);
			CompoundUndoManager.updateUndo(this.document);
		}
	}

	protected void beginCompoundEdit() {
		this.compoundEdit = new CompoundEdit();
	}

	protected void endCompoundEdit() {
		if (this.compoundEdit != null) {
			this.compoundEdit.end();
			this.undoer.addEdit(this.compoundEdit);
			CompoundUndoManager.updateUndo(this.document);
		}
		this.compoundEdit = null;
	}

	static class UndoAction extends TextAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public UndoAction() {
			super(CompoundUndoManager.i18n.str("HTMLShef.undo"));
			this.putValue("ID", "HTMLShef.undo");
			this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.UNDO));
			this.putValue(ActionManager.LARGE_ICON, ImageManager.getIcon(ImageManager.UNDO));
			this.putValue(Action.MNEMONIC_KEY, new Integer(CompoundUndoManager.i18n.mnem("HTMLShef.undo")));

			this.setEnabled(false);
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
			this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Document doc = this.getTextComponent(e).getDocument();
			final UndoManager um = CompoundUndoManager.getUndoManagerForDocument(doc);
			if (um != null) {
				try {
					um.undo();
				} catch (final CannotUndoException ex) {
					CompoundUndoManager.logger.error("Unable to undo: " + ex, ex);
				}

				CompoundUndoManager.updateUndo(doc);
			}
		}

	}

	static class RedoAction extends TextAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public RedoAction() {
			super(CompoundUndoManager.i18n.str("HTMLShef.redo"));
			this.putValue("ID", "HTMLShef.redo");
			this.putValue(Action.SMALL_ICON, ImageManager.getIcon(ImageManager.REDO));
			this.putValue(ActionManager.LARGE_ICON, ImageManager.getIcon(ImageManager.REDO));
			this.putValue(Action.MNEMONIC_KEY, new Integer(CompoundUndoManager.i18n.mnem("HTMLShef.redo")));

			this.setEnabled(false);
			final KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK);
			this.putValue(Action.ACCELERATOR_KEY, ks);
			this.putValue(Action.SHORT_DESCRIPTION, this.getValue(Action.NAME));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Document doc = this.getTextComponent(e).getDocument();
			final UndoManager um = CompoundUndoManager.getUndoManagerForDocument(doc);
			if (um != null) {
				try {
					um.redo();
				} catch (final CannotUndoException ex) {
					CompoundUndoManager.logger.error("Unable to redo: " + ex, ex);
				}

				CompoundUndoManager.updateUndo(doc);
			}
		}

	}

}
