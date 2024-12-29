package com.ontimize.util.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class PivotDetailTableUtils extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(PivotDetailTableUtils.class);

	public static String PIVOTDETAILTABLE_INFORMATION_TITLE = "pivotdetailtable.information.title";

	public static String PIVOTDETAILTABLE_GROUPINGROWS_TITLE = "pivotdetailtable.groupingrows";

	public static String PIVOTDETAILTABLE_GROUPINGCOLUMNS_TITLE = "pivotdetailtable.groupingcolumns";

	public static class PivotDetailDialog extends EJDialog {

		public static String PIVOT_DETAIL_DIALOG_TITLE = "pivotdetailtable.dialog.title";

		protected PivotDetailPanel centerPanel;

		public PivotDetailDialog(final Frame owner, final TableModel model, final Map parameters, final ResourceBundle res) {
			super(owner, PivotDetailTableUtils.translate(PivotDetailDialog.PIVOT_DETAIL_DIALOG_TITLE, res, null), true);
			this.init(model, parameters, res);
		}

		public PivotDetailDialog(final Dialog owner, final TableModel model, final Map parameters, final ResourceBundle res) {
			super(owner, PivotDetailTableUtils.translate(PivotDetailDialog.PIVOT_DETAIL_DIALOG_TITLE, res, null), true);
			this.init(model, parameters, res);
		}

		protected void init(final TableModel model, final Map parameters, final ResourceBundle res) {
			this.centerPanel = new PivotDetailPanel(model, parameters, res);
			final JScrollPane scroll = new JScrollPane(this.centerPanel);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(scroll);
			this.pack();
		}

		public void setModel(final TableModel model, final Map information) {
			this.centerPanel.setModel(model, information);
		}

		public void setWidthAndPositionColumns(final String colPositionAndWith) {
			this.centerPanel.setWidthAndPositionColumns(colPositionAndWith);
		}

		public void setRenderers(final Map<?, ?> renderersForColumnsMap) {
			this.centerPanel.setRenderersForColumns(renderersForColumnsMap);

		}

		public void updateVisibleColumns(final List visibleCols) {
			this.centerPanel.updateVisibleColumns(visibleCols);
		}

		public void setResourceBundle(final ResourceBundle res) {
			this.centerPanel.setResourceBundle(res);
		}

	}

	public static class PivotDetailPanel extends JPanel {

		protected TableModel model = null;

		protected ResourceBundle resources = null;

		protected Table table;

		protected Label rows;

		protected Label column;

		protected List cols;

		protected List visibleCols;

		static class AuxPanel extends JPanel {

			public AuxPanel(final String title, final ResourceBundle res) {
				this.setLayout(new BorderLayout());
				this.setBorder(new TitledBorder(PivotDetailTableUtils.translate(title, res, null)));
			}

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				if (d.width < 120) {
					d.width = 120;
				}
				return d;
			}

		}

		public PivotDetailPanel(final TableModel model, final Map parameters, final ResourceBundle res) {
			this.model = model;
			this.resources = res;
			this.init(parameters);
		}

		public void setModel(final TableModel model, final Map information) {
			this.model = model;
			this.updateTitle(information);
			this.updateTable();
		}

		public void setRenderersForColumns(final Map<?, ?> renderersForColumnsMap) {
			for (final Entry actualEntry : renderersForColumnsMap.entrySet()) {
				final String columnName = (String) actualEntry.getKey();
				final TableCellRenderer originalColumnRender = ((TableCellRenderer) actualEntry.getValue());
				try {
					this.table.setRendererForColumn(actualEntry.getKey().toString(),
							originalColumnRender.getClass().newInstance());
				} catch (final InstantiationException e) {
					PivotDetailTableUtils.logger.error(null, e);
				} catch (final IllegalAccessException e) {
					PivotDetailTableUtils.logger.error(null, e);
				}
			}
		}

		public void setWidthAndPositionColumns(final String colPositionAndWith) {
			if (colPositionAndWith != null) {
				final StringTokenizer st = new StringTokenizer(colPositionAndWith, ";");
				final List<String> colPositionVisible = new Vector<String>();
				while (st.hasMoreTokens()) {
					final String t = st.nextToken();
					final int iIg = t.indexOf('=');
					if (iIg < 0) {
						continue;
					}
					final int iDP = t.indexOf(':');
					if (iDP < 0) {
						continue;
					}
					final String col = t.substring(0, iIg);
					final String sWidth = t.substring(iIg + 1, iDP);
					final String pos = t.substring(iDP + 1);
					// if (this.table.isVisibleColumn(col) == false) {
					// continue;
					// }
					colPositionVisible.add(col);

					try {
						final TableColumn tc = this.table.getJTable().getColumn(col);
						if (tc != null) {
							this.table.getJTable()
							.moveColumn(this.table.getJTable().convertColumnIndexToView(tc.getModelIndex()),
									Integer.parseInt(pos));
							tc.setPreferredWidth(Integer.parseInt(sWidth));
							tc.setWidth(Integer.parseInt(sWidth));
						}
					} catch (final Exception e) {
						PivotDetailTableUtils.logger.error("{}", e.getMessage(), e);
					}

				}

				this.table.setVisibleColumns(colPositionVisible, true);
			}
		}

		public void updateVisibleColumns(final List visibleCols) {
			if ((visibleCols == null) || visibleCols.isEmpty()) {
				this.visibleCols = null;
				return;
			}
			if (this.visibleCols != null) {
				this.visibleCols.clear();
				this.visibleCols.addAll(visibleCols);
			}
		}

		public TableModel getModel() {
			return this.model;
		}

		protected void init(final Map parameters) {
			this.setLayout(new BorderLayout());

			this.add(this.configureInformationTitle(), BorderLayout.NORTH);

			try {

				final Object cols = parameters.get(Table.COLS);
				if (cols != null) {
					final String sColumnNames = cols.toString();
					this.cols = ApplicationManager.getTokensAt(sColumnNames, ";");

					final Object visiblecols = parameters.get(Table.VISIBLE_COLS);
					if (visiblecols != null) {
						this.visibleCols = ApplicationManager.getTokensAt(visiblecols.toString(), ";");
					} else { // If visiblecols parameter does not exist then use
						// the same as columns
						for (int i = 0; i < this.cols.size(); i++) {
							if (this.visibleCols.contains(this.cols.get(i)) == false) {
								this.visibleCols.add(this.cols.get(i));
							}
						}
					}
				}

				this.table = new Table(parameters);
				this.table.setResourceBundle(this.resources);
				this.table.setReferenceLocator(ApplicationManager.getApplication().getReferenceLocator());
			} catch (final Exception e) {
				PivotDetailTableUtils.logger.error(null, e);
			}
			final JScrollPane scroll = new JScrollPane(this.table);
			scroll.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
			this.add(scroll);

			this.add(this.configureCloseButton(), BorderLayout.SOUTH);
		}

		protected JPanel configureInformationTitle() {
			final JPanel pTitle = new JPanel();
			pTitle.setLayout(new GridBagLayout());

			Map param = new Hashtable();
			param.put("title", PivotDetailTableUtils.translate(PivotDetailTableUtils.PIVOTDETAILTABLE_INFORMATION_TITLE,
					this.resources, null));
			param.put("expand", "yes");
			final Column cColumn = new Column(param);

			param.put("attr", "rows");
			param.put("text", "");
			param.put("align", "left");
			param.put("dim", "text");
			param.put("valign", "center");
			this.rows = new Label(param);
			this.rows.setResourceBundle(this.resources);

			param = new Hashtable();
			param.put("attr", "column");
			param.put("text", "");
			param.put("align", "left");
			param.put("dim", "text");
			param.put("valign", "center");
			this.column = new Label(param);
			this.column.setResourceBundle(this.resources);

			cColumn.add(this.rows, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 10, 2, 2), 0, 0));
			cColumn.add(this.column, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 10, 2, 2), 0, 0));

			pTitle.add(cColumn, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
			return pTitle;
		}

		protected JPanel configureCloseButton() {
			final JPanel pClose = new JPanel();
			pClose.setLayout(new GridBagLayout());

			final Map param = new Hashtable();
			param.put("key", "close");
			param.put("text", PivotDetailTableUtils.translate("close", this.resources, null));
			final Button closeB = new Button(param);
			closeB.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Window ancestor = SwingUtilities.getWindowAncestor((Component) e.getSource());
					if (ancestor instanceof Frame) {
						ancestor.setVisible(false);
					} else if (ancestor instanceof Dialog) {
						((Dialog) ancestor).setVisible(false);
					}
				}
			});

			pClose.add(closeB, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
			return pClose;
		}

		protected void updateTable() {
			this.table.setValue(this.createEntityResult(this.model));
		}

		protected EntityResult createEntityResult(final TableModel tableModel) {
			EntityResult eR = new EntityResultMapImpl();

			if (tableModel != null) {
				final List names = new Vector();
				if (this.visibleCols != null) {
					names.addAll(this.visibleCols);
				} else {
					for (int i = 0; i < tableModel.getColumnCount(); i++) {
						names.add(tableModel.getColumnName(i));
					}
				}
				eR = new EntityResultMapImpl(names);

				for (int i = 0; i < tableModel.getRowCount(); i++) {
					final Map record = new Hashtable();
					for (int j = 0; j < names.size(); j++) {
						final String currentColName = (String) names.get(j);
						final int colIndex = PivotDetailPanel.columnIndex(tableModel, currentColName);
						if ((colIndex != -1) && (tableModel.getValueAt(i, colIndex) != null)) {
							record.put(currentColName, tableModel.getValueAt(i, colIndex));
						}
					}
					eR.addRecord(record);
				}
			}
			return eR;
		}

		protected static int columnIndex(final TableModel model, final String col) {
			if (col == null) {
				return -1;
			}
			for (int i = 0; i < model.getColumnCount(); i++) {
				if (model.getColumnName(i).equals(col)) {
					return i;
				}
			}
			return -1;
		}

		protected void updateTitle(final Map information) {
			if (information != null) {
				final Object oRowList = information.get("rows");
				if (oRowList instanceof List) {
					final String text = this.getTranslation(PivotDetailTableUtils.PIVOTDETAILTABLE_GROUPINGROWS_TITLE,
							(List) oRowList);
					this.rows.setText(text);
				} else {
					this.rows.setText(null);
				}

				final Object oColumnList = information.get("column");
				if (oColumnList instanceof List) {
					final String text = this.getTranslation(PivotDetailTableUtils.PIVOTDETAILTABLE_GROUPINGCOLUMNS_TITLE,
							(List) oColumnList);
					this.column.setText(text);
				} else {
					this.column.setText(null);
				}
			}
		}

		protected String getTranslation(final String title, final List listValues) {
			final StringBuilder sb = new StringBuilder();
			if (title != null) {
				sb.append(PivotDetailTableUtils.translate(title, this.resources, null));
				sb.append(" : ");
			}
			final Object[] args = new Object[listValues.size() * 2];
			int index = 0;
			for (int i = 0; i < listValues.size(); i++) {
				sb.append("{").append(index).append("}");
				sb.append(" = ");
				sb.append("{").append(index + 1).append("}");

				final List values = (List) listValues.get(i);
				args[index] = PivotDetailTableUtils.translate((String) values.get(0), this.resources, null);
				args[index + 1] = values.get(1);

				if (i < (listValues.size() - 1)) {
					sb.append("   ");
				}
				index = index + 2;
			}

			return MessageFormat.format(sb.toString(), args);
		}

		public void setResourceBundle(final ResourceBundle res) {
			this.resources = res;
			this.table.setResourceBundle(res);
		}

	}

	public static EJDialog createPivotDetailDialog(final Window w, final TableModel model, final Map parameters,
			final ResourceBundle res) {
		PivotDetailDialog pdd = null;
		if (w instanceof Dialog) {
			pdd = new PivotDetailDialog((Dialog) w, model, parameters, res);
		} else {
			pdd = new PivotDetailDialog((Frame) w, model, parameters, res);
		}
		pdd.pack();
		return pdd;
	}

	public static String translate(final String text, final ResourceBundle res, final Object[] args) {
		if (res == null) {
			return new String(text);
		} else {
			try {
				String trad = res.getString(text);
				if (trad != null) {
					if (trad.startsWith("<HTML>") || trad.startsWith("<html>") || trad.startsWith("<Html>")) {
						final int index = trad.indexOf("<DEFAULTBASE>");
						if (index >= 0) {
							final URL url = PivotTableUtils.class.getClassLoader().getResource("./");
							if (url != null) {
								trad = trad.substring(0, index) + "<BASE href=\"" + url.toString() + "\">"
										+ trad.substring(index + 13);
								PivotDetailTableUtils.logger.debug("Establecida BASE : " + url.toString());
							}
						}
					}
				}
				// Arguments
				if (args != null) {
					final String transArgs = MessageFormat.format(trad, args);
					return transArgs;
				} else {
					return trad;
				}
			} catch (final Exception e) {
				PivotDetailTableUtils.logger.error(e.getMessage(), e);
				return new String(text);
			}
		}
	}

}
