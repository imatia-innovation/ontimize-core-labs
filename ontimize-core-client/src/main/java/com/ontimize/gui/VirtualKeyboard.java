package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;

public class VirtualKeyboard {

	private static final String VIRTUAL_KEYBOARD_TITLE = "VirtualKeyboard";

	private static final String SELECTION_CHARACTER = "SelectCharacter";

	private static final String SYMBOLS = "Symbols";

	private static final String COPY = "Copy";

	private static VirtualKeyboard instance = null;

	private static Map blocksCharacters = new Hashtable();

	private JTextComponent comp = null;

	private static class CharacterTableModel extends AbstractTableModel {

		private List keys = null;

		private final int columns = 15;

		public CharacterTableModel(final List keys) {
			super();
			this.keys = keys;
		}

		public void setLetters(final List keys) {
			this.keys = keys;
			this.fireTableChanged(new TableModelEvent(this));
		}

		@Override
		public Class getColumnClass(final int c) {
			return Character.class;
		}

		@Override
		public int getRowCount() {
			final double d = this.keys.size() / (double) this.columns;
			final int i = this.keys.size() / this.columns;
			if (d > i) {
				return i + 1;
			} else {
				return i;
			}
		}

		@Override
		public int getColumnCount() {
			return this.columns;
		}

		@Override
		public Object getValueAt(final int r, final int c) {
			final int index = (r * this.columns) + c;
			if (index >= this.keys.size()) {
				return null;
			} else {
				return this.keys.get(index);
			}
		}

	}

	private JDialog dialog = null;

	private final JLabel labelInfo = new JLabel();

	private final JTextField textField = new JTextField();

	private final JButton copyButton = new JButton(VirtualKeyboard.COPY);

	private VirtualKeyboard() {
		for (int i = 0; i < Character.MAX_VALUE; i++) {
			final char caracter = (char) i;
			if (Character.isLetterOrDigit(caracter)) {
				final Character.UnicodeBlock block = Character.UnicodeBlock.of(caracter);
				if (!VirtualKeyboard.blocksCharacters.containsKey(block)) {
					final List v = new Vector();
					v.add(new Character(caracter));
					VirtualKeyboard.blocksCharacters.put(block, v);
				} else {
					final List v = (List) VirtualKeyboard.blocksCharacters.get(block);
					v.add(new Character(caracter));
				}
			}
		}
	}

	public void show(final Window window, final JTextComponent c) {
		if (this.dialog == null) {
			this.dialog = this.createDialog(window, c);

		} else {
			if (this.dialog.getOwner() != window) {
				this.dialog = this.createDialog(window, c);
			}
		}
		this.comp = c;
		SwingUtilities.updateComponentTreeUI(this.dialog);
		this.dialog.pack();
		ApplicationManager.center(this.dialog);
		this.dialog.setVisible(true);
	}

	public static String[] getAvaliableFonts() {
		final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String envfonts[] = gEnv.getAvailableFontFamilyNames();
		return envfonts;
	}

