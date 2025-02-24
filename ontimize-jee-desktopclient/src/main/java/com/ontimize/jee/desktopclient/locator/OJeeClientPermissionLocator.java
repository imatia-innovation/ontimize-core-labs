package com.ontimize.jee.desktopclient.locator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationHandler;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.field.ReferenceComboDataField;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.InvalidCredentialsException;
import com.ontimize.jee.common.gui.ClientWatch;
import com.ontimize.jee.common.gui.ConnectionOptimizer;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferenceReferencer;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.ErrorAccessControl;
import com.ontimize.jee.common.locator.InitialContext;
import com.ontimize.jee.common.locator.SecureEntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.ClientPermissionManager;
import com.ontimize.jee.common.security.ILoginProvider;
import com.ontimize.jee.common.services.formprovider.IFormProviderService;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.util.operation.RemoteOperationManager;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.jee.common.xml.XMLClientProvider;
import com.ontimize.jee.desktopclient.hessian.HessianSessionLocatorInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.ClientPermissionInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.ClientReferenceLocatorDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.ConnectionOptimizerInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.RemoteApplicationPreferenceReferencerDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.UtilInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.handlers.XMLClientProviderInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.security.OntimizeLoginProvider;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 * Clase abstracta que define el comportamiento que deben tener los ClientPermissionLocator contra
 * un servidor JEE.
 *
 * @author joaquin.romero
 */
