package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.locator.ClientReferenceLocator;

public class PreferenceYesNoSelectionDialog {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceYesNoSelectionDialog.class);

    protected JDialog dialog = null;

    JCheckBox checkSaveAnswer = null;

    JOptionPane questionPanel = null;

    public static final int SAVE_YES_VALUE = 1;

    public static final int NO_SAVE_YES_VALUE = 2;

    public static final int SAVE_NO_VALUE = 3;

    public static final int NO_SAVE_NO_VALUE = 4;

    public PreferenceYesNoSelectionDialog(Component parent, String message, int messageType, String checkMessage,
            String title, Icon icon) {
        this.questionPanel = new JOptionPane(ApplicationManager.getTranslation(message), messageType,
                JOptionPane.YES_NO_OPTION, icon);
        this.questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JPanel panelcheck = new JPanel(new BorderLayout());
        this.checkSaveAnswer = new JCheckBox(ApplicationManager.getTranslation(checkMessage));
        panelcheck.add(this.checkSaveAnswer, BorderLayout.CENTER);
        panelcheck.setBorder(BorderFactory.createEmptyBorder(15, 2, 0, 2));

        this.questionPanel.add(panelcheck, BorderLayout.SOUTH);

        this.dialog = this.questionPanel.createDialog(parent, ApplicationManager.getTranslation(title));
        // dialog.getContentPane().add(questionPanel);
        this.dialog.pack();
        this.dialog.setModal(true);
        this.dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public int showDialog() {
        this.dialog.show();
        this.dialog.dispose();

        Object selectedValue = this.questionPanel.getValue();
        int intValue = 0;
        if (selectedValue instanceof Integer) {
            intValue = ((Integer) selectedValue).intValue();

            if (intValue == JOptionPane.YES_OPTION) {
                if (this.checkSaveAnswer.isSelected()) {
                    return PreferenceYesNoSelectionDialog.SAVE_YES_VALUE;
                } else {
                    return PreferenceYesNoSelectionDialog.NO_SAVE_YES_VALUE;
                }
            } else if (intValue == JOptionPane.NO_OPTION) {
                if (this.checkSaveAnswer.isSelected()) {
                    return PreferenceYesNoSelectionDialog.SAVE_NO_VALUE;
                } else {
                    return PreferenceYesNoSelectionDialog.NO_SAVE_NO_VALUE;
                }
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Creates and shows a new <code>PreferenceYesNoSelectionDialog</code> when is necessary. First of
     * all checks the preferences, if this preference exists the dialog is not created, and returns
     * preference value.
     * @param parent Parent component
     * @param message message
     * @param messageType message type
     * @param checkMessage message to show as label in the check to save the preference
     * @param title Dialog title
     * @param preference Preference name
     * @param icon Icon, if icon is null it uses a default one
     * @return
     */
    public static int showYesSelectionDialog(Component parent, String message, int messageType, String checkMessage,
            String title, String preference, Icon icon) {
        String user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser();
        String preferenceValue = ApplicationManager.getApplication().getPreferences().getPreference(user, preference);
        int result = -1;

        if (preferenceValue != null) {
            try {
                result = Integer.parseInt(preferenceValue);
            } catch (Exception e) {
                PreferenceYesNoSelectionDialog.logger.trace(null, e);
            }
        }

        if (result == -1) {
            PreferenceYesNoSelectionDialog prefDialog = new PreferenceYesNoSelectionDialog(parent, message, messageType,
                    checkMessage, title, icon);
            result = prefDialog.showDialog();
            if ((result == PreferenceYesNoSelectionDialog.SAVE_NO_VALUE)
                    || (result == PreferenceYesNoSelectionDialog.SAVE_YES_VALUE)) {
                // Save the preference
                ApplicationManager.getApplication().getPreferences().setPreference(user, preference, "" + result);
            }
        }
        if ((result == PreferenceYesNoSelectionDialog.SAVE_NO_VALUE)
                || (result == PreferenceYesNoSelectionDialog.NO_SAVE_NO_VALUE)) {
            result = JOptionPane.NO_OPTION;

        } else if ((result == PreferenceYesNoSelectionDialog.SAVE_YES_VALUE)
                || (result == PreferenceYesNoSelectionDialog.NO_SAVE_YES_VALUE)) {
            result = JOptionPane.YES_OPTION;
        }
        return result;
    }

}
