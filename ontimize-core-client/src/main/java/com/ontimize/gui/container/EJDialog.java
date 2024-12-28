package com.ontimize.gui.container;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.incidences.FormCreateIncidences;
import com.ontimize.util.swing.OGlassPanel;

/**
 * Dialog that adds a listener to the VK_ESCAPE key to close the dialog WINDOW_CLOSING
 * <p>
 * Title: User interface components
 * </p>
 * <p>
 * Description: User interface components
 * </p>
 * <p>
 * Company: Imatia Innovation
 * </p>
 */

public class EJDialog extends JDialog implements Freeable {

    private static final Logger logger = LoggerFactory.getLogger(EJDialog.class);

    public static boolean defaultValueAskQuestionOnEsc = false;

    public static boolean defaultValueAskQuestionOnClose = false;

    public static final String closeQuestion = "ejdialog.close_dialog";

    protected static Action[] actions = new Action[0];

    protected static KeyStroke[] keyStrokes = new KeyStroke[0];

    protected static String[] keys = new String[0];

    protected boolean autoPackOnOpen = true;

    protected String sizePositionPreference = null;

    public static boolean opaque = false;

    protected boolean askQuestionOnEsc = EJDialog.defaultValueAskQuestionOnEsc;

    protected boolean askQuestionEverOnClose = EJDialog.defaultValueAskQuestionOnClose;

    protected Component previousGlassPane = null;

    protected boolean changeLAF = false;

