package com.ontimize.db.query;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.query.store.FileQueryStore;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.query.QueryExpression;
import com.ontimize.jee.common.db.query.store.QueryStore;

public class QueryExpressionSelection extends EJDialog {

	private static final Logger logger = LoggerFactory.getLogger(QueryExpressionSelection.class);

	public static final String EXPRESSION = "expr";

	public static final String NAME = "name";

	public static final String DEFINE = "define";

	protected String entity = null;

	protected ResourceBundle bundle = null;

	protected JList list = null;

	protected QueryStore store = null;

	protected JTextField textField = null;

	protected JButton saveButton = null;

	protected JButton loadButton = null;

	protected JButton deleteButton = null;

	protected JButton cancelButton = null;

	protected boolean listOnly = false;

	protected boolean saveOnly = false;

	protected QueryExpression query = null;

	protected JButton defineButton = null;

	protected boolean definePressed = false;

	public boolean isDefinePressed() {
		return this.definePressed;
	}

	public QueryExpressionSelection(final Frame f, final QueryExpression query, final String entity, final ResourceBundle bundle,
			final boolean listOnly, final boolean saveOnly) {
		super(f, ApplicationManager.getTranslation("QueryExpressionSelection", bundle), true);
		this.definePressed = false;
		this.entity = entity;
		this.listOnly = listOnly;
		this.saveOnly = saveOnly;
		this.bundle = bundle;
		this.query = query;
		this.init();
	}

	public QueryExpressionSelection(final Dialog d, final QueryExpression query, final String entity, final ResourceBundle bundle,
			final boolean listOnly, final boolean saveOnly) {
		super(d, ApplicationManager.getTranslation("QueryExpressionSelection", bundle), true);
		this.definePressed = false;
		this.entity = entity;
		this.listOnly = listOnly;
		this.bundle = bundle;
		this.saveOnly = saveOnly;
		this.query = query;
		this.init();
	}

