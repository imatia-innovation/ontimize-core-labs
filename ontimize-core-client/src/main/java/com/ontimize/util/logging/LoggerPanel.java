package com.ontimize.util.logging;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.util.logging.IRemoteLogManager;
import com.ontimize.jee.common.util.logging.Level;
import com.ontimize.jee.common.util.logging.LogManagerFactory;

public class LoggerPanel extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(LoggerPanel.class);

	public static final String REFRESH_logger_BUTTON = "servermonitor.refresh_logger_button";

	protected JButton refreshLoggerButton;

	protected JTable loggerTable;

	protected ResourceBundle bundle;

	protected IRemoteLogManager remoteManager;

	public LoggerPanel(final ResourceBundle bundle, final IRemoteLogManager remoteLogManager) {
		this.setLayout(new GridBagLayout());
		this.bundle = bundle;
		this.remoteManager = remoteLogManager;
		this.createComponents();
	}

	protected void createComponents() {

		this.refreshLoggerButton = new JButton(
				ApplicationManager.getTranslation(LoggerPanel.REFRESH_logger_BUTTON, this.bundle));
		this.refreshLoggerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final List<Logger> loggers = LoggerPanel.this.getLoggerList();
				((LoggerModel) LoggerPanel.this.loggerTable.getModel()).setList(loggers);
			}
		});

		this.loggerTable = new JTable();

		final List<Logger> loggers = this.getLoggerList();
		final LoggerModel tableModel = new LoggerModel(this.remoteManager);
		this.loggerTable.setModel(tableModel);
		this.loggerTable.setDefaultEditor(Level.class, new LevelCellEditor());
		this.loggerTable.setDefaultRenderer(Level.class, new LevelCellRenderer());
		tableModel.setList(loggers);

		this.add(this.refreshLoggerButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		this.add(new JScrollPane(this.loggerTable), new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
	}

	protected List<Logger> getLoggerList() {
		if (this.remoteManager != null) {
			try {
				return this.remoteManager.getLoggerList("");
			} catch (final Exception e) {
				LoggerPanel.logger.error("getLoggerList exception", e);
				return new ArrayList<Logger>();
			}
		}
		return LogManagerFactory.getLogManager().getLoggerList();
	}

}
