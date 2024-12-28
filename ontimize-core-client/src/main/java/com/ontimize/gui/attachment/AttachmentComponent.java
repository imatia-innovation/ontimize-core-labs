package com.ontimize.gui.attachment;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.popuplist.LabelItem;

public class AttachmentComponent extends JPanel {

	public static final String FORM_ATTACHMENT_PRIVATE_TIP = "form.attachment_private_tip";

	public static final String FORM_ATTACHMENT_BY_TIP = "form.attachment_by_tip";

	public static final String FORM_ATTACHMENT_DESCRIPTION_TIP = "form.attachment_description_tip";

	public static final String FORM_ATTACHMENT_DATE_TIP = "form.attachment_date_tip";

	public static final String FORM_ATTACHMENT_FILE_SIZE_TIP = "form.attachment_file_size_tip";

	public static final String FORM_ATTACHMENT_FILE_TIP = "form.attachment_file_tip";

	protected static final String ATTACHMENT_PRIVATE_TIP = AttachmentComponent.FORM_ATTACHMENT_PRIVATE_TIP;

	private Map data = null;

	private boolean isPrivate = false;

	private Object attachmentId = null;

	SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy");

	private final Dimension dMax = null;

	private int state = -1;

	protected JCheckBox check = null;

	protected LabelItem lDelete = null;

	protected LabelItem saveLabel = null;

	protected LabelItem editDescriptionLabel = null;

	protected LabelItem textLabel = null;

	private final ImageIcon delete = ImageManager.getIcon(ImageManager.RECYCLER);

	private final ImageIcon edit = ImageManager.getIcon(ImageManager.EDIT);

	private final ImageIcon save = ImageManager.getIcon(ImageManager.SAVE_TABLE_FILTER);

	public static Border componentBorder;

	public interface IActionId {

		public int getActionId();

	}

	public class LabelItemExt extends LabelItem implements IActionId {

		protected int actionId = -1;

		public LabelItemExt(final int actionId) {
			this.actionId = actionId;
		}

		public LabelItemExt(final Icon image, final int actionId) {
			super(image);
			this.actionId = actionId;
		}

		@Override
		public int getActionId() {
			return this.actionId;
		}

	}

	public class JCheckBoxExt extends JCheckBox implements IActionId {

		protected int actionId = -1;

		public JCheckBoxExt(final int actionId) {
			this.actionId = actionId;
		}

		@Override
		public int getActionId() {
			return this.actionId;
		}

	}

