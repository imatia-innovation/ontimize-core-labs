/**
 *
 */
package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class TableFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(TableFrame.class);

    protected static Action[] actions = new Action[0];

    protected static KeyStroke[] keyStrokes = new KeyStroke[0];

    protected static String[] keys = new String[0];
    static {
        class EAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ApplicationManager.DEBUG) {
                    TableFrame.logger.debug("Event: " + e);
                }
                if (SwingUtilities.getWindowAncestor((Component) e.getSource()) instanceof TableFrame) {
                    TableFrame f = (TableFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                    if (f.form.getInteractionManager() != null) {
                        if (!f.form.getInteractionManager().getModifiedFieldAttributes().isEmpty()
                                && f.form.getInteractionManager().getCheckModifiedDataChangeEvent()) {
                            boolean bClose = f.form.question(Table.M_MODIFIED_DATA_CLOSE_AND_LOST_CHANGES);
                            if (!bClose) {
                                return;
                            }
                        }
                    }
                    ((TableFrame) SwingUtilities.getWindowAncestor((Component) e.getSource()))
                        .processWindowEvent(new WindowEvent(SwingUtilities.getWindowAncestor((Component) e.getSource()),
                                WindowEvent.WINDOW_CLOSING));
                }
            }

        }

        TableFrame.setActionForKey(KeyEvent.VK_ESCAPE, 0, new EAction(), "Close window");
    }

    protected Form form = null;

    protected String sizePositionPreferenceKey = null;

    public TableFrame(String title, Form f) {
        super(title);
        this.form = f;
        this.getContentPane().add(f);
        this.registerKeyBindings();
        this.setSizePositionPreferenceKey(this.getTableWindowSizePreferenceKey(f));
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (this.form.getInteractionManager() != null) {
                if (!this.form.getInteractionManager().getModifiedFieldAttributes().isEmpty()
                        && this.form.getInteractionManager().getCheckModifiedDataChangeEvent()) {
                    boolean bClose = this.form.question(Table.M_MODIFIED_DATA_CLOSE_AND_LOST_CHANGES);
                    if (!bClose) {
                        return;
                    }
                }
            }

            if (this.sizePositionPreferenceKey != null) {
                ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
                if (prefs != null) {
                    String user = null;
                    try {
                        EntityReferenceLocator b = ApplicationManager.getApplication().getReferenceLocator();
                        if (b instanceof ClientReferenceLocator) {
                            user = ((ClientReferenceLocator) b).getUser();
                        }
                    } catch (Exception ex) {
                        TableFrame.logger.trace(null, ex);
                    }
                    prefs.setPreference(user, this.sizePositionPreferenceKey,
                            this.getWidth() + ";" + this.getHeight() + ";" + this.getX() + ";" + this.getY());
                    prefs.savePreferences();
                }
            }
        }
        super.processWindowEvent(e);

    }

    public String getTableWindowSizePreferenceKey(Form f) {
        if (f != null) {
            return BasicApplicationPreferences.DETAIL_DIALOG_SIZE_POSITION + "_" + f.getArchiveName() + "_"
                    + f.getEntityName();
        } else {
            return null;
        }
    }

    public void setSizePositionPreferenceKey(String s) {
        this.sizePositionPreferenceKey = s;
    }

    public String getSizePositionPreferenceKey() {
        return this.sizePositionPreferenceKey;
    }

    @Override
    public void pack() {
        if (this.sizePositionPreferenceKey != null) {
            try {
                ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
                if (prefs != null) {
                    String user = null;
                    try {
                        EntityReferenceLocator referenceLocator = ApplicationManager.getApplication()
                            .getReferenceLocator();
                        if (referenceLocator instanceof ClientReferenceLocator) {
                            user = ((ClientReferenceLocator) referenceLocator).getUser();
                        }
                    } catch (Exception ex) {
                        TableFrame.logger.error(null, ex);
                    }
                    String s = prefs.getPreference(user, this.sizePositionPreferenceKey);
                    if (s != null) {
                        String[] values = s.split(";");
                        if (values.length != 4) {
                            TableFrame.logger
                                .debug("Invalid preference: " + this.sizePositionPreferenceKey + " : " + s);
                            super.pack();
                            return;
                        }
                        Dimension d = new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                        Point p = new Point(Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                        if ((Double.compare(d.getWidth(), 0) != 0) && (Double.compare(d.getHeight(), 0) != 0)) {
                            this.setSize(d);
                        }
                        this.setLocation(p);
                    } else {
                        super.pack();
                    }
                }
            } catch (Exception ex1) {
                TableFrame.logger.trace(null, ex1);
            }
        } else {
            super.pack();
        }
    }

    public static void setActionForKey(int keyCode, int modifiers, Action action, String key) {
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, modifiers, false);
        Action[] a = new Action[TableFrame.actions.length + 1];
        for (int i = 0; i < TableFrame.actions.length; i++) {
            a[i] = TableFrame.actions[i];
        }
        a[a.length - 1] = action;
        KeyStroke[] k = new KeyStroke[TableFrame.keyStrokes.length + 1];
        for (int i = 0; i < TableFrame.keyStrokes.length; i++) {
            k[i] = TableFrame.keyStrokes[i];
        }
        k[k.length - 1] = ks;

        String[] ke = new String[TableFrame.keys.length + 1];
        for (int i = 0; i < TableFrame.keys.length; i++) {
            ke[i] = TableFrame.keys[i];
        }
        ke[ke.length - 1] = key;

        TableFrame.keys = ke;
        TableFrame.actions = a;
        TableFrame.keyStrokes = k;
    }

    protected void registerKeyBindings() {
        try {
            InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();
            for (int i = 0; i < TableFrame.actions.length; i++) {
                inMap.put(TableFrame.keyStrokes[i], TableFrame.keys[i]);
                actMap.put(TableFrame.keys[i], TableFrame.actions[i]);
            }
        } catch (Exception e) {
            TableFrame.logger.error("Error registering keybindings", e);
        }
    }

    public Form getForm() {
        return this.form;
    }

}