	private JDialog createDialog(final Window window, final JTextComponent com) {
		JDialog dialog = null;
		if (window instanceof Frame) {
			dialog = new JDialog((Frame) window, VirtualKeyboard.VIRTUAL_KEYBOARD_TITLE, true);
		} else {
			dialog = new JDialog((Dialog) window, VirtualKeyboard.VIRTUAL_KEYBOARD_TITLE, true);
		}
		this.comp = com;
		final Vector vBlocksNames = new Vector();
		Enumeration enumKeys = Collections.enumeration(VirtualKeyboard.blocksCharacters.keySet());
		while (enumKeys.hasMoreElements()) {
			vBlocksNames.add(enumKeys.nextElement());
		}
		this.labelInfo.setText(VirtualKeyboard.SELECTION_CHARACTER);

		final JComboBox blockCombo = new JComboBox(vBlocksNames);

		this.copyButton.setMargin(new Insets(0, 2, 0, 2));
		this.copyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((VirtualKeyboard.this.textField.getSelectedText() == null)
						|| (VirtualKeyboard.this.textField.getSelectedText().length() == 0)) {
					VirtualKeyboard.this.textField.selectAll();
				}
				VirtualKeyboard.this.textField.copy();
			}
		});

		final JTable jTable = new JTable() {

			@Override
			public Dimension getPreferredSize() {
				final FontMetrics fm = this.getFontMetrics(this.getFont());
				final int height = fm.getHeight();
				final int maxWidth = fm.getMaxAdvance();
				final Dimension d = this.getIntercellSpacing();
				this.setRowHeight(height);
				return new Dimension((this.getColumnCount() * maxWidth) + 20, this.getRowCount() * (height + d.height));
			}

			@Override
			public void updateUI() {
				super.updateUI();
				// Font size
				final Font f = this.getFont();
				int t = f.getSize();
				t = t + 2;
				this.setFont(new FontUIResource(f.deriveFont((float) t)));
				final int maxWidth = this.getFontMetrics(this.getFont()).getMaxAdvance();
				final TableColumnModel tm = this.getColumnModel();
				for (int i = 0; i < this.getColumnCount(); i++) {
					final TableColumn tc = tm.getColumn(i);
					tc.setMinWidth(maxWidth - 2);
					tc.setPreferredWidth(maxWidth);
				}
				this.revalidate();
			}
		};
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getTableHeader().setResizingAllowed(false);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (jTable.getDefaultRenderer(Character.class) instanceof JLabel) {
			((JLabel) jTable.getDefaultRenderer(Character.class)).setHorizontalAlignment(SwingConstants.CENTER);
		}
		if (!VirtualKeyboard.blocksCharacters.isEmpty()) {
			enumKeys = Collections.enumeration(VirtualKeyboard.blocksCharacters.keySet());
			final Object oKey = enumKeys.nextElement();
			final List v = (List) VirtualKeyboard.blocksCharacters.get(oKey);
			final CharacterTableModel m = new CharacterTableModel(v);
			jTable.setModel(m);
		}
		jTable.setRowSelectionAllowed(false);
		jTable.setColumnSelectionAllowed(false);
		jTable.setCellSelectionEnabled(true);
		final JPanel comboPanel = new JPanel(new BorderLayout());
		comboPanel.setBorder(BorderFactory.createEtchedBorder());
		comboPanel.add(this.labelInfo, BorderLayout.NORTH);
		final JPanel panelTextField = new JPanel(new GridBagLayout());
		panelTextField.add(this.textField, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		panelTextField.add(this.copyButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
		comboPanel.add(panelTextField, BorderLayout.SOUTH);
		comboPanel.add(blockCombo);
		dialog.getContentPane().add(comboPanel, BorderLayout.NORTH);
		final JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createTitledBorder(VirtualKeyboard.SYMBOLS));
		tablePanel.add(jTable);
		final JScrollPane scroll = new JScrollPane(tablePanel);
		dialog.getContentPane().add(scroll);
		blockCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				((CharacterTableModel) jTable.getModel())
				.setLetters((List) VirtualKeyboard.blocksCharacters.get(blockCombo.getSelectedItem()));
				jTable.revalidate();
			}
		});

		jTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 1) {
					final int f = jTable.getSelectedRow();
					final int c = jTable.getSelectedColumn();
					final Object oValue = jTable.getValueAt(f, c);
					if (oValue != null) {
						if (VirtualKeyboard.this.comp != null) {
							VirtualKeyboard.this.comp.setText(VirtualKeyboard.this.comp.getText() + oValue);
						}
						VirtualKeyboard.this.textField.setText(VirtualKeyboard.this.textField.getText() + oValue);
					}
				}
			}
		});
		dialog.pack();
		return dialog;
	}

	public static void showDialog(final Window owner, final JTextComponent c) {
		if (VirtualKeyboard.instance == null) {
			VirtualKeyboard.instance = new VirtualKeyboard();
		}
		VirtualKeyboard.instance.show(owner, c);
	}

}
