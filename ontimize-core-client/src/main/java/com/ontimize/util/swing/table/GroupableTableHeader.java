package com.ontimize.util.swing.table;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ontimize.gui.i18n.Internationalization;

public class GroupableTableHeader extends JTableHeader implements Internationalization {

	protected List columnGroups = null;

	public GroupableTableHeader(final TableColumnModel model) {
		super(model);
		this.setUI(new GroupableTableHeaderUI());
		this.setReorderingAllowed(false);
	}

	public GroupableTableHeader() {
		super();
		this.setUI(new GroupableTableHeaderUI());
		this.setReorderingAllowed(false);
	}

	@Override
	public void setColumnModel(final TableColumnModel columnModel) {
		super.setColumnModel(columnModel);
		final List vColumnGroup = new Vector();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			final TableColumn tc = columnModel.getColumn(i);
			boolean has = false;
			for (int j = 0; j < vColumnGroup.size(); j++) {
				final GroupableColumnGroup cG = (GroupableColumnGroup) vColumnGroup.get(j);
				if (cG.hasTableColumn(tc)) {
					has = true;
					cG.add(tc);
					break;
				}
			}
			if (!has) {
				final GroupableColumnGroup cG = new GroupableColumnGroup(tc.getHeaderValue().toString());
				cG.add(tc);
				vColumnGroup.add(cG);
			}
		}
		this.setColumnGroup(vColumnGroup);
	}

	public void addTableColumn(final TableColumn tc) {
		boolean has = false;
		if (this.columnGroups == null) {
			this.columnGroups = new Vector();
		}

		for (int j = 0; j < this.columnGroups.size(); j++) {
			final Object o = this.columnGroups.get(j);
			if (o instanceof GroupableColumnGroup) {
				final GroupableColumnGroup cG = (GroupableColumnGroup) this.columnGroups.get(j);
				if (cG.hasTableColumn(tc)) {
					has = true;
					cG.add(tc);
					break;
				}
			} else if (o instanceof TableColumn) {
				if (tc.getHeaderValue().equals(((TableColumn) o).getHeaderValue())) {
					has = true;
					this.columnGroups.remove(o);
					final GroupableColumnGroup cG = new GroupableColumnGroup(tc.getHeaderValue().toString());
					cG.add(tc);
					cG.add(o);
					this.columnGroups.add(j, cG);
					break;
				}
			}
		}
		if (!has) {
			this.columnGroups.add(tc);
		}
	}

	public void removeAllColumnGroups() {
		this.columnGroups = new Vector();
	}

	@Override
	public void updateUI() {
		this.setUI(new GroupableTableHeaderUI());
	}

	@Override
	public void setReorderingAllowed(final boolean b) {
		this.reorderingAllowed = false;
	}

	public void addColumnGroup(final GroupableColumnGroup g) {
		if (this.columnGroups == null) {
			this.columnGroups = new Vector();
		}
		this.columnGroups.add(g);
	}

	public void setColumnGroup(final List cg) {
		this.columnGroups = cg;
	}

	public Enumeration getColumnGroups(final TableColumn col) {
		if (this.columnGroups == null) {
			return null;
		}
		final Enumeration e = Collections.enumeration(this.columnGroups);
		while (e.hasMoreElements()) {
			final Object o = e.nextElement();
			GroupableColumnGroup cGroup = null;
			if (!(o instanceof GroupableColumnGroup)) {
				cGroup = new GroupableColumnGroup(((TableColumn) o).getHeaderValue().toString());
			} else {
				cGroup = (GroupableColumnGroup) o;
			}
			final List v_ret = cGroup.getColumnGroups(col, new Vector());
			if (v_ret != null) {
				return Collections.enumeration(v_ret);
			}
		}
		return null;
	}

	public void setColumnMargin() {
		if (this.columnGroups == null) {
			return;
		}
		final int columnMargin = this.getColumnModel().getColumnMargin();
		final Enumeration e = Collections.enumeration(this.columnGroups);
		while (e.hasMoreElements()) {
			final Object o = e.nextElement();
			if (o instanceof GroupableColumnGroup) {
				final GroupableColumnGroup cGroup = (GroupableColumnGroup) o;
				cGroup.setColumnMargin(columnMargin);
			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale locale) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourcebundle) {
		if (this.getUI() instanceof GroupableTableHeaderUI) {
			((GroupableTableHeaderUI) this.getUI()).setResourceBundle(resourcebundle);
		}
	}

}
