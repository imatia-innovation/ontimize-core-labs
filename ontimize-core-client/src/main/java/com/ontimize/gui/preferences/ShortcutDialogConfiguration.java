package com.ontimize.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationMenuBar;
import com.ontimize.gui.Menu;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.locator.ClientReferenceLocator;

public class ShortcutDialogConfiguration extends JDialog implements Internationalization {

    private static String titleKey = "keyboard_configuration";

    private static String actionColumnKey = "Action";

    private static String shortcutColumnKey = "ShortcutKey";

    protected static String usedKeyStrokeKey = "this_combination_is_in_use";

    protected static String notUsedKeyStrokeKey = "this_combination_is_not_in_use";

    protected static String keyStrokeKey = "menubar.press_any_keys";

    protected static String shortcutKeyChangeKey = "menubar.change_shortcut";

    private final JTable table = new JTable();

    private ApplicationMenuBar applicationMenuBar = null;

    private ResourceBundle resources = null;

    private static class Renderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus,
                int row, int column) {
            Object oText = "";
            if (value instanceof JMenuItem) {
                oText = ((JMenuItem) value).getText();
                if (!(value instanceof Menu)) {
                    oText = "    " + oText;
                }
            } else if (value instanceof KeyStroke) {
                oText = ApplicationMenuBar.acceleratorMessageFromKeystroke((KeyStroke) value);
            } else if (value instanceof String) {
                oText = ApplicationManager.getTranslation((String) value);
            } else if (value instanceof ComponentKeyStroke) {
                oText = "    " + ApplicationManager.getTranslation(((ComponentKeyStroke) value).getKeyName());
            }

            Component comp = super.getTableCellRendererComponent(table, oText, selected, false, row, column);
            if ((value instanceof Menu) || (value instanceof String)) {
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
            }
            return comp;
        }

    }

    protected JButton buttonClose = new JButton("close");

    protected JButton buttonChange = new JButton("Change...");

    protected JButton deleteButton = new JButton("delete");

    protected class ChangeDialog extends EJDialog {

        protected JLabel infoLabel = new JLabel("menubar.press_any_keys");

        protected JLabel repetLabel = new JLabel("EstaCombinacionNoEstaEnUso");

        protected JTextField textKeys = new JTextField() {

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int c, boolean press) {
                if (press) {
                    ChangeDialog.this.textKeys.setText(ApplicationMenuBar.acceleratorMessageFromKeystroke(ks));
                    boolean inUse = ChangeDialog.this.used(ks);
                    if (inUse) {
                        ChangeDialog.this.repetLabel
                            .setText(ApplicationManager.getTranslation(ShortcutDialogConfiguration.usedKeyStrokeKey,
                                    ShortcutDialogConfiguration.this.resources));
                        ChangeDialog.this.repetLabel.setForeground(Color.red);
                    } else {
                        ChangeDialog.this.repetLabel.setText(ApplicationManager.getTranslation(
                                ShortcutDialogConfiguration.notUsedKeyStrokeKey,
                                ShortcutDialogConfiguration.this.resources));
                        ChangeDialog.this.repetLabel.setForeground(Color.black);
                    }
                    ChangeDialog.this.ksSel = ks;
                }
                return true;
            }
        };

        protected KeyStroke ksSel = null;

        protected boolean accept = false;

        protected JButton acceptButton = new JButton("application.accept");

        protected JButton cancelButton = new JButton("application.cancel");

        public ChangeDialog(JDialog d) {
            super(d, "ChangeShortcutKey", true);
            this.getContentPane().setLayout(new BorderLayout(10, 10));
            JPanel contentPanel = new JPanel(new BorderLayout(6, 6));
            contentPanel.add(this.textKeys);
            contentPanel.add(this.repetLabel, BorderLayout.SOUTH);
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.add(this.acceptButton);
            buttonsPanel.add(this.cancelButton);
            this.getContentPane().add(this.infoLabel, BorderLayout.NORTH);
            this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
            this.textKeys.setFont(this.textKeys.getFont().deriveFont(Font.BOLD));
            this.getContentPane().add(contentPanel);
            this.addWindowListener(new WindowAdapter() {

                @Override
                public void windowActivated(WindowEvent e) {
                    ChangeDialog.this.textKeys.requestFocus();
                }
            });
            this.acceptButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ChangeDialog.this.accept = true;
                    ChangeDialog.this.setVisible(false);
                }
            });
            this.cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ChangeDialog.this.accept = false;
                    ChangeDialog.this.ksSel = null;
                    ChangeDialog.this.setVisible(false);
                }
            });
        }

        protected boolean used(KeyStroke ks) {
            boolean inUse = false;
            if (ks == null) {
                return inUse;
            }
            int selectedRow = ShortcutDialogConfiguration.this.table.getSelectedRow();

            for (int i = 0; i < ShortcutDialogConfiguration.this.table.getRowCount(); i++) {
                if (i != selectedRow) {
                    Object ks2 = ShortcutDialogConfiguration.this.table.getValueAt(i, 1);
                    if (ks.equals(ks2)) {
                        inUse = true;
                        break;
                    }
                }
            }
            return inUse;
        }

        public KeyStroke show(KeyStroke ks) {
            this.accept = false;
            this.ksSel = null;
            if (ks != null) {
                this.textKeys.setText(ApplicationMenuBar.acceleratorMessageFromKeystroke(ks));
            } else {
                this.textKeys.setText("");
            }
            this.textKeys.requestFocus();
            this.setTitle(ApplicationManager.getTranslation(ShortcutDialogConfiguration.shortcutKeyChangeKey,
                    ShortcutDialogConfiguration.this.resources));
            this.infoLabel.setText(ApplicationManager.getTranslation(ShortcutDialogConfiguration.keyStrokeKey,
                    ShortcutDialogConfiguration.this.resources));
            this.acceptButton.setText(ApplicationManager.getTranslation("application.accept",
                    ShortcutDialogConfiguration.this.resources));
            this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel",
                    ShortcutDialogConfiguration.this.resources));
            boolean inUse = this.used(ks);
            if (inUse) {
                this.repetLabel.setText(ApplicationManager.getTranslation(ShortcutDialogConfiguration.usedKeyStrokeKey,
                        ShortcutDialogConfiguration.this.resources));
                this.repetLabel.setForeground(Color.red);
            } else {
                this.repetLabel.setText(ApplicationManager.getTranslation(
                        ShortcutDialogConfiguration.notUsedKeyStrokeKey, ShortcutDialogConfiguration.this.resources));
                this.repetLabel.setForeground(Color.black);
            }
            this.pack();
            ApplicationManager.center(this);
            this.setVisible(true);
            if (this.accept) {
                return this.ksSel;
            } else {
                return null;
            }
        }

    }

    protected ChangeDialog changeD = null;

    public ShortcutDialogConfiguration(Frame aplic, ApplicationMenuBar bm) {
        super(aplic, ShortcutDialogConfiguration.titleKey, true);
        this.init(bm);
    }

    protected void init(ApplicationMenuBar bm) {
        this.applicationMenuBar = bm;
        // Table and model
        DefaultTableModel model = new DefaultTableModel(new String[] { ShortcutDialogConfiguration.actionColumnKey,
                ShortcutDialogConfiguration.shortcutColumnKey }, 0);

        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.setDefaultRenderer(Object.class, new Renderer());
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = ShortcutDialogConfiguration.this.table.getSelectedRow();
                if (selectedRow >= 0) {
                    Object valueAt = ShortcutDialogConfiguration.this.table.getValueAt(selectedRow, 0);
                    if ((valueAt instanceof JMenuItem)
                            && (ShortcutDialogConfiguration.this.table.getValueAt(selectedRow, 1) != null)) {
                        ShortcutDialogConfiguration.this.deleteButton.setEnabled(true);
                    } else {
                        ShortcutDialogConfiguration.this.deleteButton.setEnabled(false);
                    }
                }
            }
        });

        this.buildModel(model, bm);

        this.table.setModel(model);
        this.table.setDefaultEditor(Object.class, null);
        this.table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
                    int selectedRow = ShortcutDialogConfiguration.this.table.getSelectedRow();
                    if (selectedRow >= 0) {
                        ShortcutDialogConfiguration.this.change(selectedRow);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(this.buttonChange);
        buttonsPanel.add(this.deleteButton);
        buttonsPanel.add(this.buttonClose);
        this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        this.getContentPane().add(new JScrollPane(this.table));
        this.buttonClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ShortcutDialogConfiguration.this.setVisible(false);
            }
        });
        this.buttonChange.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int fs = ShortcutDialogConfiguration.this.table.getSelectedRow();
                if (fs >= 0) {
                    ShortcutDialogConfiguration.this.change(fs);
                }
            }
        });
        this.deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = ShortcutDialogConfiguration.this.table.getSelectedRow();
                if (r >= 0) {
                    Object valueAt = ShortcutDialogConfiguration.this.table.getValueAt(r, 0);
                    if (valueAt instanceof JMenuItem) {
                        ((JMenuItem) valueAt).setAccelerator(null);
                    } else if (valueAt instanceof ComponentKeyStroke) {
                        ComponentKeyStroke comp = (ComponentKeyStroke) valueAt;
                        ShortcutDialogConfiguration.this.changeComponentKeyStroke(comp, null);
                    }
                    ShortcutDialogConfiguration.this.table.setValueAt(null, r, 1);
                }
            }
        });
    }

    @Override
    public void setResourceBundle(ResourceBundle res) {
        this.resources = res;
        this.setTitle(ApplicationManager.getTranslation(ShortcutDialogConfiguration.titleKey, res));
        this.buttonChange.setText(ApplicationManager.getTranslation("Change...", res));
        this.buttonClose.setText(ApplicationManager.getTranslation("close", res));
        this.deleteButton.setText(ApplicationManager.getTranslation("delete", res));

        this.table.getColumnModel()
            .getColumn(0)
            .setHeaderValue(ApplicationManager.getTranslation("application.action", this.resources));
        this.table.getColumnModel()
            .getColumn(1)
            .setHeaderValue(ApplicationManager.getTranslation("Shortcut", this.resources));
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setComponentLocale(Locale l) {
    }

    protected void change(int r) {
        if (this.changeD == null) {
            this.changeD = new ChangeDialog(this);
            this.changeD.pack();
            ApplicationManager.center(this.changeD);
        }

        Object keyNameValue = this.table.getValueAt(r, 0);
        if ((keyNameValue != null) && ((keyNameValue instanceof Menu) || (keyNameValue instanceof String))) {
            return;
        }

        KeyStroke ks = this.changeD.show((KeyStroke) this.table.getValueAt(r, 1));
        if (ks != null) {
            Object valueAt = this.table.getValueAt(r, 0);
            if (valueAt instanceof JMenuItem) {
                ((JMenuItem) valueAt).setAccelerator(ks);
                this.deleteButton.setEnabled(true);
            } else if (valueAt instanceof ComponentKeyStroke) {
                ComponentKeyStroke comp = (ComponentKeyStroke) valueAt;
                this.changeComponentKeyStroke(comp, ks);
            }
            this.table.setValueAt(ks, r, 1);
        }
    }

    protected void changeComponentKeyStroke(ComponentKeyStroke component, KeyStroke newKeyStroke) {
        // Only add the new KeyStroke if this does not exist
        // If it already exist first of all delete the previous one
        InputMap inMap = component.getComponent().getInputMap(component.getInputMapCondition());
        ActionMap actMap = component.getComponent().getActionMap();

        inMap.remove(component.getKeyStroke());
        actMap.remove(component.getKeyName());

        component.setKeyStroke(newKeyStroke);
        if (newKeyStroke != null) {
            inMap.put(newKeyStroke, component.getKeyName());
            actMap.put(component.getKeyName(), component.getAction());
        }

        // Save the preference
        ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
        if (prefs != null) {
            String user = null;
            if (ApplicationManager.getApplication().getReferenceLocator() instanceof ClientReferenceLocator) {
                user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser();
            }
            prefs.setPreference(user,
                    ShortcutDialogConfiguration.getAcceleratorPreferencesKey(component.getGroupName(),
                            component.getKeyName()),
                    ApplicationMenuBar.acceleratorToString(newKeyStroke));
        }
    }

    public static String getAcceleratorPreferencesKey(String group, String key) {
        return com.ontimize.gui.MenuItem.MENU_ACCELERATOR + "_" + group + "_" + key;
    }

    private void buildModel(DefaultTableModel model, MenuElement element) {
        if (element instanceof JMenuItem) {
            Object action = ((JMenuItem) element).getAccelerator();
            model.addRow(new Object[] { element, action });
        } else if (element instanceof JSeparator) {
            return;
        }

        // Now children
        MenuElement[] childElements = element.getSubElements();
        for (int i = 0; i < childElements.length; i++) {
            this.buildModel(model, childElements[i]);
        }
    }

    public void addConfigurableKeyStrokeGroup(String groupName, List keyBindings) {
        if (this.table.getModel() instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) this.table.getModel();
            if ((keyBindings != null) && !keyBindings.isEmpty()) {
                model.addRow(new Object[] { groupName, null });
                for (int i = 0; i < keyBindings.size(); i++) {
                    ComponentKeyStroke keyStroke = new ComponentKeyStroke((ComponentKeyStroke) keyBindings.get(i));
                    model.addRow(new Object[] { keyStroke, keyStroke.getKeyStroke() });
                }
            }
        }
    }

    public static class ComponentKeyStroke implements Cloneable {

        protected JComponent component;

        protected String groupName;

        private KeyStroke keyStroke;

        private String keyName;

        private Action action;

        private int inputMapCondition = JComponent.UNDEFINED_CONDITION;

        public ComponentKeyStroke(JComponent comp, String groupName) {
            this.component = comp;
            this.groupName = groupName;
        }

        public ComponentKeyStroke(ComponentKeyStroke comp) {
            this.component = comp.getComponent();
            this.groupName = comp.getGroupName();
            this.keyName = comp.getKeyName();
            this.keyStroke = comp.getKeyStroke();
            this.action = comp.getAction();
            this.inputMapCondition = comp.getInputMapCondition();
        }

        public KeyStroke getKeyStroke() {
            return this.keyStroke;
        }

        public void setKeyStroke(KeyStroke keyStroke) {
            this.keyStroke = keyStroke;
        }

        public String getKeyName() {
            return this.keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public Action getAction() {
            return this.action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getInputMapCondition() {
            return this.inputMapCondition;
        }

        public void setInputMapCondition(int inputMapCondition) {
            this.inputMapCondition = inputMapCondition;
        }

        public JComponent getComponent() {
            return this.component;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }

}