	public void init() {
		this.store = new FileQueryStore();
		final String[] lis = this.store.list(this.entity);
		final DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < lis.length; i++) {
			if (!lis[i].equals("")) {
				model.addElement(lis[i]);
			}
		}
		this.list = new JList(model);
		this.list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {

				if (QueryExpressionSelection.this.list.getSelectedIndex() >= 0) {
					QueryExpressionSelection.this.textField
					.setText((String) QueryExpressionSelection.this.list.getSelectedValue());
				}

				if (e.getClickCount() == 2) {
					QueryExpressionSelection.this.query = QueryExpressionSelection.this.store.get(
							QueryExpressionSelection.this.textField.getText(),
							QueryExpressionSelection.this.entity);
					QueryExpressionSelection.this.setVisible(false);
				}
			}
		});

		this.list.setFixedCellHeight(20);
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setVisibleRowCount(5);

		this.textField = new JTextField();
		this.textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					QueryExpressionSelection.this.saveButton.setEnabled(false);
					QueryExpressionSelection.this.loadButton.setEnabled(false);
					QueryExpressionSelection.this.deleteButton.setEnabled(false);
				} else {
					if (((DefaultListModel) QueryExpressionSelection.this.list.getModel())
							.contains(QueryExpressionSelection.this.textField.getText())) {
						if (!QueryExpressionSelection.this.saveOnly) {
							QueryExpressionSelection.this.loadButton.setEnabled(true);
						}
						QueryExpressionSelection.this.deleteButton.setEnabled(true);
					} else {
						QueryExpressionSelection.this.loadButton.setEnabled(false);
						QueryExpressionSelection.this.deleteButton.setEnabled(false);
					}
					QueryExpressionSelection.this.saveButton.setEnabled(true);
				}
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					QueryExpressionSelection.this.saveButton.setEnabled(false);
					QueryExpressionSelection.this.loadButton.setEnabled(false);
					QueryExpressionSelection.this.deleteButton.setEnabled(false);
				} else {
					if (((DefaultListModel) QueryExpressionSelection.this.list.getModel())
							.contains(QueryExpressionSelection.this.textField.getText())) {
						if (!QueryExpressionSelection.this.saveOnly) {
							QueryExpressionSelection.this.loadButton.setEnabled(true);
						}
						QueryExpressionSelection.this.deleteButton.setEnabled(true);
					} else {
						QueryExpressionSelection.this.loadButton.setEnabled(false);
						QueryExpressionSelection.this.deleteButton.setEnabled(false);
					}
					QueryExpressionSelection.this.saveButton.setEnabled(true);
				}
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				if (e.getDocument().getLength() == 0) {
					QueryExpressionSelection.this.saveButton.setEnabled(false);
					QueryExpressionSelection.this.loadButton.setEnabled(false);
					QueryExpressionSelection.this.deleteButton.setEnabled(false);
				} else {
					if (((DefaultListModel) QueryExpressionSelection.this.list.getModel())
							.contains(QueryExpressionSelection.this.textField.getText())) {
						if (!QueryExpressionSelection.this.saveOnly) {
							QueryExpressionSelection.this.loadButton.setEnabled(true);
						}
						QueryExpressionSelection.this.deleteButton.setEnabled(true);
					} else {
						QueryExpressionSelection.this.loadButton.setEnabled(false);
						QueryExpressionSelection.this.deleteButton.setEnabled(false);
					}
					QueryExpressionSelection.this.saveButton.setEnabled(true);
				}
			}
		});

		class EAction extends AbstractAction {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					QueryExpressionSelection.this.defineButton.doClick(1);
				} catch (final Exception ex) {
					QueryExpressionSelection.logger.error(null, ex);
				}
			}

		}

		this.setAction(KeyEvent.VK_ENTER, 0, new EAction(), "Aceptar Filtro");

		this.getContentPane().setLayout(new GridBagLayout());
		int i = 0;

		if (this.listOnly) {
			final JLabel e = new JLabel();
			e.setPreferredSize(new Dimension(90, 120));
			e.setText(ApplicationManager.getTranslation("QueryExpressionSelectionElijaUna", this.bundle));
			this.getContentPane()
			.add(e, new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(2, 2, 2, 2), 0, 0));
			i++;
		}

		this.getContentPane()
		.add(this.textField, new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		i++;
		this.getContentPane()
		.add(new JScrollPane(this.list),
				new GridBagConstraints(0, i, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(2, 2, 2, 2), 0, 0));
		i++;
		this.getContentPane()
		.add(this.getButtonPanel(), new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		this.pack();
		ApplicationManager.center(this);
	}

	protected JPanel getButtonPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		this.saveButton = new JButton(
				ApplicationManager.getTranslation("QueryExpressionSelectionGuardar", this.bundle));
		this.saveButton.setIcon(ImageManager.getIcon(ImageManager.SAVE_FILE));

		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((QueryExpressionSelection.this.textField.getText() == null)
						|| (QueryExpressionSelection.this.textField.getText().length() == 0)) {
					return;
				}
				QueryExpressionSelection.this.store.addQuery(QueryExpressionSelection.this.textField.getText(),
						QueryExpressionSelection.this.query);
				QueryExpressionSelection.this.setVisible(false);
			}
		});
		this.saveButton.setEnabled(false);

		this.loadButton = new JButton(ApplicationManager.getTranslation("QueryExpressionSelectionCargar", this.bundle));
		this.loadButton.setIcon(ImageManager.getIcon(ImageManager.OPEN_QUERY));

		this.loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((QueryExpressionSelection.this.textField.getText() == null)
						|| (QueryExpressionSelection.this.textField.getText().length() == 0)) {
					return;
				}
				QueryExpressionSelection.this.query = QueryExpressionSelection.this.store.get(
						QueryExpressionSelection.this.textField.getText(),
						QueryExpressionSelection.this.entity);
				QueryExpressionSelection.this.setVisible(false);
			}
		});
		this.loadButton.setEnabled(false);
		this.deleteButton = new JButton(
				ApplicationManager.getTranslation("QueryExpressionSelectionBorrar", this.bundle));
		this.deleteButton.setIcon(ImageManager.getIcon(ImageManager.RECYCLER));

		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((QueryExpressionSelection.this.textField.getText() == null)
						|| (QueryExpressionSelection.this.textField.getText().length() == 0)) {
					return;
				}
				if (((DefaultListModel) QueryExpressionSelection.this.list.getModel())
						.contains(QueryExpressionSelection.this.textField.getText())) {
					QueryExpressionSelection.this.store.removeQuery(QueryExpressionSelection.this.textField.getText(),
							QueryExpressionSelection.this.entity);
					((DefaultListModel) QueryExpressionSelection.this.list.getModel())
					.removeElement(QueryExpressionSelection.this.textField.getText());
					QueryExpressionSelection.this.textField.setText("");
				}
			}
		});
		this.deleteButton.setEnabled(false);

		this.defineButton = new JButton(
				ApplicationManager.getTranslation("QueryExpressionSelectionDefinir", this.bundle));
		this.defineButton.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_EDIT));
		this.defineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (QueryExpressionSelection.this.listOnly) {
					QueryExpressionSelection.this.query = QueryExpressionSelection.this.store.get(
							QueryExpressionSelection.this.textField.getText(),
							QueryExpressionSelection.this.entity);
					QueryExpressionSelection.this.setVisible(false);
					QueryExpressionSelection.this.definePressed = true;
				}
			}
		});

		this.cancelButton = new JButton(
				ApplicationManager.getTranslation("QueryExpressionSelectionCancelar", this.bundle));
		this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (QueryExpressionSelection.this.listOnly) {
					QueryExpressionSelection.this.query = null;
					QueryExpressionSelection.this.setVisible(false);
				}
			}
		});

		if (this.listOnly) {
			this.defineButton.setEnabled(true);
			this.cancelButton.setEnabled(true);
			panel.add(this.defineButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			panel.add(this.loadButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			panel.add(this.deleteButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			panel.add(this.cancelButton, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		} else {
			panel.add(this.saveButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			panel.add(this.loadButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
			panel.add(this.deleteButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		}

		return panel;
	}

	public static Map showQueryExpressionSelection(final Component c, final QueryExpression query, final String entityName,
			final ResourceBundle bundle) {
		return QueryExpressionSelection.showQueryExpressionSelection(c, query, entityName, bundle, false, false);
	}

	public static Map showQueryExpressionSelection(final Component c, final QueryExpression query, final String entityName,
			final ResourceBundle bundle, final boolean loadOnly, final boolean saveOnly) {
		final Window w = SwingUtilities.getWindowAncestor(c);
		QueryExpressionSelection querySelection = null;

		if (w instanceof Dialog) {
			querySelection = new QueryExpressionSelection((Dialog) w, query, entityName, bundle, loadOnly, saveOnly);
		} else if (w instanceof Frame) {
			querySelection = new QueryExpressionSelection((Frame) w, query, entityName, bundle, loadOnly, saveOnly);
		}
		if (querySelection != null) {
			querySelection.pack();
			querySelection.setVisible(true);
			final Map h = new Hashtable();
			h.put(QueryExpressionSelection.DEFINE, new Boolean(querySelection.definePressed));
			if (!querySelection.textField.getText().equals("")) {
				h.put(QueryExpressionSelection.NAME, querySelection.textField.getText());
				if (querySelection.query != null) {
					h.put(QueryExpressionSelection.EXPRESSION, querySelection.query);
				}
			}
			return h;
		}
		return null;
	}

}