    static {
        JDialog.setDefaultLookAndFeelDecorated(true);

        class EMaximized extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationManager.maximize(SwingUtilities.getWindowAncestor((Component) e.getSource()));
            }

        }

        class EAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ApplicationManager.DEBUG) {
                    EJDialog.logger.debug("Event " + e);
                }
                if (SwingUtilities.getWindowAncestor((Component) e.getSource()) instanceof EJDialog) {
                    EJDialog ejDialog = (EJDialog) SwingUtilities.getWindowAncestor((Component) e.getSource());
                    boolean close = true;
                    if (ejDialog.isAskOnEsc() && !ejDialog.isAskOnClose()) {
                        close = ejDialog.askCloseQuestion();
                    }
                    if (close) {
                        ejDialog.processWindowEvent(
                                new WindowEvent(SwingUtilities.getWindowAncestor((Component) e.getSource()),
                                        WindowEvent.WINDOW_CLOSING));
                    }
                }
            }

        }

        class ECreateIncidence extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                FormCreateIncidences incidences = new FormCreateIncidences(e.getSource());
                ApplicationManager.center(incidences);
                incidences.setVisible(true);
            }

        }

        EJDialog.setActionForKey(KeyEvent.VK_ESCAPE, 0, new EAction(), "Close window");
        EJDialog.setActionForKey(KeyEvent.VK_ADD, InputEvent.CTRL_MASK, new EMaximized(), "Maximized window");
        EJDialog.setActionForKey(KeyEvent.VK_I, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, new ECreateIncidence(),
                "Create incidence");
    }

    protected boolean askCloseQuestion() {
        int result = MessageDialog.showMessage(this, EJDialog.closeQuestion, JOptionPane.QUESTION_MESSAGE,
                ApplicationManager.getApplicationBundle());
        return result == JOptionPane.YES_OPTION;
    }

    protected void transparent() {

        try {
            if (!EJDialog.opaque) {
                Class awtUtilitites = Class.forName("com.sun.awt.AWTUtilities");
                Method method = awtUtilitites.getMethod("setWindowOpaque", new Class[] { Window.class, boolean.class });
                method.invoke(null, new Object[] { this, new Boolean(false) });
            }
        } catch (Exception ex) {
            EJDialog.logger.trace(null, ex);
        }
    }

    public void setSizePositionPreference(String s) {
        this.sizePositionPreference = s;
    }

    public String getSizePositionPreference() {
        return this.sizePositionPreference;
    }

    @Override
    public void pack() {
        if (this.sizePositionPreference != null) {
            try {
                ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
                if (prefs != null) {
                    String user = null;
                    try {
                        EntityReferenceLocator b = ApplicationManager.getApplication().getReferenceLocator();
                        if (b instanceof ClientReferenceLocator) {
                            user = ((ClientReferenceLocator) b).getUser();
                        }
                    } catch (Exception ex) {
                        EJDialog.logger.error(null, ex);
                    }
                    String s = prefs.getPreference(user, this.sizePositionPreference);
                    if (s != null) {
                        String[] values = s.split(";");
                        if (values.length != 4) {
                            EJDialog.logger.debug("Invalid preference: " + this.sizePositionPreference + " : " + s);
                            super.pack();
                            return;
                        }
                        Dimension d = new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                        Point p = new Point(Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                        if ((Double.compare(d.getWidth(), 0) != 0) && (Double.compare(d.getHeight(), 0) != 0)) {
                            this.setSize(d);
                        }
                        p = ApplicationManager.checkAvailablePoint(p);
                        this.setLocation(p);
                    } else {
                        super.pack();
                        ApplicationManager.center(this);
                    }
                } else {
                    super.pack();
                    ApplicationManager.center(this);
                }
            } catch (Exception ex1) {
                EJDialog.logger.trace(null, ex1);
            }
        } else {
            super.pack();
        }
    }

    protected void initWindowListener() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                if (EJDialog.this.autoPackOnOpen) {
                    EJDialog.this.pack();
                }
            }

            @Override
            public void windowActivated(WindowEvent we) {
                // When the window is activated, the default component must get
                // the focus
                EJDialog.this.setInitialFocus();
            }
        });

        lafListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equalsIgnoreCase(evt.getPropertyName())) {
                    EJDialog.this.changeLAF = true;
                }
            }
        };
        UIManager.addPropertyChangeListener(lafListener);
    }

    protected boolean focusSet = false;

    protected void setInitialFocus() {
        if (!this.focusSet) {
            if (this.getContentPane() instanceof JComponent) {
                ((JComponent) this.getContentPane()).requestDefaultFocus();
            } else {
                this.requestFocus();
            }
            this.focusSet = true;
        }
    }

    public EJDialog(Dialog owner) {
        super(owner);
        this.registerKeyBindings();
        this.initWindowListener();
        this.setInitialFocus();
        this.focusSet = false;
        this.transparent();

    }

    public EJDialog(Dialog owner, boolean modal) {
        super(owner, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Dialog owner, String title) {
        super(owner, title);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Frame owner) {
        super(owner);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Frame owner, boolean modal) {
        super(owner, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Frame owner, String title) {
        super(owner, title);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Window owner) {
        super(owner);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Window owner, boolean modal) {
        super(owner, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Window owner, String title) {
        super(owner, title);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    public EJDialog(Window owner, String title, boolean modal) {
        super(owner, title, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        this.registerKeyBindings();
        this.initWindowListener();
        this.transparent();
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        /*
         * if(e.getKeyCode()==KeyEvent.VK_ESCAPE && e.getID()==KeyEvent.KEY_RELEASED) { e.consume();
         * processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING)); } else
         */super.processKeyEvent(e);
    }

    protected OGlassPanel glassPanel = new OGlassPanel();

    private PropertyChangeListener lafListener;

    @Override
    public void setVisible(final boolean b) {
        if (b && this.changeLAF) {
            try {
                SwingUtilities.updateComponentTreeUI(this.getContentPane());
            } finally {
                this.changeLAF = false;
            }
        }

        if (!OGlassPanel.disable) {
            if (b) {
                if (this.isModal() && (this.getOwner() instanceof RootPaneContainer)) {

                    if (this.previousGlassPane == null) {
                        this.previousGlassPane = ((RootPaneContainer) this.getOwner()).getGlassPane();
                        this.previousGlassPane.setVisible(false);
                        ((RootPaneContainer) this.getOwner()).setGlassPane(new OGlassPanel());
                        ((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(true);
                        ((RootPaneContainer) this.getOwner()).getGlassPane().repaint();
                        // ((Container)this.getOwner()).validate();
                    }
                }
            } else {
                try {
                    if (this.previousGlassPane != null) {
                        ((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(false);
                        ((RootPaneContainer) this.getOwner()).setGlassPane(this.previousGlassPane);
                        this.previousGlassPane = null;
                    }
                } catch (Exception ex) {
                    EJDialog.logger.trace(null, ex);
                }
            }
        }
        super.setVisible(b);
    }

    @Override
    public void dispose() {
        try {
            if (this.previousGlassPane != null) {
                ((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(false);
                ((RootPaneContainer) this.getOwner()).setGlassPane(this.previousGlassPane);
                this.previousGlassPane = null;
            }
        } catch (Exception ex) {
            EJDialog.logger.trace(null, ex);
        }
        super.dispose();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if ((e.getID() == WindowEvent.WINDOW_CLOSING) && this.isAskOnClose()) {
            boolean close = this.askCloseQuestion();
            if (!close) {
                return;
            }
        }

        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.savePositionPreference();
        }
    }

    public void savePositionPreference() {
        try {
            if (this.sizePositionPreference != null) {
                ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
                if (prefs != null) {
                    String user = null;
                    try {
                        EntityReferenceLocator b = ApplicationManager.getApplication().getReferenceLocator();
                        if (b instanceof ClientReferenceLocator) {
                            user = ((ClientReferenceLocator) b).getUser();
                        }
                    } catch (Exception ex) {
                        EJDialog.logger.trace(null, ex);
                    }
                    prefs.setPreference(user, this.sizePositionPreference,
                            this.getWidth() + ";" + this.getHeight() + ";" + this.getX() + ";" + this.getY());
                }
            }
        } catch (Exception ex) {
            EJDialog.logger.trace(null, ex);
        }
    }

    public static void setActionForKey(int keyCode, int modifiers, Action action, String key) {
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, modifiers, false);
        Action[] a = new Action[EJDialog.actions.length + 1];
        for (int i = 0; i < EJDialog.actions.length; i++) {
            a[i] = EJDialog.actions[i];
        }
        a[a.length - 1] = action;
        KeyStroke[] k = new KeyStroke[EJDialog.keyStrokes.length + 1];
        for (int i = 0; i < EJDialog.keyStrokes.length; i++) {
            k[i] = EJDialog.keyStrokes[i];
        }
        k[k.length - 1] = ks;

        String[] ke = new String[EJDialog.keys.length + 1];
        for (int i = 0; i < EJDialog.keys.length; i++) {
            ke[i] = EJDialog.keys[i];
        }
        ke[ke.length - 1] = key;

        EJDialog.keys = ke;
        EJDialog.actions = a;
        EJDialog.keyStrokes = k;
    }

    public void setAction(int keyCode, int modifiers, Action action, String key) {
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, modifiers, true);

        try {
            InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();
            inMap.put(ks, key);
            actMap.put(key, action);
        } catch (Exception e) {
            EJDialog.logger.error("Error setting keybindings", e);
        }
    }

    protected void registerKeyBindings() {
        try {
            InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();
            for (int i = 0; i < EJDialog.actions.length; i++) {
                inMap.put(EJDialog.keyStrokes[i], EJDialog.keys[i]);
                actMap.put(EJDialog.keys[i], EJDialog.actions[i]);
            }
        } catch (Exception e) {
            EJDialog.logger.error("Error setting keybindings", e);
        }
    }

    public void setAutoPackOnOpen(boolean auto) {
        this.autoPackOnOpen = auto;
    }

    public boolean isAskOnEsc() {
        return this.askQuestionOnEsc;
    }

    /**
     * Sets the condition to ask a question before closing the dialog when the user press the ESC key
     * @param askQuestionOnEsc
     */
    public void setAskOnEsc(boolean askQuestionOnEsc) {
        this.askQuestionOnEsc = askQuestionOnEsc;
    }

    public boolean isAskOnClose() {
        return this.askQuestionEverOnClose;
    }

    /**
     * Sets the condition to ask a question before closing the dialog in any situation
     * @param askQuestionOnClose
     */
    public void setAskOnClose(boolean askQuestionOnClose) {
        this.askQuestionEverOnClose = askQuestionOnClose;
    }

    @Override
    public void free() {
        FreeableUtils.freeComponent(previousGlassPane);
        if (lafListener != null) {
            UIManager.removePropertyChangeListener(lafListener);
        }
        InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();
        for (int i = 0; i < EJDialog.actions.length; i++) {
            inMap.remove(EJDialog.keyStrokes[i]);
            actMap.remove(EJDialog.keys[i]);
        }

    }

}