	public AttachmentComponent() {
		this.setLayout(new GridBagLayout());
		this.lDelete = new LabelItemExt(this.delete, AttachmentListCellRenderer.DELETE);
		this.saveLabel = new LabelItemExt(this.save, AttachmentListCellRenderer.SAVE);
		this.editDescriptionLabel = new LabelItemExt(this.edit, AttachmentListCellRenderer.EDIT_DESCRIPTION);
		this.textLabel = new LabelItemExt(AttachmentListCellRenderer.OPEN);

		this.check = new JCheckBoxExt(AttachmentListCellRenderer.CHECK);

		this.add(this.check, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.saveLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.lDelete, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.editDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.textLabel, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		if (ApplicationManager.useOntimizePlaf) {
			this.setOpaque(false);
		} else {
			this.check.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		}
		if (AttachmentComponent.componentBorder != null) {
			this.setBorder(AttachmentComponent.componentBorder);
		}
	}

	public void setState(final int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	public void setRecord(final Map data) {
		this.data = data;
		if (data.containsKey(Form.PRIVATE_ATTACHMENT)) {
			final Number v = (Number) data.get(Form.PRIVATE_ATTACHMENT);
			this.isPrivate = v.intValue() > 0;
		} else {
			this.isPrivate = false;
		}

		if (data.containsKey(Form.ATTACHMENT_ID)) {
			this.attachmentId = data.get(Form.ATTACHMENT_ID);
		}

		this.check.setSelected(this.isPrivate);

		final StringBuilder buffer = new StringBuilder();
		if (data.containsKey(Form.ORIGINAL_FILE_NAME)) {
			buffer.append((String) data.get(Form.ORIGINAL_FILE_NAME));
		}
		if (data.containsKey(Form.ATTACHMENT_DATE)) {
			buffer.append(" - ");
			buffer.append(this.format.format(data.get(Form.ATTACHMENT_DATE)));
		}

		this.textLabel.setText(buffer.toString());
		this.textLabel.revalidate();
	}

	public String getExtension() {
		if (this.data.containsKey(Form.ORIGINAL_FILE_NAME)) {
			final String temp = (String) this.data.get(Form.ORIGINAL_FILE_NAME);
			final int index = temp.lastIndexOf(".");
			if (index != -1) {
				return temp.substring(index + 1);
			}
		}
		return "";
	}

	public String getText() {
		final StringBuilder buffer = new StringBuilder();
		if (this.data.containsKey(Form.ORIGINAL_FILE_NAME)) {
			buffer.append((String) this.data.get(Form.ORIGINAL_FILE_NAME));
		}
		if (this.data.containsKey(Form.ATTACHMENT_DATE)) {
			buffer.append(" - ");
			buffer.append(this.format.format(this.data.get(Form.ATTACHMENT_DATE)));
		}
		return buffer.toString();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.getFontMetrics(this.getFont()).stringWidth(this.getText()) + 18 + 38 + 16 + 2 + 16,
				Math.max(this.getFontMetrics(this.getFont()).getHeight() + 4, 18));
	}

	@Override
	public void paint(final Graphics g) {
		final String ext = this.getExtension();
		Icon imageF = null;
		if (AttachmentListCellRenderer.getIcons().containsKey(ext)) {
			imageF = (Icon) AttachmentListCellRenderer.getIcons().get(ext);
		} else {
			imageF = AttachmentListCellRenderer.getUnknownIcon();
		}

		this.textLabel.setIcon(imageF);

		if (this.state == AttachmentListCellRenderer.CHECK) {
			this.check.setBorderPainted(true);
			this.saveLabel.setBorderPainted(false);
			this.lDelete.setBorderPainted(false);
			this.textLabel.setBorderPainted(false);
			this.editDescriptionLabel.setBorderPainted(false);
		} else if (this.state == AttachmentListCellRenderer.SAVE) {
			this.check.setBorderPainted(false);
			this.saveLabel.setBorderPainted(true);
			this.lDelete.setBorderPainted(false);
			this.textLabel.setBorderPainted(false);
			this.editDescriptionLabel.setBorderPainted(false);
		} else if (this.state == AttachmentListCellRenderer.DELETE) {
			this.check.setBorderPainted(false);
			this.saveLabel.setBorderPainted(false);
			this.lDelete.setBorderPainted(true);
			this.textLabel.setBorderPainted(false);
			this.editDescriptionLabel.setBorderPainted(false);
		} else if (this.state == AttachmentListCellRenderer.EDIT_DESCRIPTION) {
			this.check.setBorderPainted(false);
			this.saveLabel.setBorderPainted(false);
			this.lDelete.setBorderPainted(false);
			this.textLabel.setBorderPainted(false);
			this.editDescriptionLabel.setBorderPainted(true);
		} else if (this.state == AttachmentListCellRenderer.OPEN) {
			this.check.setBorderPainted(false);
			this.saveLabel.setBorderPainted(false);
			this.lDelete.setBorderPainted(false);
			this.textLabel.setBorderPainted(true);
			this.editDescriptionLabel.setBorderPainted(false);
		} else {
			this.check.setBorderPainted(false);
			this.saveLabel.setBorderPainted(false);
			this.lDelete.setBorderPainted(false);
			this.textLabel.setBorderPainted(false);
			this.editDescriptionLabel.setBorderPainted(false);
		}
		super.paint(g);
	}

	protected void setFileTooltip(final ResourceBundle bundle) {

		String sName = null;
		String sDescription = null;
		String sAttachmentDate = null;
		Boolean bPrivate = null;
		String sUser = null;
		String sSize = null;
		final SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		final Map hRecord = this.data;
		if (hRecord.containsKey(Form.ORIGINAL_FILE_NAME)) {
			sName = (String) hRecord.get(Form.ORIGINAL_FILE_NAME);
		}
		if (hRecord.containsKey(Form.DESCRIPTION_FILE)) {
			sDescription = (String) hRecord.get(Form.DESCRIPTION_FILE);
		}
		if (hRecord.containsKey(Form.ATTACHMENT_DATE)) {
			final Object o = hRecord.get(Form.ATTACHMENT_DATE);
			sAttachmentDate = format.format(o);
		}
		if (hRecord.containsKey(Form.SIZE)) {
			final Object o = hRecord.get(Form.SIZE);
			sSize = o.toString();
		}

		if (hRecord.containsKey(Form.PRIVATE_ATTACHMENT)) {
			final Object o = hRecord.get(Form.PRIVATE_ATTACHMENT);
			if (o instanceof Number) {
				bPrivate = new Boolean(((Number) o).intValue() > 0);
			}
		}

		if (hRecord.containsKey(Form.USER)) {
			sUser = hRecord.get(Form.USER).toString();
		}

		final StringBuilder buffer = new StringBuilder("<HTML><BODY><TABLE>");

		if (sName != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_FILE_TIP, bundle));
			buffer.append("</B>");
			buffer.append("</TD><TD>");
			buffer.append(sName);
			buffer.append("</TD></TR>");
		}

		if (sSize != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_FILE_SIZE_TIP, bundle));
			buffer.append("</B>");
			buffer.append("</TD><TD>");
			buffer.append(sSize);
			buffer.append("bytes</TD></TR>");
		}

		if (sAttachmentDate != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_DATE_TIP, bundle));
			buffer.append("</B>");
			buffer.append("</TD><TD>");
			buffer.append(sAttachmentDate);
			buffer.append("</TD></TR>");
		}

		if (sDescription != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer
			.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_DESCRIPTION_TIP, bundle));
			buffer.append("</B>");
			buffer.append("</TD><TD><PRE>");
			buffer.append(sDescription);
			buffer.append("</PRE></TD></TR>");
		}

		if (sUser != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_BY_TIP, bundle));
			buffer.append("</B>");
			buffer.append("</TD><TD><PRE>");
			buffer.append(sUser);
			buffer.append("</PRE></TD></TR>");

		}

		if (bPrivate != null) {
			buffer.append("<TR><TD>");
			buffer.append("<B>");
			buffer.append(ApplicationManager.getTranslation(AttachmentComponent.FORM_ATTACHMENT_PRIVATE_TIP, bundle));
			buffer.append(":");
			buffer.append("</B>");
			buffer.append("</TD><TD><PRE>");
			buffer.append(ApplicationManager.getTranslation(bPrivate.toString(), bundle));
			buffer.append("</PRE></TD></TR>");

		}

		buffer.append("</TABLE></BODY></HTML>");
		this.setToolTipText(buffer.toString());
	}

}
