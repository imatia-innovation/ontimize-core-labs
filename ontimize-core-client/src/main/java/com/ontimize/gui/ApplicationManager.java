package com.ontimize.gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.cache.CacheManager;
import com.ontimize.cache.CacheManager.CacheManagerViewer;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.HTMLDataField;
import com.ontimize.gui.field.RealDataField;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.ConnectionOptimizer;
import com.ontimize.jee.common.gui.RemotelyManageable;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.jee.common.util.swing.SwingUtils;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.AWTUtilities;
import com.ontimize.util.logging.LoggerPanel;
import com.ontimize.util.remote.IRemoteAdministrationWindow;
import com.ontimize.util.rmitunneling.StreamInfoComponent;
import com.ontimize.util.swing.OGlassPanel;

/**
 * Abstract class that implements several utility methods to be in the development of clients.
 */

public abstract class ApplicationManager {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationManager.class);

	private static final String SYSTEM_INFORMATION_MESSAGE = "applicationmanager.system_information";

	protected static final String SHOW_HTTP_HTTPS_TRAFFIC_MESSAGE = "applicationmanager.show_http_https_traffic";

	protected static final String BUNDLE_AND_CACHE_MEMORY_SIZE_MESSAGE = "applicationmanager.bundle_and_cache_memory_size";

	protected static final String CACHE_MANAGER_MESSAGE = "applicationmanager.cache_manager_viewer";

	protected static final String CHANGE_NETWORK_BITRATE_MESSAGE = "applicationmanager.change_network_bitrate_message";

	protected static final String SYSTEM_PROPERTIES_MESSAGE = "applicationmanager.system_properties";

	protected static final String GENERATE_BUNDLE_MESSAGE = "applicationmanager.generate_bundle";

	protected static final String IMPORT_MESSAGE = "applicationmanager.import";

	protected static final String SAVE_IN_PROPERTIES_MESSAGE = "applicationmanager.save_in_properties";

	protected static final String FILE_MESSAGE = "applicationmanager.file";

	protected static final String INTERNACIONALIZED_TEXT_TITLE = "applicationmanager.internacionaliced_text_title";

	/**
	 * Text property.
	 */
	public static final String CANCEL_DIALOG_TITLE = "applicationmanager.operation";

	/**
	 * Text property.
	 */
	public static final String CANCEL_DIALOG_TITLE_es_ES = "Operación";

	/**
	 * Text property.
	 */
	public static final String THREADS_MONITOR_TITLE = "applicationmanager.thread_monitor_title";

	/**
	 * Text property.
	 */
	public static final String THREADS_MONITOR_TITLE_es_ES = "Operaciones en curso";

	/**
	 * Variable that determines whether the visible permission must be checked in the toolbar
	 * components. If this variable is false, the permission that configures the toolbar components as
	 * not visible will be ignored.
	 */
	public static boolean CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS = false;

	/**
	 * Enables or disables the debugging of the security of the application. With this variable turned
	 * on, the security messages will be printed.
	 */
	public static boolean DEBUG_SECURITY = false;

	/**
	 * Default debug variable for the application. The main characteristics can be debugged turning this
	 * variable on.
	 */
	public static boolean DEBUG = false;

	/**
	 * Minor debug variable.
	 */
	public static boolean DEBUG_DETAILS = false;

	/**
	 * Configures the application to debug the time related issues.
	 */
	public static boolean DEBUG_TIMES = false;

	/**
	 * Variable that denotes whether Ontimize Pluggable Look&Feel is used.
	 */
	public static boolean useOntimizePlaf = false;

	private static ImageIcon imatiaIcon = null;

	private static ImageIcon okIcon = null;

	private static ImageIcon saveIcon = null;

	private static ImageIcon cancelIcon = null;

	private static ImageIcon attachmentIcon = null;

	private static ImageIcon deleteAttachIcon = null;

	private static ImageIcon extOpThreadsMonitorIcon = null;

	private static ImageIcon downloadAttachIcon = null;

	private static ImageIcon cancelOperationDialogIcon = null;

	private static long currentTime = -1;

	private static List printingProcessesInProgressId = new Vector();

	private static Cursor zoomCursor = null;

	private static Cursor detailCursor = null;

	private static Cursor deactivateLinkCursor = null;

	private static Cursor helpOnItemCursor = null;

	private static Cursor helpOnFieldCursor = null;

	private static List operationThreadQueue = null;

	private static OPThreadsMonitor monitorThreads = null;

	private static ImageIcon helpIcon = null;

	private static ImageIcon refreshTableIcon = null;

	private static Application application = null;

	private static JFrame window = null;

	private static JDialog systemPropertiesW = null;

	private static String businessPropertiesFile = null;

	private static Locale locale = Locale.getDefault();

	private static IRemoteAdministrationWindow remoteAdminWindow;

	/**
	 * This class defines a window to manage the threads that performs operations in the client. The
	 * elements to manage must be OperationThread instances.
	 */
	public static class OPThreadsMonitor extends JFrame {

		List threads = null;

		JList list = new JList(new DefaultListModel());

		Thread updateThread = new Thread() {

			@Override
			public void run() {
				while (true) {
					((DefaultListModel) OPThreadsMonitor.this.list.getModel()).removeAllElements();
					if ((OPThreadsMonitor.this.threads != null) && (!OPThreadsMonitor.this.threads.isEmpty())) {
						for (int i = 0; i < OPThreadsMonitor.this.threads.size(); i++) {
							final Object o = OPThreadsMonitor.this.threads.get(i);
							if (o instanceof OperationThread) {
								// Read info
								String description = ((OperationThread) o).getDescription();
								if (description == null) {
									description = Integer.toString(i);
								}
								final String status = ((OperationThread) o).getStatus();
								final StringBuilder sb = new StringBuilder(description);
								sb.append(": ");
								sb.append(status);
								((DefaultListModel) OPThreadsMonitor.this.list.getModel())
								.add(((DefaultListModel) OPThreadsMonitor.this.list.getModel()).getSize(),
										sb.toString());
							}
						}
					}
					try {
						Thread.sleep(1000);
					} catch (final Exception e) {
						ApplicationManager.logger.trace(null, e);
					}
				}
			}
		};

		/**
		 * Creates a OperationThread Monitor
		 * @param opThreads a List of OperationThread objects
		 */
		public OPThreadsMonitor(final List opThreads) {
			super(ApplicationManager.THREADS_MONITOR_TITLE_es_ES);
			this.threads = opThreads;
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.getContentPane().add(new JScrollPane(this.list));
			this.updateThread.start();
			this.setSize(400, 150);
			ApplicationManager.center(this);
		}

	}

	/**
	 * Creates an unique identifier for a printing process. This identifier must be used to remove the
	 * process when finished.
	 * @return the process identifier
	 */
	public synchronized static short startedPrintingProcess() {
		short max = (short) 0;
		for (int i = 0; i < ApplicationManager.printingProcessesInProgressId.size(); i++) {
			final Short index = (Short) ApplicationManager.printingProcessesInProgressId.get(i);
			max = (short) Math.max(index.shortValue(), max);
		}
		ApplicationManager.printingProcessesInProgressId.add(new Short((short) (max + 1)));
		return (short) (max + 1);
	}

	/**
	 * Removes a process from the printing processes list.
	 * @param processIndex the process index.
	 */
	public synchronized static void endedPrintingProcess(final short processIndex) {
		ApplicationManager.printingProcessesInProgressId.remove(new Short(processIndex));
	}

	/**
	 * Checks whether there are printing processes being executed.
	 * @return true in case some printing process is being executed
	 */
	public synchronized static boolean printingJobInProgress() {
		if (!ApplicationManager.printingProcessesInProgressId.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	private static ClientSecurityManager clientSecurityManager;

	/**
	 * This class implements a dialog to manage the OperatonThreads. This dialog offers the possibility
	 * of canceling the operation.
	 */
	public static class CancelOperationDialog extends JDialog {

		public static Font font = Font.decode("Arial-REGULAR-14");

		public static Color foreground = Color.white;

		public static Color background = Color.BLACK;

		public static Color borderColor = Color.white;

		protected int updateTime = 300;

		protected JButton cancelButton = new JButton("application.cancel");

		protected Component previousGlassPane = null;

		protected boolean previousVisible = false;

		protected JLabel stateLabel = new JLabel() {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				if (d.width > 500) {
					d.width = 500;
				}
				return d;
			}
		};

		protected ActionListener cancelListener = null;

		/**
		 * Frees all the resources this dialog is using.
		 */
		@Override
		public void dispose() {
			if (this.previousGlassPane != null) {
				((RootPaneContainer) this.getOwner()).setGlassPane(this.previousGlassPane);
				this.previousGlassPane.setVisible(this.previousVisible);
				this.previousGlassPane = null;
			}

			super.dispose();

			try {
				this.operationThread = null;
				this.progressThread.join(3000);

				if (this.cancelButton != null) {
					this.cancelButton.removeActionListener(this.cancelListener);
				}
				this.cancelButton = null;
				this.stateLabel = null;
				this.tFinished = null;
				this.progressBar = null;
				this.progressThread = null;
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
				this.operationThread = null;
				if (this.cancelButton != null) {
					this.cancelButton.removeActionListener(this.cancelListener);
				}
				this.cancelButton = null;
				this.stateLabel = null;
				this.tFinished = null;
				this.progressBar = null;
				this.progressThread = null;
			}
		}

		protected OperationThread operationThread = null;

		protected Thread tFinished = new Thread() {

			@Override
			public void run() {
				this.setPriority(Thread.MAX_PRIORITY);
				while ((CancelOperationDialog.this.operationThread != null)
						&& (!(CancelOperationDialog.this.operationThread
								.hasStarted()) || /*
								 * op . hasFinished ( ) == false
								 */CancelOperationDialog.this.operationThread.isAlive())) {
					try {
						Thread.sleep(100);
					} catch (final Exception e) {
						ApplicationManager.logger.trace(null, e);
					}
				}
				// Now hide the dialog because operation is finished
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						CancelOperationDialog.this.dispose();
					}
				});
			}
		};

		/**
		 * The progress bas that is shown while the operation is being performed.
		 */
		protected static class ProgressBar extends JProgressBar {

		}

		protected ProgressBar progressBar = new ProgressBar();

		protected Thread progressThread = new Thread() {

			boolean increasing = true;

			boolean changedState = false;

			@Override
			public void run() {
				if (CancelOperationDialog.this.operationThread != null) {
					this.setPriority(Thread.MAX_PRIORITY);
					CancelOperationDialog.this.progressBar
					.setMinimum(-CancelOperationDialog.this.progressBar.getPreferredSize().width);
					CancelOperationDialog.this.progressBar
					.setMaximum(CancelOperationDialog.this.progressBar.getPreferredSize().width);
					CancelOperationDialog.this.progressBar
					.setValue(-CancelOperationDialog.this.progressBar.getPreferredSize().width);
				}
				boolean bEven = false;
				String sPreviousStatus = null;
				while ((CancelOperationDialog.this.operationThread != null)
						&& (!(CancelOperationDialog.this.operationThread
								.hasStarted()) || (!CancelOperationDialog.this.operationThread.hasFinished()))) {
					bEven = !bEven;
					// Status
					this.changedState = false;
					String stringStatus = CancelOperationDialog.this.operationThread.getStatus();
					if ((sPreviousStatus != null) && sPreviousStatus.equals(stringStatus)) {
						// Not update
					} else {
						try {
							if ((ApplicationManager.getApplication() != null) && (ApplicationManager.getApplication()
									.getResourceBundle() != null)) {
								stringStatus = ApplicationManager.getApplication()
										.getResourceBundle()
										.getString(CancelOperationDialog.this.operationThread.getStatus());
							}
						} catch (final Exception e) {
							ApplicationManager.logger.debug(null, e);
						}
						CancelOperationDialog.this.stateLabel.setText(stringStatus);
						sPreviousStatus = CancelOperationDialog.this.operationThread.getStatus();
						this.changedState = true;
					}
					final boolean bAuxEven = bEven;
					try {
						SwingUtilities.invokeAndWait(new Runnable() {

							@Override
							public void run() {
								if (bAuxEven) {
									CancelOperationDialog.this.externalPack();
								}
								CancelOperationDialog.this.progressBar.setMinimum(
										-CancelOperationDialog.this.progressBar.getWidth());
								CancelOperationDialog.this.progressBar.setMaximum(
										CancelOperationDialog.this.progressBar.getWidth());
								if (changedState) {
									CancelOperationDialog.this.stateLabel.repaint();
								}
								// Paint progress panel
								final int value = CancelOperationDialog.this.progressBar.getValue();
								if ((value < CancelOperationDialog.this.progressBar
										.getWidth()) && (increasing)) {
									CancelOperationDialog.this.progressBar.setValue(value + 10);
								} else {
									increasing = false;
									CancelOperationDialog.this.progressBar.setValue(value - 10);
									if (value <= -CancelOperationDialog.this.progressBar
											.getWidth()) {
										increasing = true;
									}
								}
								CancelOperationDialog.this.progressBar.repaint();
							}
						});
					} catch (final Exception e) {
						ApplicationManager.logger.error(null, e);
					}
					try {
						Thread.sleep(CancelOperationDialog.this.updateTime);
					} catch (final Exception e) {
						ApplicationManager.logger.error(null, e);
					}
				}
			}
		};

		@Override
		public void setVisible(final boolean visible) {
			if (this.isModal() && (this.getOwner() instanceof RootPaneContainer)) {
				if (visible) {
					((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(true);
				} else {
					((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(false);
				}
			}
			super.setVisible(visible);
		};

		private void init() {
			this.tFinished.start();
			this.progressThread.start();
			Thread.yield();
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.getContentPane().setLayout(new GridBagLayout());
			if (this.getContentPane() instanceof JPanel) {
				final CompoundBorder border = new CompoundBorder(new LineBorder(CancelOperationDialog.borderColor, 1),
						new EmptyBorder(new Insets(20, 10, 0, 10)));
				((JPanel) this.getContentPane()).setBorder(border);

				((JPanel) this.getContentPane()).setBackground(CancelOperationDialog.background);
				((JPanel) this.getContentPane()).setOpaque(true);
			}

			this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel"));
			this.cancelButton.setMargin(new Insets(2, 2, 0, 2));
			this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
			this.cancelButton.setOpaque(false);
			if (ApplicationManager.cancelOperationDialogIcon != null) {
				final JLabel lIco = new JLabel(ApplicationManager.cancelOperationDialogIcon);
				lIco.setBorder(new EmptyBorder(2, 8, 2, 8));
				this.getContentPane()
				.add(lIco, new GridBagConstraints(0, 0, 1, 2, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
			}

			this.stateLabel.setForeground(CancelOperationDialog.foreground);
			this.stateLabel.setFont(CancelOperationDialog.font);
			this.getContentPane()
			.add(this.stateLabel,
					new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(6, 1, 5, 1), 0, 0));
			this.progressBar.setForeground(Color.yellow);
			this.getContentPane()
			.add(this.progressBar,
					new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 20, 5), 0, 0));
			this.getContentPane()
			.add(this.cancelButton,
					new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(1, 2, 5, 5), 0, 0));
			this.cancelListener = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (MessageDialog.showMessage(CancelOperationDialog.this, "applicationmanager.cancel_operation",
							JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
							ApplicationManager.getApplicationBundle()) == JOptionPane.YES_OPTION) {
						CancelOperationDialog.this.operationThread.cancel();
						CancelOperationDialog.this.dispose();
					}
				}
			};

			if (this.isModal() && (this.getOwner() instanceof RootPaneContainer)) {
				this.previousGlassPane = ((RootPaneContainer) this.getOwner()).getGlassPane();
				if (this.previousGlassPane != null) {
					this.previousVisible = this.previousGlassPane.isVisible();
				}
				((RootPaneContainer) this.getOwner()).setGlassPane(new OGlassPanel());
				((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(false);
			}

			this.cancelButton.addActionListener(this.cancelListener);
			this.setUndecorated(true);
			this.pack();
			this.setSize(320, 140);
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width / 2) - (this.getSize().width / 2), (d.height / 2) - (this.getSize().height / 2));
			this.setResizable(false);

			try {
				AWTUtilities.setWindowOpacity(this, 0.7f);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}

		/**
		 * Creates a dialog for an operation that can be canceled.
		 * @param dialog the dialog over which the operation dialog will be launched.
		 * @param op the operation thread that performs the operation
		 */
		public CancelOperationDialog(final Dialog dialog, final OperationThread op) {
			super(dialog, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.operationThread = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for an operation that can be canceled.
		 * @param frame the frame over which the operation dialog will be launched.
		 * @param op the operation thread that performs the operation
		 */
		public CancelOperationDialog(final Frame frame, final OperationThread op) {
			super(frame, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.operationThread = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for an operation that can be canceled.
		 * @param dialog the dialog over which the operation dialog will be launched.
		 * @param op the operation thread that performs the operation
		 * @param updateTime the time in milliseconds the thread will wait until notice that the operation
		 *        has finished
		 */
		public CancelOperationDialog(final Dialog dialog, final OperationThread op, final int updateTime) {
			super(dialog, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.updateTime = updateTime;
			this.operationThread = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for an operation that can be canceled.
		 * @param frame the frame over which the operation dialog will be launched.
		 * @param op the operation thread that performs the operation
		 * @param updateTime the time in milliseconds the thread will wait until notice that the operation
		 *        has finished
		 */
		public CancelOperationDialog(final Frame frame, final OperationThread op, final int updateTime) {
			super(frame, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.updateTime = updateTime;
			this.operationThread = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Packs the dialog.
		 */
		public void externalPack() {
			final Dimension d = this.getPreferredSize();
			if ((Math.abs(this.getSize().width - d.width) > 20) || (Math.abs(this.getSize().height - d.height) > 20)) {
				this.setSize(Math.min(500, d.width), d.height + 5);
				this.validate();
				this.doLayout();
				ApplicationManager.center(this);
			}
		}

		/**
		 * Sets a text to the dialog, to represent the status of the operation.
		 * @param text the text to display
		 */
		public void setStatusText(final String text) {
			this.stateLabel.setText(text);
			if (SwingUtilities.isEventDispatchThread()) {
				this.stateLabel.paintImmediately(this.stateLabel.getBounds());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						CancelOperationDialog.this.stateLabel
						.paintImmediately(CancelOperationDialog.this.stateLabel.getBounds());
					}
				});
			}
		}

	}

	/**
	 * Class that implements a dialog corresponding to a ExtendedOperationThread.
	 */
	public static class CancelExtendedOperationDialog extends JDialog {

		protected static final String Q_CANCEL_OPERATION = "applicationmanager.cancel_operation";

		protected static final String ESTIMATED_TIME_REMAINING = "applicationmanager.estimated_time";

		protected static final String SECONDS = " s";

		protected static final String UNKNOWN = "unknown";

		protected static final String CANCEL = "application.cancel";

		protected int timeUpdate = 300;

		protected JButton cancelButton = new JButton(
				ApplicationManager.getTranslation(CancelExtendedOperationDialog.CANCEL));

		protected JLabel state = new JLabel() {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				if (d.width > 500) {
					d.width = 500;
				}
				return d;
			}
		};

		protected JLabel estimatedTime = new JLabel();

		protected ExtendedOperationThread op = null;

		protected ActionListener cancelListener = null;

		protected Component previousGlassPane = null;

		protected boolean previousVisible = false;

		@Override
		public void setVisible(final boolean visible) {
			if (this.isModal() && (this.getOwner() instanceof RootPaneContainer)) {
				if (visible) {
					((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(true);
				} else {
					((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(false);
				}
			}
			super.setVisible(visible);
		};

		/**
		 * Frees all the resources occupied by the dialog.
		 */
		@Override
		public void dispose() {
			if (this.previousGlassPane != null) {
				((RootPaneContainer) this.getOwner()).setGlassPane(this.previousGlassPane);
				((RootPaneContainer) this.getOwner()).getGlassPane().setVisible(this.previousVisible);
				this.previousGlassPane = null;
			}

			super.dispose();
			try {
				synchronized (this.op) {
					this.op = null;
				}

				this.progressThread.join(3000);
				if (this.cancelButton != null) {
					this.cancelButton.removeActionListener(this.cancelListener);
				}
				this.cancelButton = null;
				this.state = null;
				this.tFinished = null;
				this.progressBar = null;
				this.progressThread = null;
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
				this.op = null;
				if (this.cancelButton != null) {
					this.cancelButton.removeActionListener(this.cancelListener);
				}
				this.cancelButton = null;
				this.state = null;
				this.tFinished = null;
				this.progressBar = null;
				this.progressThread = null;
			}
		}

		protected Thread tFinished = new Thread() {

			@Override
			public void run() {
				this.setPriority(Thread.MAX_PRIORITY);
				while ((CancelExtendedOperationDialog.this.op != null) && (!(CancelExtendedOperationDialog.this.op
						.hasStarted()) || CancelExtendedOperationDialog.this.op.isAlive())) {
					try {
						Thread.sleep(100);
					} catch (final Exception e) {
						ApplicationManager.logger.error(null, e);
					}
				}
				// Hide the dialog because operation is finished
				CancelExtendedOperationDialog.this.dispose();
			}
		};

		protected JProgressBar progressBar = new JProgressBar();

		protected Thread progressThread = new Thread() {

			boolean creciente = true;

			@Override
			public void run() {
				if (CancelExtendedOperationDialog.this.op != null) {
					this.setPriority(Thread.MAX_PRIORITY);
					CancelExtendedOperationDialog.this.progressBar
					.setMaximum(CancelExtendedOperationDialog.this.op.getProgressDivisions());
				}
				boolean bEven = false;
				String previousStatus = null;
				while ((CancelExtendedOperationDialog.this.op != null) && (!(CancelExtendedOperationDialog.this.op
						.hasStarted()) || (!CancelExtendedOperationDialog.this.op.hasFinished()))) {
					bEven = !bEven;
					// status
					if (CancelExtendedOperationDialog.this.progressBar
							.getMaximum() != CancelExtendedOperationDialog.this.op
							.getProgressDivisions()) {
						CancelExtendedOperationDialog.this.progressBar
						.setMaximum(CancelExtendedOperationDialog.this.op.getProgressDivisions());
					}
					String strStatus = CancelExtendedOperationDialog.this.op.getStatus();
					if ((previousStatus != null) && previousStatus.equals(strStatus)) {
						// Not update
					} else {
						try {
							if ((ApplicationManager.getApplication() != null) && (ApplicationManager.getApplication()
									.getResourceBundle() != null)) {
								strStatus = ApplicationManager.getApplication()
										.getResourceBundle()
										.getString(CancelExtendedOperationDialog.this.op.getStatus());
							}
						} catch (final Exception e) {
							ApplicationManager.logger.error(null, e);
						}
						CancelExtendedOperationDialog.this.state.setText(strStatus);
						previousStatus = CancelExtendedOperationDialog.this.op.getStatus();
					}

					if (CancelExtendedOperationDialog.this.op.getEstimagedTimeLeftText() != null) {
						CancelExtendedOperationDialog.this.estimatedTime
						.setText(CancelExtendedOperationDialog.this.op.getEstimagedTimeLeftText());
					} else if (CancelExtendedOperationDialog.this.op
							.getEstimatedTimeLeft() != ExtendedOperationThread.UNKNOWN) {
						CancelExtendedOperationDialog.this.estimatedTime.setText(ApplicationManager.getTranslation(
								CancelExtendedOperationDialog.ESTIMATED_TIME_REMAINING) + " "
								+ (int) (CancelExtendedOperationDialog.this.op
										.getEstimatedTimeLeft() / 1000.0)
								+ CancelExtendedOperationDialog.SECONDS);
					} else {
						CancelExtendedOperationDialog.this.estimatedTime.setText(ApplicationManager
								.getTranslation(CancelExtendedOperationDialog.ESTIMATED_TIME_REMAINING)
								+ ApplicationManager
								.getTranslation(CancelExtendedOperationDialog.UNKNOWN));
					}

					final boolean auxEven = bEven;
					try {
						SwingUtilities.invokeAndWait(new Runnable() {

							@Override
							public void run() {
								if (auxEven) {
									CancelExtendedOperationDialog.this.externalPack();
								}
								CancelExtendedOperationDialog.this.state.paintImmediately(
										CancelExtendedOperationDialog.this.state.getBounds());
								CancelExtendedOperationDialog.this.estimatedTime.paintImmediately(
										CancelExtendedOperationDialog.this.estimatedTime
										.getBounds());
								// Paint the progress panel
								CancelExtendedOperationDialog.this.progressBar.setValue(
										CancelExtendedOperationDialog.this.op.getCurrentPosition());
								CancelExtendedOperationDialog.this.progressBar.paintImmediately(
										CancelExtendedOperationDialog.this.progressBar.getBounds());
							}
						});
					} catch (final Exception e) {
						ApplicationManager.logger.error(null, e);
					}
					try {
						Thread.sleep(CancelExtendedOperationDialog.this.timeUpdate);
					} catch (final Exception e) {
						ApplicationManager.logger.error(null, e);
					}
				}
			}
		};

		/**
		 * Packs the dialog.
		 */
		public void externalPack() {
			final Dimension d = this.getPreferredSize();
			if ((Math.abs(this.getSize().width - d.width) > 20) || (Math.abs(this.getSize().height - d.height) > 20)) {
				this.setSize(Math.min(500, d.width), d.height + 5);
				this.validate();
				this.doLayout();
				ApplicationManager.center(this);
			}
		}

		private void init() {
			this.tFinished.start();
			this.progressThread.start();
			Thread.yield();
			this.progressBar.setStringPainted(true);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.getContentPane().setLayout(new GridBagLayout());
			this.cancelButton.setMargin(new Insets(0, 2, 0, 2));
			this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));

			if (this.getContentPane() instanceof JPanel) {
				final CompoundBorder border = new CompoundBorder(new LineBorder(CancelOperationDialog.borderColor, 1),
						new EmptyBorder(new Insets(20, 10, 0, 10)));
				((JPanel) this.getContentPane()).setBorder(border);

				((JPanel) this.getContentPane()).setBackground(CancelOperationDialog.background);
				((JPanel) this.getContentPane()).setOpaque(true);
			}

			if (ApplicationManager.cancelOperationDialogIcon != null) {
				final JLabel lIco = new JLabel(ApplicationManager.cancelOperationDialogIcon);
				lIco.setBorder(new EmptyBorder(6, 8, 6, 8));
				this.getContentPane()
				.add(lIco, new GridBagConstraints(0, 0, 1, 2, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
			}

			this.state.setForeground(CancelOperationDialog.foreground);
			this.state.setFont(CancelOperationDialog.font);

			this.estimatedTime.setForeground(CancelOperationDialog.foreground);
			this.estimatedTime.setFont(CancelOperationDialog.font);

			this.getContentPane()
			.add(this.state, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 1, 5, 1), 0, 0));
			this.getContentPane()
			.add(this.estimatedTime, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(6, 1, 1, 5), 0, 0));
			this.getContentPane()
			.add(this.progressBar,
					new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 5, 5), 0, 0));
			this.getContentPane()
			.add(this.cancelButton,
					new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(1, 2, 5, 5), 0, 0));
			this.cancelListener = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (MessageDialog.showMessage(CancelExtendedOperationDialog.this,
							CancelExtendedOperationDialog.Q_CANCEL_OPERATION, JOptionPane.QUESTION_MESSAGE,
							JOptionPane.YES_NO_OPTION,
							ApplicationManager.getApplicationBundle()) == JOptionPane.YES_OPTION) {
						CancelExtendedOperationDialog.this.op.cancel();
						CancelExtendedOperationDialog.this.dispose();
					}
				}
			};

			if (this.isModal() && (this.getOwner() instanceof RootPaneContainer)) {
				this.previousGlassPane = ((RootPaneContainer) this.getOwner()).getGlassPane();
				if (this.previousGlassPane != null) {
					this.previousVisible = this.previousGlassPane.isVisible();
				}
				((RootPaneContainer) this.getOwner()).setGlassPane(new OGlassPanel());
			}

			this.cancelButton.addActionListener(this.cancelListener);
			this.cancelButton.setOpaque(false);
			this.setUndecorated(true);
			this.pack();
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width / 2) - (this.getSize().width / 2), (d.height / 2) - (this.getSize().height / 2));
			// All the time this dialog is repacking, then resize is not a good
			// idea
			this.setResizable(false);

			try {
				AWTUtilities.setWindowOpacity(this, 0.7f);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}

		}

		/**
		 * Creates a dialog for extended operation threads.
		 * @param dialog
		 * @param op
		 */
		public CancelExtendedOperationDialog(final Dialog dialog, final ExtendedOperationThread op) {
			super(dialog, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.op = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for extended operation threads.
		 * @param frame
		 * @param op
		 */
		public CancelExtendedOperationDialog(final Frame frame, final ExtendedOperationThread op) {
			super(frame, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.op = op;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for extended operation threads.
		 * @param dialog
		 * @param op
		 * @param timeUpdate the time in milliseconds the thread will wait until notice that the operation
		 *        has finished
		 */
		public CancelExtendedOperationDialog(final Dialog dialog, final ExtendedOperationThread op, final int timeUpdate) {
			super(dialog, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.op = op;
			this.timeUpdate = timeUpdate;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Creates a dialog for extended operation threads.
		 * @param frame
		 * @param op
		 * @param timeUpdate the time in milliseconds the thread will wait until notice that the operation
		 *        has finished
		 */
		public CancelExtendedOperationDialog(final Frame frame, final ExtendedOperationThread op, final int timeUpdate) {
			super(frame, ApplicationManager.getTranslation(ApplicationManager.CANCEL_DIALOG_TITLE), true);
			this.op = op;
			this.timeUpdate = timeUpdate;
			if (op.getDescription() != null) {
				this.setTitle(this.getTitle() + ":" + op.getDescription());
			}
			this.init();
		}

		/**
		 * Sets the status text.
		 * @param text
		 */
		public void setStatusText(final String text) {
			this.state.setText(text);
			if (SwingUtilities.isEventDispatchThread()) {
				this.state.paintImmediately(this.state.getBounds());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						CancelExtendedOperationDialog.this.state
						.paintImmediately(CancelExtendedOperationDialog.this.state.getBounds());
					}
				});
			}
		}

	}

	/**
	 * Creates an ApplicationManager instance, with several client application utilities.
	 */
	private ApplicationManager() {
		// empty constructor
	}

	/**
	 * Provides a reference to the ClientSecurityManager of the client application. Only one
	 * ClientSecurityManager is allowed in each client.
	 * @return the ClientSecurityManager
	 */
	public static ClientSecurityManager getClientSecurityManager() {
		return ApplicationManager.clientSecurityManager;
	}

	/**
	 * Returns the current application locale.
	 * @return the application locale.
	 */
	public static Locale getLocale() {
		if (ApplicationManager.application != null) {
			ApplicationManager.locale = ApplicationManager.application.getLocale();
		}
		return ApplicationManager.locale;
	}

	/**
	 * Returns the name of the business properties file, in which the business calendar is defined.
	 * @return the business calendar properties file
	 * @deprecated
	 */
	@Deprecated
	public static String getBusinessPropertiesFile() {
		return ApplicationManager.businessPropertiesFile;
	}

	/**
	 * Sets the ClientSecurityManager for the application.
	 * @param clientSecurityManager
	 */
	public static void setClientSecurityManager(final ClientSecurityManager clientSecurityManager) {
		ApplicationManager.clientSecurityManager = clientSecurityManager;
	}

	/**
	 * Sets the ClientSecurityManager, the locale and the business file to the application.
	 * @param clientSecurityManager the ClientSecurityManager
	 * @param locale the application locale
	 * @param businessPropertiesFile the business properties file
	 */
	public static void setClientSecurityManager(final ClientSecurityManager clientSecurityManager, final Locale locale,
			final String businessPropertiesFile) {
		ApplicationManager.clientSecurityManager = clientSecurityManager;
		ApplicationManager.locale = locale;
		ApplicationManager.businessPropertiesFile = businessPropertiesFile;
	}

	public static OperationThread proccessNotCancelableOperation(final Component source, final OperationThread opThread,
			final int milliseconds) {
		final Window ancestor = SwingUtilities.getWindowAncestor(source);
		if (ancestor instanceof Frame) {
			return ApplicationManager.proccessNotCancelableOperation((Frame) ancestor, opThread, milliseconds);
		} else if (ancestor instanceof Dialog) {
			return ApplicationManager.proccessNotCancelableOperation((Dialog) ancestor, opThread, milliseconds);
		}
		return ApplicationManager.proccessNotCancelableOperation((Frame) null, opThread, milliseconds);
	}

	/**
	 * Executes a operation thread. If the operation takes more time to be executed that the time passed
	 * as parameter, a window to cancel the operation is showed.
	 * @param frame
	 * @param opThread
	 * @param milliseconds the time the thread waits before execution
	 * @return
	 */
	public static OperationThread proccessNotCancelableOperation(final Dialog dialog, final OperationThread opThread,
			final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Now start the operation.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(dialog, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setVisible(false);
				d.cancelButton.setEnabled(false);
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Executes a operation thread. If the operation takes more time to be executed that the time passed
	 * as parameter, a window to cancel the operation is showed.
	 * @param frame
	 * @param opThread
	 * @param milliseconds the time the thread waits before execution
	 * @return
	 */
	public static OperationThread proccessNotCancelableOperation(final Frame frame, final OperationThread opThread,
			final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Now start the operation.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(frame, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setVisible(false);
				d.cancelButton.setEnabled(false);
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Starts an operation. In case the operation takes more than the specified milliseconds to end the
	 * execution, a window to cancel the operation is shown. After the execution the operation is
	 * returned, so the result and the execution status can be checked.
	 * @param frame
	 * @param opThread
	 * @param milliseconds
	 * @return
	 */
	public static OperationThread proccessOperation(final Frame frame, final OperationThread opThread, final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(frame, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Starts an operation. In case the operation takes more than the specified milliseconds to end the
	 * execution, a window to cancel the operation is shown. After the execution the operation is
	 * returned, so the result and the execution status can be checked.
	 * @param frame
	 * @param opThread
	 * @param milliseconds
	 * @param dialogRefreshTime refresh period of the dialog
	 * @return
	 */
	public static OperationThread proccessOperation(final Frame frame, final OperationThread opThread, final int milliseconds,
			final int dialogRefreshTime) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(frame, opThread, dialogRefreshTime);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Starts an operation. In case the operation takes more than the specified milliseconds to end the
	 * execution, a window to cancel the operation is shown. After the execution the operation is
	 * returned, so the result and the execution status can be checked.
	 * @param dialog
	 * @param opThread
	 * @param milliseconds
	 * @return
	 */
	public static OperationThread proccessOperation(final Dialog dialog, final OperationThread opThread, final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(dialog, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Starts an operation. In case the operation takes more than the specified milliseconds to end the
	 * execution, a window to cancel the operation is shown. After the execution the operation is
	 * returned, so the result and the execution status can be checked.
	 * @param dialog
	 * @param opThread
	 * @param milliseconds
	 * @param updateTime
	 * @return
	 */
	public static OperationThread proccessOperation(final Dialog dialog, final OperationThread opThread, final int milliseconds,
			final int updateTime) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelOperationDialog(dialog, opThread, updateTime);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Starts an operation. In case the operation takes more than the specified milliseconds to end the
	 * execution, a window to cancel the operation is shown. After the execution the operation is
	 * returned, so the result and the execution status can be checked.
	 * @param dialog
	 * @param opThread
	 * @param milliseconds
	 * @return
	 */
	public static ExtendedOperationThread proccessOperation(final Dialog dialog, final ExtendedOperationThread opThread,
			final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelExtendedOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || /*
		 * opThread . hasFinished ( ) == false
		 */opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelExtendedOperationDialog(dialog, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Executes the ExtendedOperationThread. The dialog has a button to cancel the operation and shows
	 * the current process status.
	 * @param dialog
	 * @param opThread
	 * @param milliseconds
	 * @param timeUpdate
	 * @return
	 */
	public static ExtendedOperationThread proccessOperation(final Dialog dialog, final ExtendedOperationThread opThread,
			final int milliseconds, final int timeUpdate) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelExtendedOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelExtendedOperationDialog(dialog, opThread, timeUpdate);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Executes the ExtendedOperationThread. The dialog has a button to cancel the operation and shows
	 * the current process status.
	 * @param f
	 * @param opThread
	 * @param milliseconds
	 * @return
	 */
	public static ExtendedOperationThread proccessOperation(final Frame f, final ExtendedOperationThread opThread,
			final int milliseconds) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelExtendedOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelExtendedOperationDialog(f, opThread);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Executes the ExtendedOperationThread. The dialog has a button to cancel the operation and shows
	 * the current process status.
	 * @param f
	 * @param opThread
	 * @param milliseconds
	 * @param timeUpdate
	 * @return
	 */
	public static ExtendedOperationThread proccessOperation(final Frame f, final ExtendedOperationThread opThread, final int milliseconds,
			final int timeUpdate) {
		if (!SwingUtilities.isEventDispatchThread()) {
			ApplicationManager.logger
			.warn("The thread that invokes proccessOperation() is not an EventDispatchThread instance");
		}
		// Start the operation. Wait 3 seconds.
		final long t = System.currentTimeMillis();
		opThread.setPriority(Thread.MAX_PRIORITY);
		opThread.start();
		Thread.yield();
		CancelExtendedOperationDialog d = null;
		while (!(opThread.isCancelled()) && (!(opThread.hasStarted()) || /*
		 * opThread . hasFinished ( ) == false
		 */opThread.isAlive())) {
			if (((System.currentTimeMillis() - t) > milliseconds) && (d == null)) {
				d = new CancelExtendedOperationDialog(f, opThread, timeUpdate);
				d.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				d.cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				d.setVisible(true);
			}
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}
		if (d != null) {
			d.dispose();
		}
		return opThread;
	}

	/**
	 * Executes the ExtendedOperationThread. The dialog has a button to cancel the operation and shows
	 * the current process status.
	 */
	public static OperationThread proccessOperation(final OperationThread opThread, final int milliseconds) {
		return ApplicationManager.proccessOperation((JFrame) null, opThread, milliseconds);
	}

	/**
	 * Executes the ExtendedOperationThread. The dialog has a button to cancel the operation and shows
	 * the current process status. After 3000 milliseconds a cancel dialog will appear.
	 */
	public static OperationThread proccessOperation(final OperationThread opThread) {
		return ApplicationManager.proccessOperation(opThread, 3000);
	}

	/**
	 * Displays a window with all the application texts that must be translated.
	 *
	 * @see Internationalization
	 * @param application
	 */
	public static void viewInternacionalizedTexts(final Application application) {
		final JDialog d = new JDialog(application.getFrame(),
				ApplicationManager.getTranslation(ApplicationManager.INTERNACIONALIZED_TEXT_TITLE,
						application.getResourceBundle()),
				true);
		final Properties propTotal = new Properties();
		final JTextField statusField = new JTextField();
		final JList jList = new JList();
		final JMenuBar jMenuBar = new JMenuBar();
		final JMenu jFileMenu = new JMenu(
				ApplicationManager.getTranslation(ApplicationManager.FILE_MESSAGE, application.getResourceBundle()));
		final JMenuItem saveItem = new JMenuItem(ApplicationManager
				.getTranslation(ApplicationManager.SAVE_IN_PROPERTIES_MESSAGE, application.getResourceBundle()));
		final JMenuItem importItem = new JMenuItem(
				ApplicationManager.getTranslation(ApplicationManager.IMPORT_MESSAGE, application.getResourceBundle()));
		jMenuBar.add(jFileMenu);
		jFileMenu.add(saveItem);
		jFileMenu.addSeparator();
		jFileMenu.add(importItem);
		d.setJMenuBar(jMenuBar);
		final List vTexts = application.getTextsToTranslate();
		// Delete duplicate ones
		final Vector withoutDuplicateTexts = new Vector();
		for (int i = 0; i < vTexts.size(); i++) {
			final Object oText = vTexts.get(i);
			if ((oText != null) && (!withoutDuplicateTexts.contains(oText))) {
				if (!(oText instanceof Comparable)) {
					ApplicationManager.logger.info("Not comparable Object {}", oText);
					continue;
				}
				withoutDuplicateTexts.add(oText);
			}
		}
		// Put in a properties
		for (int i = 0; i < withoutDuplicateTexts.size(); i++) {
			final Object oText = withoutDuplicateTexts.get(i);
			if (oText != null) {
				propTotal.setProperty(oText.toString(), "");
			}
		}
		importItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Load properties
				final Properties prop = new Properties();
				final JFileChooser fc = new JFileChooser();
				final int option = fc.showOpenDialog(d);
				if (option == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					try {
						prop.load(new FileInputStream(f));
						// Put the properties in a properties file
						final Enumeration keys = prop.keys();
						while (keys.hasMoreElements()) {
							final Object key = keys.nextElement();
							final Object oValue = prop.get(key);
							propTotal.setProperty(key.toString(), oValue.toString());
						}
						// Update the list
						final Vector vData = new Vector();
						final Enumeration enumKeys = propTotal.keys();
						while (enumKeys.hasMoreElements()) {
							final Object oKey = enumKeys.nextElement();
							final Object oValue = propTotal.get(oKey);
							vData.add(vData.size(), oKey + "=" + oValue);
						}
						Collections.sort(vData);
						jList.setListData(vData);
						statusField.setText("Import process finish successfully");
					} catch (final Exception ex) {
						ApplicationManager.logger.trace(null, ex);
						statusField.setText(ex.getMessage());
					}
				} else {
					statusField.setText("Import process canceled");
				}
			}
		});
		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Get the word list
				if (jList.getModel().getSize() == 0) {
					statusField.setText("There are no texts to save");
				} else {
					// Now save
					final JFileChooser fc = new JFileChooser();
					final int option = fc.showSaveDialog(d);
					if (option == JFileChooser.APPROVE_OPTION) {
						final File f = fc.getSelectedFile();
						try {
							propTotal.store(new FileOutputStream(f), "Generated " + new Date().toString());
							// Save successfully
							statusField.setText("Save successfully");
						} catch (final Exception ex) {
							ApplicationManager.logger.trace(null, ex);
							statusField.setText(ex.getMessage());
						}
					} else {
						statusField.setText("Cancel save operation");
					}
				}
			}
		});
		Collections.sort(withoutDuplicateTexts);
		// Show in a TextArea:

		if (!withoutDuplicateTexts.isEmpty()) {
			jList.setListData(withoutDuplicateTexts);
			statusField.setText("Found " + withoutDuplicateTexts.size() + " texts.");
		} else {
			statusField.setText("There are not texts to translate");
		}
		// Show in the same window
		statusField.setEditable(false);
		d.getContentPane().add(statusField, BorderLayout.SOUTH);
		d.getContentPane().add(new JScrollPane(jList));
		d.pack();
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		d.setLocation((dim.width - d.getWidth()) / 2, (dim.height - d.getHeight()) / 2);
		d.setVisible(true);
	}

	/**
	 * Checks if the point is available.
	 * @param point
	 * @return
	 */

	public static Point checkAvailablePoint(final Point point) {
		final GraphicsEnvironment graphicsEnviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] graphicsDevice = graphicsEnviroment.getScreenDevices();

		for (int i = 0; i < graphicsDevice.length; i++) {
			final Rectangle currentBounds = graphicsDevice[i].getDefaultConfiguration().getBounds();
			if (currentBounds.contains(point)) {
				return point;
			}
		}

		final Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.getBounds();
		return new Point(bounds.x, bounds.y);
	}

	/**
	 * Centers the object in the screen. Useful when displaying new windows.
	 * @param window
	 */
	public static void center(final Window window) {
		SwingUtils.center(window,
				ApplicationManager.getApplication() != null ? ApplicationManager.getApplication().getFrame() : null);
	}

	/**
	 * Maximize the object in the screen. Useful when displaying new windows.
	 * @param window
	 */
	public static void maximize(final Window window) {

		Rectangle bounds = null;

		final GraphicsEnvironment graphicsEnviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] graphicsDevice = graphicsEnviroment.getScreenDevices();
		// Find main application bounds.

		if (graphicsDevice.length == 1) {
			// Single screen
			bounds = graphicsEnviroment.getMaximumWindowBounds();
		} else {
			final Rectangle windowBound = window.getBounds();
			final Point centerPoint = new Point(windowBound.x + (windowBound.width / 2),
					windowBound.y + (windowBound.height / 2));
			for (int i = 0; i < graphicsDevice.length; i++) {
				final Rectangle currentBounds = graphicsDevice[i].getDefaultConfiguration().getBounds();
				if (currentBounds.contains(centerPoint)) {
					bounds = currentBounds;
					break;
				}
			}
		}

		if (bounds == null) {
			bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice()
					.getDefaultConfiguration()
					.getBounds();
		}

		window.setBounds(bounds);
	}

	/**
	 * Sets a window location to be in the bottom-left corner of the screen.
	 * @param window
	 */
	public static void setLocationShouthWest(final Window window) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(1, d.height - window.getHeight() - 30);
	}

	/**
	 * Sets a window location to be in the bottom-right corner of the screen.
	 * @param window
	 */
	public static void setLocationSouthEast(final Window window) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(d.width - window.getWidth() - 2, d.height - window.getHeight() - 30);
	}

	/**
	 * Sets a window location to be in the top-left corner of the screen.
	 * @param window
	 */
	public static void setLocationNorthWest(final Window window) {
		window.setLocation(5, 5);
	}

	/**
	 * Sets a window location to be in the top-right corner of the screen.
	 * @param window
	 */
	public static void setLocationNorthEast(final Window window) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(d.width - window.getWidth() - 2, 5);
	}

	/**
	 * Returns the default cursor to use when the help for the Item can be shown.
	 * @return the help Cursor
	 */
	public static Cursor getHelpOnItemCursor() {
		if (ApplicationManager.helpOnItemCursor == null) {
			try {
				final ImageIcon helpOnItemIcon = ImageManager.getIcon(ImageManager.HELP_ON_ITEM_CURSOR);
				java.awt.Image iconA = null;
				if (helpOnItemIcon != null) {
					iconA = helpOnItemIcon.getImage();
				}
				ApplicationManager.helpOnItemCursor = Toolkit.getDefaultToolkit()
						.createCustomCursor(iconA, new Point(0, 0), "HelpOnItemCursor");
			} catch (final Exception e) {
				ApplicationManager.logger.error("Help cursor in item cannot be created ", e);
				return Cursor.getDefaultCursor();
			}
		}
		return ApplicationManager.helpOnItemCursor;
	}

	/**
	 * Returns the default cursor to use when the help for the Field can be shown.
	 * @return the help Cursor
	 */
	public static Cursor getHelpOnFieldCursor() {
		if (ApplicationManager.helpOnFieldCursor == null) {
			try {
				// Aux:
				final ImageIcon helpOnFieldIcon = ImageManager.getIcon(ImageManager.HELP_ON_FIELD_CURSOR);
				java.awt.Image iconA = null;
				if (helpOnFieldIcon != null) {
					iconA = helpOnFieldIcon.getImage();
				}
				ApplicationManager.helpOnFieldCursor = Toolkit.getDefaultToolkit()
						.createCustomCursor(iconA, new Point(0, 0), "HelpOnFieldCursor");
			} catch (final Exception e) {
				ApplicationManager.logger.error("Help cursor in item cannot be created", e);
				return Cursor.getDefaultCursor();
			}
		}
		return ApplicationManager.helpOnFieldCursor;
	}

	/**
	 * Returns the default cursor to use when the mouse is over a disabled link.
	 * @return the disabled link Cursor
	 */
	public static Cursor getDisabledLinkCursor() {
		if (ApplicationManager.deactivateLinkCursor == null) {
			try {
				final ImageIcon cursorLinkDisableIcon = ImageManager.getIcon(ImageManager.CURSOR_LINK_DISABLE);
				java.awt.Image iconA = null;
				if (cursorLinkDisableIcon != null) {
					iconA = cursorLinkDisableIcon.getImage();
				}
				ApplicationManager.deactivateLinkCursor = Toolkit.getDefaultToolkit()
						.createCustomCursor(iconA, new Point(0, 0), "CursorLinkDesactivado");
				ApplicationManager.logger.debug("Created cursor: {}",
						ApplicationManager.deactivateLinkCursor.toString());
			} catch (final Exception e) {
				ApplicationManager.logger.error("'Deactivated link' cursor cannot be created:", e);
				return Cursor.getDefaultCursor();
			}
		}
		ApplicationManager.logger.debug("Cursor returned: {}", ApplicationManager.deactivateLinkCursor.toString());
		return ApplicationManager.deactivateLinkCursor;
	}

	/**
	 * Returns the default cursor to use when the mouse is over an element with details.
	 * @return the details Cursor
	 */
	public static Cursor getDetailsCursor() {
		if (ApplicationManager.detailCursor == null) {
			try {
				// Aux:
				final ImageIcon cursorDetailIcon = ImageManager.getIcon(ImageManager.CURSOR_DETAIL);
				java.awt.Image iconA = null;
				if (cursorDetailIcon != null) {
					iconA = cursorDetailIcon.getImage();
				} else {
					ApplicationManager.logger.info("'Details cursor' image not found");
				}
				ApplicationManager.detailCursor = Toolkit.getDefaultToolkit()
						.createCustomCursor(iconA, new Point(0, 0), "CursorDetalles");
			} catch (final Exception e) {
				ApplicationManager.logger.error("'Details cursor' cannot be created", e);
				return Cursor.getDefaultCursor();
			}
		}
		return ApplicationManager.detailCursor;
	}

	/**
	 * Returns the cursor to use in operations involving zoom.
	 * @return the cursor to use in operations involving zoom.
	 */
	public static Cursor getZoomCursor() {
		if (ApplicationManager.zoomCursor == null) {
			try {
				// Aux:
				final ImageIcon zoomIcon = ImageManager.getIcon(ImageManager.ZOOM_CURSOR);
				java.awt.Image iconA = null;
				if (zoomIcon != null) {
					iconA = zoomIcon.getImage();
				}
				ApplicationManager.zoomCursor = Toolkit.getDefaultToolkit()
						.createCustomCursor(iconA, new Point(0, 0), "CursorZoom");
			} catch (final Exception e) {
				ApplicationManager.logger.error("'Zoom cursor' cannot be created", e);
				return Cursor.getDefaultCursor();
			}
		}
		return ApplicationManager.zoomCursor;
	}

	/**
	 * Sets the application Look&Feel. It is not recommended to call this method once the application
	 * has been launched.
	 * @param application the application, to get the frame
	 * @param lf the new look and feel
	 * @throws Exception
	 */
	public static void setLookAndFeel(final Application application, final LookAndFeel lf) throws Exception {
		UIManager.setLookAndFeel(lf);
		SwingUtilities.updateComponentTreeUI(application.getFrame());
		application.getFrame().doLayout();
	}

	/**
	 * Enqueues a thread in the OperationThreadMonitor and starts the thread. All threads executed in
	 * this way can be monitored
	 * @param thread
	 * @see OPThreadsMonitor
	 */
	public static void enqueueOperationThread(final OperationThread thread) {
		if (ApplicationManager.operationThreadQueue == null) {
			ApplicationManager.operationThreadQueue = new Vector();
			ApplicationManager.monitorThreads = new OPThreadsMonitor(ApplicationManager.operationThreadQueue);
		}
		if (ApplicationManager.monitorThreads != null) {
			ApplicationManager.monitorThreads
			.setTitle(ApplicationManager.getTranslation(ApplicationManager.THREADS_MONITOR_TITLE));
		}
		ApplicationManager.operationThreadQueue.add(thread);
		if (!thread.hasStarted()) {
			thread.start();
		}
	}

	/**
	 * Returns the class that monitors the execution of all the operation threads executed through the
	 * {@link #enqueueOperationThread(OperationThread)} method.
	 * @return the class that monitor the thread execution
	 */
	public static OPThreadsMonitor getOPThreadsMonitor() {
		if (ApplicationManager.operationThreadQueue == null) {
			ApplicationManager.operationThreadQueue = new Vector();
			ApplicationManager.monitorThreads = new OPThreadsMonitor(ApplicationManager.operationThreadQueue);
		}
		if (ApplicationManager.monitorThreads != null) {
			ApplicationManager.monitorThreads
			.setTitle(ApplicationManager.getTranslation(ApplicationManager.THREADS_MONITOR_TITLE));
		}
		return ApplicationManager.monitorThreads;
	}

	/**
	 * Checks whether the client debug window is visible.
	 * @return true if the debug window is visible, false otherwise.
	 */

	public static boolean isApplicationManagerWindowVisible() {
		if (ApplicationManager.window == null) {
			return false;
		} else {
			return ApplicationManager.window.isVisible();
		}
	}

	/**
	 * Shows or hides the client debug window, in which the client variables can be check and the debug
	 * flags can be modified.
	 * @param visible if true, the window will be shown; if false, the window will be hide.
	 */

	public static void setApplicationManagerWindowVisible(final boolean visible) {
		if (ApplicationManager.window == null) {
			ApplicationManager.createWindow();
		}
		ApplicationManager.window.setVisible(visible);
	}

	public static void setResourceBundle(final ResourceBundle bundle) {
		if (ApplicationManager.networkBitrateButton != null) {
			ApplicationManager.networkBitrateButton
			.setText(ApplicationManager.getTranslation(ApplicationManager.CHANGE_NETWORK_BITRATE_MESSAGE, bundle));
		}

		if (ApplicationManager.bundleCacheMemorySizeButton != null) {
			ApplicationManager.bundleCacheMemorySizeButton.setText(
					ApplicationManager.getTranslation(ApplicationManager.BUNDLE_AND_CACHE_MEMORY_SIZE_MESSAGE, bundle));
		}

		if (ApplicationManager.generateBundleButton != null) {
			ApplicationManager.generateBundleButton
			.setText(ApplicationManager.getTranslation(ApplicationManager.GENERATE_BUNDLE_MESSAGE, bundle));
		}
		if (ApplicationManager.systemPropertiesButton != null) {
			ApplicationManager.systemPropertiesButton
			.setText(ApplicationManager.getTranslation(ApplicationManager.SYSTEM_PROPERTIES_MESSAGE, bundle));
		}
		if (ApplicationManager.httpHTTPSTrafficButton != null) {
			ApplicationManager.httpHTTPSTrafficButton
			.setText(ApplicationManager.getTranslation(ApplicationManager.SHOW_HTTP_HTTPS_TRAFFIC_MESSAGE, bundle));
		}
	}

	protected static JButton networkBitrateButton = null;

	protected static JButton bundleCacheMemorySizeButton = null;

	protected static JButton cacheMemoryButton = null;

	protected static JButton generateBundleButton = null;

	protected static JButton systemPropertiesButton = null;

	protected static JButton httpHTTPSTrafficButton = null;

	/**
	 * Creates the client debug window.
	 */
	protected static void createWindow() {
		ApplicationManager.window = new JFrame("Application Manager Debug Window");
		final JCheckBox checkDEBUGCM = new JCheckBox("ConnectionManager.DEBUG");
		final JCheckBox checkRELOAD_BUTTON_VISIBLE = new JCheckBox("Form.RELOAD_BUTTON_VISIBLE");

		final Map p = new Hashtable();

		p.put("attr", "Bytes/seg");
		final RealDataField netSpeed = new RealDataField(p);

		ApplicationManager.networkBitrateButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.CHANGE_NETWORK_BITRATE_MESSAGE));
		ApplicationManager.bundleCacheMemorySizeButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.BUNDLE_AND_CACHE_MEMORY_SIZE_MESSAGE));
		ApplicationManager.cacheMemoryButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.CACHE_MANAGER_MESSAGE));
		ApplicationManager.generateBundleButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.GENERATE_BUNDLE_MESSAGE));
		ApplicationManager.systemPropertiesButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.SYSTEM_PROPERTIES_MESSAGE));
		ApplicationManager.httpHTTPSTrafficButton = new JButton(
				ApplicationManager.getTranslation(ApplicationManager.SHOW_HTTP_HTTPS_TRAFFIC_MESSAGE));

		ApplicationManager.window.getContentPane().setLayout(new GridBagLayout());
		final JPanel loggerPanel = new LoggerPanel(ApplicationManager.getApplicationBundle(), null);
		final JPanel controls = new JPanel(new GridLayout(0, 1));
		ApplicationManager.window.getContentPane()
		.add(loggerPanel,
				new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		ApplicationManager.window.getContentPane()
		.add(controls,
				new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));
		controls.add(checkRELOAD_BUTTON_VISIBLE);
		controls.add(netSpeed);
		controls.add(ApplicationManager.networkBitrateButton);

		controls.add(ApplicationManager.bundleCacheMemorySizeButton);

		controls.add(ApplicationManager.cacheMemoryButton);

		controls.add(ApplicationManager.generateBundleButton);

		controls.add(ApplicationManager.systemPropertiesButton);

		controls.add(ApplicationManager.httpHTTPSTrafficButton);

		checkRELOAD_BUTTON_VISIBLE.setSelected(Form.RELOAD_BUTTON_VISIBLE);

		ApplicationManager.networkBitrateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!netSpeed.isEmpty()) {
					final Double d = (Double) netSpeed.getDoubleValue();
					if (d != null) {
						final int threshold = ConnectionManager.getCompresionThreshold((int) (d.doubleValue() * 1000),
								1000000);
						ApplicationManager.logger.info("Compression threshold: {}", threshold);
						if (threshold > 0) {
							final ConnectionOptimizer opt = ConnectionManager.getConnectionOptimizer();
							if ((opt != null) && (ApplicationManager.getApplication()
									.getReferenceLocator() instanceof ClientReferenceLocator)) {
								try {
									opt.setDataCompressionThreshold(
											((ClientReferenceLocator) ApplicationManager.getApplication()
													.getReferenceLocator()).getUser(),
											ApplicationManager.getApplication().getReferenceLocator().getSessionId(),
											threshold);
									ApplicationManager.logger.info("Compression threshold set : {}", threshold);
								} catch (final Exception ex) {
									ApplicationManager.logger
									.error("ApplicationManager: Error setting compression threshold.}", ex);
								}
							}
						}
					}
				}
			}
		});

		ApplicationManager.bundleCacheMemorySizeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int t = ExtendedPropertiesBundle.getSizeInMemory();
				double f = t;
				if (t != 0) {
					f = t / 1024.0;
				}
				final CacheManager cm = CacheManager
						.getDefaultCacheManager(ApplicationManager.application.getReferenceLocator());
				final int c = cm.getCacheSize();
				double g = c;
				if (c != 0) {
					g = c / 1024.0;
				}
				MessageDialog.showMessage(ApplicationManager.window,
						"Size Bundles: " + f + " Kbytes \n\n Size data cache: " + g + " KBytes for entities: "
								+ cm.getCachedEntities(),
								JOptionPane.INFORMATION_MESSAGE, null);
			}
		});

		ApplicationManager.cacheMemoryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ApplicationManager.showCacheInformation();
			}
		});


		checkDEBUGCM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		});

		checkRELOAD_BUTTON_VISIBLE.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Form.RELOAD_BUTTON_VISIBLE = checkRELOAD_BUTTON_VISIBLE.isSelected();
				if (Form.RELOAD_BUTTON_VISIBLE) {
					MessageDialog.showMessage((Frame) null, "Chane is only efective for new forms, not created yet",
							JOptionPane.INFORMATION_MESSAGE, null);
				}
			}
		});

		ApplicationManager.generateBundleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					if (ApplicationManager.application instanceof MainApplication) {
						final JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						int option = fc.showSaveDialog(ApplicationManager.application.getFrame());
						if (option == JFileChooser.APPROVE_OPTION) {
							final File fGuardar = fc.getSelectedFile();
							option = fc.showOpenDialog(ApplicationManager.application.getFrame());
							Properties props = null;
							if (option == JFileChooser.APPROVE_OPTION) {
								props = new Properties();
								props.load(new FileInputStream(fc.getSelectedFile()));
							}
							((MainApplication) ApplicationManager.application)
							.saveTextsToTranslate(fGuardar.getAbsolutePath(), props);
							MessageDialog.showMessage(ApplicationManager.application.getFrame(),
									"File save in: " + fGuardar.getAbsolutePath(), JOptionPane.INFORMATION_MESSAGE,
									null);
						}
					}
				} catch (final Exception ex) {
					ApplicationManager.logger.error(null, ex);
				}
			}
		});

		ApplicationManager.systemPropertiesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (ApplicationManager.systemPropertiesW == null) {
					ApplicationManager.systemPropertiesW = new JDialog(ApplicationManager.window,
							ApplicationManager.SYSTEM_PROPERTIES_MESSAGE, false);
					ApplicationManager.systemPropertiesW.getContentPane()
					.add(new JScrollPane(ApplicationManager.getSystemPropertiesComponent()));
					ApplicationManager.systemPropertiesW.pack();
					ApplicationManager.systemPropertiesW.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					ApplicationManager.setLocationSouthEast(ApplicationManager.systemPropertiesW);
				}
				ApplicationManager.systemPropertiesW.setVisible(true);
			}
		});

		ApplicationManager.httpHTTPSTrafficButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StreamInfoComponent.setStreamInfoWindowVisible(true);
			}

		});

		ApplicationManager.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		ApplicationManager.window.pack();
		ApplicationManager.window.setLocation(0,
				Toolkit.getDefaultToolkit().getScreenSize().height - ApplicationManager.window.getHeight() - 50);
	}

	/**
	 * Returns the default icon for the <code>OK</code> buttons.
	 * @return the <code>OK</code> button default icon
	 */
	public static ImageIcon getImatiaIcon() {
		try {
			if (ApplicationManager.imatiaIcon == null) {
				// Aux:
				ApplicationManager.imatiaIcon = ImageManager.getIcon(ImageManager.ICON_IMATIA);
				if (ApplicationManager.imatiaIcon == null) {
					ApplicationManager.logger.warn("'iconimatia.gif' not found");
					return null;
				}
			}
			return ApplicationManager.imatiaIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>OK</code> buttons.
	 * @return the <code>OK</code> button default icon
	 */
	public static ImageIcon getDefaultOKIcon() {
		try {
			if (ApplicationManager.okIcon == null) {
				// Aux:
				ApplicationManager.okIcon = ImageManager.getIcon(ImageManager.OK);
				if (ApplicationManager.okIcon == null) {
					ApplicationManager.logger.warn("'ok.png' not found");
					return null;
				}
			}
			return ApplicationManager.okIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code> Refresh Table </code> buttons.
	 * @return the <code> Refresh Table </code>button default icon
	 */
	public static ImageIcon getDefaultRefreshTableIcon() {
		try {
			if (ApplicationManager.refreshTableIcon == null) {
				ApplicationManager.refreshTableIcon = ImageManager.getIcon(ImageManager.TABLE_REFRESH);
				if (ApplicationManager.refreshTableIcon == null) {
					ApplicationManager.logger.warn("'tablerefresh.png' icon not found");
					return null;
				}
			}
			return ApplicationManager.refreshTableIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Save</code> buttons.
	 * @return the <code>Save</code> button default icon
	 */
	public static ImageIcon getDefaultSaveIcon() {
		try {
			if (ApplicationManager.saveIcon == null) {
				ApplicationManager.saveIcon = ImageManager.getIcon(ImageManager.SAVE_FILE);
				if (ApplicationManager.saveIcon == null) {
					ApplicationManager.logger.warn("'{}' icon not found", ImageManager.SAVE_FILE);
					return null;
				}
			}
			return ApplicationManager.saveIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Cancel</code> buttons.
	 * @return the <code>Cancel</code> button default icon
	 */
	public static ImageIcon getDefaultCancelIcon() {
		try {
			if (ApplicationManager.cancelIcon == null) {
				ApplicationManager.cancelIcon = ImageManager.getIcon(ImageManager.CANCEL);
				if (ApplicationManager.cancelIcon == null) {
					ApplicationManager.logger.warn("'{}' icon not found", ImageManager.CANCEL);
					return null;
				}
			}
			return ApplicationManager.cancelIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Attach File</code> buttons.
	 * @return the <code>Atach File</code> button default icon
	 */
	public static ImageIcon getDefaultAttachIcon() {
		try {
			if (ApplicationManager.attachmentIcon == null) {
				ApplicationManager.attachmentIcon = ImageManager.getIcon(ImageManager.ATTACH_FILE);
				if (ApplicationManager.attachmentIcon == null) {
					ApplicationManager.logger.warn("'{}' icon not found", ImageManager.ATTACH_FILE);
					return null;
				}

			}
			return ApplicationManager.attachmentIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Delete File</code> buttons.
	 * @return the <code>Delete File</code> button default icon
	 */
	public static ImageIcon getDefaultDeleteAttachmentIcon() {
		try {
			if (ApplicationManager.deleteAttachIcon == null) {
				ApplicationManager.deleteAttachIcon = ImageManager.getIcon(ImageManager.DELETE_ATTACHMENT);
				if (ApplicationManager.deleteAttachIcon == null) {
					ApplicationManager.logger.warn("'{}' icon not found", ImageManager.DELETE_ATTACHMENT);
					return null;
				}

			}
			return ApplicationManager.deleteAttachIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Help</code> buttons.
	 * @return the <code>Help</code> button default icon
	 */
	public static ImageIcon getDefaultHelpIcon() {
		try {
			if (ApplicationManager.helpIcon == null) {
				ApplicationManager.helpIcon = ImageManager.getIcon(ImageManager.HELPBOOK);
				if (ApplicationManager.helpIcon == null) {
					ApplicationManager.logger.warn("'{}' icon not found", ImageManager.HELPBOOK);
					return null;
				}
			}
			return ApplicationManager.helpIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Sets an Application to the Application
	 * @param application
	 */
	public static void setApplication(final Application application) {
		ApplicationManager.application = application;
	}

	/**
	 * Returns a reference to the Application managed by this class.
	 * @return the application or null in case the application is not set
	 */
	public static Application getApplication() {
		return ApplicationManager.application;
	}

	/**
	 * Converts the boolean in a string.
	 * @param value the boolean to parse
	 * @return 'yes' if the value is true, 'no' otherwise
	 */
	public static String parseBooleanValue(final boolean value) {
		if (value) {
			return "yes";
		} else {
			return "no";
		}
	}

	/**
	 * Converts the string into a boolean.
	 * @param value the string to parse
	 * @return the boolean true if the value of the string is 'true' or 'yes', false otherwise
	 */
	public static boolean parseStringValue(final String value) {
		if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Parses a String and converts it into a Insets class. The String must be composed by 4 values
	 * separated by ';'. The order of the parsing is
	 * <p>
	 * top;left;bottom;right
	 * @param insets the String defining the insets as "top;left;bottom;right"
	 * @return the Insets class made from the parameter
	 */
	public static Insets parseInsets(final String insets) throws IllegalArgumentException {
		final StringTokenizer st = new StringTokenizer(insets, ";");
		if (st.countTokens() != 4) {
			throw new IllegalArgumentException("Insets must have 4 tokens");
		}
		final int top = Integer.parseInt(st.nextToken());
		final int left = Integer.parseInt(st.nextToken());
		final int bottom = Integer.parseInt(st.nextToken());
		final int right = Integer.parseInt(st.nextToken());
		return new Insets(top, left, bottom, right);
	}

	public static Dimension parseSize(final String size) throws IllegalArgumentException {
		final StringTokenizer st = new StringTokenizer(size, ";");
		if (st.countTokens() != 2) {
			throw new IllegalArgumentException("Size must have 2 tokens");
		}
		final int width = Integer.parseInt(st.nextToken());
		final int height = Integer.parseInt(st.nextToken());
		return new Dimension(width, height);
	}

	/**
	 * Parses a String to convert it to a boolean. If the String value is 'yes' or 'true' the boolean
	 * response will be true, and if the String value is 'no' or 'false' the result will be false. If
	 * the String is not one of the previous one specified previously, the defaultValue will be
	 * returned.
	 * @param string the string to parse
	 * @param defaultValue the default value to return if no coincidence found
	 * @return
	 */
	public static boolean parseStringValue(final String string, final boolean defaultValue) {
		if (string == null) {
			return defaultValue;
		}
		if (string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true")) {
			return true;
		} else if (string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false")) {
			return false;
		}
		return defaultValue;
	}

	/**
	 * Converts a String value into a Point. The String must contain the point description as String
	 * separated by ; that is xcoor;ycoor
	 * @param string the string to parse
	 * @param defaultPoint the default point in case a null is past as parameter or an exception happens
	 * @return the Point equivalent to the String
	 */
	public static Point parseStringValue(final String string, final Point defaultPoint) {
		if (string == null) {
			return defaultPoint;
		}
		final int index = string.lastIndexOf(";");
		if (index >= 0) {
			try {
				final String xSTR = string.substring(0, index);
				final String ySTR = string.substring(index + 1, string.length());
				final int x = Integer.parseInt(xSTR);
				final int y = Integer.parseInt(ySTR);
				return new Point(x, y);
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
				return defaultPoint;
			}
		} else {
			return defaultPoint;
		}
	}

	/**
	 * Converts a String value into a Dimension. The String must contain the dimension description as
	 * String separated by ; that is xcoor;ycoor
	 * @param string the String to parse
	 * @param defaultDimension the default dimension in case a null is past as parameter or an exception
	 *        happens
	 * @return the Dimension equivalent to the String
	 */
	public static Dimension parseStringValue(final String string, final Dimension defaultDimension) {
		if (string == null) {
			return defaultDimension;
		}
		final int index = string.lastIndexOf(";");
		if (index >= 0) {
			try {
				final String xSTR = string.substring(0, index);
				final String ySTR = string.substring(index + 1, string.length());
				final int x = Integer.parseInt(xSTR);
				final int y = Integer.parseInt(ySTR);
				return new Dimension(x, y);
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
				return defaultDimension;
			}
		} else {
			return defaultDimension;
		}
	}

	/**
	 * Converts the Dimension into a String, with the format
	 * <p>
	 * dimension.width;dimension.height
	 * @param dimension the Dimension to convert to String
	 * @return a String that represents the Dimension
	 */
	public static String parseDimensionValue(final Dimension dimension) {
		return Integer.toString(dimension.width) + ";" + Integer.toString(dimension.height);
	}

	/**
	 * Converts the Point into a String, with the format
	 * <p>
	 * point.x;point.y
	 * @param point the point to converto to String
	 * @return
	 */
	public static String parsePointValue(final Point point) {
		return Integer.toString(point.x) + ";" + Integer.toString(point.y);
	}

	/**
	 * Checks if a parameter is contained by a Map (or by an extended class as {@link Properties})
	 * @param key the parameter to check
	 * @param prop the Map that contains the properties
	 * @return the value of the property if it is contained by the prop, or null if the property is not
	 *         in the Hashtable
	 */
	public static Object getParameterValue(final String key, final Map prop) {
		Object value = null;
		value = prop.get(key);
		if (value == null) {
			value = prop.get(key.toLowerCase());
		}
		return value;
	}

	/**
	 * Returns a JTable containing the System properties.
	 * @return a JTable containing the System properties
	 */
	public static JTable getSystemPropertiesComponent() {
		// Now we are going to read the system properties, you know...
		final Properties prop = System.getProperties();
		final JTable table = new JTable(prop.size(), 2);
		table.setDefaultEditor(String.class, null);
		table.setDefaultEditor(Object.class, null);
		final DefaultTableCellRenderer rend = new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object v, final boolean s, final boolean f, final int r, final int c) {
				final Component comp = super.getTableCellRendererComponent(table, v, s, f, r, c);
				if (comp instanceof JLabel) {
					((JLabel) comp).setToolTipText(((JLabel) comp).getText());
				}
				return comp;
			}
		};
		table.setDefaultRenderer(String.class, rend);
		table.setDefaultRenderer(Object.class, rend);
		table.getColumnModel().getColumn(0).setHeaderValue("Name");
		table.getColumnModel().getColumn(1).setHeaderValue("table.value");
		final Enumeration en = prop.propertyNames();
		int i = 0;
		while (en.hasMoreElements()) {
			final String p = en.nextElement().toString();
			table.setValueAt(p, i, 0);
			table.setValueAt(prop.get(p), i, 1);
			i++;
		}
		return table;
	}

	/**
	 * Register the StatusComponent as current application StatusComponent
	 * @param component the StatusComponent to be set
	 */
	public static void registerStatusComponent(final StatusComponent component) {
		if (ApplicationManager.application != null) {
			ApplicationManager.application.registerStatusComponent(component);
		}
	}

	/**
	 * Removes the component from the list of StatusComponents registered for the application.
	 * @param component the StatusComponent to be removed
	 */
	public static void unregisterStatusComponent(final StatusComponent component) {
		if (ApplicationManager.application != null) {
			ApplicationManager.application.unregisterStatusComponent(component);
		}
	}

	/**
	 * Sets a ImageIcon to display in the dialog windows for the operations related to the
	 * OperationTread and the ExtendedOperationThread.
	 * @param imageIcon the ImageIcon to show
	 */
	public static void setCancelOperationDialogIcon(final ImageIcon imageIcon) {
		ApplicationManager.cancelOperationDialogIcon = imageIcon;
	}

	/**
	 * Sets the time used by the time based client permissions to perform the checking.
	 * @param time current time
	 */
	public static void setTime(final long time) {
		ApplicationManager.currentTime = time;
	}

	/**
	 * Returns the current time set to the application.
	 * @return if the {@link #setTime(long)} method set a value into the application, that value; the
	 *         System.currentTimeMillis otherwise.
	 */
	public static long getTime() {
		if (ApplicationManager.currentTime != -1) {
			return ApplicationManager.currentTime;
		} else {
			return System.currentTimeMillis();
		}
	}

	/**
	 * Checks whether the JVM version is higher than the 1.3.0
	 * @return if the JVM version is higher than the 1.3.0, false otherwise.
	 */
	public static boolean jvmVersionHigherThan_1_3_0() {
		if (ApplicationManager.version == null) {
			ApplicationManager.detectJVMVersion();
		}
		if (ApplicationManager.version[0] > 1) {
			return true;
		} else if (ApplicationManager.version[0] == 1) {
			if (ApplicationManager.version[1] > 3) {
				return true;
			} else if (ApplicationManager.version[1] == 3) {
				if (ApplicationManager.version[2] > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * Checks whether the JVM version is higher than the 1.4.0
	 * @return if the JVM version is higher than the 1.4.0, false otherwise.
	 */
	public static boolean jvmVersionHigherThan_1_4_0() {
		if (ApplicationManager.version == null) {
			ApplicationManager.detectJVMVersion();
		}
		if (ApplicationManager.version[0] > 1) {
			return true;
		} else if (ApplicationManager.version[0] == 1) {
			if (ApplicationManager.version[1] >= 4) {
				return true;
			} else if (ApplicationManager.version[1] == 4) {
				if (ApplicationManager.version[2] > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Checks whether the JVM version is higher than the 1.5.0
	 * @return if the JVM version is higher than the 1.5.0, false otherwise.
	 */
	public static boolean jvmVersionHigherThan_1_5_0() {
		if (ApplicationManager.version == null) {
			ApplicationManager.detectJVMVersion();
		}
		if (ApplicationManager.version[0] > 1) {
			return true;
		} else if (ApplicationManager.version[0] == 1) {
			if (ApplicationManager.version[1] >= 5) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Checks whether the JVM version is higher or equal than the 1.6.0
	 * @return if the JVM version is higher than the 1.5.0, false otherwise.
	 */
	public static boolean jvmVersionHigherThan_1_6_0() {
		if (ApplicationManager.version == null) {
			ApplicationManager.detectJVMVersion();
		}
		if (ApplicationManager.version[0] > 1) {
			return true;
		} else if (ApplicationManager.version[0] == 1) {
			if (ApplicationManager.version[1] >= 6) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isOSWindows() {
		String osName = System.getProperty("os.name");
		if (osName == null) {
			return true;
		}
		osName = osName.toUpperCase();
		if (osName.indexOf("WINDOWS") >= 0) {
			return true;
		}
		return false;
	}

	private static int[] version = null;

	private static String versionProperty = "java.vm.version";

	private static void detectJVMVersion() {
		ApplicationManager.logger.info("Checking JVM Version");
		ApplicationManager.version = new int[3];
		final String ver = System.getProperty(ApplicationManager.versionProperty, "0.0.0");
		final StringTokenizer st = new StringTokenizer(ver, ".");
		if (st.hasMoreTokens()) {
			final String p = st.nextToken();
			try {
				ApplicationManager.version[0] = Integer.parseInt(p);
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
			}
		}

		if (st.hasMoreTokens()) {
			final String p = st.nextToken();
			try {
				ApplicationManager.version[1] = Integer.parseInt(p);
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
			}
		}

		if (st.hasMoreTokens()) {
			String p = st.nextToken();

			try {
				ApplicationManager.version[2] = Integer.parseInt(p);
			} catch (final Exception e) {
				ApplicationManager.logger.trace(null, e);
				if (p.length() > 1) {
					p = p.substring(0, 1);
					try {
						ApplicationManager.version[2] = Integer.parseInt(p);
					} catch (final Exception e2) {
						ApplicationManager.logger.trace(null, e2);
					}
				}
			}

		}
		ApplicationManager.logger.info("Detected JVM Version: {}.{}.{}", ApplicationManager.version[0],
				ApplicationManager.version[1], ApplicationManager.version[2]);
	}

	/**
	 * Debug method that prints the thread methods from the current execution point. This allows the
	 * developer to know the call hierarchy in each point.
	 * @param lines the number of thread lines to print
	 */
	public static void printCurrentThreadMethods(final int lines) {
		ApplicationManager.printCurrentThreadMethods(new Throwable(), lines);
	}

	public static void printCurrentThreadMethods(final Throwable thowable, final PrintWriter pw) {
		try {
			final java.lang.reflect.Method method = Throwable.class.getMethod("printStackTrace",
					new Class[] { PrintWriter.class });
			method.invoke(thowable, new Object[] { pw });
		} catch (final Exception ex) {
			ApplicationManager.logger.error("{}", ex.getMessage(), ex);
		}
	}

	public static void printCurrentThreadMethods(final Throwable thowable, final PrintStream ps) {
		try {
			final java.lang.reflect.Method method = Throwable.class.getMethod("printStackTrace",
					new Class[] { PrintStream.class });
			method.invoke(thowable, new Object[] { ps });
		} catch (final Exception ex) {
			ApplicationManager.logger.error("{}", ex.getMessage(), ex);
		}
	}

	/**
	 * Debug method that prints the thread methods from a determined {@link Throwable} object. This
	 * allows the developer to know the call hierarchy in a determined point.
	 * @param thowable the object to inspect
	 * @param lines the number of thread lines to print
	 */
	public static void printCurrentThreadMethods(final Throwable thowable, int lines) {
		try {
			if (lines < 2) {
				lines = 2;
			}

			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);

			ApplicationManager.printCurrentThreadMethods(thowable, pw);

			final String sText = sw.toString();
			// 2lineas
			final StringBuilder sb = new StringBuilder();
			int nLines = 0;
			for (int i = 0; i < sText.length(); i++) {
				sb.append(sText.charAt(i));
				if (sText.charAt(i) == '\n') {
					nLines++;
				}
				if (nLines >= lines) {
					break;
				}
			}
			ApplicationManager.logger.info("Trace: current thread methods: {}", sb.toString());
		} catch (final Exception ex) {
			ApplicationManager.logger.error(null, ex);
		}
	}

	public static StringBuilder printStackTrace(final Throwable thowable) {
		return ApplicationManager.printStackTrace(thowable, 15);
	}

	public static StringBuilder printStackTrace(final Throwable thowable, int lines) {
		final StringBuilder sb = new StringBuilder();

		try {
			if (lines < 2) {
				lines = 2;
			}
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			ApplicationManager.printCurrentThreadMethods(thowable, pw);
			final String sText = sw.toString();

			int nLines = 0;
			for (int i = 0; i < sText.length(); i++) {
				sb.append(sText.charAt(i));
				if (sText.charAt(i) == '\n') {
					nLines++;
				}
				if (nLines >= lines) {
					break;
				}
			}

			return sb;
		} catch (final Exception ex) {
			ApplicationManager.logger.error("Error printCurrentThreadMethods()", ex);
		}

		return sb;
	}

	/**
	 * Returns a String containing the thread methods from the current execution point. This allows the
	 * developer to know the call hierarchy in each point.
	 * @param lines
	 * @return a String containing the thread methods from the execution point
	 */
	public static String getCurrentThreadMethods(final int lines) {
		return ApplicationManager.getCurrentThreadMethods(new Throwable(), lines);
	}

	/**
	 * Returns a String containing the thread methods from the current execution point. This allows the
	 * developer to know the call hierarchy in each point.
	 * @param throwable
	 * @param lines
	 * @return a String containing the thread methods from the execution point
	 */
	public static String getCurrentThreadMethods(final Throwable throwable, int lines) {
		try {
			if (lines < 2) {
				lines = 2;
			}
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			ApplicationManager.printCurrentThreadMethods(throwable, pw);
			final String texto = sw.toString();
			// 2 lines
			final StringBuilder sb = new StringBuilder();
			int nLineas = 0;
			for (int i = 0; i < texto.length(); i++) {
				sb.append(texto.charAt(i));
				if (texto.charAt(i) == '\n') {
					nLineas++;
				}
				if (nLineas >= lines) {
					break;
				}
			}
			pw.close();
			return sb.toString();
		} catch (final Exception ex) {
			ApplicationManager.logger.error("Error printCurrentThreadMethods()", ex);
			return "Error";
		}
	}

	/**
	 * @deprecated. Use {@link ImageManager#getIcon(String icon)}
	 */
	public static ImageIcon getIcon(final String icon) {
		return ImageManager.getIcon(icon);
	}

	/**
	 * Plays a sound stored in a media file. {@see java.applet.AudioClip}
	 * @param mediaFile the URI to the media file, from the classpath; for example,
	 *        'com/ontimize/sound/beep.wav'
	 */
	public static void playSound(final String mediaFile) {
		try {
			final ReferenceFieldAttribute ar = new ReferenceFieldAttribute("", "", "", new Vector());
			final URL url = ar.getClass().getClassLoader().getResource(mediaFile);
			final AudioClip ac = Applet.newAudioClip(url);
			ac.play();
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
		}
	}

	/**
	 * Returns the application bundle, which contains the default translations for the application
	 * texts, corresponding to the current language selected for the application.
	 * @return the application bundle, with the GUI texts translations
	 */
	public static ResourceBundle getApplicationBundle() {
		if (ApplicationManager.getApplication() != null) {
			return ApplicationManager.getApplication().getResourceBundle();
		} else {
			return null;
		}

	}

	/**
	 * Returns the text translation corresponding to the application's Resource Bundle. That is, returns
	 * the text to show in the GUI depending on the current configured locale.
	 * @param text the text to translate
	 * @return the translated text, according to the application current language
	 */
	public static String getTranslation(final String text) {
		if (ApplicationManager.getApplication() != null) {
			return ApplicationManager.getTranslation(text, ApplicationManager.getApplication().getResourceBundle());
		} else {
			return text;
		}
	}

	/**
	 * Returns the text translation according to the bundle past as parameter.
	 * @param text the text to translate
	 * @param bundle the translations to look up
	 * @return
	 */
	public static String getTranslation(final String text, final ResourceBundle bundle) {
		return ApplicationManager.getTranslation(text, bundle, null);
	}

	/**
	 * Returns a formated text which can have parameters, in the language determined by the bundle.
	 * @param text the text to translated; for example, the text
	 *        <p>
	 *        form.find_?_templates = Find {0} templates
	 *        <p>
	 *        will replace the {0} (and {1}, {2}...etc., if any) by the values past in the param args
	 * @param bundle the object that contains the translations
	 * @param args the values to use to complete the text; if null, {@link MessageFormat} will be used
	 *        to format the text.
	 * @return the formated text
	 */
	public static String getTranslation(final String text, final ResourceBundle bundle, final Object[] args) {
		if (bundle == null) {
			final String trad = new String(text);
			final String tradArgs = MessageFormat.format(trad, args);
			return tradArgs;
		} else {
			try {
				String trad = bundle.getString(text);
				if ((trad != null)
						&& (trad.startsWith("<HTML>") || trad.startsWith("<html>") || trad.startsWith("<Html>"))) {
					final int index = trad.indexOf("<DEFAULTBASE>");
					if (index >= 0) {
						final URL url = ApplicationManager.class.getClassLoader().getResource("./");
						if (url != null) {
							trad = trad.substring(0, index) + "<BASE href=\"" + url.toString() + "\">"
									+ trad.substring(index + 13);
							ApplicationManager.logger.info("BASE: {} has been established", url.toString());
						}
					}
				}
				// Args
				return ApplicationManager.processTranslationArguments(trad, args);
			} catch (final Exception e) {
				ApplicationManager.logger.warn("translation not found {} with parameters {}", text, args, e);

				// Args
				return ApplicationManager.processTranslationArguments(text, args);
			}
		}
	}

	private static String processTranslationArguments(final String text, final Object[] args) {

		if ((args != null) && (text != null)) {

			final String tradArgs = MessageFormat.format(text, args);

			return tradArgs;
		} else {

			return text;
		}
	}

	/**
	 * Returns the Form that contains a determined component.
	 * @param component
	 * @return the form in the application that contains the component past as parameter
	 */
	public static Form getFormAncestor(final Component component) {
		Container parent = component.getParent();
		while (parent != null) {
			if (parent instanceof Form) {
				return (Form) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	private static JFrame frameMonitorMem = null;

	private static MemoryMonitorComponent memMonitor = null;

	/**
	 * Sets the Memory Monitor Window visible or not.
	 * @param visible if true, the window will be visible; the window will be hide otherwise
	 */
	public static void setMemoryMonitorWindowVisible(final boolean visible) {

		if (ApplicationManager.frameMonitorMem == null) {
			ApplicationManager.memMonitor = new MemoryMonitorComponent(new Hashtable());
			ApplicationManager.frameMonitorMem = new JFrame("Memory");
			final JButton buttonGC = new JButton("Execute Garbage Collector");
			buttonGC.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					// Execution of the Garbage Collector should be triggered only by the JVM
				}
			});
			ApplicationManager.frameMonitorMem.getContentPane().add(buttonGC, BorderLayout.SOUTH);
			ApplicationManager.frameMonitorMem.getContentPane().add(ApplicationManager.memMonitor);
			ApplicationManager.memMonitor.start();
			ApplicationManager.frameMonitorMem.pack();
			ApplicationManager.setLocationNorthWest(ApplicationManager.frameMonitorMem);
		}
		ApplicationManager.frameMonitorMem.setVisible(visible);
	}

	/**
	 * An OutputStream configured for text. When flushing, the content of the JTextComponent used to
	 * create the TextStream will be flushed.
	 *
	 */
	public static class TextStream extends OutputStream {

		JTextComponent tc = null;

		byte[] buffer = new byte[1024];

		int currentPosition = 0;

		int maxTextSize = 50 * 1024;

		boolean echoDate = true;

		public TextStream(final JTextComponent tc) {
			super();
			this.tc = tc;
		}

		public TextStream(final JTextComponent tc, final int maxTextSize) {
			super();
			this.tc = tc;
			this.maxTextSize = maxTextSize;
		}

		public TextStream(final JTextComponent tc, final int maxTextSize, final int bufferSize) {
			super();
			this.buffer = new byte[bufferSize];
			this.tc = tc;
			this.maxTextSize = maxTextSize;
		}

		public TextStream(final JTextComponent tc, final int maxTextSize, final int bufferSize, final boolean echoDate) {
			super();
			this.buffer = new byte[bufferSize];
			this.tc = tc;
			this.maxTextSize = maxTextSize;
		}

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			if (this.currentPosition == 0) {
				return;
			}
			// Now write the buffer content
			final byte[] buf = new byte[this.currentPosition];
			System.arraycopy(this.buffer, 0, buf, 0, this.currentPosition);
			final String s = new String(buf);
			this.currentPosition = 0;
			try {
				final Document doc = this.tc.getDocument();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							if ((doc.getLength() + s.length()) > TextStream.this.maxTextSize) {
								doc.remove(0, (doc.getLength() + s.length()) - TextStream.this.maxTextSize);
							}
							doc.insertString(doc.getLength(), s, null);
						} catch (final Exception e) {
							ApplicationManager.logger.error(null, e);
						}
					}
				});
			} catch (final Exception e) {
				ApplicationManager.logger.error(null, e);
			}
		}

		@Override
		public synchronized void write(final byte[] bytes, final int off, final int len) throws IOException {
			if (len >= this.buffer.length) {
				/* If it exceeds the buffer then write directly */
				this.flush();
				try {
					final Document doc = this.tc.getDocument();
					final String s = new String(bytes);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							try {
								if ((doc.getLength() + s.length()) > TextStream.this.maxTextSize) {
									doc.remove(0, (doc.getLength() + s.length()) - TextStream.this.maxTextSize);
								}
								doc.insertString(doc.getLength(), s, null);
							} catch (final Exception e) {
								ApplicationManager.logger.error(null, e);

							}
						}
					});
				} catch (final Exception e) {
					ApplicationManager.logger.error(null, e);
				}
				return;
			}
			if (len > (this.buffer.length - this.currentPosition)) {
				this.flush();
			}
			System.arraycopy(bytes, off, this.buffer, this.currentPosition, len);
			this.currentPosition += len;
		}

		@Override
		public synchronized void write(final byte[] bytes) throws IOException {
			this.write(bytes, 0, bytes.length);
		}

		@Override
		public synchronized void write(final int b) throws IOException {
			if (this.currentPosition >= this.buffer.length) {
				this.flush();
			}
			this.buffer[this.currentPosition++] = (byte) b;
		}

	};

	/**
	 * Class that prints the current date to an OutputStream with a nice format.
	 */
	protected static class DatePrintStream extends PrintStream {

		static class CurrentDate {

			SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getInstance();

			public CurrentDate() {
				final DateFormatSymbols symbols = new DateFormatSymbols();
				symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
				this.df.setDateFormatSymbols(symbols);
				this.df.applyPattern("hh:mm dd/MM/yyyy");
			}

			String str = null;

			long time = 0;

			/**
			 * Gets the application time in millisecond with a resolution of 30 seconds.
			 * @return
			 */
			public long getTime() {
				return this.time;
			}

			/**
			 * Returns the current date.
			 * @return
			 */
			public String getString() {
				if ((this.str == null) || ((System.currentTimeMillis() - this.time) > 30000)) {
					this.time = System.currentTimeMillis();
					this.str = this.df.format(new Date()) + "--> ";
				}
				return this.str;
			}

		}

		CurrentDate date = new CurrentDate();

		/**
		 * Sets the stream to be used to print the dates. See {@link PrintStream}.
		 * @param out
		 */
		public DatePrintStream(final OutputStream out) {
			super(out);
		}

		/**
		 * Prints the text adding the current date.
		 */
		@Override
		public void println(final String s) {
			super.println(this.date.getString() + s);
		}

		/**
		 * Prints an object adding the current date.
		 */
		@Override
		public void println(final Object s) {
			super.print(this.date.getString());
			super.println(s);
		}

	}

	/**
	 * Creates an Stream associated the the text component, so that all what is printed in the Stream
	 * will be printed in the text component as well.
	 * @param tc the text component that will print the stream
	 * @param maxTextSize the maximum size of the text in bytes
	 * @return a Stream with a JTextComponent associated
	 */
	public static PrintStream getPrintStreamOnTextComponent(final JTextComponent tc, final int maxTextSize) {
		final TextStream out = new TextStream(tc, maxTextSize);
		return new DatePrintStream(out);
	}

	/**
	 * Creates an Stream associated the the text component, so that all what is printed in the Stream
	 * will be printed in the text component as well.
	 * @param tc the text component that will print the stream
	 * @param maxTextSize the maximum size of the text in bytes
	 * @param bufferSize the size of the buffer, in bytes; when the buffer is full, the contents will be
	 *        written in the text component
	 * @return a Stream with a JTextComponent associated
	 */
	public static PrintStream getPrintStreamOnTextComponent(final JTextComponent tc, final int maxTextSize, final int bufferSize) {
		final TextStream out = new TextStream(tc, maxTextSize, bufferSize, true);
		return new PrintStream(out);
	}

	/**
	 * Formats a date in the form 'dd/MM/yyyy'
	 * @param date
	 * @return the formatted date
	 */
	public static String format(final Date date) {
		return ApplicationManager.format(date, "dd/MM/yyyy");
	}

	/**
	 * Formats a date accoding to the patter
	 * @param date the date to format
	 * @param pattern the pattern; see {@link SimpleDateFormat#applyPattern(String)}
	 * @return the formated date
	 */
	public static String format(final Date date, final String pattern) {
		final SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		df.setLenient(false);
		df.applyPattern(pattern);
		final String res = df.format(date);
		return res;
	}

	/**
	 * Parses a String to create a Date, if the String follows the pattern 'dd/MM/yyyy'
	 * @param date the date in String format
	 * @return the object Date corresponding to the String
	 * @throws ParseException in case the String does not correspond to a Date
	 */
	public static java.util.Date parseDate(final String date) throws ParseException {
		return ApplicationManager.parseDate(date, "dd/MM/yyyy");
	}

	/**
	 * Creates a Date from the String if the String follow the patter.
	 * @param date the String that represents a Date
	 * @param pattern the patter the String must follows; {@link SimpleDateFormat#applyPattern(String)}
	 * @return
	 * @throws ParseException in case the String does not correspond to a Date
	 */
	public static java.util.Date parseDate(final String date, final String pattern) throws ParseException {
		final SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
		df.setLenient(false);
		df.applyPattern(pattern);
		final Date res = df.parse(date);
		return res;
	}

	/**
	 * Copies a text to the system clipboard.
	 * @param text
	 * @throws Exception in case the opperation cannot be done
	 */
	public static void copyToClipboard(final String text) throws Exception {
		final java.awt.datatransfer.StringSelection sselection = new java.awt.datatransfer.StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
	}

	/**
	 * Copies a file.
	 * @param src the source file
	 * @param dst the destiny file
	 */
	public static void copyFile(final File src, final File dst) {
		try {
			final InputStream in = new FileInputStream(src);
			final OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (final IOException ex) {
			ApplicationManager.logger.error(null, ex);
		}
	}

	/**
	 * Class that represents a monitor for the Operation Threads. With this class, the evolution of the
	 * threads can be monitored.
	 */
	public static class ExtOpThreadsMonitor extends EJDialog implements Internationalization {

		protected ExtOpThreadsMonitorComponent eOPC = null;

		protected String titleKey = null;

		/**
		 * Creates a ExtOpThreadsMonitor for a dialog
		 * @param dialog the window
		 * @param title the title
		 * @param modal if true, the window will be modal
		 */
		public ExtOpThreadsMonitor(final Dialog dialog, final String title, final boolean modal) {
			super(dialog, title, modal);
			this.titleKey = title;
		}

		/**
		 * Creates a ExtOpThreadsMonitor for a frame
		 * @param frame the frame
		 * @param title the title
		 * @param modal if true, the window will be modal
		 */
		public ExtOpThreadsMonitor(final Frame frame, final String title, final boolean modal) {
			super(frame, title, modal);
			this.titleKey = title;
		}

		/**
		 * The container can be a ExtOpThreadsMonitorComponent. See {@see JDialog#setContentPane(Container)}
		 */
		@Override
		public void setContentPane(final Container container) {
			super.setContentPane(container);
			if (container instanceof ExtOpThreadsMonitorComponent) {
				this.eOPC = (ExtOpThreadsMonitorComponent) container;
			}
		}

		/**
		 * Adds a thread to the monitor.
		 * @param extendedOperationThread a new thread
		 */
		public void addExtOpThread(final ExtendedOperationThread extendedOperationThread) {
			this.eOPC.addExtOpThread(extendedOperationThread);
		}

		@Override
		public void setResourceBundle(final ResourceBundle resourceBundle) {
			this.setTitle(ApplicationManager.getTranslation(this.titleKey, resourceBundle));
			this.eOPC.setResourceBundle(resourceBundle);
		}

		@Override
		public void setComponentLocale(final Locale l) {

		}

		/**
		 * Unused
		 */
		@Override
		public List getTextsToTranslate() {
			return null;
		}

		/**
		 * Returns the threads that are alive.
		 * @return the number of threads alive
		 */
		public int getAliveThreadsCount() {
			return this.eOPC.getAliveThreadsCount();
		}

	}

	/**
	 * Class that defines a graphical component to monitor the evolution of the threads.
	 */
	public static class ExtOpThreadsMonitorComponent extends JComponent implements Internationalization {

		protected JTable table = null;

		protected List threads = new ArrayList();

		protected JButton hideButton = new JButton("applicationmanager.hide");

		protected JButton cancelButton = new JButton("application.cancel");

		protected JButton deleteEndedBt = new JButton("applicationmanager.clear");

		protected ResourceBundle res = null;

		@Override
		public void setResourceBundle(final ResourceBundle res) {
			this.res = res;
			this.hideButton.setText(ApplicationManager.getTranslation("applicationmanager.hide", res));
			this.deleteEndedBt.setText(ApplicationManager.getTranslation("applicationmanager.clear", res));
			this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel", res));
			if (this.table != null) {
				this.table.getColumnModel()
				.getColumn(0)
				.setHeaderValue(ApplicationManager.getTranslation("applicationmanager.operation", res));
				this.table.getColumnModel()
				.getColumn(1)
				.setHeaderValue(ApplicationManager.getTranslation("applicationmanager.state", res));
				this.table.getColumnModel()
				.getColumn(2)
				.setHeaderValue(ApplicationManager.getTranslation("applicationmanager.completed", res));
			}
			this.hideButton.setToolTipText(ApplicationManager.getTranslation("applicationmanager.hide", res));
			this.deleteEndedBt
			.setToolTipText(ApplicationManager.getTranslation("applicationmanager.clear_finished", res));
			this.cancelButton.setToolTipText(
					ApplicationManager.getTranslation("applicationmanager.cancel_selected_operations", res));
		}

		/**
		 * No function.
		 */
		@Override
		public void setComponentLocale(final Locale l) {

		}

		/**
		 * No function.
		 */
		@Override
		public List getTextsToTranslate() {
			return null;
		}

		/**
		 * Renderers a progress bar.
		 */
		class ProgressRenderer implements TableCellRenderer {

			protected CustomBar bar = new CustomBar();

			protected Color yellow = Color.yellow;

			protected Color green = Color.green.darker();

			/**
			 * Creates a ProgressRenderer
			 */
			public ProgressRenderer() {
			}

			/**
			 * Renderers the progress depending on if the progress is done successfully (green), with errors
			 * (red) etc.
			 */
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected,
					final boolean hasFocus, final int row, final int column) {
				if (table != null) {
					this.bar.setFont(table.getFont());
				}
				this.bar.setBackground(Color.blue);
				if (value instanceof Number) {
					this.bar.setValue((int) (100 * ((Number) value).doubleValue()));
					if (this.bar.getValue() >= 100) {
						this.bar.setBackground(this.green);
					}
				} else {
					this.bar.setValue(0);
				}
				// If it has an error then it is red
				if ((row < ExtOpThreadsMonitorComponent.this.threads.size())
						&& (((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(row))
								.hasFinished()
								|| ((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(row))
								.isCancelled())
						&& (((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(row))
								.getResult() instanceof EntityResult)
						&& (((EntityResult) ((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads
								.get(row))
								.getResult()).getCode() == EntityResult.OPERATION_WRONG)) {
					this.bar.setBackground(Color.red);
				}
				return this.bar;
			}

		}

		/**
		 * Sets a tooltip to the rendered component with the text contained by it.
		 */
		protected static class StringRenderer extends DefaultTableCellRenderer {

			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected,
					final boolean hasFocus, final int row, final int column) {
				final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
				if (c instanceof JLabel) {
					((JLabel) c).setToolTipText(((JLabel) c).getText());
				}
				return c;
			}

		}

		/**
		 * The table model managed by the component, to display the thread evolution.
		 */
		class Model extends AbstractTableModel {

			Thread tUpdate = new Thread() {

				@Override
				public void run() {
					this.setPriority(Thread.MIN_PRIORITY);
					while (true) {
						try {
							Thread.sleep(100);
							if (ExtOpThreadsMonitorComponent.this.table.isVisible()) {
								ExtOpThreadsMonitorComponent.this.table.repaint();
							}
						} catch (final Exception e) {
							ApplicationManager.logger.error(null, e);
						}
					}
				}
			};

			/**
			 * Starts the thread that repaints the table each 100 milliseconds, to monitor the tread evolution.
			 */
			public Model() {
				this.tUpdate.start();
			}

			/**
			 * Fires a TableModelEvent
			 */
			public void update() {
				super.fireTableChanged(new TableModelEvent(this));
			}

			/**
			 * Removes from the model all the threads that finished.
			 */
			public void deleteEnded() {
				int removed = 0;
				synchronized (ExtOpThreadsMonitorComponent.this.threads) {
					for (int i = 0; i < ExtOpThreadsMonitorComponent.this.threads.size(); i++) {
						if (((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(i))
								.hasFinished()) {
							ExtOpThreadsMonitorComponent.this.threads.remove(i);
							removed++;
							i--;
						}
					}
				}
				if (removed > 0) {
					this.update();
				}
			}

			String[] columns = { "applicationmanager.operation", "applicationmanager.state",
			"applicationmanager.completed" };

			@Override
			public String getColumnName(final int index) {
				return ApplicationManager.getTranslation(this.columns[index], ExtOpThreadsMonitorComponent.this.res);
			}

			/**
			 * Returns the number of threads in the table.
			 */
			@Override
			public int getColumnCount() {
				return this.columns.length;
			}

			/**
			 * Returns the number of threads.
			 */
			@Override
			public int getRowCount() {
				final int iRows = ExtOpThreadsMonitorComponent.this.threads.size();
				return iRows;
			}

			@Override
			public Object getValueAt(final int r, final int c) {
				if (c == 0) {
					return ((Thread) ExtOpThreadsMonitorComponent.this.threads.get(r)).getName();
				} else if (c == 1) {
					return ((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(r)).getStatus();
				} else if (c == 2) {
					return new Float(((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(r))
							.getCurrentPosition()
							/ (double) ((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(r))
							.getProgressDivisions());
				} else {
					return null;
				}
			}

		}

		/**
		 * Creates the thread that monitors the execution of OperationThreads
		 */
		public ExtOpThreadsMonitorComponent() {
			this.init();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			d.width = Math.min(320, d.width);
			return d;
		}

		/**
		 * Creates the component.
		 */
		protected void init() {
			this.setLayout(new BorderLayout());
			final JPanel jbButtonsPanel = new JPanel(new FlowLayout());
			final JPanel panelAux = new JPanel(new GridLayout(1, 0, 5, 5));
			panelAux.add(this.deleteEndedBt);
			panelAux.add(this.cancelButton);
			panelAux.add(this.hideButton);
			jbButtonsPanel.add(panelAux);
			this.add(jbButtonsPanel, BorderLayout.SOUTH);

			this.deleteEndedBt.setMargin(new Insets(1, 2, 2, 1));
			this.cancelButton.setMargin(new Insets(1, 2, 2, 1));
			this.hideButton.setMargin(new Insets(1, 2, 2, 1));

			this.deleteEndedBt.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					((Model) ExtOpThreadsMonitorComponent.this.table.getModel()).deleteEnded();
				}
			});

			this.cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int[] fS = ExtOpThreadsMonitorComponent.this.table.getSelectedRows();
					for (int i = 0; i < fS.length; i++) {
						if (!((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(fS[i]))
								.hasFinished()) {
							((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(fS[i])).cancel();
							ApplicationManager.logger.debug("Cancelled {} ",
									((ExtendedOperationThread) ExtOpThreadsMonitorComponent.this.threads.get(fS[i]))
									.getName());
						}
					}
				}
			});

			this.hideButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Window w = SwingUtilities.getWindowAncestor(ExtOpThreadsMonitorComponent.this.hideButton);
					if (w != null) {
						w.setVisible(false);
					}
				}
			});

			this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
			this.deleteEndedBt.setIcon(ImageManager.getIcon(ImageManager.RECYCLER));
			this.table = new JTable(new Model()) {

				@Override
				public Dimension getPreferredScrollableViewportSize() {
					final Dimension d = super.getPreferredScrollableViewportSize();
					d.height = 6 * this.getRowHeight();
					return d;
				}
			};
			this.table.getTableHeader().setFont(this.table.getTableHeader().getFont().deriveFont((float) 10));
			this.table.setFont(this.table.getFont().deriveFont((float) 10));
			this.table.setRowHeight(this.table.getRowHeight() - 2);
			this.table.setRowMargin(0);

			this.table.getTableHeader().setReorderingAllowed(false);
			this.add(new JScrollPane(this.table));
			this.table.getColumnModel().getColumn(0).setCellRenderer(new StringRenderer());
			this.table.getColumnModel().getColumn(1).setCellRenderer(new StringRenderer());
			this.table.getColumnModel().getColumn(2).setCellRenderer(new ProgressRenderer());
			this.table.getColumnModel().getColumn(2).setPreferredWidth(80);
			this.table.getColumnModel().getColumn(2).setMaxWidth(200);
			this.table.getColumnModel().getColumn(1).setPreferredWidth(90);
			this.table.getColumnModel().getColumn(1).setWidth(90);
			this.table.getColumnModel().getColumn(2).setWidth(80);
			this.table.getColumnModel().getColumn(0).setPreferredWidth(150);
		}

		/**
		 * Clear finished operation threads in table.
		 *
		 * @author Imatia Innovation SL
		 * @since 5.2067EN-0.1
		 */
		public void removeEndedOperationThreads() {
			((Model) this.table.getModel()).deleteEnded();
		}

		/**
		 * Adds a new thread to the monitor
		 * @param th
		 */
		public void addExtOpThread(final ExtendedOperationThread th) {
			synchronized (this.threads) {
				ApplicationManager.logger.debug("Adding thread to queue: {}", th);
				if (!this.threads.contains(th)) {
					this.threads.add(0, th);
					th.start();
					ApplicationManager.logger.debug("Added thread to queue: {}", th);
					((Model) this.table.getModel()).update();
				}
			}
		}

		/**
		 * Returns the threads alive in the monitor.
		 * @return the number of threads running
		 */
		public int getAliveThreadsCount() {
			int j = 0;
			synchronized (this.threads) {
				for (int i = 0; i < this.threads.size(); i++) {
					if (!((ExtendedOperationThread) this.threads.get(i)).hasFinished()) {
						j++;
					}
				}
			}
			return j;
		}

	}

	public static IRemoteAdministrationWindow getRemoteAdminWindow(final RemotelyManageable locator) {

		if (ApplicationManager.remoteAdminWindow == null) {
			try {
				final Class windowClass = Class
						.forName("com.ontimize.util.remote.RemoteUtilities$RemoteAdministrationWindow");
				final Constructor constructor = windowClass.getConstructor(new Class[] { RemotelyManageable.class });
				ApplicationManager.remoteAdminWindow = (IRemoteAdministrationWindow) constructor
						.newInstance(new Object[] { locator });
				// ApplicationManager.remoteAdminWindow = new RemoteAdministrationWindow(locator);
				if (remoteAdminWindow instanceof JFrame) {
					((JFrame) ApplicationManager.remoteAdminWindow)
					.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
				}
			} catch (final Exception e) {
				logger.error("{}", e.getMessage(), e);
			}
		}
		return ApplicationManager.remoteAdminWindow;
	}

	private static ExtOpThreadsMonitor monitor = null;

	/**
	 * Returns the ExtOpThreadsMonitor for the application. If no ExtOpThreadsMonitor has been set, the
	 * method creates one and sets it to the application.
	 * @param component any application component
	 * @return the ExtOpThreadsMonitor
	 */
	public static ExtOpThreadsMonitor getExtOpThreadsMonitor(final Component component) {
		if (ApplicationManager.monitor != null) {
			Window w = SwingUtilities.getWindowAncestor(component);
			if ((component instanceof Dialog) || (component instanceof Frame)) {
				w = (Window) component;
			}
			if (w == ApplicationManager.monitor.getOwner()) {
				if (ApplicationManager.application != null) {
					ApplicationManager.monitor.setResourceBundle(ApplicationManager.application.getResourceBundle());
				}
				return ApplicationManager.monitor;
			} else {

				final Container comp = ApplicationManager.monitor.eOPC;
				ApplicationManager.monitor.dispose();
				if (w instanceof Frame) {
					ApplicationManager.monitor = new ExtOpThreadsMonitor((Frame) w,
							"applicationmanager.current_operations", false);
				} else if (w instanceof Dialog) {
					ApplicationManager.monitor = new ExtOpThreadsMonitor((Dialog) w,
							"applicationmanager.current_operations", false);
				} else {
					throw new IllegalArgumentException("The component must be in a Frame or a Dialog");
				}

				ApplicationManager.monitor.setContentPane(comp);
				if (ApplicationManager.application != null) {
					ApplicationManager.monitor.setResourceBundle(ApplicationManager.application.getResourceBundle());
				}
				ApplicationManager.monitor.pack();
				ApplicationManager.setLocationSouthEast(ApplicationManager.monitor);
				return ApplicationManager.monitor;
			}
		} else {
			Window w = SwingUtilities.getWindowAncestor(component);
			if ((component instanceof Dialog) || (component instanceof Frame)) {
				w = (Window) component;
			}
			if (w instanceof Frame) {
				ApplicationManager.monitor = new ExtOpThreadsMonitor((Frame) w, "applicationmanager.current_operations",
						false);
			} else if (w instanceof Dialog) {
				ApplicationManager.monitor = new ExtOpThreadsMonitor((Dialog) w,
						"applicationmanager.current_operations", false);
			} else {
				throw new IllegalArgumentException("The component must be in a Frame or Dialog");
			}
			ApplicationManager.monitor.setContentPane(new ExtOpThreadsMonitorComponent());
			if (ApplicationManager.application != null) {
				ApplicationManager.monitor.setResourceBundle(ApplicationManager.application.getResourceBundle());
			}
			ApplicationManager.monitor.pack();
			ApplicationManager.setLocationSouthEast(ApplicationManager.monitor);
			return ApplicationManager.monitor;
		}
	}

	/**
	 * Returns the icon that defines the ExtOpThreadsMonitor window.
	 * @return the ExtOpThreadsMonitor icon
	 */
	public static ImageIcon getDefaultExtOpThreadsMonitorIcon() {
		try {
			if (ApplicationManager.extOpThreadsMonitorIcon == null) {
				ApplicationManager.extOpThreadsMonitorIcon = ImageManager.getIcon(ImageManager.EXT_OP_THREADS_MONITOR);
				if (ApplicationManager.extOpThreadsMonitorIcon == null) {
					ApplicationManager.logger.debug("{} icon not found", ImageManager.EXT_OP_THREADS_MONITOR);
					return null;
				}
			}
			return ApplicationManager.extOpThreadsMonitorIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Returns the default icon for the <code>Download</code> buttons.
	 * @return the <code>Download</code> button default icon
	 */
	public static ImageIcon getDefaultDownloadAttachIcon() {
		try {
			if (ApplicationManager.downloadAttachIcon == null) {
				ApplicationManager.downloadAttachIcon = ImageManager.getIcon(ImageManager.DOWNLOADING);
				if (ApplicationManager.downloadAttachIcon == null) {
					ApplicationManager.logger.debug("{} icon not found", ImageManager.DOWNLOADING);
					return null;
				}
			}
			return ApplicationManager.downloadAttachIcon;
		} catch (final Exception e) {
			ApplicationManager.logger.error(null, e);
			return null;
		}
	}

	/**
	 * Creates a String with the elements contained by a Vector, separating the contents by ';'
	 * @param v
	 * @return the String with the List elements separated by ';'
	 */
	public static String vectorToStringSeparateBySemicolon(final List v) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < v.size(); i++) {
			sb.append(v.get(i));
			if (i < (v.size() - 1)) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

	/**
	 * Creates a String with the elements contained by a Vector, separating the contents by the
	 * separator passed as parameter
	 * @param v
	 * @param s the separator to use
	 * @return the String with the List elements separated by the separator
	 */
	public static String vectorToStringSeparateBy(final List v, final String s) {
		return ParseTools.ListToStringSeparateBy(v, s);
	}

	/**
	 * Splits a String into a vector, using the separator as delimiter
	 * @param s the String to split
	 * @param separator the string that separates the components
	 * @return a List with the separated elements
	 */
	public static List getTokensAt(final String s, final String separator) {
		final List v = new Vector();
		if (s == null) {
			return v;
		}
		if (separator == null) {
			v.add(s);
			return v;
		}
		final StringTokenizer st = new StringTokenizer(s, separator);
		while (st.hasMoreTokens()) {
			v.add(st.nextToken());
		}
		return v;
	}

	/**
	 * Replaces a piece of text for another
	 * @param source the source
	 * @param text the text to change
	 * @param newText the text that will replace the match
	 * @param forceWord forces the substitution of the text
	 * @return the original text with the replacements
	 */
	public static String replaceText(final String source, final String text, final String newText, final boolean forceWord) {
		// TODO complete the javadoc, to determine what is forceWord
		final String sHTMLContent = source;
		// Now search and replace
		final int textLength = text.length();
		final StringBuilder sbResult = new StringBuilder(sHTMLContent.length());
		int i = 0;
		for (i = 0; i <= (sHTMLContent.length() - textLength); i++) {
			// Additional comprobation. Previous characters are not letters to
			// consider the complete word
			if ((sHTMLContent.regionMatches(i, text, 0, textLength))
					&& (!(forceWord) || ((!Character.isLetterOrDigit(sHTMLContent.charAt(i - 1))) && (!Character
							.isLetterOrDigit(sHTMLContent.charAt(i + textLength)))))) {
				sbResult.append(newText);
				// Now update the index i
				i = (i + textLength) - 1;
				// Loop continue to allo more than one substitution
			} else { // If there is not match then add the character
				sbResult.append(sHTMLContent.charAt(i));
			}
		}

		// Now, from contenidoHTML.length()-textLength to the end append
		for (int j = Math.max(i, sHTMLContent.length() - textLength); j < sHTMLContent.length(); j++) {
			sbResult.append(sHTMLContent.charAt(j));
		}
		return sbResult.toString();
	}

	/**
	 * Replaces a piece of text for another
	 * @param source the source
	 * @param text the text to change
	 * @param newText the text that will replace the match
	 * @return the original text with the replacements
	 */
	public static String replaceText(final String source, final String text, final String nexText) {
		return ApplicationManager.replaceText(source, text, nexText, true);
	}

	/**
	 * Creates an Icon with a determined width and height
	 * @param imageIcon the source icon
	 * @param width the desired icon width
	 * @param heighth the desired icon height
	 * @return the icon with the new dimensions
	 */
	public static Icon rescaleIcon(final ImageIcon imageIcon, final int width, final int heighth) {
		final java.awt.Image ir = imageIcon.getImage().getScaledInstance(width, heighth, java.awt.Image.SCALE_DEFAULT);
		return new ImageIcon(ir);
	}

	/**
	 * Returns a Map with key-value corresponding with result to apply two 'tokenizer' actions.
	 * For example, <br>
	 * <br>
	 * s= "field1:equivalentfield1;field2:equivalentfield2;...;fieldn:equivalententfieldn" <br>
	 * separator1=";" <br>
	 * separator2=":" <br>
	 * <br>
	 * returns <code>Hashtable</code>: <br>
	 * <br>
	 * { field1 equivalentfield1} <br>
	 * { field2 equivalentfield2} <br>
	 * { ... ... } <br>
	 * { fieldn equivalentfieldn} <br>
	 * <br>
	 *
	 * Note: It also accepts : string =
	 * "formfieldpk1;formfieldpk2:equivalententityfieldpk2;formfieldpk3...;formfieldpkn:equivalententityfieldpkn"
	 * <br>
	 * returning: <br>
	 * <br>
	 *
	 * { field1 field1} <br>
	 * { field2 equivalentfield2} <br>
	 * { field3 field3} <br>
	 * { ... ... } <br>
	 * { fieldn equivalentfieldn} <br>
	 * <br>
	 * @param sValue The <code>String</code> with values
	 * @param separator1 Separator for first <code>Tokenizer</code>
	 * @param separator2 Separator for second <code>Tokenizer</code> for each token obtained previously
	 * @return <code>Hashtable</code> with key-value
	 */
	public static Map getTokensAt(final String sValue, final String separator1, final String separator2) {
		final Map hashTokens = new Hashtable();
		if ((sValue.indexOf(separator1) == -1) && (sValue.indexOf(separator2) == -1)) {
			hashTokens.put(sValue, sValue);
			return hashTokens;
		}
		final StringTokenizer stSeparator1 = new StringTokenizer(sValue, separator1);
		while (stSeparator1.hasMoreTokens()) {
			final StringTokenizer stSeparator2 = new StringTokenizer(stSeparator1.nextToken(), separator2);
			final String tokenValue = stSeparator2.nextToken();
			if (!stSeparator2.hasMoreTokens()) {
				hashTokens.put(tokenValue, tokenValue);
			} else {
				hashTokens.put(tokenValue, stSeparator2.nextToken());
			}

		}
		return hashTokens;
	}

	/**
	 * Class that defines a dialog to show the Help to the application user.
	 */
	protected static class HelpDialog extends EJDialog implements Internationalization {

		protected HTMLDataField htmlDataField = null;

		protected JButton save = new JButton("save");

		protected JButton close = new JButton("close");

		protected boolean saveContents = false;

		/**
		 * Creates a help dialog.
		 * @param owner
		 */
		public HelpDialog(final Frame owner) {
			super(owner, false);
			this.init();
		}

		protected void init() {
			this.setAutoPackOnOpen(false);

			this.getContentPane().setLayout(new GridBagLayout());
			final Map p = new Hashtable();
			p.put("attr", "html");
			p.put("rows", "20");
			p.put("expand", "yes");
			p.put("labelvisible", "no");
			p.put("dim", "text");
			this.htmlDataField = new HTMLDataField(p) {

				@Override
				protected boolean checkDefineHelpPermission() {
					return false;
				}

			};
			final JPanel jbButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			jbButtonsPanel.add(this.save);
			jbButtonsPanel.add(this.close);
			this.save.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					HelpDialog.this.saveContents = true;
					HelpDialog.this.setVisible(false);
				}
			});
			this.close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					HelpDialog.this.saveContents = false;
					HelpDialog.this.setVisible(false);
				}
			});
			this.save.setIcon(ImageManager.getIcon(ImageManager.SAVE_FILE));
			this.close.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
			this.getContentPane()
			.add(this.htmlDataField,
					new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(1, 1, 1, 1), 0, 0));

			this.getContentPane()
			.add(jbButtonsPanel,
					new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
			this.htmlDataField.setEnabled(true);
		}

		/**
		 * Creates a Help dialog.
		 * @param owner
		 */
		public HelpDialog(final Dialog owner) {
			super(owner, false);
			this.init();
		}

		public void setContent(final String s) {
			this.htmlDataField.setValue(s);
		}

		/**
		 * Returns the dialog content in HTML.
		 * @return
		 */
		public String getContent() {
			if (this.htmlDataField.isEmpty()) {
				return "";
			}
			return (String) this.htmlDataField.getValue();
		}

		/**
		 * Shows or hides the help dialog.
		 * @param visible
		 * @param showSave if true, the 'Save' button will be shown
		 * @param pack if true, the dialog will be packed
		 * @param point the point in which the dialog will be shown
		 * @param dimension the dialog dimension
		 */
		public void setVisible(final boolean visible, final boolean showSave, final boolean pack, final Point point, final Dimension dimension) {
			this.saveContents = false;
			this.save.setVisible(showSave);
			((JTextComponent) this.htmlDataField.getDataField()).setEditable(showSave);
			this.htmlDataField.setButtonPanelVisible(showSave);
			if ((point != null) && (dimension != null)) {
				this.setLocation(point);
				this.setSize(dimension);
			} else if (pack) {
				this.pack();
				if (this.getWidth() < 400) {
					this.setSize(400, this.getHeight());
				}
				ApplicationManager.center(this);
			}
			super.setVisible(visible);
		}

		@Override
		public void setResourceBundle(final ResourceBundle res) {
			this.save.setText(ApplicationManager.getTranslation("save", res));
			this.close.setText(ApplicationManager.getTranslation("close", res));
		}

		/**
		 * getTextsToTranslate
		 * @return Vector
		 */
		@Override
		public List getTextsToTranslate() {
			return null;
		}

		/**
		 * setComponentLocale
		 * @param locale Locale
		 */
		@Override
		public void setComponentLocale(final Locale locale) {
		}

	}

	/**
	 * The ApplicationManager HelpDialog
	 */
	protected static HelpDialog helpDialog = null;

	/**
	 * Shows the help dialog.
	 * @param owner
	 * @param title the window title
	 * @param html the contents in HTML
	 * @param showSave allows to save the modifications
	 * @return the result if the showSave option has been chosen, null otherwise
	 */
	public static String showHelpDialog(final Window owner, final String title, final String html, final boolean showSave) {
		Point p = null;
		Dimension d = null;
		boolean wasNull = false;
		if ((ApplicationManager.helpDialog == null) || (ApplicationManager.helpDialog.getOwner() != owner)) {
			if (ApplicationManager.helpDialog != null) {
				p = ApplicationManager.helpDialog.getLocation();
				d = ApplicationManager.helpDialog.getSize();
				ApplicationManager.helpDialog.dispose();
			} else {
				wasNull = true;
			}
			if (owner instanceof Frame) {
				ApplicationManager.helpDialog = new HelpDialog((Frame) owner);
			} else if (owner instanceof Dialog) {
				ApplicationManager.helpDialog = new HelpDialog((Dialog) owner);
			} else {
				ApplicationManager.helpDialog = new HelpDialog((Frame) null);
			}
		}
		ApplicationManager.helpDialog.setModal(showSave);
		ApplicationManager.helpDialog.setContent(html);
		ApplicationManager.helpDialog.setTitle(
				ApplicationManager.getTranslation(title, ApplicationManager.getApplication().getResourceBundle()));
		ApplicationManager.helpDialog.setResourceBundle(ApplicationManager.getApplication().getResourceBundle());

		ApplicationManager.helpDialog.setVisible(true, showSave, wasNull, p, d);
		if (ApplicationManager.helpDialog.saveContents) {
			return ApplicationManager.helpDialog.getContent();
		} else {
			return null;
		}
	}

	/**
	 * A progress bar.
	 */
	public static class CustomBar extends JComponent {

		private int v = 0;

		private String s = "";

		/**
		 * Creates a progress bar.
		 */
		public CustomBar() {
			this.setOpaque(false);
			this.setForeground(Color.black);
		}

		/**
		 * Set the progress bar value, in %. The value must be between 0 and 100.
		 * @param value the progress bar value
		 */
		public void setValue(final int value) {
			this.v = value;
			this.s = value + "%";
		}

		/**
		 * Returns the progress bar value in %
		 * @return the progress bar value between 0 and 100
		 */
		public int getValue() {
			return this.v;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			final int w = (int) ((this.getWidth() - 2) * (this.v / 100.0));
			g.setColor(this.getBackground());
			g.fillRect(1, 1, w, this.getHeight() - 3);
			this.paintString(g, w);
		}

		protected void paintString(final Graphics g, final int x) {
			final Shape c = g.getClip();
			final int l = g.getFontMetrics().stringWidth(this.s);
			final int start = (this.getWidth() / 2) - (l / 2);
			g.setColor(Color.white);
			g.drawString(this.s, start, (((this.getHeight() - 2) + g.getFontMetrics().getAscent())
					- g.getFontMetrics().getLeading() - g.getFontMetrics().getDescent()) / 2);
			g.clipRect(x, 1, this.getWidth() - x, this.getHeight() - 3);
			g.setColor(this.getForeground());
			g.drawString(this.s, start, (((this.getHeight() - 2) + g.getFontMetrics().getAscent())
					- g.getFontMetrics().getLeading() - g.getFontMetrics().getDescent()) / 2);
			g.setClip(c);
		}

	};

	/**
	 * Shows a dialog containing the System Information.
	 */
	public static void showSystemInformation() {
		try {
			final JDialog dialog = new JDialog(ApplicationManager.application.getFrame(),
					ApplicationManager.getTranslation(ApplicationManager.SYSTEM_INFORMATION_MESSAGE,
							ApplicationManager.application.getResourceBundle()),
					true);
			dialog.getContentPane().add(new JScrollPane(ApplicationManager.getSystemPropertiesComponent()));
			dialog.pack();
			ApplicationManager.center(dialog);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		} catch (final Exception ex) {
			ApplicationManager.logger.error(null, ex);
			MessageDialog.showMessage(ApplicationManager.getApplication().getFrame(),
					"applicationmanager.show_help_error", ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					ApplicationManager.getApplication().getResourceBundle());

		}
	}

	public static void showCacheInformation() {
		try {
			CacheManagerViewer.showViewer(null);
		} catch (final Exception ex) {
			MessageDialog.showMessage(ApplicationManager.getApplication().getFrame(),
					"applicationmanager.show_help_error", ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					ApplicationManager.getApplication().getResourceBundle());
			ApplicationManager.logger.error("{}", ex.getMessage(), ex);
		}
	}


	/**
	 * Wraps an image with the java.awt.datatransfer.DataFlavor.imageFlavor flavour fixed.
	 */
	private static class ImageSelection implements Transferable, ClipboardOwner {

		public static final DataFlavor myFlavor = java.awt.datatransfer.DataFlavor.imageFlavor;

		private java.awt.Image image = null;

		public ImageSelection(final java.awt.Image im) {
			this.image = im;
		}

		@Override
		public void lostOwnership(final Clipboard cb, final Transferable trans) {

		}

		@Override
		public synchronized DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { ImageSelection.myFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return ImageSelection.myFlavor.equals(flavor);
		}

		@Override
		public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!ImageSelection.myFlavor.equals(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return this.image;
		}

	}

	/**
	 * Copies an image to the clipboard.
	 * @param im the image to copy
	 * @throws Exception
	 */
	public static void copyToClipboard(final java.awt.Image im) throws Exception {
		Clipboard clipboard;
		ImageSelection selec;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		selec = new ImageSelection(im);
		clipboard.setContents(selec, selec);
	}

	/**
	 * Gets an image from the clipboard.
	 * @return The contents of the clipboard as image.
	 * @throws Exception
	 */
	public static java.awt.Image pasteImageFromClipboard() throws Exception {
		Clipboard clipboard;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable trans = clipboard.getContents(new ImageSelection(null));
		if ((trans != null) && trans.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.imageFlavor)) {
			final Object image = trans.getTransferData(java.awt.datatransfer.DataFlavor.imageFlavor);
			if (image instanceof java.awt.Image) {
				return (BufferedImage) image;
			}
		}
		return null;
	}

}
