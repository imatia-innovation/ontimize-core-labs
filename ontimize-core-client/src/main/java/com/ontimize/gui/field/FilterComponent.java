package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import com.ontimize.db.query.store.FileQueryStore;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.RolloverButton;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.jee.common.db.ContainsExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.db.query.QueryExpression;
import com.ontimize.jee.common.db.query.store.QueryStore;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.util.CollectionTools;

/**
 * This class implements a component for filtering in advanced queries.
 * <p>
 *
 * @author Imatia Innovation
 */
public class FilterComponent extends IdentifiedAbstractFormComponent implements HasPreferenceComponent {

	public static final String EXPRESSION = "Expression";

	protected String entityName;

	protected RolloverButton filterButton = null;

	protected String text = null;

	protected JTextField filterText = new JTextField(10);

	protected JLabel label = new JLabel();

	protected String[] lCols = null;

	protected String[] tCols = null;

	protected QueryExpression queryExpression = null;

	protected JPopupMenu popup = new JPopupMenu();

	protected JPopupMenu pList = null;

	protected RolloverButton arrowButton = null;

	protected JButton clearButton = new RolloverButton(ImageManager.getIcon(ImageManager.RECYCLER));

	protected class PopupListener extends MouseAdapter {

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger() && (FilterComponent.this.queryExpression != null)) {
				FilterComponent.this.popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger() && (FilterComponent.this.queryExpression != null)) {
				FilterComponent.this.popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

	}

	protected static String[] initList(String columns) {
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
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (this.text != null) {
			this.filterButton.setText(ApplicationManager.getTranslation(this.text, res));
		}
	}

