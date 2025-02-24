package com.ontimize.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

public class FindDialog extends JDialog implements Internationalization {

	public static boolean DEBUG = true;

	protected JTextComponent textComponent = null;

	protected JLabel labelFindText = new JLabel();

	public static String findKey = "finddialog.text_to_search";

	public static String capitalSmallLetterKey = "case-sensitive";

	public static String fullWordKey = "finddialog.full_words_only";

	public static final String optionsKey = "finddialog.options";

	public static String acceptKey = "application.accept";

	public static String cancelKey = "application.cancel";

	public static String noFoundKey = "finddialog.not_found";

	public static String titleKey = "search";

	protected String noFoundText = FindDialog.noFoundKey;

	protected JTextField findJText = new JTextField();

	protected JCheckBox capitalLetter = new JCheckBox();

	protected JCheckBox fullWord = new JCheckBox("finddialog.full_words_only");

	protected JButton acceptButton = new JButton("application.accept");

	protected JButton cancelButton = new JButton("application.cancel");

	protected JPanel optionPanel = new JPanel(new GridBagLayout());

	protected JPanel buttonPanel = new JPanel(new GridBagLayout());

	protected JLabel labelIcon = new JLabel();

	protected ResourceBundle resources = null;

	protected int initialOffset = 0;

	protected Locale locale = null;

	protected String str = null;

	public FindDialog(final Frame f, final JTextComponent source) {
		super(f, FindDialog.titleKey, true);
		this.init(source);
	}

	public FindDialog(final Dialog d, final JTextComponent source) {
		super(d, FindDialog.titleKey, true);
		this.init(source);
	}

