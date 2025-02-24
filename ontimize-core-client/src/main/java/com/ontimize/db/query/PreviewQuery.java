package com.ontimize.db.query;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.EntityResultViewer;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.dto.EntityResult;

class QueryEntityResultViewer extends EntityResultViewer {

	public QueryEntityResultViewer(final Map h) {
		super(h);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public void setValue(final Object o) {
		super.setValue(o);
		TableColumn column = null;
		for (int i = 0, a = this.table.getColumnCount(); i < a; i++) {
			column = this.table.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(25);
			}
		}
	}

}

public class PreviewQuery extends EJDialog {

	private static final Logger logger = LoggerFactory.getLogger(PreviewQuery.class);

	protected ResourceBundle bundle = null;

	protected class TableModel extends AbstractTableModel {

		protected ResourceBundle bundle = null;

		protected String[] column = new String[0];

		protected EntityResult entityResult = null;

		public TableModel(final EntityResult rs, final ResourceBundle bundle) {
			this.bundle = bundle;
			this.entityResult = rs;
			final Enumeration enu = rs.keys();
			while (enu.hasMoreElements()) {
				final String[] aux = new String[this.column.length + 1];
				System.arraycopy(this.column, 0, aux, 0, this.column.length);
				aux[this.column.length] = (String) enu.nextElement();
				this.column = aux;
			}

		}

		@Override
		public int getColumnCount() {
			return this.column.length;
		}

		@Override
		public int getRowCount() {
			return this.entityResult.calculateRecordNumber();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final String nColumn = this.column[columnIndex];
			final List vData = (List) this.entityResult.get(nColumn);
			return vData.get(rowIndex);
		}

		@Override
		public String getColumnName(final int columnIndex) {
			return this.column[columnIndex];
		}

	}

	public PreviewQuery(final Frame f, final EntityResult entityResult, final ResourceBundle bundle) {
		super(f, ApplicationManager.getTranslation("PreviewQuery", bundle), true);
		this.bundle = bundle;
		this.init(entityResult);
	}

	public PreviewQuery(final Dialog d, final EntityResult entityResult, final ResourceBundle bundle) {
		super(d, ApplicationManager.getTranslation("PreviewQuery", bundle), true);
		this.bundle = bundle;
		this.init(entityResult);
	}

	public void init(final EntityResult entityResult) {

		final QueryEntityResultViewer v = new QueryEntityResultViewer(new Hashtable());

		v.setValue(entityResult);
		v.setResourceBundle(this.bundle);

		final JScrollPane scroll = new JScrollPane(v);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.revalidate();

		final JLabel label = new JLabel(ApplicationManager.getTranslation("previewquery.Results", this.bundle));
		final JButton bOK = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OK));
		bOK.setToolTipText(ApplicationManager.getTranslation("previewquery.Accept", this.bundle));
		bOK.setText(ApplicationManager.getTranslation("previewquery.Accept", this.bundle));

		final FlowLayout f = new FlowLayout();
		f.setAlignment(FlowLayout.CENTER);
		final JPanel pButton = new JPanel();
		pButton.setLayout(f);
		pButton.add(bOK);
		bOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
				w.setVisible(false);
			}
		});

		label.setBorder(new EmptyBorder(5, 15, 5, 5));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(label, BorderLayout.NORTH);
		this.getContentPane().add(scroll, BorderLayout.CENTER);
		this.getContentPane().add(pButton, BorderLayout.SOUTH);
		this.pack();
	}

	public static void show(final Component c, final EntityResult rs, final ResourceBundle bundle) {
		final Window w = SwingUtilities.getWindowAncestor(c);
		PreviewQuery preview = null;

		if (w instanceof Frame) {
			preview = new PreviewQuery((Frame) w, rs, bundle);
		} else if (w instanceof Dialog) {
			preview = new PreviewQuery((Dialog) w, rs, bundle);
		}

		if (preview != null) {

			ApplicationManager.center(preview);
			preview.setVisible(true);
		} else {
			PreviewQuery.logger.debug("Preview can not be created....");
		}
	}

}
