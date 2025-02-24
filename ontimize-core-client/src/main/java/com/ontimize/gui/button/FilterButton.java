package com.ontimize.gui.button;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.ontimize.db.query.ContainsSQLConditionValuesProcessorHelper;
import com.ontimize.db.query.QueryBuilder;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.IdentifiedAbstractFormComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.ContainsExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.db.query.QueryExpression;

/**
 * <p>
 * Title: Filter Button
 * </p>
 * <p>
 * Description: Component used in advanced queries
 * </p>
 * <p>
 * <p>
 * Company:
 * </p>
 */

public class FilterButton extends IdentifiedAbstractFormComponent {

	private String entity;

	private Button filterButton = null;

	private final JTextField filterText = new JTextField(10);

	private final JLabel label = new JLabel();

	private String[] lCols = null;

	private String[] tCols = null;

	protected QueryExpression queryExpression = null;

	protected JPopupMenu popup = new JPopupMenu();

	protected JButton clearButton = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.RECYCLER));

	class PopupListener extends MouseAdapter {

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger() && (FilterButton.this.queryExpression != null)) {
				FilterButton.this.popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger() && (FilterButton.this.queryExpression != null)) {
				FilterButton.this.popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

	}

	private static String[] initList(String columns) {
		final java.util.List l = new ArrayList();
		if ((columns != null) && (columns.length() != 0)) {
			int i;
			while ((i = columns.indexOf(";")) != -1) {
				l.add(columns.substring(0, i));
				columns = columns.substring(i + 1).trim();
			}
			l.add(columns);
		}

		final String[] s = new String[l.size()];
		for (int i = 0, a = l.size(); i < a; i++) {
			s[i] = new String((String) l.get(i));
		}
		return s;
	}

	@Override
	public void init(final Map h) {
		final JMenuItem menuItem = new JMenuItem(ApplicationManager.getTranslation("filterbutton.delete_filter",
				ApplicationManager.getApplication().getResourceBundle()));
		this.popup.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterButton.this.queryExpression = null;
				FilterButton.this.filterText.setText("");
			}
		});

		this.filterButton = new Button(h);
		this.entity = (String) h.get("entity");
		this.filterText.setEditable(false);
		this.filterText.setBorder(new EtchedBorder());
		this.clearButton.setEnabled(true);
		this.clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterButton.this.queryExpression = null;
				FilterButton.this.filterText.setText("");
			}
		});

		if (h.get("cols") != null) {
			this.lCols = FilterButton.initList((String) h.get("cols"));
		}
		if ((h.get("typecols") != null) && (this.lCols != null)) {
			this.tCols = FilterButton.initList((String) h.get("typecols"));
		}

		if (h.get("attr") != null) {
			this.attribute = h.get("attr");
		}
	}

	/**
	 * Uses the same parameters as a Button and some more. Specific component parameters.
	 * <ul>
	 * <li>attr: component attribute</li>
	 * <li>entity: entity to use in the filter action</li>
	 * <li>icon: filter button icon</li>
	 * <li>text: filter button text</li>
	 * <li>icon: labelvisible 'yes' when the label of the field must be visible</li>
	 * <li>labeltext: label text</li>
	 * <li>filtervisible: to hide the text field with the filter expression</li>
	 * <li>cols: entity columns to use. By default all columns are used</li>
	 * <li>typecols: column types</li>
	 * </ul>
	 *
	 * Operation mode:<BR>
	 * <UL>
	 * <LI>If parameter 'entity' exist:
	 * <UL>
	 * <LI>If not other parameters are specified all columns and types are requested to the entity</LI>
	 * <LI>If <B>only</B> 'cols' parameter exists, then it look for the types in the entity.</LI>
	 * <LI>If both parameters 'cols' and 'typecols' exist then it uses both of them.</LI>
	 * <LI>If only 'typecols' parameter exist then it ignore it.</LI>
	 * </UL>
	 * </LI>
	 * <LI>If 'entity' parameter does not exist:
	 * <UL>
	 * <LI>If <B>only</B> 'cols' parameter exists this field uses this columns and associates the same
	 * <B>String</B> type to all of them.</LI>
	 * <LI>If both parameters 'cols' and 'typecols' exist then uses them.</LI>
	 * </UL>
	 * </LI>
	 * </UL>
	 * <BR>
	 * The only allowed types are: <I>String</I>,<I>Date</I> and <I>Number</I>.<BR>
	 * @param hInit
	 */
	public FilterButton(final Map hInit) {
		super();
		boolean labelVisible = true;
		boolean filterVisible = true;

		this.init(hInit);

		if ((this.lCols != null) && (this.tCols != null)) {
			if (this.lCols.length != this.tCols.length) {
				return;
			}
		}

		this.filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				if ((FilterButton.this.entity == null) && (FilterButton.this.lCols != null)
						&& (FilterButton.this.tCols == null)) {
					FilterButton.this.tCols = new String[FilterButton.this.lCols.length];
					for (int i = 0, a = FilterButton.this.lCols.length; i < a; i++) {
						FilterButton.this.tCols[i] = new String("String");
					}
				}

				QueryExpression aux = FilterButton.this.queryExpression;
				if ((FilterButton.this.queryExpression == null) && (FilterButton.this.lCols != null)) {
					FilterButton.this.queryExpression = new QueryExpression(null, FilterButton.this.entity,
							(java.util.List) null, null);
					FilterButton.this.queryExpression.setCols(FilterButton.this.lCols);
				}

				if (FilterButton.this.entity != null) {

					aux = com.ontimize.db.query.QueryBuilder.showQueryBuilder((Component) e.getSource(),
							FilterButton.this.entity,
							ApplicationManager.getApplication().getResourceBundle(),
							ApplicationManager.getApplication().getReferenceLocator(),
							FilterButton.this.queryExpression,
							true, true, true, FilterButton.this.tCols);

				} else if (FilterButton.this.tCols != null) {
					aux = com.ontimize.db.query.QueryBuilder.showQueryBuilder((Component) e.getSource(),
							ApplicationManager.getApplication().getResourceBundle(),
							FilterButton.this.lCols, FilterButton.this.tCols,
							FilterButton.this.queryExpression.getExpression(), new Vector());
				}

				if (aux != null) {
					FilterButton.this.filterText.setText(ContainsSQLConditionValuesProcessorHelper
							.renderQueryConditionsExpressBundle(aux.getExpression(),
									ApplicationManager.getApplication().getResourceBundle()));
					FilterButton.this.queryExpression = aux;
				}

			}
		});

		final MouseListener popupListener = new PopupListener();
		this.filterText.addMouseListener(popupListener);

		// Initialize the component with the form data
		ImageIcon icon = null;
		if (hInit.get("icon") != null) {
			icon = ImageManager.getIcon((String) hInit.get("icon"));
		} else {
			icon = ImageManager.getIcon(ImageManager.FUNNEL_NEW);
		}

		if (icon != null) {
			this.filterButton.setIcon(icon);
		}
		if (hInit.get("text") != null) {
			this.filterButton.setText(ApplicationManager.getTranslation((String) hInit.get("text"),
					ApplicationManager.getApplication().getResourceBundle()));
		} else {
			this.filterButton.setText(ApplicationManager.getTranslation("filterbutton.new_filter",
					ApplicationManager.getApplication().getResourceBundle()));
		}
		// Initialize the label
		if (hInit.get("labeltext") != null) {
			this.label.setText(ApplicationManager.getTranslation((String) hInit.get("labeltext"),
					ApplicationManager.getApplication().getResourceBundle()));
		} else {
			this.label.setText(ApplicationManager.getTranslation("filterbutton.default_filter_text",
					ApplicationManager.getApplication().getResourceBundle()));
		}

		// Renderer the component
		if (hInit.get("labelvisible") != null) {
			final String aux = (String) hInit.get("labelvisible");
			if (aux.equalsIgnoreCase("no") || aux.equalsIgnoreCase("false")) {
				labelVisible = false;
			}
		}
		if (hInit.get("filtrovisible") != null) {
			final String aux = (String) hInit.get("filtervisible");
			if (aux.equalsIgnoreCase("no") || aux.equalsIgnoreCase("false")) {
				filterVisible = false;
			}
		}

		this.setLayout(new GridBagLayout());
		this.setBorder(new EmptyBorder(2, 2, 2, 2));
		int i = 0;

		if (labelVisible) {
			this.add(this.label, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			i++;
		}

		if (filterVisible) {
			this.add(this.filterText, new GridBagConstraints(i, 0, 1, 1, 0.01, 0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			i++;
		}

		this.add(this.filterButton, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		i++;

		this.add(this.clearButton, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

	}

	/**
	 * Return the filter expression in a standard mode.<BR>
	 * If the expression contains the field QueryBuilder.ANY_COLUMN, this field changes it by an OR
	 * Expression with all String columns.<BR>
	 * It is possible that the visible expression is for example QueryBuilder.ANY_COLUMN contains
	 * 'value' and the result expression is null because no String columns exist.
	 * @return Expression
	 */
	public Expression getFilter() {
		if (this.queryExpression == null) {
			return null;
		}
		Expression x = this.queryExpression.getExpression();
		if (x != null) {
			if (this.tCols == null) {
				x = ContainsExtendedSQLConditionValuesProcessor.queryToStandard(this.queryExpression.getExpression(),
						this.queryExpression.getEntity(),
						this.queryExpression.getCols(), ApplicationManager.getApplication().getReferenceLocator());
			} else {
				x = ContainsExtendedSQLConditionValuesProcessor.queryToStandard(this.queryExpression.getExpression(),
						this.lCols, QueryBuilder.getAllColsType(this.tCols));
			}
		}
		return x;
	}

	/**
	 * Returns the queryExpression. The expression comes from <b> {@link #getFilter()}</b>.
	 * @return QueryExpression
	 */
	public QueryExpression getQueryExpression() {
		final QueryExpression qexp = new QueryExpression(this.getFilter(), this.queryExpression.getEntity(),
				this.queryExpression.getCols(), this.queryExpression.getColumnToQuery());
		return qexp;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.filterButton.setEnabled(enabled);
		this.clearButton.setEnabled(enabled);
		this.filterText.setEnabled(enabled);
	}

}