	protected void init(final JTextComponent source) {
		this.textComponent = source;
		this.acceptButton.setMargin(new Insets(2, 2, 2, 2));
		this.cancelButton.setMargin(new Insets(2, 2, 2, 2));
		this.getRootPane().setDefaultButton(this.acceptButton);
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane()
		.add(this.labelFindText, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		this.getContentPane()
		.add(this.findJText, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.optionPanel.setBorder(new TitledBorder(FindDialog.optionsKey));
		this.getContentPane()
		.add(this.optionPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.optionPanel.add(this.capitalLetter, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.optionPanel.add(this.fullWord, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.getContentPane()
		.add(this.buttonPanel, new GridBagConstraints(1, 0, 1, 3, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 2, 2), 0, 0));

		this.buttonPanel.add(this.acceptButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.cancelButton, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.labelIcon, new GridBagConstraints(0, 2, 1, 1, 0, 1, GridBagConstraints.NORTH,
				GridBagConstraints.VERTICAL, new Insets(5, 0, 0, 0), 0, 0));

		final ImageIcon searchIcon = ImageManager.getIcon(ImageManager.SEARCH);
		if (searchIcon != null) {
			this.labelIcon.setIcon(searchIcon);
		}

		this.installButtonListeners();
		this.setResourceBundle(null);
		this.pack();
		ApplicationManager.center(this);
	}

	protected void installButtonListeners() {
		this.acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FindDialog.this.str = FindDialog.this.findJText.getText();
				FindDialog.this.find();
			}
		});
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FindDialog.this.setVisible(false);
			}
		});
	}

	public void show(final int pos) {
		this.initialOffset = pos;
		this.findJText.requestFocus();
		super.setVisible(true);
	}

	public void find() {
		this.find(this.initialOffset);
	}

	@Override
	protected void processKeyEvent(final KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_ESCAPE) && (e.getID() == KeyEvent.KEY_PRESSED)) {
			this.setVisible(false);
			return;
		}
		super.processKeyEvent(e);
	}

	public void find(final int pos) {
		if ((this.str == null) || (this.str.length() == 0)) {
			this.setVisible(false);
			return;
		}

		this.initialOffset = pos;

		// Mark the found text. If nothing found then show a message
		final String sContent = this.textComponent.getText();
		if ((this.initialOffset < 0) || (this.initialOffset >= (sContent.length() - 1))) {
			this.initialOffset = 0;
		}
		if (sContent == null) {
			this.setVisible(false);
			return;
		}
		String stringToSearchIn = sContent.substring(this.initialOffset);
		final boolean caseSensitive = this.capitalLetter.isSelected();
		final boolean bCompleteWord = this.fullWord.isSelected();
		if (!bCompleteWord) {
			int foundIndex = -1;
			if (caseSensitive) {
				foundIndex = stringToSearchIn.indexOf(this.str);
			} else {
				stringToSearchIn = stringToSearchIn.toLowerCase();
				final String stringToSearch = this.str.toLowerCase();
				foundIndex = stringToSearchIn.indexOf(stringToSearch);
			}
			if (foundIndex >= 0) {
				this.setVisible(false);
				if (!this.textComponent.hasFocus()) {
					this.textComponent.requestFocus();
				}
				this.textComponent.setCaretPosition(this.initialOffset + foundIndex + this.str.length());

				this.textComponent.setSelectionStart(this.initialOffset + foundIndex);
				this.textComponent.setSelectionEnd(this.initialOffset + foundIndex + this.str.length());
				this.initialOffset = this.initialOffset + foundIndex + this.str.length();
			} else {
				this.setVisible(false);
				final Window w = this.getOwner();
				if (w instanceof Frame) {
					MessageDialog.showMessage((Frame) w, this.noFoundText + " '" + this.str + "'",
							JOptionPane.INFORMATION_MESSAGE, null);
				} else {
					MessageDialog.showMessage((Dialog) w, this.noFoundText + " '" + this.str + "'",
							JOptionPane.INFORMATION_MESSAGE, null);
				}
			}
		} else {
			// Use break iterator
			final BreakIterator iterator = BreakIterator
					.getWordInstance(this.locale != null ? this.locale : Locale.getDefault());
			iterator.setText(stringToSearchIn);
			int foundIndex = -1;
			if (caseSensitive) {
				// Search words
				int initialIndex = iterator.following(0);
				int currentIndex = iterator.next();
				while (currentIndex != BreakIterator.DONE) {
					final String sNextWord = stringToSearchIn.substring(initialIndex, currentIndex);
					if (sNextWord.equals(this.str)) {
						foundIndex = initialIndex;
					}
					if (foundIndex >= 0) {
						break;
					}
					initialIndex = currentIndex;
					currentIndex = iterator.next();
				}
			} else {
				// Search words
				int ininitalIndex = iterator.following(0);
				int currentIndex = iterator.next();
				while (currentIndex != BreakIterator.DONE) {
					final String sNextWord = stringToSearchIn.substring(ininitalIndex, currentIndex);
					if (sNextWord.equalsIgnoreCase(this.str)) {
						foundIndex = ininitalIndex;
					}
					if (foundIndex >= 0) {
						break;
					}
					ininitalIndex = currentIndex;
					currentIndex = iterator.next();
				}
			}
			if (foundIndex >= 0) {
				this.setVisible(false);
				if (!this.textComponent.hasFocus()) {
					this.textComponent.requestFocus();
				}
				this.textComponent.setCaretPosition(this.initialOffset + foundIndex + this.str.length());
				this.textComponent.setSelectionStart(this.initialOffset + foundIndex);
				this.textComponent.setSelectionEnd(this.initialOffset + foundIndex + this.str.length());
				this.initialOffset = this.initialOffset + foundIndex + this.str.length();
			} else {
				this.setVisible(false);
				final Window w = this.getOwner();
				if (w instanceof Frame) {
					MessageDialog.showMessage((Frame) this.getOwner(), this.noFoundText + " " + this.str,
							JOptionPane.INFORMATION_MESSAGE, null);
				} else {
					MessageDialog.showMessage((Dialog) this.getOwner(), this.noFoundText + " " + this.str,
							JOptionPane.INFORMATION_MESSAGE, null);
				}

			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(FindDialog.noFoundKey);
		v.add(FindDialog.acceptKey);
		v.add(FindDialog.cancelKey);
		v.add(FindDialog.capitalSmallLetterKey);
		v.add(FindDialog.fullWordKey);
		v.add(FindDialog.findKey);
		v.add(FindDialog.titleKey);
		v.add(FindDialog.optionsKey);
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resources = res;
		final String textToSearch = ApplicationManager.getTranslation(FindDialog.findKey, res);
		this.labelFindText.setText(textToSearch);

		this.noFoundText = ApplicationManager.getTranslation(FindDialog.noFoundKey, res);

		final String sAcceptText = ApplicationManager.getTranslation(FindDialog.acceptKey, res);
		this.acceptButton.setText(sAcceptText);

		final String sCancelText = ApplicationManager.getTranslation(FindDialog.cancelKey, res);
		this.cancelButton.setText(sCancelText);

		final String textKey = ApplicationManager.getTranslation(FindDialog.capitalSmallLetterKey, res);
		this.capitalLetter.setText(textKey);

		final String sFullWordText = ApplicationManager.getTranslation(FindDialog.fullWordKey, res);
		this.fullWord.setText(sFullWordText);

		final String sTitle = ApplicationManager.getTranslation(FindDialog.titleKey, res);
		this.setTitle(sTitle);

		final String sOptionsTitle = ApplicationManager.getTranslation(FindDialog.optionsKey, res);
		this.optionPanel.setBorder(new TitledBorder(sOptionsTitle));

		this.pack();
	}

}
