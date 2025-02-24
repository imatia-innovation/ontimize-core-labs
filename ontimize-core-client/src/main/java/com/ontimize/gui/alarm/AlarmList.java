package com.ontimize.gui.alarm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Freeable;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.images.ImageManager;

/**
 * Component that implements one alarm list. These alarms can be classified in two different
 * types:<br>
 * - information: a normal informative text.<br>
 * -warnings: informative text with audible and visual signals until these alarms are 'recognized'.
 * The alarm will be 'recognized' when the button, which is associated with this alarm, is
 * stroked.(It appears near the text). <BR>
 * Alarms are shown in a list component.
 */
public class AlarmList extends JPanel implements DataComponent, ListSelectionListener, ListDataListener, MouseListener,
OpenDialog, KeyListener, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(AlarmList.class);

	ImageIcon informationIcon = null;

	ImageIcon warningIcon = null;

	AlarmListener[] listeners = new AlarmListener[0];

	int rowNumber = 10;

	class AlarmWindow extends JDialog {

		JLabel alarmJLabel = new JLabel("Alarm: ");

		JLabel dateJLabel = new JLabel("Date: ");

		JTextField alarmJText = new JTextField();

		JTextField dateJText = new JTextField();

		JButton recognizeButton = new JButton("Recognize Alarm");

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		Alarm alarm = null;

		public AlarmWindow(final Frame parent, final String tit) {
			super(parent, tit, true);
			this.init();
		}

		public AlarmWindow(final Dialog parent, final String tit) {
			super(parent, tit, true);
			this.init();
		}

		private void init() {
			this.getContentPane().setLayout(new GridBagLayout());
			this.getContentPane()
			.add(this.alarmJLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			this.getContentPane()
			.add(this.alarmJText,
					new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 5, 5, 5), 0, 0));
			this.getContentPane()
			.add(this.dateJLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
			this.getContentPane()
			.add(this.dateJText,
					new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 5, 5, 5), 0, 0));
			this.getContentPane()
			.add(this.recognizeButton,
					new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(10, 5, 10, 5), 0, 0));
			this.alarmJText.setEditable(false);
			this.dateJText.setEditable(false);
			this.alarmJText.setBackground(this.getBackground());
			this.dateJText.setBackground(this.getBackground());
			this.recognizeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (AlarmWindow.this.alarm != null) {
						AlarmWindow.this.alarm.setRecognize(true);
						AlarmList.this.fireAlarmAcknowledge(AlarmWindow.this.alarm);
						AlarmList.this.fireContentsChanged();
					}
					AlarmWindow.this.setVisible(false);
				}
			});
		}

		public void setAlarm(final Alarm alarm) {
			this.alarm = alarm;
			this.alarmJText.setText(alarm.getText());
			this.dateJText.setText(this.dateFormat.format(alarm.getHour()));
		}

		public void centerInWindow() {
			this.pack();
			// Center the window
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		}

	}

	class AlarmThread extends Thread {

		boolean alarmsWithoutRecognize = false;

		AlarmList alarmList = null;

		boolean red = false;

		public AlarmThread(final AlarmList l) {
			this.alarmList = l;
		}

		@Override
		public void run() {
			while (true) {
				while (this.alarmsWithoutRecognize) {
					this.red = !this.red;
					if (this.red) {
						Toolkit.getDefaultToolkit().beep();
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							AlarmThread.this.alarmList.repaint();
						}
					});
					try {
						Thread.sleep(500);
					} catch (final Exception e) {
						AlarmList.logger.trace(null, e);
					}
				}
				try {
					Thread.sleep(500);
				} catch (final Exception e) {
					AlarmList.logger.trace(null, e);
				}
			}
		}

	}

	/*********************** MODEL *****************************************/

	class MyListModel implements ListModel {

		/**
		 * alarms
		 */
		protected List alarms = new Vector();

		protected List listeners = new Vector();

		public MyListModel() {
		}

		@Override
		public void addListDataListener(final ListDataListener l) {
			this.listeners.add(l);
		}

		@Override
		public Object getElementAt(final int index) {
			// Sort by date.
			try {
				return this.alarms.get(index);
			} catch (final Exception e) {
				AlarmList.logger.trace(null, e);
				return null;
			}
		}

		@Override
		public int getSize() {
			return this.alarms.size();
		}

		@Override
		public void removeListDataListener(final ListDataListener l) {
			this.listeners.remove(l);
		}

		public void fireContentsChanged() {
			for (int i = 0; i < this.listeners.size(); i++) {
				final ListDataListener l = (ListDataListener) this.listeners.get(i);
				if (l != null) {
					l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.alarms.size()));
				}
			}
		}

		/**
		 * A new alarm is added to the list. If the alarm key ( {@link Alarm#getKey())} already exist the
		 * old one is replaced by the entry parameter.
		 * @param alarm
		 */
		public void addAlarm(final Alarm alarm) {
			for (int i = 0; i < this.alarms.size(); i++) {
				if (((Alarm) this.alarms.get(i)).getKey().equals(alarm.key)) {
					this.alarms.remove(i);
					i = i - 1;
				}
			}
			this.alarms.add(this.alarms.size(), alarm);
			Collections.sort(this.alarms);
			this.fireContentsChanged();
		}

		public void removeAlarm(final Alarm alarm) {
			if ((alarm.getType() == Alarm.WARNING) && !alarm.isRecognized()) {
				AlarmList.logger.debug("It is not allowed to delete unrecognized WARNINGS");
			} else {
				this.alarms.remove(alarm);
				Collections.sort(this.alarms);
				this.fireContentsChanged();
			}
		}

		public void removeAlarm(final int index) {
			final Alarm alarm = (Alarm) this.alarms.get(index);
			if ((alarm.getType() == Alarm.WARNING) && !alarm.isRecognized()) {
				AlarmList.logger.debug("It is not allowed to delete unrecognized WARNINGS");
			} else {
				this.alarms.remove(alarm);
				Collections.sort(this.alarms);
				this.fireContentsChanged();
			}
		}

		public void removeAlarm(final int[] indexes) {
			// Alarms that are not removed are copied to another vector;
			final List aux = new Vector();
			for (int i = 0; i < this.alarms.size(); i++) {
				boolean deleteI = false;
				for (int j = 0; j < indexes.length; j++) {
					if (indexes[j] == i) {
						deleteI = true;
					}
				}
				// If alarm must be deleted, then check that it is not WARNING
				// without
				// recognize.
				if (!deleteI) {
					aux.add(aux.size(), this.alarms.get(i));
				} else {
					final Alarm alarm = (Alarm) this.alarms.get(i);
					if ((alarm.getType() == Alarm.WARNING) && !alarm.isRecognized()) {
						aux.add(aux.size(), this.alarms.get(i));
					} else {
						// Do not add to aux
					}
				}
			}
			this.alarms = aux;
			Collections.sort(this.alarms);
			this.fireContentsChanged();
		}

		public void deleteAll() {
			for (int i = 0; i < this.alarms.size(); i++) {
				final Alarm alarma = (Alarm) this.alarms.get(i);
				if ((alarma.getType() == Alarm.WARNING) && !alarma.isRecognized()) {
					this.alarms.remove(alarma);
					i = i - 1;
				} else {
					this.alarms.remove(alarma);
					i = i - 1;
				}
			}
			Collections.sort(this.alarms);
			this.fireContentsChanged();
		}

	}

	/*********************** RENDER *****************************************/
	/**
	 * Renderer used to represent the alarms in the list
	 */
	class AlarmRender extends JPanel implements ListCellRenderer {

		JLabel text = new JLabel();

		JLabel hour = new JLabel();

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		public AlarmRender() {
			this.setLayout(new GridBagLayout());
			this.add(this.text, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
			this.add(this.hour, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));

		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			if (value == null) {
				return null;
			} else {
				this.setBackground(isSelected ? Color.blue : Color.white);
				this.text.setForeground(isSelected ? Color.white : Color.black);
				this.hour.setForeground(isSelected ? Color.white : Color.blue);
				if (value instanceof Alarm) {
					if (((Alarm) value).getType() == Alarm.WARNING) {
						this.text.setIcon(AlarmList.this.warningIcon);
						if (!((Alarm) value).isRecognized() && AlarmList.this.t.red) {
							this.setBackground(Color.red);
						}
					} else {
						this.text.setIcon(AlarmList.this.informationIcon);
					}
					this.text.setText(((Alarm) value).toString());
					this.hour.setText(((Alarm) value).getHourAsString());
				} else {
					this.text.setText(value.toString());
				}
				return this;
			}
		}

	}

	public static String BUTTON_KEY = "M_RECOGNIZE_ALARMS";

	/**
	 * List
	 */
	private final JList list = new JList();

	private final JLabel labelComponent = new JLabel();

	private final JPanel northPanel = new JPanel(new GridBagLayout());

	private final JPanel southPanel = new JPanel(new GridBagLayout());

	private final JLabel state = new JLabel();

	private final JLabel labelInfo = new JLabel();

	private final JLabel labelWarning = new JLabel();

	private final JButton recognizeButton = new JButton(AlarmList.BUTTON_KEY);

	protected Object attribute = null;

	protected Object storedValue = null;

	protected Frame parentFrame = null;

	AlarmThread t = new AlarmThread(this);

	AlarmWindow alarmWindow = null;

	protected boolean show = true;

	protected boolean required = false;

	protected boolean modificable = false;

	public AlarmList(final Map parameters) {
		this.init(parameters);
		this.list.setVisibleRowCount(this.rowNumber);

		// Icons
		this.informationIcon = ImageManager.getIcon(ImageManager.INFO_16);
		this.warningIcon = ImageManager.getIcon(ImageManager.WARNING);
		this.setLayout(new BorderLayout());
		this.list.setCellRenderer(new AlarmRender());
		this.list.setModel(new MyListModel());
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.southPanel, BorderLayout.SOUTH);
		if (this.informationIcon != null) {
			this.labelInfo.setIcon(ImageManager.getIcon(ImageManager.INFO_16));
		}
		this.labelInfo.setText("Information");
		if (this.warningIcon != null) {
			this.labelWarning.setIcon(ImageManager.getIcon(ImageManager.WARNING));
		}
		this.labelWarning.setText("Warning");
		this.northPanel.add(this.labelComponent, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.southPanel.add(this.state, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.southPanel.add(this.recognizeButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		try {
			this.labelComponent.setFont(this.labelComponent.getFont().deriveFont(Font.BOLD));
		} catch (final Exception e) {
			AlarmList.logger.error("Error changing the font", e);
		}
		this.northPanel.add(this.labelInfo, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
		this.northPanel.add(this.labelWarning, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
		this.add(new JScrollPane(this.list));
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		this.recognizeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int[] indexes = AlarmList.this.list.getSelectedIndices();
				if (indexes.length == 1) {
					// Alarm
					final Alarm alarm = (Alarm) ((MyListModel) AlarmList.this.list.getModel()).getElementAt(indexes[0]);
					if ((alarm.getType() == Alarm.WARNING) && !alarm.isRecognized()) {
						// Window
						if (AlarmList.this.alarmWindow == null) {
							final Window w = SwingUtilities.getWindowAncestor(AlarmList.this);
							if (w instanceof Dialog) {
								AlarmList.this.alarmWindow = new AlarmWindow((Dialog) w, "Recognize Alarm");
							} else {
								AlarmList.this.alarmWindow = new AlarmWindow((Frame) w, "Recognize Alarm");
							}
						}
						AlarmList.this.alarmWindow.setAlarm(alarm);
						AlarmList.this.alarmWindow.centerInWindow();
						AlarmList.this.alarmWindow.setVisible(true);
					}
				} else {
					final int option = MessageDialog.showMessage(AlarmList.this.parentFrame,
							"Do you want to recognize all selected alarms?", JOptionPane.QUESTION_MESSAGE,
							JOptionPane.YES_NO_OPTION, null);
					if (option == JOptionPane.YES_OPTION) {
						for (int i = 0; i < indexes.length; i++) {
							final Alarm alarm = (Alarm) ((MyListModel) AlarmList.this.list.getModel())
									.getElementAt(indexes[i]);
							if (alarm.getType() == Alarm.WARNING) {
								alarm.setRecognize(true);
								AlarmList.this.fireAlarmAcknowledge(
										(Alarm) ((MyListModel) AlarmList.this.list.getModel()).getElementAt(i));
							}
						}
						AlarmList.this.fireContentsChanged();
					}
				}
			}
		});
		this.recognizeButton.setEnabled(false);
		this.t.start();
		this.list.getModel().addListDataListener(this);
		this.list.addListSelectionListener(this);
		this.list.addMouseListener(this);
		this.list.addKeyListener(this);
		this.list.setToolTipText("Double click to recognize alarms. Space bar to go to the next unrecognized alarm");
	}

	public List getAlarmList() {
		final List v = new Vector();
		for (int i = 0; i < this.list.getModel().getSize(); i++) {
			v.add(i, this.list.getModel().getElementAt(i));
		}
		return v;
	}

	public ListCellRenderer getRenderer() {
		return this.list.getCellRenderer();
	}

	public void addAlarm(final Object key, final String code, final int type, final boolean recognized) {
		this.addAlarm(key, code, type, code, recognized);
	}

	public void addAlarm(final Object key, final String text, final int type, final String code, final boolean recognized) {
		this.addAlarm(key, text, type, code, new Date(), recognized);
	}

	public void addAlarm(final Object key, final String text, final int type, final String code, final Date date, final boolean recognized) {
		final MyListModel m = (MyListModel) this.list.getModel();
		final Alarm a = new Alarm(key, text, type, date, code, recognized);
		m.addAlarm(a);
		this.list.setModel(m);
		this.list.ensureIndexIsVisible(m.getSize());
		this.fireAlarmFired(a);
	}

	public void removeAlarm(final int index) {
		final MyListModel m = (MyListModel) this.list.getModel();
		m.removeAlarm(index);
	}

	public void removeSelected() {
		final MyListModel m = (MyListModel) this.list.getModel();
		final int[] indexes = this.list.getSelectedIndices();
		m.removeAlarm(indexes);
		this.list.clearSelection();
	}

	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr;
			this.labelComponent.setText(attr.toString());
		} else {
			this.labelComponent.setText("Alarms:");
			AlarmList.logger.debug(this.getClass().toString() + ": 'attr' parameter is missing");
		}
		// If it is or not visible
		final Object visible = parameters.get("visible");
		if (visible != null) {
			if (visible.equals("no")) {
				this.show = false;
			} else {
				this.show = true;
			}
		}

		// Required
		final Object required = parameters.get("required");
		if (required != null) {
			if (required.equals("yes")) {
				this.required = true;
			} else {
				this.required = false;
			}
		}

		final Object rows = parameters.get("rows");
		if (rows != null) {
			try {
				this.rowNumber = Integer.parseInt(rows.toString());
			} catch (final Exception e) {
				AlarmList.logger.error("Error in 'rows' parameter " + e.getMessage(), e);
			}
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		v.add(AlarmList.BUTTON_KEY);
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle r) {
		if (this.attribute != null) {
			try {
				if (r != null) {
					this.labelComponent.setText(r.getString(this.attribute.toString()));
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					AlarmList.logger.debug(null, e);
				} else {
					AlarmList.logger.trace(null, e);
				}
			}
		}
		try {
			if (r != null) {
				this.recognizeButton.setText(r.getString(AlarmList.BUTTON_KEY));
			}
		} catch (final Exception e) {
			this.recognizeButton.setText("Recognize alarms");
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				AlarmList.logger.debug(null, e);
			} else {
				AlarmList.logger.trace(null, e);
			}
		}
		// For each alarm, setResourceBundle
		final MyListModel m = (MyListModel) this.list.getModel();
		for (int i = 0; i < m.getSize(); i++) {
			((Alarm) m.getElementAt(i)).setResourceBundle(r);
		}
		this.fireContentsChanged();
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setParentFrame(final Frame parent) {
		this.parentFrame = parent;
	}

	public JList getJList() {
		return this.list;
	}

	@Override
	public void contentsChanged(final ListDataEvent e) {
		this.t.alarmsWithoutRecognize = false;
		final MyListModel m = (MyListModel) e.getSource();
		for (int i = 0; i < m.getSize(); i++) {
			final Alarm al = (Alarm) m.getElementAt(i);
			if ((al.getType() == Alarm.WARNING) && !al.isRecognized()) {
				this.t.alarmsWithoutRecognize = true;
				// Ensure that it is visible
				this.list.ensureIndexIsVisible(i);
				this.state.setForeground(Color.red);
				this.state.setText("There are unrecognized alarms.");
				return;
			}
		}
		this.state.setForeground(new Color(0, 153, 0));
		this.state.setText("All alarms are recognized. Total: " + Integer.toString(m.getSize()) + " alarms.");
	}

	public void fireContentsChanged() {
		((MyListModel) this.list.getModel()).fireContentsChanged();
	}

	@Override
	public void intervalRemoved(final ListDataEvent e) {
	}

	@Override
	public void intervalAdded(final ListDataEvent e) {
	}

	@Override
	public void mouseClicked(final MouseEvent e) {

		if (e.getClickCount() == 2) {
			final int[] indexes = this.list.getSelectedIndices();
			if (indexes.length == 1) {
				// Alarm
				final Alarm alarma = (Alarm) ((MyListModel) this.list.getModel()).getElementAt(indexes[0]);
				if ((alarma.getType() == Alarm.WARNING) && !alarma.isRecognized()) {
					// Window
					if (this.alarmWindow == null) {
						final Window w = SwingUtilities.getWindowAncestor(AlarmList.this);
						if (w instanceof Dialog) {
							this.alarmWindow = new AlarmWindow((Dialog) w, "Recognize Alarm");
						} else {
							this.alarmWindow = new AlarmWindow((Frame) w, "Recognize Alarm");
						}
					}
					this.alarmWindow.setAlarm(alarma);
					this.alarmWindow.centerInWindow();
					this.alarmWindow.setVisible(true);
				}
			} else {
				final int option = MessageDialog.showMessage(this.parentFrame,
						"Do you want to recognize all selected alarms?", JOptionPane.QUESTION_MESSAGE,
						JOptionPane.YES_NO_OPTION,
						null);
				if (option == JOptionPane.YES_OPTION) {
					for (int i = 0; i < indexes.length; i++) {
						final Alarm alarm = (Alarm) ((MyListModel) this.list.getModel()).getElementAt(indexes[i]);
						if (alarm.getType() == Alarm.WARNING) {
							alarm.setRecognize(true);
						}
					}
					this.fireContentsChanged();
				}
			}
		}
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			this.removeSelected();
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			// Select next in the model
			final int selected = this.list.getSelectedIndex();
			if (selected >= -1) {
				for (int i = selected + 1; i < this.list.getModel().getSize(); i++) {
					final Alarm a = (Alarm) ((MyListModel) this.list.getModel()).getElementAt(i);
					if ((a.getType() == Alarm.WARNING) && !a.isRecognized()) {
						this.list.setSelectedIndex(i);
						this.list.ensureIndexIsVisible(i);
						break;
					}
				}
			}
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		// If selection exists and some of the selected are a warning then
		// enable the button
		if (e.getFirstIndex() < 0) {
			this.recognizeButton.setEnabled(false);
		} else {
			this.recognizeButton.setEnabled(false);
			final int[] indices = this.list.getSelectedIndices();
			for (int i = 0; i < indices.length; i++) {
				if (((Alarm) ((MyListModel) this.list.getModel()).getElementAt(indices[i])).isWarning()) {
					this.recognizeButton.setEnabled(true);
					return;
				}
			}
		}
	}

	public void addAlarmListener(final AlarmListener l) {
		final AlarmListener[] aux = new AlarmListener[this.listeners.length + 1];
		for (int i = 0; i < this.listeners.length; i++) {
			aux[i] = this.listeners[i];
		}
		aux[this.listeners.length] = l;
		this.listeners = aux;
	}

	public void fireAlarmFired(final Alarm a) {
		for (int i = 0; i < this.listeners.length; i++) {
			this.listeners[i].alarmFired(a);
		}
	}

	public void fireAlarmAcknowledge(final Alarm a) {
		for (int i = 0; i < this.listeners.length; i++) {
			this.listeners[i].alarmAcknowledge(a);
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(super.getMaximumSize().width, 600);
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return this.modificable;
	}

	@Override
	public void setModifiable(final boolean modif) {
		this.modificable = modif;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.JAVA_OBJECT;
	}

	@Override
	public void deleteData() {
		((MyListModel) this.list.getModel()).deleteAll();
	}

	@Override
	public void setValue(final Object value) {
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		return null;
	}

	public boolean existNoRecognize() {
		final MyListModel m = (MyListModel) this.list.getModel();
		for (int i = 0; i < m.getSize(); i++) {
			final Alarm a = (Alarm) m.getElementAt(i);
			if (!a.isRecognized() && a.isWarning()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initPermissions() {
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
