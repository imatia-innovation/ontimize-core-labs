package com.ontimize.gui.attachment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.dto.EntityResult;

public class AttachmentListPopup extends JList implements Internationalization {

	public static final String FORM_N_ATTACHMENT_FOUND = "form.n_attachment_found";

	public static final String FORM_NO_AVAILABLE_ATTACHMENT = "form.no_available_attachment";

	private JPopupMenu popup = null;

	private JLabel emptyLabel = null;

	private JLabel lInfo = null;

	private JScrollPane scroll = null;

	private ResourceBundle res = null;

	private final MouseHandler handler;

	protected int currentAction = -1;

	protected int currentSelectionIndex = -1;

	public static Border itemsBorder;

	public AttachmentListPopup(final Form form) {
		this.handler = new MouseHandler(form, this);
		this.setModel(new AttachmentListModel());
		this.setCellRenderer(new AttachmentListCellRenderer(this.res));

		this.setEnabled(true);
		this.setBorder(null);
		this.installMouseHandler();
	}

	protected void installMouseHandler() {
		this.addMouseListener(this.handler);
		this.addMouseMotionListener(this.handler);
	}

	@Override
	public int getVisibleRowCount() {
		if (this.getModel().getSize() > 10) {
			return 20;
		} else {
			return this.getModel().getSize();
		}
	}

	public void show(final Component c, final int x, final int y) {
		if (this.popup == null) {
			this.popup = new JPopupMenu();
			this.popup.setLayout(new BorderLayout());
			if (!ApplicationManager.useOntimizePlaf) {
				this.popup.setBorder(new LineBorder(Color.black));
			}
			this.emptyLabel = new JLabel(ApplicationManager
					.getTranslation(AttachmentListPopup.FORM_NO_AVAILABLE_ATTACHMENT, this.res, null));
			this.lInfo = new JLabel(
					ApplicationManager.getTranslation(AttachmentListPopup.FORM_N_ATTACHMENT_FOUND, this.res,
							new Object[] { new Integer(this.getModel().getSize()) }));
			this.lInfo.setFont(this.lInfo.getFont().deriveFont(Font.BOLD));
			this.popup.add(this.lInfo, BorderLayout.NORTH);
			this.popup.add(this.emptyLabel, BorderLayout.SOUTH);
			this.scroll = new JScrollPane(this);
			this.popup.add(this.scroll);

			if (AttachmentListPopup.itemsBorder != null) {
				this.emptyLabel.setBorder(AttachmentListPopup.itemsBorder);
				this.lInfo.setBorder(AttachmentListPopup.itemsBorder);
			}
		}
		if (this.getModel().getSize() > 0) {
			this.emptyLabel.setVisible(false);
			this.lInfo.setVisible(true);
			this.scroll.setVisible(true);
			this.lInfo.setText(ApplicationManager.getTranslation(AttachmentListPopup.FORM_N_ATTACHMENT_FOUND, this.res,
					new Object[] { new Integer(this.getModel().getSize()) }));
		} else {
			this.lInfo.setVisible(false);
			this.emptyLabel.setVisible(true);
			this.scroll.setVisible(false);
		}
		this.popup.pack();
		this.popup.show(c, x, y);
	}

	public void setAttachments(final EntityResult record) {
		((AttachmentListModel) this.getModel()).setAttachment(record);
	}

	public void removeAttachment() {
		((AttachmentListModel) this.getModel()).setAttachment(null);
	}

	public Map getRecord(final Object o) {
		return ((AttachmentListModel) this.getModel()).getRecord(o);
	}

	public Map getRecord(final int i) {
		return ((AttachmentListModel) this.getModel()).getRecord(i);
	}

	public void setPopupVisible(final boolean visible) {
		this.popup.setVisible(visible);
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector(Arrays.asList(new String[] { AttachmentListPopup.FORM_NO_AVAILABLE_ATTACHMENT,
				AttachmentListPopup.FORM_N_ATTACHMENT_FOUND }));
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.res = resourceBundle;
		if (this.getCellRenderer() instanceof AttachmentListCellRenderer) {
			((AttachmentListCellRenderer) this.getCellRenderer()).setResourceBundle(this.res);
		}
	}

	public int getCurrentAction() {
		return this.currentAction;
	}

	protected void setCurrentAction(final int currentAction) {
		this.currentAction = currentAction;
	}

	public int getCurrentSelectionIndex() {
		return this.currentSelectionIndex;
	}

	protected void setCurrentSelectionIndex(final int currentSelectionIndex) {
		this.currentSelectionIndex = currentSelectionIndex;
	}

}