public class OJeeClientPermissionLocator implements SecureEntityReferenceLocator, ConnectionOptimizer,
XMLClientProvider, UtilReferenceLocator, ClientPermissionManager, RemoteApplicationPreferenceReferencer,
ClientReferenceLocator {

	private static final Logger logger = LoggerFactory.getLogger(OJeeClientPermissionLocator.class);

	private static final String REMOTE_LOCATOR_INVOCATION_HANDLER = "remoteLocatorInvocationHandler";

	public static final String CHECK_SERVER_MESSAGES_PERIOD = "CheckServerMessagePeriod";

	public static final String REMOTE_REFERENCE_LOCATOR_NAME = "RemoteLocatorName";

	public static final String CLIENT_PERMISSION_COLUMN = "ClientPermissionColumn";

	private static String REMOTE_LOCATOR_NAME_PROPERTY = "com.ontimize.locator.ReferenceLocator.RemoteLocatorName";

	protected String referenceLocatorServerName = "";

	protected String clientPermissionsColumn = "ClientPermissions";

	private boolean localLocator;

	protected int messagesCheckTime = -1;

	protected int chatCheckTime = 2000;

	protected List<ISessionListener> sessionListeners;

	protected UserInformation userInformation;

	private final boolean startSession = false;

	protected int userId = -1;

	private final HessianSessionLocatorInvocationDelegate sessionLocatorInvocationDelegate = new HessianSessionLocatorInvocationDelegate();

	private final ConnectionOptimizerInvocationDelegate connectionOptimizerInvocationDelegate = new ConnectionOptimizerInvocationDelegate();

	private final XMLClientProviderInvocationDelegate xmlClientProviderInvocationDelegate = new XMLClientProviderInvocationDelegate();

	private final UtilInvocationDelegate utilInvocationDelegate = new UtilInvocationDelegate();

	private final ClientPermissionInvocationDelegate clientPermissionInvocationDelegate = new ClientPermissionInvocationDelegate();

	private final RemoteApplicationPreferenceReferencerDelegate remoteApplicationPreferenceReferencerDelegate = new RemoteApplicationPreferenceReferencerDelegate();

	private final ClientReferenceLocatorDelegate clientReferenceLocatorDelegate = new ClientReferenceLocatorDelegate();

	protected JDialog chatWindow = null;

	protected JButton b = new JButton("Send");

	protected JTextArea text = new JTextArea(40, 5) {

		@Override
		protected void processKeyEvent(final KeyEvent e) {
			super.processKeyEvent(e);
			if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isControlDown() && (e.getID() == KeyEvent.KEY_RELEASED)) {
				OJeeClientPermissionLocator.this.b.doClick();
			}
		}
	};

	protected JScrollPane s = new JScrollPane(this.chatTextPane);

	protected SimpleAttributeSet textAttribute = new SimpleAttributeSet();

	JTextPane chatTextPane = new JTextPane() {

		Dimension d = new Dimension(300, 500);

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return this.d == null ? super.getPreferredScrollableViewportSize() : this.d;
		}
	};


	public OJeeClientPermissionLocator(final Map params) {
		initializeParameters(params);
		this.sessionListeners = new ArrayList<>();

		InvocationHandler handler = BeansFactory.getBean(OJeeClientPermissionLocator.REMOTE_LOCATOR_INVOCATION_HANDLER,
				InvocationHandler.class);
		if (handler == null) {
			handler = ReflectionTools.newInstance(
					(String) params.get(OJeeClientPermissionLocator.REMOTE_LOCATOR_INVOCATION_HANDLER),
					InvocationHandler.class);
		}
	}

	/**
	 * Session start
	 */
	@Override
	public int startSession(final String user, final String password, final ClientWatch cw) throws Exception {
		ILoginProvider bean = null;
		try {
			try {
				bean = BeansFactory.getBean(ILoginProvider.class);
			} catch (final NoSuchBeanDefinitionException ex) {
				OJeeClientPermissionLocator.logger.trace(null, ex);
				BeansFactory.registerBeanDefinition("loginProvider",
						new AnnotatedGenericBeanDefinition(OntimizeLoginProvider.class));
				// yes, duplicate line. seems spring bug... review when possible
				BeansFactory.registerBeanDefinition("loginProvider",
						new AnnotatedGenericBeanDefinition(OntimizeLoginProvider.class));
				bean = BeansFactory.getBean(ILoginProvider.class);
			}
			final String url = System.getProperty("com.ontimize.services.baseUrl");
			final URI uri = new URI(url);
			bean.doLogin(uri, user, password);
			final IUserInformationService userService = BeansFactory.getBean(IUserInformationService.class);
			this.userInformation = userService.getUserInformation();
			if (this.userInformation == null) {
				throw new AuthenticationCredentialsNotFoundException(user);
			}
			// Save password in local
			this.userInformation.setPassword(password);
		} catch (final InvalidCredentialsException ex) {
			throw new SecurityException("E_LOGIN__INVALID_CREDENTIALS", ex);
		} catch (final ConnectException ex) {
			throw new SecurityException("E_CONNECT_SERVER", ex);
		} catch (final Exception ex) {
			throw new SecurityException("E_LOGIN__ERROR", ex);
		}

		final int sesId = this.initializeSession(user, password, cw);
		for (final ISessionListener listener : this.sessionListeners) {
			listener.sessionStarted(sesId);
		}
		return sesId;
	}

	protected int initializeSession(final String user, final String password, final ClientWatch cw) throws Exception {
		if (!this.startSession) {
			this.userId = this.startRemoteSession(user, password, cw);
		}
		return 1;
	}

	protected int startRemoteSession(final String user, final String password, final ClientWatch cw) throws Exception {
		return sessionLocatorInvocationDelegate.startSession(user, password, cw);
	}

	public UserInformation getUserInformation() {
		return this.userInformation;
	}

	public void registerSessionListener(final ISessionListener listener) {
		this.sessionListeners.add(listener);
	}

	public void unregisterSessionListener(final ISessionListener listener) {
		this.sessionListeners.remove(listener);
	}

	@Override
	public String getXMLForm(final String form, final int userid) throws Exception {
		try {
			final IFormProviderService current = BeansFactory.getBean(IFormProviderService.class);
			return current.getXMLForm(form);
		} catch (final Exception error) {
			OJeeClientPermissionLocator.logger.debug("Form provider not available: {}", error.getMessage(), error);
			OJeeClientPermissionLocator.logger.info("Form provider not available");
			return null;
		}
	}

	public void initializeParameters(final Map<String, Object> params) {
		// Check Message Server Period
		checkMessageServerPeriod(params);
		// Local locator
		this.localLocator = false;
		// Configure Remote Reference Locator
		configureRemoteReferenceLocatorName(params);
		// Configure client permission columns
		configureClientPermissionColumn(params);

	}

	private void configureClientPermissionColumn(final Map<String, Object> params) {
		final Object col = ApplicationManager.getParameterValue(OJeeClientPermissionLocator.CLIENT_PERMISSION_COLUMN,
				params);
		if (col != null) {
			this.clientPermissionsColumn = col.toString();
		}
	}

	private void configureRemoteReferenceLocatorName(final Map<String, Object> params) {
		final Object oRemoteReferenceLocatorName = ApplicationManager
				.getParameterValue(OJeeClientPermissionLocator.REMOTE_REFERENCE_LOCATOR_NAME, params);
		if (oRemoteReferenceLocatorName != null) {
			this.referenceLocatorServerName = oRemoteReferenceLocatorName.toString();
		} else {
			this.referenceLocatorServerName = "";
			OJeeClientPermissionLocator.logger.error("'{}' parameter not found",
					OJeeClientPermissionLocator.REMOTE_REFERENCE_LOCATOR_NAME);
		}

		final String sRemoteProperty = System.getProperty(OJeeClientPermissionLocator.REMOTE_LOCATOR_NAME_PROPERTY);
		if ((sRemoteProperty != null) && (!this.localLocator)) {
			this.referenceLocatorServerName = sRemoteProperty;
			OJeeClientPermissionLocator.logger.info("Using '{}'.",
					OJeeClientPermissionLocator.REMOTE_LOCATOR_NAME_PROPERTY);
		}
	}

	private void checkMessageServerPeriod(final Map<String, Object> params) {
		final Object checkservermessagesperiod = ApplicationManager
				.getParameterValue(OJeeClientPermissionLocator.CHECK_SERVER_MESSAGES_PERIOD, params);
		if (checkservermessagesperiod != null) {
			try {
				this.messagesCheckTime = Integer.parseInt(checkservermessagesperiod.toString());
				if ((this.messagesCheckTime != -1) && (this.messagesCheckTime < 10000)) {
					this.messagesCheckTime = 10000;
				}
			} catch (final Exception e) {
				OJeeClientPermissionLocator.logger.error(
						"'" + OJeeClientPermissionLocator.CHECK_SERVER_MESSAGES_PERIOD + "' parameter error.", e);
			}
		}
	}

	@Override
	public Entity getEntityReference(final String entityName) throws Exception {
		return this.sessionLocatorInvocationDelegate.getEntityReference(entityName);
	}

	@Override
	public int getSessionId() throws Exception {
		return this.sessionLocatorInvocationDelegate.getSessionId();
	}

	@Override
	public void endSession(final int id) throws Exception {
		this.sessionLocatorInvocationDelegate.endSession(id);

	}

	@Override
	public Entity getEntityReference(final String entity, final String user, final int sessionId) throws Exception {
		return this.sessionLocatorInvocationDelegate.getEntityReference(entity, user, sessionId);
	}

	@Override
	public boolean hasSession(final String user, final int id) throws Exception {
		return this.sessionLocatorInvocationDelegate.hasSession(user, id);
	}


	@Override
	public EntityResult testConnectionSpeed(final int sizeInBytes, final boolean compressed) throws Exception {
		return this.connectionOptimizerInvocationDelegate.testConnectionSpeed(sizeInBytes, compressed);
	}

	@Override
	public void setDataCompressionThreshold(final String user, final int id, final int compression) throws Exception {
		this.connectionOptimizerInvocationDelegate.setDataCompressionThreshold(user, id, compression);

	}

	@Override
	public int getDataCompressionThreshold(final int sessionId) throws Exception {
		return this.connectionOptimizerInvocationDelegate.getDataCompressionThreshold(sessionId);
	}

	@Override
	public Map getFormManagerParameters(final String formManagerId, final int userid) throws Exception {
		return this.xmlClientProviderInvocationDelegate.getFormManagerParameters(formManagerId, userid);
	}

	@Override
	public String getXMLRules(final String form, final int userid) throws Exception {
		return this.xmlClientProviderInvocationDelegate.getXMLRules(form, userid);
	}

	@Override
	public String getXMLMenu(final int userid) throws Exception {
		return this.xmlClientProviderInvocationDelegate.getXMLMenu(userid);
	}

	@Override
	public void reloadXMLMenu(final int userId) throws Exception {
		this.xmlClientProviderInvocationDelegate.reloadXMLMenu(userId);
	}

	@Override
	public String getXMLToolbar(final int userid) throws Exception {
		return this.xmlClientProviderInvocationDelegate.getXMLToolbar(userid);
	}

	@Override
	public void reloadXMLToolbar(final int userId) throws Exception {
		this.xmlClientProviderInvocationDelegate.reloadXMLToolbar(userId);
	}

	@Override
	public BytesBlock getImage(final String image, final int userId) throws Exception {
		return this.xmlClientProviderInvocationDelegate.getImage(image, userId);
	}

	@Override
	public List getMessages(final int sessionIdTo, final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getMessages(sessionIdTo, sessionId);
	}

	@Override
	public void sendMessage(final String message, final String user, final int sessionId) throws Exception {
		this.utilInvocationDelegate.sendMessage(message, user, sessionId);
	}

	@Override
	public void sendMessage(final Message message, final String user, final int sessionId) throws Exception {
		this.utilInvocationDelegate.sendMessage(message, user, sessionId);
	}

	@Override
	public void sendMessageToAll(final String message, final int sessionId) throws Exception {
		this.utilInvocationDelegate.sendMessageToAll(message, sessionId);
	}

	@Override
	public void sendRemoteAdministrationMessages(final String message, final int sessionId) throws Exception {
		this.utilInvocationDelegate.sendRemoteAdministrationMessages(message, sessionId);
	}

	@Override
	public List getRemoteAdministrationMessages(final int sessionIdTo, final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getRemoteAdministrationMessages(sessionIdTo, sessionId);
	}

	@Override
	public Entity getAttachmentEntity(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getAttachmentEntity(sessionId);
	}

	@Override
	public Entity getPrintingTemplateEntity(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getPrintingTemplateEntity(sessionId);
	}

	@Override
	public List getConnectedUsers(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getConnectedUsers(sessionId);
	}

	@Override
	public List getConnectedSessionIds(final int sessionid) throws Exception {
		return this.utilInvocationDelegate.getConnectedSessionIds(sessionid);
	}

	@Override
	public RemoteOperationManager getRemoteOperationManager(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getRemoteOperationManager(sessionId);
	}

	@Override
	public Object getRemoteReference(final String name, final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getRemoteReference(name, sessionId);
	}

	@Override
	public void removeEntity(final String entityName, final int sessionId) throws Exception {
		this.utilInvocationDelegate.removeEntity(entityName, sessionId);
	}

	@Override
	public List getLoadedEntities(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getLoadedEntities(sessionId);
	}

	@Override
	public TimeZone getServerTimeZone(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getServerTimeZone(sessionId);
	}

	@Override
	public String getLoginEntityName(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getLoginEntityName(sessionId);
	}

	@Override
	public String getToken() throws Exception {
		return this.utilInvocationDelegate.getToken();
	}

	@Override
	public String getUserFromCert(final String certificate) throws Exception {
		return this.utilInvocationDelegate.getUserFromCert(certificate);
	}

	@Override
	public String getPasswordFromCert(final String certificate) throws Exception {
		return this.utilInvocationDelegate.getPasswordFromCert(certificate);
	}

	@Override
	public InitialContext retrieveInitialContext(final int sessionId, final Map params) throws Exception {
		return this.utilInvocationDelegate.retrieveInitialContext(sessionId, params);
	}

	@Override
	public Locale getLocale(final int sessionId) throws Exception {
		return this.utilInvocationDelegate.getLocale(sessionId);
	}

	@Override
	public void setLocale(final int sessionId, final Locale locale) throws Exception {
		this.utilInvocationDelegate.setLocale(sessionId, locale);
	}

	@Override
	public String getSuffixString() throws Exception {
		return this.utilInvocationDelegate.getSuffixString();
	}

	@Override
	public String getLocaleEntity() throws Exception {
		return this.utilInvocationDelegate.getLocaleEntity();
	}

	@Override
	public boolean supportIncidenceService() throws Exception {
		return this.utilInvocationDelegate.supportIncidenceService();
	}

	@Override
	public boolean supportChangePassword(final String user, final int sessionId) throws Exception {
		return this.utilInvocationDelegate.supportChangePassword(user, sessionId);
	}

	@Override
	public EntityResult changePassword(final String password, final int sessionId, final Map av, final Map kv) throws Exception {
		return this.utilInvocationDelegate.changePassword(password, sessionId, av, kv);
	}

	@Override
	public boolean getAccessControl() throws Exception {
		return this.utilInvocationDelegate.getAccessControl();
	}

	@Override
	public ErrorAccessControl getErrorAccessControl() throws Exception {
		return this.utilInvocationDelegate.getErrorAccessControl();
	}

	@Override
	public void blockUserDB(final String user) throws Exception {
		this.utilInvocationDelegate.blockUserDB(user);
	}

	@Override
	public boolean checkBlockUserDB(final String user) throws Exception {
		return this.utilInvocationDelegate.checkBlockUserDB(user);
	}

	@Override
	public EntityResult getClientPermissions(final Map userKeys, final int sessionId) throws Exception {
		return this.clientPermissionInvocationDelegate.getClientPermissions(userKeys, sessionId);
	}

	@Override
	public void installClientPermissions(final Map userKeys, final int sessionId) throws Exception {
		this.clientPermissionInvocationDelegate.installClientPermissions(userKeys, sessionId);
	}

	@Override
	public long getTime() throws Exception {
		return this.clientPermissionInvocationDelegate.getTime();
	}

	@Override
	public RemoteApplicationPreferences getRemoteApplicationPreferences(final int sessionId) throws Exception {
		return this.remoteApplicationPreferenceReferencerDelegate.getRemoteApplicationPreferences(sessionId);
	}

	@Override
	public int getChatCheckTime() {
		return this.chatCheckTime;
	}

	@Override
	public int getMessageCheckTime() {
		return this.messagesCheckTime;
	}

	@Override
	public boolean hasChat() {
		return true;
	}

	@Override
	public void showMessageDialog(final Component component) {

		if (this.messagesCheckTime < 0) {
			return;
		}
		if (this.chatWindow == null) {
			StyleConstants.setForeground(this.textAttribute, Color.red);
			Window w = null;
			if (component instanceof Window) {
				w = (Window) component;
			} else {
				w = SwingUtilities.getWindowAncestor(component);
			}
			if (w instanceof Frame) {
				this.chatWindow = new JDialog((Frame) w, "Chat", false);
			} else if (w instanceof Dialog) {
				this.chatWindow = new JDialog((Dialog) w, "Chat", false);
			} else {
				this.chatWindow = new JDialog((Frame) null, "Chat", false);
			}

			final JPanel southPanel = new JPanel(new GridBagLayout());
			this.chatTextPane.setEditable(false);

			final Map filter = new Hashtable();

			try {
				filter.put("entity", this.getLoginEntityName(this.getSessionId()));
				filter.put("cod", "User_");
				filter.put("attr", "User");
				filter.put("cols", "User_");
				filter.put("dim", "text");
			} catch (final Exception e) {
				e.printStackTrace();
			}


			this.s.setPreferredSize(new Dimension(300, 500));
			this.chatWindow.getContentPane().setLayout(new GridBagLayout());

			final ReferenceComboDataField tUser = new ReferenceComboDataField(filter);
			tUser.setResourceBundle(
					ApplicationManager.getApplication() != null
					? ApplicationManager.getApplication().getResourceBundle()
							: null);
			tUser.setAdvancedQueryMode(true);

			tUser.setReferenceLocator(this);

			southPanel.add(tUser,
					new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
							new Insets(0, 0, 0, 0), 0, 0));
			southPanel.add(new JScrollPane(this.text),
					new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));

			southPanel.add(this.b,
					new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
							new Insets(0, 0, 0, 0), 0, 0));
			this.chatWindow.getContentPane()
			.add(southPanel,
					new GridBagConstraints(0, 1, 1, 1, 0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			this.chatWindow.getContentPane()
			.add(this.s,
					new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			this.chatWindow.pack();

			this.b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						if ((OJeeClientPermissionLocator.this.text.getText() != null)
								&& (OJeeClientPermissionLocator.this.text.getText().length() > 0) && !tUser.isEmpty()) {
							final String m = OJeeClientPermissionLocator.this.text.getText();
							try {
								final StyledDocument doc = (StyledDocument) OJeeClientPermissionLocator.this.chatTextPane
										.getDocument();
								doc.insertString(doc.getLength(), "\n>> " + m, null);
								OJeeClientPermissionLocator.this.text.setText("");
								OJeeClientPermissionLocator.this.s.getVerticalScrollBar()
								.setValue(OJeeClientPermissionLocator.this.s.getVerticalScrollBar().getMaximum());
							} catch (final Exception ex) {
								OJeeClientPermissionLocator.logger.error(null, ex);
							}
							final Object usu = tUser.getValue();
							if (usu == null) {
								return;
							} else if (usu instanceof SearchValue) {
								final SearchValue vb = (SearchValue) usu;
								final Object oValue = vb.getValue();
								if (oValue instanceof List) {
									for (int i = 0; i < ((List) oValue).size(); i++) {
										final Object v = ((List) oValue).get(i);
										OJeeClientPermissionLocator.this
										.sendMessage(m, (String) v,
												OJeeClientPermissionLocator.this.getSessionId());
									}
								} else if (oValue instanceof String) {
									OJeeClientPermissionLocator.this
									.sendMessage(m, (String) oValue,
											OJeeClientPermissionLocator.this.getSessionId());
								}
							} else {
								OJeeClientPermissionLocator.this
								.sendMessage(m, (String) usu, OJeeClientPermissionLocator.this.getSessionId());
							}

						}
					} catch (final Exception ex) {
						OJeeClientPermissionLocator.logger.error(null, ex);
						MessageDialog
						.showMessage(OJeeClientPermissionLocator.this.chatWindow, ex.getMessage(),
								JOptionPane.ERROR_MESSAGE,
								ApplicationManager.getApplication() != null ? ApplicationManager.getApplication()
										.getResourceBundle() : null);
					}
				}
			});
			this.chatWindow.setSize(400, 500);
			ApplicationManager.center(this.chatWindow);
		}
		if (ApplicationManager.getApplication() != null) {
			if (!ApplicationManager.getApplication().getFrame().isVisible()) {
				ApplicationManager.getApplication().getFrame().setVisible(true);
			}
		}
		this.chatWindow.setVisible(true);
	}

	@Override
	public String getUser() {
		if (userInformation != null) {
			return userInformation.getUsername();
		}

		return null;
	}

	@Override
	public InitialContext getInitialContext() {
		return this.clientReferenceLocatorDelegate.getInitialContext();
	}

	@Override
	public Object getReference(final String s) throws Exception {
		// TODO Using in local mode
		return null;
	}

	@Override
	public Object getLocaleId(final int i) throws Exception {
		return this.clientReferenceLocatorDelegate.getLocaleId(i);
	}

	@Override
	public void addModuleMemoryEntity(final String s, final List<String> list) {
		// TODO
	}

	@Override
	public boolean isLocalMode() {
		return localLocator;
	}

	@Override
	public boolean isAllowCertificateLogin() {
		// TODO implementar certifacate
		return false;
	}
}