	@Override
	public void init(final Map h) {

		final JMenuItem menuItem = new JMenuItem(ApplicationManager.getTranslation("filtercomponent.delete_filter",
				ApplicationManager.getApplication().getResourceBundle()));
		this.popup.add(menuItem);

		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterComponent.this.clearAction();
			}
		});

		this.filterButton = new RolloverButton();

		this.text = (String) h.get("text");
		if (this.text != null) {
			this.filterButton.setText(this.text);
		}

		this.entityName = (String) h.get("entity");
		this.filterText.setEditable(false);
		this.filterText.setBorder(new EtchedBorder());
		this.clearButton.setEnabled(true);
		this.clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterComponent.this.clearAction();
			}
		});

		if (h.get("cols") != null) {
			this.lCols = FilterComponent.initList((String) h.get("cols"));
		}
		if ((h.get("typecols") != null) && (this.lCols != null)) {
			this.tCols = FilterComponent.initList((String) h.get("typecols"));
		}

		if (h.get("attr") != null) {
			this.attribute = h.get("attr");
		}

		this.arrowButton = new RolloverButton();
		final Dimension d = this.arrowButton.getPreferredSize();
		d.width = 12;
		this.arrowButton.setPreferredSize(d);

		this.arrowButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final MouseEvent e) {
				if (FilterComponent.this.pList != null) {
					if (FilterComponent.this.pList.isVisible()) {
						FilterComponent.this.pList.setVisible(false);
						FilterComponent.this.pList = null;
						return;
					}
				}

				final QueryStore qe = new FileQueryStore();
				final String[] sList = qe.list(FilterComponent.this.entityName);
				FilterComponent.this.pList = new JPopupMenu();

				for (int i = 0, a = sList.length; i < a; i++) {
					final JMenuItem it = new JMenuItem(sList[i]);
					it.setText(sList[i]);
					it.setActionCommand(sList[i]);
					it.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							final String n = e.getActionCommand();
							if (n != null) {
								final QueryStore q = new FileQueryStore();
								final QueryExpression qexp = q.get(n, FilterComponent.this.entityName);
								boolean ok = false;
								if (FilterComponent.this.entityName != null) {
									ok = QueryBuilder.showParameterValuesTable(
											ApplicationManager.getApplication().getFrame(),
											ApplicationManager.getApplication().getResourceBundle(),
											qexp.getExpression(), FilterComponent.this.entityName);
								} else {
									ok = QueryBuilder.showParameterValuesTable(
											ApplicationManager.getApplication().getFrame(),
											ApplicationManager.getApplication().getResourceBundle(), qexp);
								}
								if (ok) {
									FilterComponent.this.queryExpression = qexp;
									FilterComponent.this.savePreferences(n);
									FilterComponent.this.filterText.setText(ContainsSQLConditionValuesProcessorHelper
											.renderQueryConditionsExpressBundle(
													FilterComponent.this.queryExpression.getExpression(),
													ApplicationManager.getApplication().getResourceBundle()));
								}

							}
						}
					});
					FilterComponent.this.pList.add(it);
				}
				if (sList.length != 0) {
					FilterComponent.this.pList.show((Component) e.getSource(),
							(int) (0 - FilterComponent.this.filterButton.getPreferredSize().getWidth()),
							FilterComponent.this.arrowButton.getHeight());
				} else {
					FilterComponent.this.pList = null;
				}
			}
		});

	}

	/**
	 * Initializes parameters. Possible <code>XML</code> configurations:<br>
	 * <br>
	 * <B>entity</B> parameter defined:</br>
	 * <ul>
	 * <li><code>cols</code> and <code>typecols</code> parameters no defined: All columns of this entity
	 * are used. Column types are infered from entity columns.
	 * <li><code>cols</code> and <code>typecols</code> parameters defined: Columns and their types are
	 * obtained from these parameters.
	 * <li>Only <code>typecols</code> parameter defined: It is obvied.
	 * </ul>
	 * <p>
	 * <br>
	 * <B>entity</B> parameter <b>no defined</b>:</br>
	 * <ul>
	 * <li><code>cols</code> and <code>typecols</code> parameters defined: Columns and their types are
	 * obtained from these parameters.
	 * <li>Only <code>cols</code> parameter defined: Columns are obtained from this parameter and they
	 * are associated with a <code>String</code> type.
	 * </ul>
	 * @param h the <code>Hashtable</code> with <code>XML</code> parameters:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>yes</td>
	 *        <td>Indicates the component attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The entity where filter is applied.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td><code>ImageManager.FUNNEL_NEW</code></td>
	 *        <td>no</td>
	 *        <td>Path for component icon.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates button text for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labelvisible</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates the visibility condition of component label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>labeltext</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the label text for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>filtervisible</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates the visibility condition of filter text field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>cols</td>
	 *        <td><i>col1;col2;...;coln</td>
	 *        <td></td>
	 *        <td>Only required when <code>entity</code> parameter is not specified.</td>
	 *        <td>Entity columns.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>typecols</td>
	 *        <td><i>typecol1;typecol2;...;typecoln</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Data types for columns. Only <code>String</code>, <code>Date</code> or
	 *        <code>Number</code> are allowed./td>
	 *        </tr>
	 *        </TABLE>
	 */
	public FilterComponent(final Map h) {
		super();
		boolean labelVisible = true;
		boolean visibleFilter = true;

		this.init(h);
		if ((this.lCols != null) && (this.tCols != null)) {
			if (this.lCols.length != this.tCols.length) {
				return;
			}
		}

		this.filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FilterComponent.this.filterAction(e);
			}
		});

		final MouseListener popupListener = new PopupListener();
		this.filterText.addMouseListener(popupListener);

		this.clearButton.setMargin(new Insets(1, 1, 1, 1));
		this.filterButton.setMargin(new Insets(1, 1, 1, 1));

		// Initializes the button with the data form
		ImageIcon icon = null;
		if (h.get("icon") != null) {
			icon = ApplicationManager.getIcon((String) h.get("icon"));
		} else {
			icon = ImageManager.getIcon(ImageManager.FUNNEL_NEW);
		}
		if (icon != null) {
			this.filterButton.setIcon(icon);
		}
		if (h.get("text") != null) {
			this.filterButton.setText(ApplicationManager.getTranslation((String) h.get("text"),
					ApplicationManager.getApplication().getResourceBundle()));
		}

		// Initializes the label
		if (h.get("labeltext") != null) {
			this.label.setText(ApplicationManager.getTranslation((String) h.get("labeltext"),
					ApplicationManager.getApplication().getResourceBundle()));
		}
		this.arrowButton.setIcon(ImageManager.getIcon(ImageManager.POPUP_ARROW));

		// Renderize the component
		if (h.get("labelvisible") != null) {
			final String aux = (String) h.get("labelvisible");
			if (aux.equalsIgnoreCase("no") || aux.equalsIgnoreCase("false")) {
				labelVisible = false;
			}
		}
		if (h.get("filtervisible") != null) {
			final String aux = (String) h.get("filtervisible");
			if (aux.equalsIgnoreCase("no") || aux.equalsIgnoreCase("false")) {
				visibleFilter = false;
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

		if (visibleFilter) {
			this.add(this.filterText, new GridBagConstraints(i, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			i++;
		}

		this.add(this.filterButton, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
		i++;

		this.add(this.arrowButton, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.VERTICAL, new Insets(2, 0, 2, 2), 0, 0));
		i++;

		this.add(this.clearButton, new GridBagConstraints(i, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

	}

	/**
	 * Returns the standard expression. If the expression contains the <code>typecols</code> parameter,
	 * it will be returned a OR operation between <code>String</code> column types.
	 * @return the filter expression
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
	 * Returns the query expression for field. Filter expression is obtained from
	 * <code>getFilter()</code> method.
	 * <p>
	 * @return the query expression
	 */
	public QueryExpression getQueryExpression() {

		final QueryExpression qexp = new QueryExpression(this.getFilter(), this.queryExpression.getEntity(),
				this.queryExpression.getCols(), this.queryExpression.getColumnToQuery());
		return qexp;
	}

	// Save preferences
	public String getDetailFormSizePreferenceKey() {
		if (this.parentForm == null) {
			return null;
		}
		if (this.attribute == null) {
			return null;
		}
		return FilterComponent.EXPRESSION + "_" + this.parentForm.getArchiveName() + "_" + this.attribute;

	}

	@Override
	public void initPreferences(final ApplicationPreferences ap, final java.lang.String user) {
		final String sKey = this.getDetailFormSizePreferenceKey();
		if (sKey == null) {
			return;
		}
		final String sName = ap.getPreference(user, sKey);
		if (sName != null) {
			final QueryStore qe = new FileQueryStore();
			final QueryExpression aux = qe.get(sName, this.entityName);
			if (aux == null) {
				return;
			}
			if (((aux.getEntity() == null) && (this.entityName == null)) || ((aux.getEntity() != null)
					&& (this.entityName != null) && aux.getEntity().equals(this.entityName))) {
				this.queryExpression = aux;
				this.filterText.setText(ContainsSQLConditionValuesProcessorHelper.renderQueryConditionsExpressBundle(
						this.queryExpression.getExpression(),
						ApplicationManager.getApplication().getResourceBundle()));
			}
		}
	}

	public void savePreferences(final String preferenceName) {
		if (this.parentForm != null) {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String s = ((ClientReferenceLocator) this.parentForm.getFormManager().getReferenceLocator()).getUser();
			if (ap.getPreferences() != null) {
				final String sKey = this.getDetailFormSizePreferenceKey();
				if (sKey != null) {
					ap.getPreferences().setPreference(s, sKey, preferenceName);
					ap.getPreferences().savePreferences();
				}
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.arrowButton.setEnabled(enabled);
		this.filterButton.setEnabled(enabled);
		this.clearButton.setEnabled(enabled);
		this.filterText.setEnabled(enabled);
	}

	public String[] getCols() {
		return this.lCols;
	}

	public String[] getTypeOfCols() {
		return this.tCols;
	}

	public void clearAction() {
		this.queryExpression = null;
		this.filterText.setText("");
		this.savePreferences("");
	}

	public void filterAction(final ActionEvent e) {
		if ((this.entityName == null) && (this.lCols != null) && (this.tCols == null)) {
			this.tCols = new String[this.lCols.length];
			for (int i = 0, a = this.lCols.length; i < a; i++) {
				this.tCols[i] = new String("String");
			}
		}
		QueryExpression aux = this.queryExpression;
		final List lastName = new Vector();

		if ((this.queryExpression == null) && (this.lCols != null)) {
			this.queryExpression = new QueryExpression(null, this.entityName, (java.util.List) null, null);
			this.queryExpression.setCols(this.lCols);
		}

		if (this.entityName != null) {
			aux = com.ontimize.db.query.QueryBuilder.showQueryBuilder((Component) e.getSource(), this.entityName,
					ApplicationManager.getApplication().getResourceBundle(),
					ApplicationManager.getApplication().getReferenceLocator(), this.queryExpression, true, true, true,
					this.tCols, lastName);
		} else if (this.tCols != null) {
			aux = com.ontimize.db.query.QueryBuilder.showQueryBuilder((Component) e.getSource(),
					ApplicationManager.getApplication().getResourceBundle(), this.lCols, this.tCols,
					this.queryExpression.getExpression(), lastName);
		}
		if (aux != null) {
			this.filterText.setText(
					ContainsSQLConditionValuesProcessorHelper.renderQueryConditionsExpressBundle(aux.getExpression(),
							ApplicationManager.getApplication().getResourceBundle()));
			this.queryExpression = aux;
			if ((lastName != null) && !lastName.isEmpty()) {
				// Save this name
				this.savePreferences((String) CollectionTools.firstElement(lastName));
			}
		}
	}

}
