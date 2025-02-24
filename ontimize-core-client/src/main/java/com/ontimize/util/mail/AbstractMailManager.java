package com.ontimize.util.mail;

import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that contains useful methods to get mail parameters like server mail, message,
 * authentication type...
 *
 * @see {@link http://java.sun.com/products/javamail/}
 * @author Imatia Innovation
 */
public abstract class AbstractMailManager {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMailManager.class);

	public static boolean DEBUG = false;

	protected static class SilentAuthenticator extends Authenticator {

		private PasswordAuthentication pwA = null;

		private String us = null;

		private String passw = null;

		public SilentAuthenticator(final String us, final String passw) {
			this.us = us;
			this.passw = passw;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			if (this.pwA == null) {
				this.pwA = new PasswordAuthentication(this.us, this.passw);
			}
			AbstractMailManager.logger.debug("SilentAuthenticator: Authentication requested");
			return this.pwA;
		}

	}

	protected static class PasswordDocument extends PlainDocument {

		StringBuilder contentBuffer = new StringBuilder();

		public String getContents() {
			return this.contentBuffer.toString();
		}

		@Override
		public void insertString(final int offset, final String s, final AttributeSet at) throws BadLocationException {
			final StringBuilder sAux = new StringBuilder(s.length());
			for (int i = 0; i < s.length(); i++) {
				sAux.append("*");
			}
			super.insertString(offset, sAux.toString(), at);
			this.contentBuffer.insert(offset, s);
		}

		@Override
		public void remove(final int offset, final int lenght) throws BadLocationException {
			super.remove(offset, lenght);
			this.contentBuffer.delete(offset, offset + lenght);
		}

	};

	protected static class PopupAuthenticator extends Authenticator {

		protected JDialog popup = null;

		protected JTextField userTF = new JTextField(8);

		protected JTextField passwordTF = new JTextField(8);

		protected JLabel passLabel = new JLabel("Password:        ");

		protected JLabel userLabel = new JLabel("User name: ");

		protected JButton oKButton = new JButton("application.accept");

		protected JButton cancelButton = new JButton("application.cancel");

		protected String user = null;

		protected String password = null;

		protected PasswordAuthentication pwA = null;

		public PopupAuthenticator(final JFrame parent) {

			this.passwordTF.setDocument(new PasswordDocument());
			this.createPopup(parent);
		}

		protected void createPopup(final JFrame parent) {
			if (GraphicsEnvironment.isHeadless()) {
				AbstractMailManager.logger.debug(this.getClass().getName()
						+ ": Error creating popup authenticator -> Graphics environment not supported.");
				return;
			}
			this.popup = new JDialog(parent, "Start session", true);
			this.popup.getContentPane().setLayout(new GridBagLayout());
			this.popup.getContentPane()
			.add(this.userLabel,
					new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(10, 10, 5, 5), 0, 0));
			this.popup.getContentPane()
			.add(this.passLabel,
					new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
							new Insets(10, 10, 5, 5), 0, 0));
			this.popup.getContentPane()
			.add(this.userTF,
					new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(10, 5, 5, 10), 0, 0));
			this.popup.getContentPane()
			.add(this.passwordTF,
					new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(10, 5, 5, 10), 0, 0));
			final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonsPanel.add(this.oKButton);
			buttonsPanel.add(this.cancelButton);
			this.popup.getContentPane()
			.add(buttonsPanel,
					new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(15, 5, 10, 10), 0, 0));
			this.oKButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					PopupAuthenticator.this.user = PopupAuthenticator.this.userTF.getText();
					PopupAuthenticator.this.password = ((PasswordDocument) PopupAuthenticator.this.passwordTF
							.getDocument()).getContents();
					PopupAuthenticator.this.popup.setVisible(false);
				}
			});
			this.cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					PopupAuthenticator.this.user = null;
					PopupAuthenticator.this.password = null;
					PopupAuthenticator.this.popup.setVisible(false);
				}
			});

		}

		public void showPopup() {
			if (GraphicsEnvironment.isHeadless()) {
				AbstractMailManager.logger.debug(this.getClass().getName()
						+ ": Error showing popup authenticator -> Graphics environment not supported.");
				return;
			}
			this.popup.pack();
			this.popup.setVisible(true);
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			if (this.pwA != null) {
				return this.pwA;
			}
			this.showPopup();
			if (AbstractMailManager.DEBUG) {
				AbstractMailManager.logger.debug(this.user + " : " + this.password);
			}
			this.pwA = new PasswordAuthentication(this.user, this.password);
			return this.pwA;
		}

	}

	private String mailServer = null;

	private String userName = null;

	private Authenticator authenticator = null;

	private boolean useAuth = false;

	protected int timeoutSMTP = -1;

	// Useful for gmail auth (5.2070EN-0.1)
	protected int port = -1;

	// Useful for gmail auth (5.2070EN-0.1)
	protected String starttls;

	// Useful for gmail auth (5.2070EN-0.1)
	protected String socketFactoryFallback;

	// Useful for gmail auth (5.2070EN-0.1)
	protected String socketFactoryClass;

	public String getSocketFactoryFallback() {
		return this.socketFactoryFallback;
	}

	public void setSocketFactoryFallback(final String socketFactoryFallback) {
		this.socketFactoryFallback = socketFactoryFallback;
	}

	public String getSocketFactoryClass() {
		return this.socketFactoryClass;
	}

	public void setSocketFactoryClass(final String socketFactoryClass) {
		this.socketFactoryClass = socketFactoryClass;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public String getStarttls() {
		return this.starttls;
	}

	public void setStarttls(final String starttls) {
		this.starttls = starttls;
	}

	public void setAuthenticator(final Authenticator a) {
		this.authenticator = a;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public void setMailServer(final String mailServer) {
		this.mailServer = mailServer;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getMailServer() {
		return this.mailServer;
	}

	public Authenticator getAuthenticator() {
		return this.authenticator;
	}

	public Message createMessage(final Session s) {
		return new MimeMessage(s);
	}

	public Session getSession(final String mailServer) {
		final Properties prop = new Properties();
		prop.put("mail.host", mailServer);
		if (this.timeoutSMTP != -1) {
			prop.put("mail.smtp.timeout", Integer.toString(this.timeoutSMTP));
		}

		final Session s = Session.getInstance(prop, this.authenticator);
		s.setDebug(AbstractMailManager.DEBUG);

		return s;
	}

	public Session getSession(final String mailServer, final String userName) {
		final Properties prop = new Properties();
		prop.put("mail.host", mailServer);

		if (userName != null) {
			prop.put("user.name", userName);
		}
		if (this.timeoutSMTP != -1) {
			prop.put("mail.smtp.timeout", Integer.toString(this.timeoutSMTP));
		}

		final Session s = Session.getInstance(prop, this.authenticator);
		s.setDebug(AbstractMailManager.DEBUG);
		return s;
	}

	public Session getSessionWithSMTPAuth(final String mailServer, final String userName) {
		final Properties prop = new Properties();
		prop.put("mail.host", mailServer);
		prop.put("mail.smtp.auth", "true");
		if (this.timeoutSMTP != -1) {
			prop.put("mail.smtp.timeout", Integer.toString(this.timeoutSMTP));
		}
		if (userName != null) {
			// Not used because ontimize manages auth with object
			// this.authenticator
			prop.put("user.name", userName);
		}

		// Since 5.2070EN-0.1 (used in gmail accounts)
		if (this.getStarttls() != null) {
			prop.setProperty("mail.smtp.starttls.enable", this.getStarttls());
		}

		// Since 5.2070EN-0.1 (used in gmail accounts)
		if (this.getPort() != -1) {
			prop.setProperty("mail.smtp.port", Integer.toString(this.getPort()));
		}

		// Since 5.2070EN-0.1 (used in gmail accounts)
		if (this.getSocketFactoryClass() != null) {
			prop.put("mail.smtp.socketFactory.class", this.getSocketFactoryClass());
		}

		// Since 5.2070EN-0.1 (used in gmail accounts)
		if (this.getSocketFactoryFallback() != null) {
			prop.put("mail.smtp.socketFactory.fallback", this.getSocketFactoryFallback());
		}

		final Session s = Session.getInstance(prop, this.authenticator);
		s.setDebug(AbstractMailManager.DEBUG);
		return s;
	}

	public Session getNewSession(final String mailServer, final String userName) {
		final Properties prop = new Properties();
		prop.put("mail.host", mailServer);

		prop.put("user.name", userName);
		if (this.timeoutSMTP != -1) {
			prop.put("mail.smtp.timeout", Integer.toString(this.timeoutSMTP));
		}

		final Session s = Session.getInstance(prop, this.authenticator);
		s.setDebug(AbstractMailManager.DEBUG);
		return s;
	}

	public Address getAddress(final String mailAddress, final String name) throws java.io.UnsupportedEncodingException {
		return new InternetAddress(mailAddress, name);
	}

	public Address getAddress(final String mailAddress) throws AddressException {
		return new InternetAddress(mailAddress);
	}

	/**
	 * Creates a <code>Message</code> class instance using the entry parameters.
	 * @param session Session
	 * @param from Message source
	 * @param to Message destiny
	 * @param subject <code>String</code> with the message subject.
	 * @param text <code>String</code> with the message contained.
	 * @return a {@link Message}
	 * @throws MessagingException
	 */
	public Message createMessage(final Session session, final String from, final String to, final String subject, final String text)
			throws MessagingException {
		return this.createMessage(session, from, new String[] { to }, subject, text);
	}

	/**
	 * Creates a <code>Message</code> class instance using the entry parameters. This
	 * <code>Message</code> could have multiple destinies.
	 * @param session Session
	 * @param from Message source
	 * @param to Message destinies
	 * @param subject <code>String</code> with the message subject.
	 * @param text <code>String</code> with the message contained.
	 * @return
	 * @throws MessagingException
	 */
	public Message createMessage(final Session session, final String from, final String to[], final String subject, final String text)
			throws MessagingException {
		final Message message = this.createMessage(session);
		message.setFrom(this.getAddress(from));
		for (int i = 0; i < to.length; i++) {
			message.addRecipient(Message.RecipientType.TO, this.getAddress(to[i]));
		}
		try {
			final String stringValue = MimeUtility.encodeText(subject);
			message.setSubject(stringValue);
		} catch (final Exception e) {
			AbstractMailManager.logger.trace(null, e);
			message.setSubject(subject);
		}

		if (text != null) {
			if (text.trim().toLowerCase().startsWith("<html")) {
				message.setContent(text, "text/html");
			} else {
				message.setText(text);
			}
		} else {
			message.setText("");
		}
		return message;
	}

	/**
	 * Creates a <code>Message</code> class instance using the entry parameters.
	 * @param auth boolean to indicates when the authentication is required
	 * @param from Message source
	 * @param to Message destiny
	 * @param subject <code>String</code> with the message subject.
	 * @param text <code>String</code> with the message contained.
	 * @return
	 * @throws MessagingException
	 */
	public Message createMessage(final boolean auth, final String from, final String to, final String subject, final String text)
			throws MessagingException {
		Session s = null;
		if (!auth) {
			s = this.getSession(this.mailServer, this.userName);
		} else {
			s = this.getSessionWithSMTPAuth(this.mailServer, this.userName);
		}
		return this.createMessage(s, from, to, subject, text);
	}

	/**
	 * Creates a <code>Message</code> class instance using the entry parameters. This
	 * <code>Message</code> could have multiple destinies.
	 * @param auth true is the authentication is required.
	 * @param from Message source
	 * @param to Message destinies
	 * @param subject <code>String</code> with the message subject.
	 * @param text <code>String</code> with the message contained.
	 * @return
	 * @throws MessagingException
	 */
	public Message createMessage(final boolean auth, final String from, final String to[], final String subject, final String text)
			throws MessagingException {
		Session s = null;
		if (!auth) {
			s = this.getSession(this.mailServer, this.userName);
		} else {
			s = this.getSessionWithSMTPAuth(this.mailServer, this.userName);
		}
		return this.createMessage(s, from, to, subject, text);
	}

	public Message createMultipartMessage(final Session s, final String from, final String to, final String subject, final String text)
			throws MessagingException {
		final Message message = this.createMessage(s);
		message.setFrom(this.getAddress(from));
		message.addRecipient(Message.RecipientType.TO, this.getAddress(to));
		message.setSubject(subject);
		final Multipart multipart = new MimeMultipart();
		final BodyPart bodyText = new MimeBodyPart();
		bodyText.setContent(text, "text/html");
		multipart.addBodyPart(bodyText);
		message.setContent(multipart);
		return message;
	}

	/**
	 * Creates a <code>Message</code> class instance using the entry parameters. This
	 * <code>Message</code> could have attachment files.
	 * @param session Session
	 * @param from Message source
	 * @param to Message destinies
	 * @param subject <code>String</code> with the message subject.
	 * @param text <code>String</code> with the message contained.
	 * @param attachment File paths to be attached.
	 * @return
	 * @throws MessagingException
	 */

	public Message createMessage(final Session session, final String from, final String to[], String subject, final String text,
			final String attachment[]) throws MessagingException {
		// Define message
		final Message message = this.createMessage(session);

		message.setFrom(this.getAddress(from));
		for (int i = 0; i < to.length; i++) {
			message.addRecipient(Message.RecipientType.TO, this.getAddress(to[i]));
		}

		try {
			if (subject == null) {
				subject = "";
			}
			final String cadena = MimeUtility.encodeText(subject);
			message.setSubject(cadena);
		} catch (final Exception e) {
			AbstractMailManager.logger.trace(null, e);
			message.setSubject(subject);
		}

		// Create the message part
		BodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		if (text != null) {
			if (text.trim().toLowerCase().startsWith("<html")) {
				messageBodyPart.setContent(text, "text/html");
			} else {
				messageBodyPart.setText(text);
			}
		} else {
			messageBodyPart.setText("");
		}

		final Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		if ((attachment != null) && (attachment.length > 0)) {
			for (int i = 0; i < attachment.length; i++) {
				messageBodyPart = new MimeBodyPart();
				final DataSource source = new FileDataSource(attachment[i]);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(messageBodyPart.getDataHandler().getName());// filename[i]);
				multipart.addBodyPart(messageBodyPart);
			}
		}

		// Put parts in message
		message.setContent(multipart);
		return message;
	}

	public Message createHTMLMessage(final Session s, final String from, final String to, final String subject, final String text)
			throws MessagingException {
		final Message message = this.createMessage(s);
		message.setFrom(this.getAddress(from));
		message.addRecipient(Message.RecipientType.TO, this.getAddress(to));
		try {
			final String textValue = MimeUtility.encodeText(subject);
			message.setSubject(textValue);
		} catch (final Exception e) {
			AbstractMailManager.logger.trace(null, e);
			message.setSubject(subject);
		}
		message.setContent(text, "text/html");
		return message;
	}

	public void send(final String from, final String to, final String subject, final String text) throws MessagingException {
		this.send(from, to, subject, text, this.useAuth);
	}

	public void send(final String from, final String to, final String subject, final String text, final boolean auth) throws MessagingException {
		Session s = null;
		if (!auth) {
			s = this.getSession(this.mailServer, this.userName);
		} else {
			s = this.getSessionWithSMTPAuth(this.mailServer, this.userName);
		}

		final Message message = this.createMessage(s, from, to, subject, text);
		message.setSentDate(new Date(System.currentTimeMillis()));
		this.send(message);
	}

	public void send(final String from, final String to[], final String subject, final String text, final String filename[], final boolean auth)
			throws Exception {
		Session s = null;
		if (!auth) {
			s = this.getSession(this.mailServer, this.userName);
		} else {
			s = this.getSessionWithSMTPAuth(this.mailServer, this.userName);
		}

		final Message message = this.createMessage(s, from, to, subject, text, filename);
		this.send(message);
	}

	public void send(final Message message) throws MessagingException {
		Transport.send(message);
	}

	public void send(final Message messages[], final boolean auth) throws MessagingException {
		Session s = null;
		if (!auth) {
			s = this.getSession(this.mailServer, this.userName);
		} else {
			s = this.getSessionWithSMTPAuth(this.mailServer, this.userName);
		}

		final Transport transport = s.getTransport("smtp");
		transport.connect();
		if (transport.isConnected()) {
			try {
				for (int i = 0; i < messages.length; i++) {
					transport.sendMessage(messages[i], messages[i].getRecipients(RecipientType.TO));
				}
			} finally {
				transport.close();
			}
		}
	}

	public void sendHTMLMessage(final String from, final String to, final String subject, final String html) throws MessagingException {
		final Session s = this.getSession(this.mailServer, this.userName);
		final Message message = this.createHTMLMessage(s, from, to, subject, html);
		message.setSentDate(new Date(System.currentTimeMillis()));
		this.send(message);
	}

	public Store getStore() throws NoSuchProviderException, MessagingException {
		return this.getStore("pop3");
	}

	public Store getStore(final String provider) throws NoSuchProviderException, MessagingException {
		final Session s = this.getSession(this.mailServer);
		final Store store = s.getStore(provider);
		if (this.authenticator != null) {
			final PasswordAuthentication pwA = ((PopupAuthenticator) this.authenticator).getPasswordAuthentication();
			store.connect(this.mailServer, pwA.getUserName(), pwA.getPassword());
		} else {
			this.authenticator = new PopupAuthenticator(null);
			final PasswordAuthentication pwA = ((PopupAuthenticator) this.authenticator).getPasswordAuthentication();
			store.connect(this.mailServer, pwA.getUserName(), pwA.getPassword());
		}
		return store;
	}

	public void setUseAuth(final boolean useAuth) {
		this.useAuth = useAuth;
	}

	public void setSMTPTimeout(final int timeout) {
		this.timeoutSMTP = timeout;
	}

	protected boolean useOnlyUSASCII = false;

	public void setUseOnlyUSASCII(final boolean b) {
		this.useOnlyUSASCII = b;
	}

	public synchronized Message[] checkMail(final String password, final boolean deleteMessages) {
		Session session = null;
		session = this.getSession(this.getMailServer());

		Folder f = null;
		Store s = null;

		try {
			s = session.getStore("pop3");
			s.connect(this.getMailServer(), this.getUserName(), password);
			f = s.getDefaultFolder();
			if (f == null) {
				throw new Exception("No found folder");
			}
			f = f.getFolder("INBOX");

			f.open(Folder.READ_WRITE);
			if (f.getMessageCount() > 0) {
				final Message message[] = f.getMessages();
				final Message[] res = new Message[message.length];
				for (int i = 0; i < message.length; i++) {
					if (deleteMessages) {
						message[i].setFlag(Flags.Flag.DELETED, true);
					}
					res[i] = new MimeMessage((MimeMessage) message[i]);

				}
				return res;
			}

		} catch (final Exception e) {
			AbstractMailManager.logger.error(null, e);
		} finally {
			try {
				if (f != null) {
					f.close(true);
				}
				if (s != null) {
					s.close();
				}
			} catch (final Exception e) {
				AbstractMailManager.logger.error(null, e);
			}
		}
		return null;
	}

	public synchronized int checkMailFolder(final String password, final String folderName) {
		Session session = null;
		session = this.getSession(this.getMailServer());

		Folder f = null;
		Store s = null;

		try {
			s = session.getStore("pop3");
			s.connect(this.getMailServer(), this.getUserName(), password);
			f = s.getDefaultFolder();
			if (f == null) {
				throw new Exception("No default folder");
			}
			f = f.getFolder(folderName);
			if (f == null) {
				throw new Exception("No found folder " + folderName);
			}
			f.open(Folder.READ_ONLY);
			return f.getNewMessageCount();
		} catch (final Exception e) {
			AbstractMailManager.logger.error(null, e);
		} finally {
			try {
				if (f != null) {
					f.close(false);
				}
				if (s != null) {
					s.close();
				}
			} catch (final Exception e) {
				AbstractMailManager.logger.error(null, e);
			}
		}
		return 0;
	}

	/**
	 * Analyzes the information in a fragment of the message and put it in the parameter info
	 * @param part
	 * @param info
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static void handlePart(final Part part, final Map info) throws MessagingException, IOException {
		final String disposition = part.getDisposition();
		final String contentType = part.getContentType();

		if (disposition == null) { // When just body

			AbstractMailManager.logger.debug("Disposition: Null, contentType: " + contentType);
			info.put("Content", part.getContent());
		} else if (disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
			if (AbstractMailManager.DEBUG) {
				AbstractMailManager.logger.debug("Attachment: " + part.getFileName());
			}

			if (info.get("Attachment") == null) {
				final Map attachment = new Hashtable();
				info.put("Attachment", attachment);
			}
			final Map attachment = (Map) info.get("Attachment");
			attachment.put(part.getFileName(), part.getInputStream());

		} else if (disposition.equalsIgnoreCase(Part.INLINE)) {

			if (info.get("Inline") == null) {
				final Map inline = new Hashtable();
				info.put("Inline", inline);
			}
			final Map inline = (Map) info.get("Inline");
			if ((part.getFileName() != null) && (part.getInputStream() != null)) {
				inline.put(part.getFileName(), part.getInputStream());
			} else if (part.getInputStream() != null) {
				inline.put("unknown", part.getInputStream());
			}
		} else { // Should never happen
			AbstractMailManager.logger.debug("Other: " + disposition);
		}
	}

	/**
	 * Get a Map with the information of each part of the message. This part are: source, subject,
	 * content, attachment and inlines.
	 * @param message Message to get the information from.
	 * @return Part of the message, with the following keys: Source, Subject, Content, Attachment,
	 *         Inline
	 * @throws Exception
	 */
	public Map getMessageInfo(final Message message) throws Exception {
		final Map res = new Hashtable();
		res.put("Source", message.getFrom()[0]);
		res.put("Subject", message.getSubject());

		final Object content = message.getContent();
		if (content instanceof Multipart) {
			final Multipart mP = (Multipart) content;
			for (int i = 0, n = mP.getCount(); i < n; i++) {
				AbstractMailManager.handlePart(mP.getBodyPart(i), res);
			}
		} else {
			AbstractMailManager.handlePart(message, res);
		}

		return res;
	}

}
