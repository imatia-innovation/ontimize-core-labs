package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

/**
 * The class that display the printing window progress, while the application is printing the table
 * information.
 */
public class ProgressPrintingWindow extends JFrame implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(ProgressPrintingWindow.class);

	public static final String PRINT_PROGRESS = "table.print_progress";

	JProgressBar progressBar = new JProgressBar();

	private final JLabel state = new JLabel();

	JButton cancelButton = new JButton();

	private PrintableTable printableTable = null;

	public ProgressPrintingWindow(final PrintableTable printTable) {
		super(ApplicationManager.getTranslation(ProgressPrintingWindow.PRINT_PROGRESS));
		this.printableTable = printTable;
		if (printTable.table.parentFrame != null) {
			this.setIconImage(printTable.table.parentFrame.getIconImage());
		}
		this.progressBar.setMinimum(0);
		this.progressBar.setValue(0);
		this.progressBar.setMaximum(10);
		this.progressBar.setSize(new Dimension(200, 20));
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.getContentPane().add(this.progressBar, BorderLayout.NORTH);
		final JPanel panelAux = new JPanel(new GridBagLayout());
		panelAux.add(this.state, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		panelAux.add(this.cancelButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(panelAux, BorderLayout.CENTER);
		this.cancelButton.setEnabled(false);
		this.cancelButton.setMargin(new Insets(1, 1, 1, 1));
		this.cancelButton.setToolTipText("Cancel printing job in progress");

		final ImageIcon cancelIcon = ImageManager.getIcon(ImageManager.CANCEL);
		if (cancelIcon != null) {
			this.cancelButton.setIcon(cancelIcon);
		} else {
			this.cancelButton.setText("Print cancel");
		}
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean choice = MessageDialog.showQuestionMessage(
						SwingUtilities.getWindowAncestor(ProgressPrintingWindow.this.cancelButton), "Printing cancel?");
				if (choice) {
					ProgressPrintingWindow.this.cancelButton.setEnabled(false);
					ProgressPrintingWindow.this.printableTable.cancelPrinting();
					ProgressPrintingWindow.this.setVisible(false);
					ProgressPrintingWindow.this.dispose();
				}
			}
		});
		this.setSize(250, 70);
		// this.setResizable(false);
		ApplicationManager.center(this);
	}

	public void setMaxProgressBar(final int max) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.progressBar.setMaximum(max);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						ProgressPrintingWindow.this.progressBar.setMaximum(max);
					}
				});
			} catch (final Exception e) {
				ProgressPrintingWindow.logger.trace(null, e);
				this.progressBar.setMaximum(max);
			}
		}
	}

	public void setPosProgressBar(final int pos) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.progressBar.setValue(pos);
			this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						ProgressPrintingWindow.this.progressBar.setValue(pos);
						ProgressPrintingWindow.this.progressBar.paintImmediately(0, 0,
								ProgressPrintingWindow.this.progressBar.getWidth(),
								ProgressPrintingWindow.this.progressBar.getHeight());
					}
				});
			} catch (final Exception e) {
				ProgressPrintingWindow.logger.trace(null, e);
				this.progressBar.setValue(pos);
				this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
			}
		}
	}

	public void incrementPosInProgressBar() {
		if (SwingUtilities.isEventDispatchThread()) {
			this.progressBar.setValue(this.progressBar.getValue() + 1);
			this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						ProgressPrintingWindow.this.progressBar
						.setValue(ProgressPrintingWindow.this.progressBar.getValue() + 1);
						ProgressPrintingWindow.this.progressBar.paintImmediately(0, 0,
								ProgressPrintingWindow.this.progressBar.getWidth(),
								ProgressPrintingWindow.this.progressBar.getHeight());
					}
				});
			} catch (final Exception e) {
				ProgressPrintingWindow.logger.trace(null, e);
				this.progressBar.setValue(this.progressBar.getValue() + 1);
				this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
			}
		}
	}

	public void setStateText(final String text) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.state.setText(text);
			this.state.paintImmediately(0, 0, this.state.getWidth(), this.state.getHeight());
		} else {
			try {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						ProgressPrintingWindow.this.state.setText(text);
						ProgressPrintingWindow.this.state.paintImmediately(0, 0,
								ProgressPrintingWindow.this.state.getWidth(),
								ProgressPrintingWindow.this.state.getHeight());
					}
				});
			} catch (final Exception e) {
				ProgressPrintingWindow.logger.trace(null, e);
				this.state.setText(text);
				this.state.paintImmediately(0, 0, this.state.getWidth(), this.state.getHeight());
			}
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

}
