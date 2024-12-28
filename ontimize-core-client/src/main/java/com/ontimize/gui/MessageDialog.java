package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.util.incidences.FormCreateIncidences;

/**
 * Utility class that implements a JDialog to show client application messages. It presents several
 * methods to customize the dialog.
 */

public class MessageDialog extends JOptionPane implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(MessageDialog.class);

	private static boolean JVM_VERSION_HIGHER_1_4 = false;

	public static final String NULL_MESSAGE = "NULL_MESSAGE";

	static {
		MessageDialog.JVM_VERSION_HIGHER_1_4 = ApplicationManager.jvmVersionHigherThan_1_4_0();
	}

	public static String INFO_MESSAGE_TITLE = "messagedialog.information";

	public static String ERROR_MESSAGE_TITLE = "messagedialog.error";

	public static String WARNING_MESSAGE_TITLE = "messagedialog.warning";

	public static String QUESTION_MESSAGE_TITLE = "messagedialog.question";

	public static String INFO_MESSAGE_TITLE_es_ES = "Información";

	public static String ERROR_MESSAGE_TITLE_es_ES = "Error";

	public static String WARNING_MESSAGE_TITLE_es_ES = "Advertencia";

	public static String QUESTION_MESSAGE_TITLE_es_ES = "Pregunta";

	private static String DEBUG_INFORMATION = "Error debug information \n";

	private final JTextArea detailText = new JTextArea();

	private JDialog dialog = null;

	private String messageKey = null;

	private Object[] args = null;

	private String titleKey = null;

	private ResourceBundle resources = null;

	private ImageIcon hideIcon = null;

	private ImageIcon showIcon = null;

	protected int characterNumberPerLine = 80;

	private MessageDialog(final Dialog parent, final String message, String details, final int type) {
		super(message, type);

		if ((message == null) || (message.length() == 0) || message.equals("null")) {
			details = MessageDialog.DEBUG_INFORMATION + ApplicationManager.getCurrentThreadMethods(10);
		}

		int optionT = JOptionPane.DEFAULT_OPTION;
		if (type == JOptionPane.QUESTION_MESSAGE) {
			optionT = JOptionPane.YES_NO_OPTION;
		}
		this.setOptionType(optionT);

		this.messageKey = message;
		this.detailText.setEditable(false);
		this.detailText.setWrapStyleWord(true);
		this.detailText.setLineWrap(true);
		this.detailText.setBackground(this.getBackground());

		if (details != null) {
			this.dialog = new JDialog(parent, true);

			this.dialog.getContentPane().setLayout(new GridBagLayout());
			this.dialog.getContentPane()
			.add(this,
					new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			final JPanel jpDetailsPanel = new JPanel(new BorderLayout());
			final JPanel jpButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			final JScrollPane scroll = new JScrollPane(this.detailText);
			scroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			final JButton bDetailsButton = new JButton();
			final ImageIcon showDetailsIcon = ImageManager.getIcon(ImageManager.VIEW_DETAILS);
			final ImageIcon hideDetailsIcon = ImageManager.getIcon(ImageManager.HIDE_DETAILS);
			if ((showDetailsIcon != null) && (hideDetailsIcon != null)) {
				this.showIcon = showDetailsIcon;
				this.hideIcon = hideDetailsIcon;
				bDetailsButton.setIcon(this.showIcon);
			} else {
				bDetailsButton.setText("table.details");
			}
			bDetailsButton.setMargin(new Insets(0, 0, 0, 0));
			bDetailsButton.setFont(bDetailsButton.getFont().deriveFont((float) 10));
			bDetailsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent evento) {
					// Center the window
					scroll.setVisible(!scroll.isVisible());
					if (MessageDialog.this.showIcon != null) {
						if (scroll.isVisible()) {
							bDetailsButton.setIcon(MessageDialog.this.hideIcon);
						} else {
							bDetailsButton.setIcon(MessageDialog.this.showIcon);
						}
					}
					MessageDialog.this.dialog.pack();
					final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					MessageDialog.this.dialog.setLocation((d.width - MessageDialog.this.dialog.getSize().width) / 2,
							(d.height - MessageDialog.this.dialog.getSize().height) / 2);
				}
			});
			jpButtonsPanel.add(bDetailsButton);
			jpDetailsPanel.add(jpButtonsPanel, BorderLayout.NORTH);
			this.detailText.setText(details);
			this.detailText.setRows(3);
			scroll.setVisible(false);
			jpDetailsPanel.add(scroll);
			this.dialog.getContentPane()
			.add(jpDetailsPanel,
					new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
		} else {
			this.dialog = this.createDialog(parent, "Message");
		}
		// dialogo.setSize(300,100);
		this.invalidate();
		this.dialog.validate();

		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (MessageDialog.this.dialog.isVisible() && (event.getSource() == MessageDialog.this)
						&& (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY) || event
								.getPropertyName()
								.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
					MessageDialog.this.dialog.setVisible(false);
					MessageDialog.this.dialog.dispose();
				}
			}
		});
	}

	private MessageDialog(final Dialog parent, final String message, final String details, final int type, final int newType) {
		this(parent, message, details, type);
		this.setOptionType(newType);
		// Center the window
		// this.validateTree();
		this.validate();
		this.dialog.validate();

		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
	}

	private MessageDialog(final Dialog parent, final String message, final String details, final int type, final int newType, final Object[] newValues) {
		this(parent, message, details, type);
		this.setOptionType(newType);
		this.setSelectionValues(newValues);
		// Center the window
		// this.validateTree();
		this.validate();
		this.dialog.validate();
		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
	}

	private MessageDialog(final Frame parent, String message, String details, final int type) {
		super(message, type);
		if ((message == null) || (message.length() == 0) || message.equals("null")) {
			details = MessageDialog.DEBUG_INFORMATION + ApplicationManager.getCurrentThreadMethods(10);
			message = MessageDialog.NULL_MESSAGE;
		}
		int optionT = JOptionPane.DEFAULT_OPTION;
		if (type == JOptionPane.QUESTION_MESSAGE) {
			optionT = JOptionPane.YES_NO_OPTION;
		}
		this.setOptionType(optionT);

		this.messageKey = message;
		this.detailText.setEditable(false);
		this.detailText.setWrapStyleWord(true);
		this.detailText.setLineWrap(true);
		this.detailText.setBackground(this.getBackground());

		if (details != null) {
			this.dialog = new JDialog(parent, true);

			this.dialog.getContentPane().setLayout(new GridBagLayout());
			this.dialog.getContentPane()
			.add(this,
					new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			final JPanel jpDetailsPanel = new JPanel(new BorderLayout());
			final JPanel jpButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			final JScrollPane scroll = new JScrollPane(this.detailText);
			scroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			final JButton bDetailsButton = new JButton();
			final ImageIcon viewDetails = ImageManager.getIcon(ImageManager.VIEW_DETAILS);
			final ImageIcon hideDetails = ImageManager.getIcon(ImageManager.HIDE_DETAILS);
			if ((viewDetails != null) && (hideDetails != null)) {
				this.showIcon = viewDetails;
				this.hideIcon = hideDetails;
				bDetailsButton.setIcon(this.showIcon);
			} else {
				// TODO change this text and put a more suitable one
				bDetailsButton.setText("Details");
			}
			bDetailsButton.setMargin(new Insets(0, 0, 0, 0));
			bDetailsButton.setFont(bDetailsButton.getFont().deriveFont((float) 10));
			bDetailsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					// Center the window
					scroll.setVisible(!scroll.isVisible());
					if (MessageDialog.this.showIcon != null) {
						if (scroll.isVisible()) {
							bDetailsButton.setIcon(MessageDialog.this.hideIcon);
						} else {
							bDetailsButton.setIcon(MessageDialog.this.showIcon);
						}
					}
					MessageDialog.this.dialog.pack();

					final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					MessageDialog.this.dialog.setLocation((d.width - MessageDialog.this.dialog.getSize().width) / 2,
							(d.height - MessageDialog.this.dialog.getSize().height) / 2);
				}
			});
			jpButtonsPanel.add(bDetailsButton);

			try {
				if (((UtilReferenceLocator) ApplicationManager.getApplication().getReferenceLocator())
						.supportIncidenceService()
						&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
					final JButton bIncidenceButton = new JButton();
					bIncidenceButton.setIcon(ImageManager.getIcon(ImageManager.INCIDENCE_BUTTON));
					bIncidenceButton.setToolTipText(ApplicationManager.getTranslation("M_CREATE_INCIDENCE_BUTTON"));
					bIncidenceButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							final FormCreateIncidences incidences = new FormCreateIncidences(e.getSource());
							ApplicationManager.center(incidences);
							incidences.setVisible(true);
						}

					});

					jpButtonsPanel.add(bIncidenceButton);

				}
			} catch (final Exception e) {
				MessageDialog.logger.error(null, e);
			}

			jpDetailsPanel.add(jpButtonsPanel, BorderLayout.NORTH);
			this.detailText.setText(details);
			this.detailText.setRows(3);
			scroll.setVisible(false);
			jpDetailsPanel.add(scroll);

			this.dialog.getContentPane()
			.add(jpDetailsPanel,
					new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
		} else {
			// TODO set the right label here
			this.dialog = this.createDialog(parent, "Message");

		}
		this.invalidate();
		this.dialog.validate();

		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (MessageDialog.this.dialog.isVisible() && (event.getSource() == MessageDialog.this)
						&& (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY) || event
								.getPropertyName()
								.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
					MessageDialog.this.dialog.setVisible(false);
					MessageDialog.this.dialog.dispose();
				}
			}
		});
	}

	private MessageDialog(final Frame parent, final String message, final String details, final int type, final int newType) {
		this(parent, message, details, type);
		this.setOptionType(newType);
		// Removed since 5.2067EN-0.3
		// Throwed exception closing app with java7
		// this.validateTree();
		this.dialog.validate();
		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
	}

	private MessageDialog(final Frame parent, final String message, final String details, final int type, final int newType, final Object[] newValues) {
		this(parent, message, details, type);
		this.setOptionType(newType);
		this.setSelectionValues(newValues);
		// this.validateTree();
		this.validate();
		this.dialog.validate();

		this.dialog.pack();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setLocation((d.width - this.dialog.getSize().width) / 2,
				(d.height - this.dialog.getSize().height) / 2);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final ResourceBundle resourceBundle,
			final Object[] args) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, args);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, (Object[]) null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, details, type, resourceBundle, (Object[]) null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final ResourceBundle resourceBundle,
			final Object[] args) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type);

		if (args != null) {
			messageDialog.setArgs(args, false);
		}
		messageDialog.setResourceBundle(resourceBundle);

		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, null, null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final Object[] args) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, null, args);
	}

	/**
	 * Shows a dialog which allows the user to choose among several options.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param resourceBundle
	 *            the resource bundle that will be used to look up the message key
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param newValues an array of <code>Objects</code> the user to be displayed (usually in a list or
	 *        combo-box) from which the user can make a selection
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showOptionMessage(final Frame parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final int newType, final Object[] newValues) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType, newValues);
		messageDialog.setResourceBundle(resourceBundle);

		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		final Object oComboOption = messageDialog.getInputValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		for (int i = 0; i < newValues.length; i++) {
			if (newValues[i].equals(oComboOption)) {
				return i;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final ResourceBundle resourceBundle,
			final Object[] args) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, args);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         choosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, (Object[]) null);
	}

	/**
	 * Shows a message. The message type can be selected among the following values from the
	 * {@link JOptionPane} class. <br/>
	 * <ul>
	 * <li>{@link JOptionPane#ERROR_MESSAGE}</li>
	 * <li>{@link JOptionPane#INFORMATION_MESSAGE}</li>
	 * <li>{@link JOptionPane#WARNING_MESSAGE}</li>
	 * <li>{@link JOptionPane#QUESTION_MESSAGE}</li>
	 * <li>{@link JOptionPane#PLAIN_MESSAGE}</li>
	 * </ul>
	 * @param parent the Dialog that will hold the message
	 * @param message the message to show
	 * @param type the message type
	 * @param newType an integer specifying the options the L&F is to display:
	 *        <code> {@link JOptionPane#DEFAULT_OPTION} </code> ,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>,
	 *        <code>{@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         choosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, details, type, resourceBundle, (Object[]) null);
	}

	/**
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final Object[] args) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type);
		if (args != null) {
			messageDialog.setArgs(args, false);
		}
		messageDialog.setResourceBundle(resourceBundle);

		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Shows a dialog.
	 * @param parent the Dialog that will hold the message.
	 * @param message the key of the message to display
	 * @param details
	 * @param type type the message type. The message type can be selected among the following values
	 *        from the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE} </code>
	 * @param newType an integer specifying the options the L&F is to display:
	 *        <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, null, null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the arguments
	 *        parameters will be sets
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final Object[] args) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, null, args);
	}

	/**
	 * Shows a dialog which allows the user to choose among several options.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param resourceBundle
	 *            the resource bundle that will be used to look up the message key
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param newValues an array of <code>Objects</code> the user to be displayed (usually in a list or
	 *        combo-box) from which the user can make a selection
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showOptionMessage(final Dialog parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final int newType, final Object[] newValues) {

		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType, newValues);
		messageDialog.setResourceBundle(resourceBundle);

		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		final Object oComboOption = messageDialog.getInputValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		for (int i = 0; i < newValues.length; i++) {
			if (newValues[i].equals(oComboOption)) {
				return i;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.messageKey);
		v.add("Yes");
		v.add("No");
		v.add("application.accept");
		v.add("application.cancel");
		return v;
	}

	/**
	 * All the messages that can be translated, apart from those translated in the getTextsToTranslate
	 * method.
	 * @return
	 */
	public static List getTextsToTranslateS() {
		final List v = new Vector();
		v.add("Yes");
		v.add("No");
		v.add("application.accept");
		v.add("application.cancel");
		v.add(MessageDialog.WARNING_MESSAGE_TITLE);
		v.add(MessageDialog.ERROR_MESSAGE_TITLE);
		v.add(MessageDialog.INFO_MESSAGE_TITLE);
		v.add(MessageDialog.QUESTION_MESSAGE_TITLE);
		return v;
	}

	protected void setArgs(final Object[] args) {
		this.setArgs(args, true);
	}

	private void setArgs(final Object[] args, final boolean updateMessage) {
		this.args = args;
		if (updateMessage) {
			this.updateMessage();
		}
	}

	protected void updateMessage() {
		try {
			String sLocaleMessage = this.messageKey;
			if (this.resources != null) {
				sLocaleMessage = ApplicationManager.getTranslation(this.messageKey, this.resources, this.args);
			}
			if (sLocaleMessage.toUpperCase().indexOf("<HTML>") >= 0) {
				this.characterNumberPerLine = Integer.MAX_VALUE;
			} else if (this.characterNumberPerLine == Integer.MAX_VALUE) {
				this.characterNumberPerLine = 80;
			}
			this.setMessage(sLocaleMessage);
			this.invalidate();
			// this.validateTree();
			this.validate();
			this.dialog.validate();
			this.dialog.pack();

			ApplicationManager.center(this.dialog);
		} catch (final Exception e) {
			MessageDialog.logger.debug(null, e);
		}

	}

	@Override
	public void setResourceBundle(final ResourceBundle bundle) {
		// Title
		this.resources = bundle;
		if (this.titleKey == null) {
			if (this.getMessageType() == JOptionPane.INFORMATION_MESSAGE) {
				String sTitle = MessageDialog.INFO_MESSAGE_TITLE_es_ES;
				try {
					if (this.resources != null) {
						sTitle = bundle.getString(MessageDialog.INFO_MESSAGE_TITLE);
					}
				} catch (final Exception e) {
					MessageDialog.logger.debug(null, e);
				}
				this.dialog.setTitle(sTitle);
			} else if (this.getMessageType() == JOptionPane.QUESTION_MESSAGE) {
				String sTitle = MessageDialog.QUESTION_MESSAGE_TITLE_es_ES;
				try {
					if (this.resources != null) {
						sTitle = bundle.getString(MessageDialog.QUESTION_MESSAGE_TITLE);
					}
				} catch (final Exception e) {
					MessageDialog.logger.debug(null, e);
				}
				this.dialog.setTitle(sTitle);
			} else if (this.getMessageType() == JOptionPane.WARNING_MESSAGE) {
				String sTitle = MessageDialog.WARNING_MESSAGE_TITLE_es_ES;
				try {
					if (this.resources != null) {
						sTitle = bundle.getString(MessageDialog.WARNING_MESSAGE_TITLE);
					}
				} catch (final Exception e) {
					MessageDialog.logger.debug(null, e);
				}
				this.dialog.setTitle(sTitle);
			} else if (this.getMessageType() == JOptionPane.ERROR_MESSAGE) {
				String sTitle = MessageDialog.ERROR_MESSAGE_TITLE_es_ES;
				try {
					if (this.resources != null) {
						sTitle = bundle.getString(MessageDialog.ERROR_MESSAGE_TITLE);
					}
				} catch (final Exception e) {
					MessageDialog.logger.debug(null, e);
				}
				this.dialog.setTitle(sTitle);
			}
		} else {
			String sTitle = this.titleKey;
			try {
				if (this.resources != null) {
					sTitle = bundle.getString(this.titleKey);
				}
			} catch (final Exception e) {
				MessageDialog.logger.debug(null, e);
			}
			this.dialog.setTitle(sTitle);
		}
		try {
			try {
				if (this.resources != null) {
					UIManager.put("OptionPane.yesButtonText", bundle.getString("Yes"));
				}
			} catch (final Exception e2) {
				MessageDialog.logger.debug(null, e2);
			}
			try {
				if (this.resources != null) {
					UIManager.put("OptionPane.noButtonText", bundle.getString("No"));
				}
			} catch (final Exception e3) {
				MessageDialog.logger.debug(null, e3);
			}
			try {
				if (this.resources != null) {
					UIManager.put("OptionPane.cancelButtonText", bundle.getString("application.cancel"));
				}
			} catch (final Exception e4) {
				MessageDialog.logger.debug(null, e4);
			}
			try {
				if (this.resources != null) {
					UIManager.put("OptionPane.okButtonText", bundle.getString("application.accept"));
				}
			} catch (final Exception e5) {
				MessageDialog.logger.debug(null, e5);
			}

			// Update the message is this key has a associated value
			try {
				String sLocaleMessage = this.messageKey;
				if (this.resources != null) {
					sLocaleMessage = ApplicationManager.getTranslation(this.messageKey, this.resources, this.args);
				}
				if (sLocaleMessage.toUpperCase().indexOf("<HTML>") >= 0) {
					this.characterNumberPerLine = Integer.MAX_VALUE;
				} else if (this.characterNumberPerLine == Integer.MAX_VALUE) {
					this.characterNumberPerLine = 80;
				}

				this.setMessage(sLocaleMessage);
				this.invalidate();
				// this.validateTree();
				this.validate();
				this.dialog.validate();
				this.dialog.pack();

				ApplicationManager.center(this.dialog);
			} catch (final Exception e) {
				MessageDialog.logger.debug(null, e);
			}
		} catch (final Exception e) {
			MessageDialog.logger.trace(null, e);
		} finally {
			try {
				SwingUtilities.updateComponentTreeUI(this);
				this.invalidate();
				this.dialog.validate();
				this.dialog.pack();
				ApplicationManager.center(this.dialog);
			} catch (final Exception e) {
				MessageDialog.logger.debug(null, e);
			}
		}

	}

	public void setTitle(final String title) {
		this.titleKey = title;
		try {
			if (this.resources != null) {
				if (this.resources != null) {
					this.dialog.setTitle(this.resources.getString(this.titleKey));
				}
			}
		} catch (final Exception e) {
			MessageDialog.logger.debug(null, e);
			this.dialog.setTitle(this.titleKey);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final ResourceBundle resourceBundle, final String title) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, title);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle,
			final String title) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle, title);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle,
			final String title, final Object[] args) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle, title, args);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final ResourceBundle resourceBundle,
			final String title) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type);
		messageDialog.setResourceBundle(resourceBundle);

		messageDialog.setTitle(title);
		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invocado fuera del thread de eventos");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final String title) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, title, null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Frame parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final String title, final Object[] args) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType);
		if (args != null) {
			messageDialog.setArgs(args, false);
		}
		messageDialog.setResourceBundle(resourceBundle);
		if (title != null) {
			messageDialog.setTitle(title);
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Shows a dialog which allows the user to choose among several options.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param resourceBundle
	 *            the resource bundle that will be used to look up the message key
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param newValues an array of <code>Objects</code> the user to be displayed (usually in a list or
	 *        combo-box) from which the user can make a selection
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showOptionMessage(final Frame parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final int newType, final Object[] newValues, final String title) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType, newValues);
		messageDialog.setResourceBundle(resourceBundle);

		messageDialog.setTitle(title);
		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		final Object oComboOption = messageDialog.getInputValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		for (int i = 0; i < newValues.length; i++) {
			if (newValues[i].equals(oComboOption)) {
				return i;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final ResourceBundle resourceBundle,
			final String title) {
		return MessageDialog.showMessage(parent, message, null, type, resourceBundle, title);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Dialog} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle,
			final String title) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle, title);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final int type, final int newType, final ResourceBundle resourceBundle,
			final String title, final Object[] args) {
		return MessageDialog.showMessage(parent, message, null, type, newType, resourceBundle, title, args);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code> {@link JOptionPane#ERROR_MESSAGE},
	 *        {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE},
	 *        {@link JOptionPane#QUESTION_MESSAGE}, {@link JOptionPane#PLAIN_MESSAGE}
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated. * @return the selected
	 *        value in the dialog, that is, the index of the selected option when the chosen type of the
	 *        dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final String title) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type);
		messageDialog.setResourceBundle(resourceBundle);

		messageDialog.setTitle(title);

		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 *           {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final String title) {
		return MessageDialog.showMessage(parent, message, details, type, newType, resourceBundle, title, null);
	}

	/**
	 * Creates an instance of this class which shows a dialog with a configurable message.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param title the key of the dialog title; the key will be translated.
	 * @param args an object array with the values that will configure the message, in case the message
	 *        can be configurable, that is, uses {0}, {1}, etc. to mark the spots where the args params
	 *        will be set
	 * @return the selected value in the dialog, that is, the index of the selected option when the
	 *         chosen type of the dialog enables it, or {@link JOptionPane#CLOSED_OPTION} if no options
	 *         selected / available
	 */
	public static int showMessage(final Dialog parent, final String message, final String details, final int type, final int newType,
			final ResourceBundle resourceBundle, final String title, final Object[] args) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}

		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType);

		if (args != null) {
			messageDialog.setArgs(args, false);
		}
		messageDialog.setResourceBundle(resourceBundle);

		if (title != null) {
			messageDialog.setTitle(title);
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		if (messageDialog.options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = messageDialog.options.length; counter < maxCounter; counter++) {
			if (messageDialog.options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	/**
	 * Shows a dialog which allows the user to choose among several options.
	 * @param parent the window that will hold the message, in this case a {@link Frame} instance
	 * @param message the key of the message to display; this key will be translated
	 * @param details the content of details will be displayed into an auxiliary text box of the dialog
	 *        and can be shown or hidden by the user
	 * @param type the message type. The message type can be selected among the following values from
	 *        the {@link JOptionPane} class. Values can be: <code>
	 *            {@link JOptionPane#ERROR_MESSAGE}, {@link JOptionPane#INFORMATION_MESSAGE}, {@link JOptionPane#WARNING_MESSAGE}, {@link JOptionPane#QUESTION_MESSAGE},
	 * {@link JOptionPane#PLAIN_MESSAGE}
	 * &#64;param newType
	 *            an integer specifying the options the L&F is to display: <code> {@link JOptionPane#DEFAULT_OPTION} </code>,
	 *        <code>{@link JOptionPane#YES_NO_OPTION}</code>, <code>
	 *            {@link JOptionPane#YES_NO_CANCEL_OPTION}</code>, or
	 *        <code>{@link JOptionPane#OK_CANCEL_OPTION}</code>
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param newValues an array of <code>Objects</code> the user to be displayed (usually in a list or
	 *        combo-box) from which the user can make a selection
	 * @param title the key of the dialog title; the key will be translated.
	 * @return
	 */
	public static int showOptionMessage(final Dialog parent, final String message, final String details, final int type,
			final ResourceBundle resourceBundle, final int newType, final Object[] newValues, final String title) {
		if (MessageDialog.logger.isDebugEnabled()
				&& ((type == JOptionPane.ERROR_MESSAGE) || (type == JOptionPane.WARNING_MESSAGE))) {
			ApplicationManager.printCurrentThreadMethods(10);
		}
		final MessageDialog messageDialog = new MessageDialog(parent, message, details, type, newType, newValues);
		messageDialog.setResourceBundle(resourceBundle);
		messageDialog.setTitle(title);
		if (!SwingUtilities.isEventDispatchThread()) {
			MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						messageDialog.dialog.setVisible(true);
					}
				});
			} catch (final Exception e) {
				MessageDialog.logger.trace(null, e);
			}
		} else {
			messageDialog.dialog.setVisible(true);
		}
		final Object selectedValue = messageDialog.getValue();
		final Object oComboOption = messageDialog.getInputValue();
		if (selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		for (int i = 0; i < newValues.length; i++) {
			if (newValues[i].equals(oComboOption)) {
				return i;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}

	@Override
	public int getMaxCharactersPerLineCount() {
		if (this.characterNumberPerLine == 0) {
			return Integer.MAX_VALUE;
		}
		return this.characterNumberPerLine;
	}


	/**
	 * Shows an error message.
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message the key of the message to display
	 */
	public static void showErrorMessage(final Window window, final String message) {
		MessageDialog.showErrorMessage(window, message, null);
	}

	/**
	 * Shows an error message.
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message the key of the message to display
	 * @param resourceBundle the resourceBundle to traslate
	 */
	public static void showErrorMessage(final Window window, final String message, final ResourceBundle resourceBundle) {
		if (window instanceof Dialog) {
			MessageDialog.showMessage((Dialog) window, message, JOptionPane.ERROR_MESSAGE, resourceBundle);
		} else {
			MessageDialog.showMessage((Frame) window, message, JOptionPane.ERROR_MESSAGE, resourceBundle);
		}
	}

	/**
	 * Shows a yes/no question message. These kind of messages has two buttons, to get the client
	 * response to the query
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message the key of the message to display
	 * @return true if the user pressed yes, false otherwise
	 */
	public static boolean showQuestionMessage(final Component component, final String message) {
		final Window window = SwingUtilities.getWindowAncestor(component);
		return MessageDialog.showQuestionMessage(window, message);
	}

	/**
	 * Shows a yes/no question message. These kind of messages has two buttons, to get the client
	 * response to the query
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message the key of the message to display
	 * @return true if the user pressed yes, false otherwise
	 */
	public static boolean showQuestionMessage(final Window window, final String message) {
		if (window instanceof Dialog) {
			return MessageDialog.showMessage((Dialog) window, message, JOptionPane.QUESTION_MESSAGE,
					null) == JOptionPane.YES_OPTION;
		} else {
			return MessageDialog.showMessage((Frame) window, message, JOptionPane.QUESTION_MESSAGE,
					null) == JOptionPane.YES_OPTION;
		}
	}

	/**
	 * Shows a yes/no question message. These kind of messages has two buttons, to get the client
	 * response to the query
	 * @param component the component that will invoke the message.
	 * @param message the key of the message to display
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return true if the user pressed yes, false otherwise
	 */
	public static boolean showQuestionMessage(final Component component, final String message, final ResourceBundle resourceBundle) {
		final Window window = SwingUtilities.getWindowAncestor(component);
		return MessageDialog.showQuestionMessage(window, message, resourceBundle);
	}

	/**
	 * Shows a yes/no question message. These kind of messages has two buttons, to get the client
	 * response to the query
	 * @param w the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message the key of the message to display
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return true if the user pressed yes, false otherwise
	 */
	public static boolean showQuestionMessage(final Window w, final String message, final ResourceBundle resourceBundle) {
		if (w instanceof Dialog) {
			return MessageDialog.showMessage((Dialog) w, message, JOptionPane.QUESTION_MESSAGE,
					resourceBundle) == JOptionPane.YES_OPTION;
		} else {
			return MessageDialog.showMessage((Frame) w, message, JOptionPane.QUESTION_MESSAGE,
					resourceBundle) == JOptionPane.YES_OPTION;
		}
	}

	/**
	 * Shows a dialog of the class {@link MessageDialog} to get a response from the client.
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message message the key of the message to display
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @return the client response
	 */
	public static Object showInputMessage(final Window window, final String message, final ResourceBundle resourceBundle) {
		return MessageDialog.showInputMessage(window, message, resourceBundle, null);
	}

	/**
	 * Shows a dialog of the class {@link MessageDialog} to get a response from the client.
	 * @param window the window that will hold the message. It can be either a {@link #Dialog} or a
	 *        {@link Frame}
	 * @param message message the key of the message to display
	 * @param resourceBundle the resource bundle that will be used to look up the message key
	 * @param initialValue
	 * @return the client response
	 */
	public static Object showInputMessage(final Window window, final String message, final ResourceBundle resourceBundle,
			final Object initialValue) {
		if (window instanceof Dialog) {
			final MessageDialog messageDialog = new MessageDialog((Dialog) window, message, null,
					JOptionPane.QUESTION_MESSAGE);

			messageDialog.setWantsInput(true);
			messageDialog.setSelectionValues(null);
			messageDialog.setInitialSelectionValue(initialValue);

			messageDialog.setResourceBundle(resourceBundle);

			if (!SwingUtilities.isEventDispatchThread()) {
				MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
				try {
					SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							messageDialog.dialog.setVisible(true);
						}
					});
				} catch (final Exception e) {
					MessageDialog.logger.trace(null, e);
				}
			} else {
				messageDialog.dialog.setVisible(true);
			}
			final Object value = messageDialog.getInputValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				return null;
			}
			return value;

		} else {

			final MessageDialog messageDialog = new MessageDialog((Frame) window, message, null,
					JOptionPane.QUESTION_MESSAGE);

			messageDialog.setWantsInput(true);
			messageDialog.setSelectionValues(null);
			messageDialog.setInitialSelectionValue(initialValue);

			messageDialog.setResourceBundle(resourceBundle);

			if (!SwingUtilities.isEventDispatchThread()) {
				MessageDialog.logger.debug("MessageDialog.showMessage invoked out of the events thread");
				try {
					SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							messageDialog.dialog.setVisible(true);
						}
					});
				} catch (final Exception e) {
					MessageDialog.logger.trace(null, e);
				}
			} else {
				messageDialog.dialog.setVisible(true);
			}
			final Object value = messageDialog.getInputValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				return null;
			}
			return value;
		}
	}

}
