package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.BMessage;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.SMessage;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator.Message;

public class Chat extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(Chat.class);

	protected static String USER = "User_";

	protected static String CONVERSATION = "chat.conversations";

	protected static String CLOSE_CONVERSATION = "chat.close_conversation";

	protected JUserList userList = null;

	protected JComponent headerUser = null;

	protected JComponent headerConnection = null;

	protected JOpenConnection connectionList = null;

	protected Map windowCache = new Hashtable();

	protected WindowHandler wHandler = new WindowHandler();

	protected EntityReferenceLocator locator = null;

	protected ChatControlThread control = null;

	protected int messagesCheckTime = -1;

	protected int chatCheckTime = 2000;

	protected static class UserListCellRenderer extends DefaultListCellRenderer {

		protected ImageIcon connectIcon = ImageManager.getIcon(ImageManager.USER_CONNECT);

		protected ImageIcon disconnectIcon = ImageManager.getIcon(ImageManager.USER_DISCONNECT);

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ((c instanceof JLabel) && (value instanceof User)) {
				final User u = (User) value;
				if (u.isConnected()) {
					((JLabel) c).setIcon(this.connectIcon);
				} else {
					((JLabel) c).setIcon(this.disconnectIcon);
				}
			}
			return c;
		}

	}

	protected static class JUserList extends JList implements UserChangeListener {

		public JUserList(final List l) {
			super();
			final DefaultListModel model = new DefaultListModel();
			for (int i = 0; i < l.size(); i++) {
				final User user = (User) l.get(i);
				user.addUserChangeListener(this);
				model.addElement(user);
			}
			this.setModel(model);

		}

		public User getUser(final String s) {
			final ListModel model = this.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				final User user = (User) model.getElementAt(i);
				if (user.equals(s)) {
					return user;
				}
			}
			return null;
		}

		@Override
		public void userChange(final UserChangeEvent event) {
			final ListModel model = this.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				final User user = (User) model.getElementAt(i);
				if (user.equals(event.getSource())) {
					this.fireSelectionValueChanged(i, i, false);
					return;
				}
			}
		}

	}

	protected class ConnectionListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ((c instanceof JLabel) && Chat.this.windowCache.containsKey(value)) {
				final UserChatWindow user = (UserChatWindow) Chat.this.windowCache.get(value);
				final ArrayList alList = user.getUser();
				((JLabel) c).setText(alList.toString());
			}
			return c;
		}

	}

	protected class JOpenConnection extends JList {

		public JOpenConnection() {
			this.setModel(new DefaultListModel());
			this.setCellRenderer(new ConnectionListCellRenderer());
		}

		public void addConnection(final Long connection) {
			((DefaultListModel) this.getModel()).addElement(connection);
		}

		public void removeConnection(final Long connection) {
			((DefaultListModel) this.getModel()).removeElement(connection);
		}

	}

	public Chat(final EntityReferenceLocator locator, final int messagesCheckTime, final int chatCheckTime) {
		this.locator = locator;
		this.messagesCheckTime = messagesCheckTime;
		this.chatCheckTime = chatCheckTime;
		this.init();
		this.initThreads();

	}

	protected List getUserList() {
		final List list = new Vector();
		if (this.locator != null) {
			try {
				final Entity entity = this.locator.getEntityReference(
						((UtilReferenceLocator) this.locator).getLoginEntityName(this.locator.getSessionId()));
				final List av = new Vector();
				av.add(Chat.USER);
				final EntityResult res = entity.query(new Hashtable(), av, this.locator.getSessionId());
				if ((res.getCode() != EntityResult.OPERATION_WRONG) && res.containsKey(Chat.USER)) {
					final List v = (List) res.get(Chat.USER);
					final String s = ((ClientReferenceLocator) this.locator).getUser();
					if (v.contains(s)) {
						v.remove(s);
					}
					for (int i = 0; i < v.size(); i++) {
						list.add(new User(v.get(i).toString(), false));
					}
				}
			} catch (final Exception e) {
				Chat.logger.error(null, e);
			}
		}
		return list;
	}

	protected void updateConnectUser() {
		if (this.locator != null) {
			try {
				final List listConnect = ((UtilReferenceLocator) this.locator).getConnectedUsers(this.locator.getSessionId());
				final ListModel model = this.userList.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final User user = (User) model.getElementAt(i);
					if (listConnect.contains(user.toString())) {
						user.connect();
					} else {
						user.disconnect();
					}
				}
			} catch (final Exception e) {
				Chat.logger.error(null, e);
			}
		}
	}

	protected User getUser(final String user) {
		try {
			if (user == null) {
				return this.userList.getUser(((ClientReferenceLocator) this.locator).getUser());
			}
			return this.userList.getUser(user);
		} catch (final Exception ex) {
			Chat.logger.error(null, ex);
			return null;
		}
	}

	protected class WindowHandler implements WindowListener {

		@Override
		public void windowActivated(final WindowEvent e) {
		}

		@Override
		public void windowClosed(final WindowEvent e) {
		}

		@Override
		public void windowClosing(final WindowEvent e) {
			final Object s = e.getSource();
			if ((s != null) && (s instanceof UserChatWindow)) {
				final UserChatWindow w = (UserChatWindow) s;
				if (Chat.this.windowCache.containsValue(w)) {
					final Enumeration enu = Collections.enumeration(Chat.this.windowCache.keySet());
					while (enu.hasMoreElements()) {
						final Object k = enu.nextElement();
						final UserChatWindow user = (UserChatWindow) Chat.this.windowCache.get(k);
						if (w.equals(user)) {
							Chat.this.windowCache.remove(k);
							return;
						}
					}
				}
			}
		}

		@Override
		public void windowDeactivated(final WindowEvent e) {

		}

		@Override
		public void windowDeiconified(final WindowEvent e) {

		}

		@Override
		public void windowIconified(final WindowEvent e) {

		}

		@Override
		public void windowOpened(final WindowEvent e) {

		}

	}

	protected class MouseHandler implements MouseListener {

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				final int index = Chat.this.userList.locationToIndex(e.getPoint());

				if (index >= 0) {
					final Object user = Chat.this.userList.getModel().getElementAt(index);
					if ((user instanceof User) && ((User) user).isConnected()) {
						final UserChatWindow w = Chat.this.openWindow((User) user);
						final int[] indexSelected = Chat.this.userList.getSelectedIndices();
						for (int i = 0; i < indexSelected.length; i++) {
							final Object oCurrentUser = Chat.this.userList.getModel().getElementAt(indexSelected[i]);
							if ((oCurrentUser instanceof User) && ((User) oCurrentUser).isConnected()
									&& (oCurrentUser != user)) {
								w.addUser((User) oCurrentUser);
							}
						}
						if (!w.isVisible()) {
							w.setVisible(true);
						}
						w.toFront();
					}
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

	}

	protected class UserChatWindow extends JDialog {

		protected ImageIcon iconAdd = ImageManager.getIcon(ImageManager.ADD_USER);

		protected ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.DELETE);

		protected ImageIcon closeIcon = ImageManager.getIcon(ImageManager.CANCEL);

		private final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		private JScrollPane sReceiver = null;

		private JTextPane receiveText = null;

		private JTextPane sendText = null;

		private JButton bSend = null;

		private final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss");

		private final Date date = new Date();

		private final AttributeSet atName = new SimpleAttributeSet();

		private final AttributeSet atMessage = new SimpleAttributeSet();

		private JToolBar toolBar = null;

		private final StoreUser userList = new StoreUser();

		private boolean initCommunication = false;

		private class StoreUser extends ArrayList {

			public List toStringArray() {
				final ArrayList list = new ArrayList();
				for (int i = 0; i < this.size(); i++) {
					list.add(this.get(i).toString());
				}
				return list;
			}

		}

		private long sessionCommunicationId = -1;

		public UserChatWindow(final User root) {
			this(System.currentTimeMillis());
			this.addUser(root);
		}

		public UserChatWindow(final long comunication) {
			super(Chat.fChat, false);
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.sessionCommunicationId = comunication;
			this.initCommunication = false;
			this.init();
		}

		protected long getSessionCommunicationId() {
			return this.sessionCommunicationId;
		}

		protected ArrayList getUser() {
			return this.userList;
		}

		protected void addUser(final User user) {
			this.userList.add(user);
			this.paintTitle();
		}

		protected void removeUser(final User user, final boolean notify) {
			if (this.userList.contains(user)) {
				if (notify && this.initCommunication) {
					for (int i = 0; i < this.userList.size(); i++) {
						try {
							final String currentUser = ((ClientReferenceLocator) Chat.this.locator).getUser();
							final List list = this.userList.toStringArray();
							list.add(currentUser);
							final SMessage message = new SMessage(currentUser, SMessage.REMOVE_USER, user.toString(),
									this.sessionCommunicationId, list);
							((UtilReferenceLocator) Chat.this.locator).sendMessage(message,
									this.userList.get(i).toString(), Chat.this.locator.getSessionId());
						} catch (final Exception e) {
							Chat.logger.error(null, e);
						}
					}
				}
				this.userList.remove(user);
				this.paintTitle();
			}
		}

		protected void paintTitle() {
			final List l = this.userList.toStringArray();
			final StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < l.size(); i++) {
				buffer.append(l.get(i));
				buffer.append(" ");
			}
			this.setTitle(buffer.toString());
		}

		protected void checkUser(final List list) {
			if (list != null) {
				for (int i = 0; i < this.userList.size(); i++) {
					final User currentUser = (User) this.userList.get(i);
					if (!list.contains(currentUser.toString())) {
						this.removeUser(currentUser, false);
					} else {
						list.remove(currentUser.toString());
					}
				}

				for (int i = 0; i < list.size(); i++) {
					final User currentUser = Chat.this.getUser((String) list.get(i));
					if (currentUser != null) {
						this.addUser(currentUser);
					}
				}
			}
		}

		protected void receiveMessage(final Message msg, final boolean checkUser) {
			this.initCommunication = true;

			final Message message = msg;
			final boolean check = checkUser;
			final Runnable r = new Runnable() {

				@Override
				public void run() {
					if (check) {
						UserChatWindow.this.checkUser(message.getUsers());
					}
					final StyledDocument doc = (StyledDocument) UserChatWindow.this.receiveText.getDocument();
					try {
						UserChatWindow.this.date.setTime(System.currentTimeMillis());
						final StringBuilder bufferHead = new StringBuilder();
						final StringBuilder bufferBody = new StringBuilder();

						bufferHead.append("[");
						bufferHead.append(UserChatWindow.this.formatDate.format(UserChatWindow.this.date));
						bufferHead.append("] ");
						final String userFrom = message.getUserFrom();
						bufferHead.append(userFrom);
						bufferHead.append(":\n");
						doc.insertString(doc.getLength(), bufferHead.toString(), UserChatWindow.this.atName);

						bufferBody.append(message.getMessage());

						final Color c = UserChatWindow.this.getColorUser(userFrom);
						if (c != null) {
							((SimpleAttributeSet) UserChatWindow.this.atMessage).addAttribute(StyleConstants.Foreground,
									c);
							doc.insertString(doc.getLength(), bufferBody.toString(), UserChatWindow.this.atMessage);
						} else {
							doc.insertString(doc.getLength(), bufferBody.toString(), null);
						}
						doc.insertString(doc.getLength(), "\n", null);

						UserChatWindow.this.sReceiver.getVerticalScrollBar()
						.setValue(UserChatWindow.this.sReceiver.getVerticalScrollBar().getMaximum());
						UserChatWindow.this.toFront();

					} catch (final BadLocationException e) {
						Chat.logger.error(null, e);
					}
				}
			};

			if (msg instanceof BMessage) {
				try {
					if (SwingUtilities.isEventDispatchThread()) {
						r.run();
					} else {
						SwingUtilities.invokeAndWait(r);
					}
				} catch (final Exception e) {
					Chat.logger.error(null, e);
				}
			} else if (msg instanceof SMessage) {
				// Status message, Register or remove an user in the window
				String currentUser = null;
				try {
					currentUser = ((ClientReferenceLocator) Chat.this.locator).getUser();
				} catch (final Exception ex) {
					Chat.logger.error(null, ex);
				}
				final SMessage smsg = (SMessage) msg;
				if (SMessage.REMOVE_USER == smsg.getType()) {
					final String user = smsg.getUser();
					if (user.equals(currentUser) && UserChatWindow.this.isVisible()) {
						final String sMessage = ApplicationManager.getTranslation("Chat.someone_put_you_out_conversation",
								ApplicationManager.getApplicationBundle(),
								new Object[] { smsg.getUserFrom() });
						JOptionPane.showMessageDialog(UserChatWindow.this, sMessage);
						Chat.this.removeWindow(UserChatWindow.this);
					}
					final User actual = Chat.this.getUser(user);
					if (actual != null) {
						this.removeUser(actual, false);
					}
				} else if (SMessage.SIGN_DOWN == smsg.getType()) {
					final String userFrom = smsg.getUserFrom();
					final String sMessage = ApplicationManager.getTranslation("Chat.someone_left_conversation",
							ApplicationManager.getApplicationBundle(), new Object[] { userFrom });
					JOptionPane.showMessageDialog(UserChatWindow.this, sMessage);
					final Long l = new Long(smsg.getCommunicationId());
					if (Chat.this.windowCache.containsKey(l)) {
						final UserChatWindow actual = (UserChatWindow) Chat.this.windowCache.get(l);
						actual.removeUser(Chat.this.getUser(userFrom), false);
						Chat.this.connectionList.repaint();
					}
				}
			}
		}

		protected Map colorUser = new Hashtable();

		protected Color getColorUser(final String user) {
			if ((user == null) || (user.length() == 0)) {
				return null;
			}
			if (this.colorUser.containsKey(user)) {
				return (Color) this.colorUser.get(user);
			} else {
				int r = (int) (Math.random() * 255);
				int g = (int) (Math.random() * 255);
				int b = (int) (Math.random() * 255);
				if (r > 200) {
					r = 200;
				}
				if (g > 200) {
					g = 200;
				}
				if (b > 200) {
					b = 200;
				}
				final Color c = new Color(r, g, b);
				this.colorUser.put(user, c);
				return c;
			}
		}

		protected void sendMessage() {
			try {
				this.initCommunication = true;
				final String textToSend = this.sendText.getText();
				final String currentUser = ((ClientReferenceLocator) Chat.this.locator).getUser();
				for (int i = 0; i < this.userList.size(); i++) {
					try {
						final List list = this.userList.toStringArray();
						list.add(currentUser);
						final BMessage message = new BMessage(currentUser, textToSend, this.sessionCommunicationId, list);
						((UtilReferenceLocator) Chat.this.locator).sendMessage(message, this.userList.get(i).toString(),
								Chat.this.locator.getSessionId());
						final Document doc = this.sendText.getDocument();
						doc.remove(0, doc.getLength());
					} catch (final Exception e) {
						Chat.logger.error(null, e);
					}
				}
				this.receiveMessage(new BMessage(currentUser, textToSend), false);
			} catch (final Exception e1) {
				Chat.logger.error(null, e1);
			}
		}

		protected void init() {
			((SimpleAttributeSet) this.atName).addAttribute(StyleConstants.Bold, new Boolean(true));
			((SimpleAttributeSet) this.atName).addAttribute(StyleConstants.Italic, new Boolean(true));
			((SimpleAttributeSet) this.atName).addAttribute(StyleConstants.Size,
					new Integer(this.getFont().getSize() - 2));

			this.receiveText = new JTextPane();
			this.receiveText.setPreferredSize(new Dimension(200, 300));
			this.receiveText.setEditable(false);
			this.receiveText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

			this.sendText = new JTextPane();
			this.sendText.setPreferredSize(new Dimension(200, 75));
			this.sendText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.sendText.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(final KeyEvent e) {
					if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isControlDown()) {
						try {
							UserChatWindow.this.sendText.getDocument()
							.insertString(UserChatWindow.this.sendText.getDocument().getLength(), "\n", null);
						} catch (final BadLocationException ex) {
							Chat.logger.error(null, ex);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								UserChatWindow.this.sendMessage();
							}
						});
					}
				}

				@Override
				public void keyReleased(final KeyEvent e) {
				}

				@Override
				public void keyTyped(final KeyEvent e) {

				}

			});
			this.bSend = new JButton(ApplicationManager.getTranslation("Enviar", null));
			this.bSend.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					UserChatWindow.this.sendMessage();

				}
			});
			final JPanel bottomPanel = new JPanel(new GridBagLayout());
			bottomPanel.add(new JScrollPane(this.sendText),
					new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(1, 1, 1, 1), 0, 0));
			bottomPanel.add(this.bSend, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

			final JButton bAdd = new JButton(this.iconAdd);
			bAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final List lAddList = UserSelectionList.showAddList((Component) e.getSource(), Chat.this.locator,
							UserChatWindow.this.userList.toStringArray());
					if (lAddList == null) {
						return;
					}
					for (int i = 0; i < lAddList.size(); i++) {
						final String user = (String) lAddList.get(i);
						final User actual = Chat.this.getUser(user);
						if (actual != null) {
							UserChatWindow.this.addUser(actual);
						}

					}
				}
			});

			final JButton bDelete = new JButton(this.deleteIcon);
			bDelete.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					String currentUser = null;
					try {
						currentUser = ((ClientReferenceLocator) Chat.this.locator).getUser();
					} catch (final Exception ex) {
						Chat.logger.error(null, ex);
					}
					final List listDelete = UserSelectionList.showRemoveList((Component) e.getSource(), Chat.this.locator,
							UserChatWindow.this.userList);
					if (listDelete == null) {
						return;
					}
					boolean myself = false;
					for (int i = 0; i < listDelete.size(); i++) {
						final User user = (User) listDelete.get(i);
						if (user != null) {
							if (user.equals(currentUser)) {
								myself = true;
							} else {
								UserChatWindow.this.removeUser(user, true);
							}
						}
					}
					if (myself) {
						final User user = Chat.this.getUser(currentUser);
						if (user != null) {
							UserChatWindow.this.removeUser(user, true);
						}
					}
				}
			});

			final JButton bClose = new JButton(ApplicationManager.getTranslation(Chat.CLOSE_CONVERSATION,
					ApplicationManager.getApplication().getResourceBundle()), this.closeIcon);
			bClose.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					String currentUser = null;
					try {
						currentUser = ((ClientReferenceLocator) Chat.this.locator).getUser();
					} catch (final Exception ex) {
						Chat.logger.error(null, ex);
					}

					if (Chat.this.windowCache.containsValue(UserChatWindow.this)) {
						final Enumeration enu = Collections.enumeration(Chat.this.windowCache.keySet());
						while (enu.hasMoreElements()) {
							final Object k = enu.nextElement();
							final UserChatWindow actual = (UserChatWindow) Chat.this.windowCache.get(k);
							if (UserChatWindow.this.equals(actual)) {
								Chat.this.windowCache.remove(k);
								Chat.this.connectionList.removeConnection(new Long(actual.getSessionCommunicationId()));
								final ArrayList list = actual.getUser();

								for (int i = 0; i < list.size(); i++) {
									final String destino = list.get(i).toString();
									if (currentUser.equalsIgnoreCase(destino)) {
										continue;
									}
									final SMessage message = new SMessage(currentUser, SMessage.SIGN_DOWN, destino,
											UserChatWindow.this.sessionCommunicationId, null);
									try {
										((UtilReferenceLocator) Chat.this.locator).sendMessage(message, destino,
												Chat.this.locator.getSessionId());
									} catch (final Exception ex) {
										Chat.logger.error(null, ex);
									}
								}
								actual.dispose();
							}
						}
					}
				}
			});
			this.toolBar = new JToolBar();
			this.toolBar.setFloatable(false);
			this.toolBar.add(bAdd);
			this.toolBar.add(bDelete);

			final JPanel jbButtonsPanel = new JPanel(new GridBagLayout());
			jbButtonsPanel.add(this.toolBar, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
			jbButtonsPanel.add(bClose, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(jbButtonsPanel, BorderLayout.NORTH);
			this.sReceiver = new JScrollPane(this.receiveText);
			this.split.add(this.sReceiver);
			this.split.add(bottomPanel);
			this.getContentPane().add(this.split);
			this.pack();
			ApplicationManager.center(this);
			this.setVisible(true);
		}

	}

	protected static class UserSelectionList extends JList {

		protected static JDialog dUserList = null;

		protected static JList userList = null;

		protected static ArrayList returnList = null;

		public static List showAddList(final Component comp, final EntityReferenceLocator locator, final List connect) {
			UserSelectionList.returnList = null;
			if (UserSelectionList.dUserList == null) {
				UserSelectionList.showUserList(comp);
			}
			try {
				final List list = ((UtilReferenceLocator) locator).getConnectedUsers(locator.getSessionId());
				// Have to remove the connected users, the application user and
				// the repeat ones.

				for (int i = list.size() - 1; i >= 0; i--) {
					if (connect.contains(list.get(i))) {
						list.remove(i);
					}
				}

				while (list.contains(((ClientReferenceLocator) locator).getUser())) {
					list.remove(((ClientReferenceLocator) locator).getUser());
				}

				final ArrayList finalList = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					if (!finalList.contains(list.get(i))) {
						finalList.add(list.get(i));
					}
				}
				UserSelectionList.userList.setListData(finalList.toArray());
			} catch (final Exception e) {
				Chat.logger.error(null, e);
			}

			UserSelectionList.dUserList.pack();
			final Point p = new Point(comp.getHeight(), 0);
			SwingUtilities.convertPointToScreen(p, comp);
			UserSelectionList.dUserList.setLocation(p);
			UserSelectionList.dUserList.setVisible(true);
			return UserSelectionList.returnList;
		}

		public static List showRemoveList(final Component comp, final EntityReferenceLocator locator, final List init) {
			UserSelectionList.returnList = null;
			if (UserSelectionList.dUserList == null) {
				UserSelectionList.showUserList(comp);
			}
			UserSelectionList.userList.setListData(init.toArray());
			UserSelectionList.dUserList.pack();
			final Point p = new Point(comp.getHeight(), 0);
			SwingUtilities.convertPointToScreen(p, comp);
			UserSelectionList.dUserList.setLocation(p);
			UserSelectionList.dUserList.setVisible(true);
			return UserSelectionList.returnList;
		}

		protected static void showUserList(final Component comp) {
			final Window w = SwingUtilities.getWindowAncestor(comp);
			if (w instanceof Frame) {
				UserSelectionList.dUserList = new JDialog((Frame) w, true);
			} else if (w instanceof Dialog) {
				UserSelectionList.dUserList = new JDialog((Dialog) w, true);
			}
			UserSelectionList.dUserList.getContentPane().setLayout(new BorderLayout());
			UserSelectionList.userList = new JList();
			UserSelectionList.dUserList.getContentPane().add(UserSelectionList.userList);
			final JPanel panel = new JPanel(new GridBagLayout());
			final JButton bAccept = new JButton(ImageManager.getIcon(ImageManager.OK));
			bAccept.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Object[] selected = UserSelectionList.userList.getSelectedValues();
					UserSelectionList.returnList = new ArrayList();
					for (int i = 0; i < selected.length; i++) {
						UserSelectionList.returnList.add(selected[i]);
					}
					UserSelectionList.dUserList.setVisible(false);
				}
			});

			final JButton bCancel = new JButton(ImageManager.getIcon(ImageManager.CANCEL));
			bCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					UserSelectionList.dUserList.setVisible(false);
				}
			});
			panel.add(bAccept, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
			panel.add(bCancel, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
			UserSelectionList.dUserList.getContentPane().add(panel, BorderLayout.SOUTH);
		}

	}

	protected static class UserChangeEvent extends EventObject {

		public UserChangeEvent(final Object source) {
			super(source);
		}

	}

	protected static interface UserChangeListener extends EventListener {

		public void userChange(UserChangeEvent event);

	}

	protected static class User implements Comparable {

		private String user = null;

		private boolean connect = false;

		private final ArrayList changeListener = new ArrayList();

		protected User(final String user) {
			this.user = user;
		}

		protected User(final String user, final boolean connect) {
			this.user = user;
			this.connect = connect;
		}

		public void connect() {
			this.connect = true;
		}

		public void disconnect() {
			this.connect = false;
		}

		public boolean isConnected() {
			return this.connect;
		}

		@Override
		public String toString() {
			return this.user;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null) {
				return false;
			}
			if (o instanceof User) {
				return this.user.equals(((User) o).toString());
			} else if (o instanceof String) {
				return this.user.equals(o);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public int compareTo(final Object o) {
			if (o == null) {
				return -1;
			}
			if (o instanceof User) {
				return this.user.compareTo(((User) o).toString());
			}
			return -1;
		}

		public void addUserChangeListener(final UserChangeListener listener) {
			if (this.changeListener.contains(listener)) {
				return;
			}
			this.changeListener.add(listener);
		}

		public void removeUserChangeListener(final UserChangeListener listener) {
			if (!this.changeListener.contains(listener)) {
				return;
			}
			this.changeListener.remove(listener);
		}

		protected void fireUserChangeListener() {
			final UserChangeEvent event = new UserChangeEvent(this);
			for (int i = this.changeListener.size() - 1; i >= 0; i--) {
				((UserChangeListener) this.changeListener.get(i)).userChange(event);
			}
		}

	}

	protected void init() {
		this.userList = new JUserList(this.getUserList());
		this.updateConnectUser();
		this.userList.addMouseListener(new MouseHandler());
		this.userList.setCellRenderer(new UserListCellRenderer());

		final JScrollPane sPane = new JScrollPane(this.userList);
		this.headerUser = new JLabel(
				ApplicationManager.getTranslation(Chat.USER, ApplicationManager.getApplication().getResourceBundle()));
		this.headerUser.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		sPane.setColumnHeaderView(this.headerUser);

		this.connectionList = new JOpenConnection();
		this.connectionList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final int index = Chat.this.connectionList.locationToIndex(e.getPoint());
					if (index >= 0) {
						final Object o = Chat.this.connectionList.getModel().getElementAt(index);
						if (Chat.this.windowCache.containsKey(o)) {
							final UserChatWindow w = (UserChatWindow) Chat.this.windowCache.get(o);
							w.setVisible(true);
						}
					}
				}
			}
		});
		this.connectionList.setVisibleRowCount(3);
		this.headerConnection = new JLabel(ApplicationManager.getTranslation(Chat.CONVERSATION,
				ApplicationManager.getApplication().getResourceBundle()));
		this.headerConnection.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		final JScrollPane connectionPanel = new JScrollPane(this.connectionList);
		connectionPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		connectionPanel.setColumnHeaderView(this.headerConnection);

		this.setLayout(new GridBagLayout());
		this.add(sPane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(new JSeparator(), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(connectionPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	protected void initThreads() {
		if (this.messagesCheckTime < 0) {
			return;
		}
		this.control = new ChatControlThread();
		this.control.start();
	}

	protected void receiveMessage(final Message message) {
		final long id = message.getCommunicationId();
		if (id >= 0) {
			final UserChatWindow w = this.openWindow(id, message);
			w.receiveMessage(message, true);
			if (!w.isVisible()) {
				w.setVisible(true);
			}
		}
	}

	protected UserChatWindow openWindow(final User user) {
		final Enumeration enu = Collections.enumeration(this.windowCache.keySet());
		while (enu.hasMoreElements()) {
			final Long lSessionId = (Long) enu.nextElement();
			final UserChatWindow w = (UserChatWindow) this.windowCache.get(lSessionId);
			final ArrayList list = w.getUser();
			if (list.size() > 1) {
				continue;
			}
			if ((list.size() == 1) && list.contains(user)) {
				return w;
			}
		}
		final UserChatWindow w = new UserChatWindow(user);
		final Long l = new Long(w.getSessionCommunicationId());
		this.windowCache.put(l, w);
		this.connectionList.addConnection(l);
		return w;
	}

	protected UserChatWindow openWindow(final long id, final Message message) {
		final Long d = new Long(id);
		UserChatWindow w = this.getCacheWindow(d);
		if (w == null) {
			w = new UserChatWindow(id);
			final List users = message.getUsers();
			if (users != null) {
				for (int i = 0; i < users.size(); i++) {
					final String user = (String) users.get(i);
					final User uUser = this.userList.getUser(user);
					if (uUser != null) {
						w.addUser(uUser);
					}
				}
			}
			final Long l = new Long(id);
			this.windowCache.put(l, w);
			this.connectionList.addConnection(l);
		}
		return w;
	}

	protected UserChatWindow getCacheWindow(final Long d) {
		UserChatWindow w = null;
		if (this.windowCache.containsKey(d)) {
			w = (UserChatWindow) this.windowCache.get(d);
			w.toFront();
		}
		return w;
	}

	protected void removeWindow(final UserChatWindow w) {
		final long l = w.getSessionCommunicationId();
		final Long keyL = new Long(l);
		if (this.windowCache.containsKey(keyL)) {
			this.windowCache.remove(keyL);
		}
		w.setVisible(false);
	}

	protected boolean isVisibleWindow() {
		if (this.isVisible()) {
			return true;
		}
		final Enumeration enu = Collections.enumeration(this.windowCache.keySet());
		while (enu.hasMoreElements()) {
			final UserChatWindow w = (UserChatWindow) this.windowCache.get(enu.nextElement());
			if (w.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public void setVisibleUserWindow() {
		final Enumeration enu = Collections.enumeration(this.windowCache.keySet());
		while (enu.hasMoreElements()) {
			final UserChatWindow w = (UserChatWindow) this.windowCache.get(enu.nextElement());
			w.setVisible(true);
		}
	}

	protected class ChatControlThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (final InterruptedException e) {
				Chat.logger.error(null, e);
			}
			while (true) {
				try {
					if (Chat.this.isVisibleWindow()) {
						Thread.sleep(Chat.this.chatCheckTime);
					} else {
						Thread.sleep(Chat.this.messagesCheckTime);
					}
					Chat.this.updateConnectUser();
					final List v = ((UtilReferenceLocator) Chat.this.locator).getMessages(Chat.this.locator.getSessionId(),
							Chat.this.locator.getSessionId());
					if (v != null) {
						for (int i = 0; i < v.size(); i++) {
							final Object oMessage = v.get(i);
							if (oMessage != null) {
								SwingUtilities.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										if (oMessage instanceof UtilReferenceLocator.Message) {
											Chat.this.receiveMessage((UtilReferenceLocator.Message) oMessage);
										}

									}
								});
							}
						}
					}
				} catch (final Exception e) {
					Chat.logger.error(null, e);
				}
			}
		}

	}

	private static JFrame fChat = null;

	private static Chat sChat = null;

	public static void showChat(final EntityReferenceLocator locator, final List messages, final int messagesCheckTime,
			final int chatCheckTime) {
		try {
			if (Chat.fChat == null) {
				Chat.fChat = new JFrame("Chat: " + ((ClientReferenceLocator) locator).getUser());
				final Image icon = ApplicationManager.getApplication().getFrame().getIconImage();
				if (icon != null) {
					Chat.fChat.setIconImage(icon);
				}
				Chat.fChat.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				Chat.fChat.getContentPane().setLayout(new BorderLayout());
				Chat.sChat = new Chat(locator, messagesCheckTime, chatCheckTime);
				Chat.fChat.getContentPane().add(Chat.sChat);
				Chat.fChat.addComponentListener(new ComponentAdapter() {

					@Override
					public void componentShown(final ComponentEvent e) {
						Chat.sChat.setVisibleUserWindow();
					}
				});

				Chat.fChat.pack();
				final int w = 250;
				if (Chat.fChat.getWidth() < w) {
					Chat.fChat.setSize(w, Chat.fChat.getHeight());
				}
				ApplicationManager.center(Chat.fChat);
			}
			Chat.fChat.setVisible(true);
			Chat.fChat.toFront();
			if (messages != null) {
				for (int i = 0; i < messages.size(); i++) {
					Chat.sChat.receiveMessage((Message) messages.get(i));
				}
			}
		} catch (final Exception ex) {
			Chat.logger.error(null, ex);
		}
	}

}
