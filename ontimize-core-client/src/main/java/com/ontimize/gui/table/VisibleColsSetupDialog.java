package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.RolloverButton;
import com.ontimize.util.swing.selectablelist.SelectableItemRenderer;

/**
 * Class that defines the dialog that allows to configure the table visible columns.
 */
public class VisibleColsSetupDialog extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(VisibleColsSetupDialog.class);

	private static String INFO_KEY = "table.choose_visible_columns";

	private static String SELECTION_KEY = "table.select_all_columns";

	private static String DESELECTION_KEY = "table.unselect_all_columns";

	private static String REVERSE_KEY = "table.reverse_selection";

	public static final String DISPLACE_BEGIN_es_ES = "table.move_to_start";

	public static final String DISPLACE_END_es_ES = "table.move_to_end";

	public static final String DISPLACE_UP_es_ES = "table.move_to_up";

	public static final String DISPLACE_DOWN_es_ES = "table.move_to_down";

	public static final String SAVE_POSITION_AND_WIDTH = "table.save_column_position_and_width";

	public static final int LIST_MOUSE_X_MAX = 20;

	protected JList colsList = null;

	protected JButton buttonOK = new JButton("application.accept");

	protected JButton buttonCancel = new JButton("application.cancel");

	protected JButton buttonSaveColsPositionWidth = new RolloverButton();

	protected JButton buttonSelection = new RolloverButton();

	protected JButton buttonDeselect = new RolloverButton();

	protected JButton buttonReplace = new RolloverButton();

	protected JButton buttonDefault = new RolloverButton();

	protected JButton buttonAll = new JButton();

	protected JButton buttonNothing = new JButton();

	protected JButton up = new RolloverButton(ImageManager.getIcon(ImageManager.PREVIOUS_2_VERTICAL));

	protected JButton down = new RolloverButton(ImageManager.getIcon(ImageManager.NEXT_2_VERTICAL));

	protected JButton allDown = new RolloverButton(ImageManager.getIcon(ImageManager.END_2_VERTICAL));

	protected JButton allUp = new RolloverButton(ImageManager.getIcon(ImageManager.START_2_VERTICAL));

	protected Table table = null;

	protected List initVisibleCols = null;

	public VisibleColsSetupDialog(final Dialog d, final Table t) {
		super(d, VisibleColsSetupDialog.INFO_KEY, true);
		this.init(t);
	}

	public VisibleColsSetupDialog(final Frame d, final Table t) {
		super(d, VisibleColsSetupDialog.INFO_KEY, true);
		this.init(t);
	}

	protected void disableAll() {
		this.allUp.setEnabled(false);
		this.up.setEnabled(false);
		this.allDown.setEnabled(false);
		this.down.setEnabled(false);
	}

	protected void checkSelectPosition(final int index) {
		if ((index > 0) && (index < (((DefaultListModel) this.colsList.getModel()).size() - 1))) {
			this.allUp.setEnabled(true);
			this.up.setEnabled(true);
			this.allDown.setEnabled(true);
			this.down.setEnabled(true);
		} else if (index == 0) {
			this.allUp.setEnabled(false);
			this.up.setEnabled(false);
			this.allDown.setEnabled(true);
			this.down.setEnabled(true);
		} else if (index == (((DefaultListModel) this.colsList.getModel()).size() - 1)) {
			this.allUp.setEnabled(true);
			this.up.setEnabled(true);
			this.allDown.setEnabled(false);
			this.down.setEnabled(false);
		} else {
			this.allUp.setEnabled(false);
			this.up.setEnabled(false);
			this.allDown.setEnabled(false);
			this.down.setEnabled(false);
		}

		if (index == table.getBlockedColumnIndex() - 1) {
			this.down.setEnabled(false);
		}

		if (index < table.getBlockedColumnIndex()) {
			this.allDown.setEnabled(false);
		}

		if (table.getBlockedColumnIndex() > 0) {
			this.allUp.setEnabled(false);
		}

		if (index == table.getBlockedColumnIndex()) {
			this.up.setEnabled(false);
		}

	}

	protected void init(final Table t) {
		this.table = t;

		this.colsList = new JList();

		this.colsList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.getModifiers() == InputEvent.META_MASK) {
					return;
				}
				try {
					if (VisibleColsSetupDialog.this.colsList.getSelectedIndices().length != 1) {
						VisibleColsSetupDialog.this.disableAll();
						return;
					}
					final int index = VisibleColsSetupDialog.this.colsList.locationToIndex(e.getPoint());
					VisibleColsSetupDialog.this.checkSelectPosition(index);
					if (e.getX() > VisibleColsSetupDialog.LIST_MOUSE_X_MAX) {
						return;
					}
					final SelectableItemRenderer.SelectableItem it = (SelectableItemRenderer.SelectableItem) VisibleColsSetupDialog.this.colsList
							.getModel()
							.getElementAt(index);
					it.setSelected(!it.isSelected());
					// If more than one are selected then unique series are not
					// allowed
					int selected = 0;
					final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
					for (int i = 0; i < model.getSize(); i++) {
						if (((SelectableItemRenderer.SelectableItem) model.getElementAt(i)).isSelected()) {
							selected++;
						}
					}
					final Rectangle rect = VisibleColsSetupDialog.this.colsList.getCellBounds(index, index);
					VisibleColsSetupDialog.this.colsList.repaint(rect);
				} catch (final Exception ex) {
					VisibleColsSetupDialog.logger.trace(null, ex);
				}
			}
		});

		this.colsList.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.getModifiers() == InputEvent.META_MASK) {
					return;
				}
				if ((e.getKeyCode() == 40) || (e.getKeyCode() == 38)) {
					// up and down
					if (VisibleColsSetupDialog.this.colsList.getSelectedIndices().length != 1) {
						VisibleColsSetupDialog.this.disableAll();
					} else {
						VisibleColsSetupDialog.this
						.checkSelectPosition(VisibleColsSetupDialog.this.colsList.getSelectedIndex());
					}
				}
			}

			@Override
			public void keyTyped(final KeyEvent e) {
			}
		});

		final JPanel jButtonsPanel = new JPanel();
		jButtonsPanel.add(this.buttonOK);
		jButtonsPanel.add(this.buttonCancel);
		final JPanel panelTop = new JPanel(new GridLayout(0, 1));

		this.getContentPane().add(panelTop, BorderLayout.NORTH);
		this.getContentPane().add(jButtonsPanel, BorderLayout.SOUTH);

		final JPanel aux = new JPanel(new GridBagLayout());

		aux.add(new JScrollPane(this.colsList),
				new GridBagConstraints(0, 1, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		aux.add(this.getSelectionButtonsPanel(), new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonSaveColsPositionWidth.setMargin(new Insets(0, 0, 0, 0));
		aux.add(this.buttonSaveColsPositionWidth,
				new GridBagConstraints(1, 2, 1, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.SOUTH,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(aux);
		this.colsList.setCellRenderer(new com.ontimize.util.swing.selectablelist.SelectableItemRenderer());
		// Up and dow buttons listeners
		this.buttonOK.setIcon(ImageManager.getIcon(ImageManager.OK));
		this.buttonCancel.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
		this.buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				VisibleColsSetupDialog.this.applyChanges();
				VisibleColsSetupDialog.this.setVisible(false);
			}
		});

		this.buttonSaveColsPositionWidth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				VisibleColsSetupDialog.this.applyChanges();
				VisibleColsSetupDialog.this.table.saveColumnsPositionAndWith();
			}
		});

		this.buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Save
				VisibleColsSetupDialog.this.setVisible(false);
			}
		});
		this.buttonSaveColsPositionWidth.setIcon(ImageManager.getIcon(ImageManager.SAVE_DISC));
	}

	protected void applyChanges() {
		// all columns visible and not visible ones
		List vOriginalPosition = this.table.getRealColumns();

		// Visible columns
		final List oOriginalVisible = this.table.getCurrentColumns();

		// Columns not visible than are visible now
		final List vHideColumns = new Vector();

		// New visible columns
		final List vFinalVisible = new Vector();

		for (int i = 0; i < this.colsList.getModel().getSize(); i++) {
			if (this.colsList.getModel().getElementAt(i) instanceof SelectableItemRenderer.SelectableItem) {
				final String col = ((SelectableItemRenderer.SelectableItem) this.colsList.getModel().getElementAt(i))
						.getText();
				if (((SelectableItemRenderer.SelectableItem) this.colsList.getModel().getElementAt(i)).isSelected()) {
					vFinalVisible.add(col);
					if (!oOriginalVisible.contains(col)) {
						// visible column that is not in original visible ones
						vHideColumns.add(col);
					}

				}
				final int originalIndex = vOriginalPosition.indexOf(col);
				if (originalIndex != i) {
					// Move the columns
					// Change the column in originalIndex with column in index i
					if (originalIndex < this.table.getBlockedColumnIndex()) {
						this.table.getBlockedTable()
						.moveColumn(this.table.getColumnIndex(col),
										this.table.getColumnIndex((String) vOriginalPosition.get(i)));
					} else {
						this.table.getJTable()
						.moveColumn(this.table.getColumnIndex(col),
										this.table.getColumnIndex((String) vOriginalPosition.get(i)));
					}

					vOriginalPosition = this.table.getRealColumns();
				}
			}
		}
		if (vFinalVisible.isEmpty()) {
			return;
		}

		// For the new columns set the preferred width
		this.table.setVisibleColumns(vFinalVisible, false);

		// Save the preference
		final String cols = ApplicationManager.vectorToStringSeparateBySemicolon(vFinalVisible);
		final String sPreferenceKey = this.table.getVisibleColumnsPreferenceKey();
		if (this.table.parentForm != null) {
			final Application ap = this.table.parentForm.getFormManager().getApplication();
			if (ap.getPreferences() != null) {
				ap.getPreferences().setPreference(this.table.getUser(), sPreferenceKey, cols);
				ap.getPreferences().savePreferences();
			}
		}

		// If some of the new visible columns has a different height or some of
		// the hidden one it is needed reevaluate the row height
		this.table.evaluatePreferredRowsHeight();

		for (int i = 0; i < vHideColumns.size(); i++) {
			final Object col = vHideColumns.get(i);
			this.table.fitColumnSize(this.table.getColumnIndex((String) col));
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add("table.choose_visible_columns");
		return v;
	}

	public void setColumn() {
		final List v = new Vector();

		final List act = this.table.getRealColumns();
		final List visible = this.table.getVisibleColumns();
		for (int i = act.size() - 1; i >= 0; i--) {
			if (!this.table.checkColumnTablePermission(act.get(i), "visible")) {
				act.remove(i);
			}
		}
		ResourceBundle resourceBundle = null;
		if (this.table.translateHeader) {
			resourceBundle = this.table.getResourceBundle();
		}

		for (int i = 0; i < act.size(); i++) {
			final SelectableItemRenderer.SelectableItem item = new SelectableItemRenderer.SelectableItem((String) act.get(i),
					resourceBundle);
			if (visible.contains(act.get(i))) {
				item.setSelected(true);
			} else {
				item.setSelected(false);
			}

			v.add(item);
		}

		final DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < v.size(); i++) {
			model.addElement(v.get(i));
		}
		this.colsList.setModel(model);
	}

	protected JPanel getSelectionButtonsPanel() {
		final JPanel aux = new JPanel(new GridBagLayout());
		aux.add(this.allUp, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.allUp.setMargin(new Insets(0, 0, 0, 0));
		this.allUp.setEnabled(false);
		aux.add(this.up, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.up.setMargin(new Insets(0, 0, 0, 0));
		this.up.setEnabled(false);
		aux.add(this.down, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.down.setMargin(new Insets(0, 0, 0, 0));
		this.down.setEnabled(false);
		aux.add(this.allDown, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.allDown.setMargin(new Insets(0, 0, 0, 0));
		this.allDown.setEnabled(false);

		aux.add(this.buttonSelection, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonSelection.setIcon(ImageManager.getIcon(ImageManager.SELECTION_UP));
		this.buttonSelection.setMargin(new Insets(0, 0, 0, 0));
		aux.add(this.buttonDeselect, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonDeselect.setIcon(ImageManager.getIcon(ImageManager.SELECTION_DOWN));
		this.buttonDeselect.setMargin(new Insets(0, 0, 0, 0));
		aux.add(this.buttonReplace, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonReplace.setIcon(ImageManager.getIcon(ImageManager.SELECTION_REPLACE));
		this.buttonReplace.setMargin(new Insets(0, 0, 0, 0));

		this.allUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object oSelected = VisibleColsSetupDialog.this.colsList.getSelectedValue();
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				if ((model != null) && (model instanceof DefaultListModel)) {
					final DefaultListModel dModel = (DefaultListModel) model;
					dModel.removeElement(oSelected);
					dModel.add(0, oSelected);
					VisibleColsSetupDialog.this.colsList.setSelectedIndex(0);
					VisibleColsSetupDialog.this.checkSelectPosition(0);
				}
			}
		});

		this.up.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int index = VisibleColsSetupDialog.this.colsList.getSelectedIndex();
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				if ((model != null) && (model instanceof DefaultListModel)) {
					final DefaultListModel dModel = (DefaultListModel) model;
					final Object oSelected = dModel.remove(index);
					dModel.add(index - 1, oSelected);
					VisibleColsSetupDialog.this.colsList.setSelectedIndex(index - 1);
					VisibleColsSetupDialog.this.checkSelectPosition(index - 1);
				}
			}
		});

		this.down.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int index = VisibleColsSetupDialog.this.colsList.getSelectedIndex();
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				if ((model != null) && (model instanceof DefaultListModel)) {
					final DefaultListModel dModel = (DefaultListModel) model;
					final Object oSelected = dModel.remove(index);
					dModel.add(index + 1, oSelected);
					VisibleColsSetupDialog.this.colsList.setSelectedIndex(index + 1);
					VisibleColsSetupDialog.this.checkSelectPosition(index + 1);
				}
			}
		});

		this.allDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object oSelected = VisibleColsSetupDialog.this.colsList.getSelectedValue();
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				if ((model != null) && (model instanceof DefaultListModel)) {
					final DefaultListModel dModel = (DefaultListModel) model;
					dModel.removeElement(oSelected);
					dModel.add(dModel.getSize(), oSelected);
					final int index = dModel.getSize() - 1;
					VisibleColsSetupDialog.this.colsList.setSelectedIndex(index);
					VisibleColsSetupDialog.this.checkSelectPosition(index);
				}
			}
		});

		this.buttonSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final Object o = model.getElementAt(i);
					if ((o != null) && (o instanceof SelectableItemRenderer.SelectableItem)) {
						((SelectableItemRenderer.SelectableItem) o).setSelected(true);
					}
				}
				VisibleColsSetupDialog.this.colsList.repaint();
			}
		});

		this.buttonDeselect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final Object o = model.getElementAt(i);
					if ((o != null) && (o instanceof SelectableItemRenderer.SelectableItem)) {
						((SelectableItemRenderer.SelectableItem) o).setSelected(false);
					}
				}
				VisibleColsSetupDialog.this.colsList.repaint();
			}
		});

		this.buttonReplace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final Object o = model.getElementAt(i);
					if ((o != null) && (o instanceof SelectableItemRenderer.SelectableItem)) {
						if (((SelectableItemRenderer.SelectableItem) o).isSelected()) {
							((SelectableItemRenderer.SelectableItem) o).setSelected(false);
						} else {
							((SelectableItemRenderer.SelectableItem) o).setSelected(true);
						}
					}
				}
				VisibleColsSetupDialog.this.colsList.repaint();
			}
		});

		this.buttonDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final List v = VisibleColsSetupDialog.this.table.getOriginallyVisibleColumns();
				final ListModel model = VisibleColsSetupDialog.this.colsList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final Object o = model.getElementAt(i);
					if ((o != null) && (o instanceof SelectableItemRenderer.SelectableItem)) {
						final SelectableItemRenderer.SelectableItem item = (SelectableItemRenderer.SelectableItem) o;
						if (v.contains(item.getText())) {
							item.setSelected(true);
						} else {
							item.setSelected(false);
						}
					}
				}
				VisibleColsSetupDialog.this.colsList.repaint();
			}
		});

		return aux;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.setTitle(
				ApplicationManager.getTranslation(VisibleColsSetupDialog.INFO_KEY, this.table.getResourceBundle()));

		// If the table is dynamic maybe the name of the columns must not be
		// translated
		if ((this.table == null) || this.table.isTranslateHeader()) {
			for (int i = 0; i < this.colsList.getModel().getSize(); i++) {
				if (this.colsList.getModel().getElementAt(i) instanceof SelectableItemRenderer.SelectableItem) {
					((SelectableItemRenderer.SelectableItem) this.colsList.getModel().getElementAt(i))
					.setResourceBundle(this.table.getResourceBundle());
				}
			}
		}

		this.buttonCancel.setText(ApplicationManager.getTranslation("application.cancel", res));
		this.buttonOK.setText(ApplicationManager.getTranslation("application.accept", res));

		this.buttonSelection
		.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.SELECTION_KEY, res));
		this.buttonDeselect
		.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.DESELECTION_KEY, res));
		this.buttonReplace.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.REVERSE_KEY, res));

		this.allUp.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.DISPLACE_BEGIN_es_ES, res));
		this.up.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.DISPLACE_UP_es_ES, res));
		this.down.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.DISPLACE_DOWN_es_ES, res));
		this.allDown.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.DISPLACE_END_es_ES, res));
		this.buttonSaveColsPositionWidth
		.setToolTipText(ApplicationManager.getTranslation(VisibleColsSetupDialog.SAVE_POSITION_AND_WIDTH, res));
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void free() {
		super.free();
		FreeableUtils.freeComponent(colsList);
		FreeableUtils.freeComponent(buttonOK);
		FreeableUtils.freeComponent(buttonCancel);
		FreeableUtils.freeComponent(buttonSaveColsPositionWidth);
		FreeableUtils.freeComponent(buttonSelection);
		FreeableUtils.freeComponent(buttonDeselect);
		FreeableUtils.freeComponent(buttonReplace);
		FreeableUtils.freeComponent(buttonDefault);
		FreeableUtils.freeComponent(buttonAll);
		FreeableUtils.freeComponent(buttonNothing);
		FreeableUtils.freeComponent(up);
		FreeableUtils.freeComponent(down);
		FreeableUtils.freeComponent(allDown);
		FreeableUtils.freeComponent(allUp);
		table = null;
	}

}
