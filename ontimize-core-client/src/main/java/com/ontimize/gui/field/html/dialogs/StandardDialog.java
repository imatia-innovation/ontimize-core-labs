package com.ontimize.gui.field.html.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.i18n.Internationalization;

/**
 * This class implements a standard data entry dialog with "OK" and "Cancel" buttons. Subclasses can
 * override the isDataValid(), okButtonPressed(), and cancelButtonPressed() methods to perform
 * implementation specific processing.
 * <P>
 * By default, the dialog is modal, and has a JPanel with a BorderLayout for its content pane.
 *
 */
public class StandardDialog extends EJDialog implements Internationalization {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance();

    // Constants
    public static final int BUTTONS_CENTER = FlowLayout.CENTER;

    public static final int BUTTONS_LEFT = FlowLayout.LEFT;

    public static final int BUTTONS_RIGHT = FlowLayout.RIGHT;

    /** The spacing between components in pixels */
    public static final int COMPONENT_SPACING = 5;

    // Attributes

    /** Flag indicating if the "Cancel" button was pressed to close dialog */
    protected boolean myIsDialogCancelled = true;

    /** The content pane for holding user components */
    protected Container myUserContentPane;

    protected ResourceBundle resourceBundle;

    protected JButton okB;

    protected JButton cancelB;

    protected String dialogTitle;

    // Methods

    /**
     * This method creates a StandardDialog with the given parent frame and title.
     * @param parent The parent frame for the dialog.
     * @param title The title to display in the dialog.
     */
    public StandardDialog(Frame parent, String title) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(StandardDialog.BUTTONS_CENTER, StandardDialog.COMPONENT_SPACING);
    }

    /**
     * This method creates a StandardDialog with the given parent dialog and title.
     * @param parent The parent dialog for the dialog.
     * @param title The title to display in the dialog.
     */
    public StandardDialog(Dialog parent, String title) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(StandardDialog.BUTTONS_CENTER, StandardDialog.COMPONENT_SPACING);
    }

    public StandardDialog(Dialog parent, String title, int orientation, int spacing) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(orientation, spacing);
    }

    public StandardDialog(Frame parent, String title, int orientation, int spacing) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(orientation, spacing);
    }

    public StandardDialog(Frame parent, String title, int orientation) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(orientation, StandardDialog.COMPONENT_SPACING);
    }

    public StandardDialog(Dialog parent, String title, int orientation) {
        super(parent, StandardDialog.i18n.str(title));
        this.dialogTitle = title;
        this.init(orientation, StandardDialog.COMPONENT_SPACING);
    }

    /**
     * This method sets up the default attributes of the dialog and the content pane.
     */
    protected void init(int orientation, int spacing) {
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Setup the internal content pane to hold the user content pane
        // and the standard button panel

        JPanel internalContentPane = new JPanel();

        internalContentPane
            .setLayout(new BorderLayout(StandardDialog.COMPONENT_SPACING, StandardDialog.COMPONENT_SPACING));

        Action okAction = new AbstractAction(StandardDialog.i18n.str("Accept")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (StandardDialog.this.isValidData()) {
                    StandardDialog.this.myIsDialogCancelled = false;
                    StandardDialog.this.dispose();
                }
            }
        };

        Action cancelAction = new AbstractAction(StandardDialog.i18n.str("cancel")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StandardDialog.this.myIsDialogCancelled = true;
                StandardDialog.this.dispose();
            }
        };

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        this.okB = new JButton(okAction);
        buttonPanel.add(this.okB);
        this.cancelB = new JButton(cancelAction);
        buttonPanel.add(this.cancelB);
        this.getRootPane().setDefaultButton(this.okB);

        JPanel spacer = new JPanel(new FlowLayout(orientation));
        spacer.add(buttonPanel);

        internalContentPane.add(spacer, BorderLayout.SOUTH);

        // Initialize the user content pane with a JPanel
        this.myUserContentPane = new JPanel(new BorderLayout());
        super.setContentPane(internalContentPane);

        // Finally, add a listener for the window close button.
        // Process this event the same as the "Cancel" button.

        WindowAdapter windowAdapter = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                StandardDialog.this.myIsDialogCancelled = true;
                StandardDialog.this.dispose();
            }
        };

        this.addWindowListener(windowAdapter);
    }

    /**
     * This method gets the content pane for adding components. Components should not be added directly
     * to the dialog.
     *
     * @returns the content pane for the dialog.
     */
    @Override
    public Container getContentPane() {
        return this.myUserContentPane;
    }

    /**
     * This method sets the content pane for adding components. Components should not be added directly
     * to the dialog.
     * @param contentPane The content pane for the dialog.
     */
    @Override
    public void setContentPane(Container contentPane) {
        this.myUserContentPane = contentPane;
        super.getContentPane().add(this.myUserContentPane, BorderLayout.CENTER);
    }

    /**
     * This method returns <code>true</code> if the User cancelled the dialog otherwise
     * <code>false</code>. The dialog is cancelled if the "Cancel" button is pressed or the "Close"
     * window button is pressed, or the "Escape" key is pressed. In other words, if the User has caused
     * the dialog to close by any method other than by pressing the "Ok" button, this method will return
     * <code>true</code>.
     */
    public boolean hasUserCancelled() {
        return this.myIsDialogCancelled;
    }

    /**
     * This method is used to validate the current dialog box. This method provides a default response
     * of <code>true</code>. This method should be implemented by each dialog that extends this class.
     *
     * @returns a boolean indicating if the data is valid. <code>true</code> indicates that all of the
     *          fields were validated correctly and <code>false</code> indicates the validation failed
     */
    protected boolean isValidData() {
        return true;
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        if (this.dialogTitle != null) {
            this.setTitle(StandardDialog.i18n.str(this.dialogTitle));
        }
        if (this.okB != null) {
            this.okB.setText(StandardDialog.i18n.str("Accept"));
        }
        if (this.cancelB != null) {
            this.cancelB.setText(StandardDialog.i18n.str("cancel"));
        }

    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
