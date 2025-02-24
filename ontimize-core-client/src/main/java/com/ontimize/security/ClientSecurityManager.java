package com.ontimize.security;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.SecureElement;
import com.ontimize.gui.TipScroll;
import com.ontimize.jee.common.security.ApplicationPermission;
import com.ontimize.jee.common.security.ClientPermission;
import com.ontimize.jee.common.security.FMPermission;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.security.GeneralSecurityException;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.jee.common.security.NotInPeriodException;
import com.ontimize.jee.common.security.TableFormPermission;
import com.ontimize.jee.common.security.TreePermission;
import com.ontimize.jee.common.security.XMLClientUtilities;

/**
 * This class manages permissions in client application. Menu, forms, form managers and applications
 * are categories where permissions are divided. Method <code>checkPermission</code> checks the
 * different permissions for application components.
 *
 * <p>
 * Most common tags for XML permission definitions are:<br>
 * <br>
 * <TABLE BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
 *
 * <tr>
 * <td>Component</td>
 * <td>Permission Tag</td>
 * </tr>
 *
 * <tr>
 * <td>Menu</td>
 * <td><i>MENU</td>
 * </tr>
 *
 * <tr>
 * <td>Form</td>
 * <td><i>FORM</td>
 * </tr>
 *
 * <tr>
 * <td>FormManager</td>
 * <td><i>FM</td>
 * </tr>
 *
 * <tr>
 * <td>Tree</td>
 * <td><i>TREE</td>
 * </tr>
 *
 * <tr>
 * <td>Application</td>
 * <td><i>APPLICATION</td>
 * </tr>
 * </TABLE>
 *
 * @author Imatia Innovation
 */
public class ClientSecurityManager {

	public static final Logger logger = LoggerFactory.getLogger(ClientSecurityManager.class);

	public static String MENU_ID = "MENU";

	public static String FORM_ID = "FORM";

	public static String FM_ID = "FM";

	public static String TREE_ID = "TREE";

	public static String APPLICATION_ID = "APPLICATION";

	protected Map permissions = new Hashtable();

	protected static boolean mouseListenerEnabled = true;

	protected static class ListenerSecuredElements extends MouseAdapter {

		protected javax.swing.Timer timer = null;

		protected TipScroll popup = null;

		protected String text = SecureElement.DESACTIVATE_COMPONENT_BY_PERMISSION_TIP;

		protected Component currentComponent = null;

		protected ResourceBundle resourceBundle = null;

		ActionListener showTip = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if ((ListenerSecuredElements.this.currentComponent == null)
						|| !ListenerSecuredElements.this.currentComponent.isShowing()) {
					return;
				} else {
					final Application ap = ApplicationManager.getApplication();
					if ((ap != null) && (ListenerSecuredElements.this.resourceBundle != ap.getResourceBundle())) {
						try {
							ListenerSecuredElements.this.text = ap.getResourceBundle()
									.getString(SecureElement.DESACTIVATE_COMPONENT_BY_PERMISSION_TIP);

						} catch (final Exception ex) {
							ClientSecurityManager.logger.debug(null, ex);
						}
					}
					ListenerSecuredElements.this.popup.show(ListenerSecuredElements.this.currentComponent,
							ListenerSecuredElements.this.currentComponent.getWidth() / 2,
							ListenerSecuredElements.this.currentComponent.getHeight() + 20,
							ListenerSecuredElements.this.text);
				}
			}
		};

		public ListenerSecuredElements() {
			this.timer = new javax.swing.Timer(500, this.showTip);
			this.timer.setRepeats(false);
			this.popup = new TipScroll();
			try {
				final Application ap = ApplicationManager.getApplication();
				if ((ap != null) && (ap.getResourceBundle() != null)) {
					this.text = ap.getResourceBundle().getString(SecureElement.DESACTIVATE_COMPONENT_BY_PERMISSION_TIP);
				}
			} catch (final Exception e) {
				ClientSecurityManager.logger.trace(null, e);
			}
			this.popup.pack();
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (e.getSource() instanceof SecureElement) {
				// It is registered
				if (((SecureElement) e.getSource()).isRestricted()) {
					// popup.show((Component)e.getSource(),0,((Component)e.getSource()
					// ).getHeight()/2,text);
					this.currentComponent = (Component) e.getSource();
					this.timer.start();
				}
			} else if (e.getSource() instanceof Component) {
				Component c = (Component) e.getSource();
				if (ClientSecurityManager.references.containsKey(c)) {
					final SecureElement el = (SecureElement) ClientSecurityManager.references.get(c);
					if (el instanceof Component) {
						c = (Component) el;
					}
					if (el.isRestricted()) {
						this.currentComponent = c;
						this.timer.start();
					}
				}
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			this.timer.stop();
			this.popup.setVisible(false);
			this.currentComponent = null;

		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			this.timer.stop();
			this.popup.setVisible(false);
			this.currentComponent = null;
		}

	};

	protected static MouseListener mouseListener = null;

	public static void registerSecuredElement(final SecureElement e) {
		if (ClientSecurityManager.mouseListener == null) {
			ClientSecurityManager.mouseListener = new ListenerSecuredElements();
		}
		if (e instanceof Component) {
			((Component) e).removeMouseListener(ClientSecurityManager.mouseListener);
			((Component) e).addMouseListener(ClientSecurityManager.mouseListener);
		}
	}

	public static void registerSecuredElement(final SecureElement e, final Component[] cs) {
		if (ClientSecurityManager.mouseListener == null) {
			ClientSecurityManager.mouseListener = new ListenerSecuredElements();
		}
		if (e instanceof Component) {
			((Component) e).removeMouseListener(ClientSecurityManager.mouseListener);
			((Component) e).addMouseListener(ClientSecurityManager.mouseListener);
		}
		for (int i = 0; i < cs.length; i++) {
			cs[i].addMouseListener(ClientSecurityManager.mouseListener);
			ClientSecurityManager.references.put(cs[i], e);
		}
	}

	public static void unregisterSecuredElement(final Component comp) {
		if (comp != null) {
			comp.removeMouseListener(mouseListener);
			references.remove(comp);
		}
	}

	protected static Map references = new Hashtable();

	public static void setMouseListenerEnabled(final boolean en) {
		ClientSecurityManager.mouseListenerEnabled = en;
	}

	public ClientSecurityManager(final String xmlPermissionDescriptionFile) throws Exception {
		final URL url = this.getClass().getClassLoader().getResource(xmlPermissionDescriptionFile);
		if (url == null) {
			throw new Exception("File " + xmlPermissionDescriptionFile + " not Found");
		}
		// Reads
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(url.openStream());
			final StringBuffer sb = new StringBuffer();
			int car = -1;
			while ((car = in.read()) != -1) {
				sb.append((char) car);
			}
			in.close();
			this.permissions = XMLClientUtilities.buildClientPermissions(sb);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public ClientSecurityManager(final StringBuffer xmlPermissionDescription) throws Exception {
		this.permissions = XMLClientUtilities.buildClientPermissions(xmlPermissionDescription);
	}

	public ClientSecurityManager(final Map permissions) throws Exception {
		this.permissions = permissions;
	}

	public void setPermissions(final Map permissions) {
		this.permissions = permissions;
	}

	public void checkPermission(final ClientPermission clientpermission)
			throws NotInPeriodException, GeneralSecurityException {
		ClientSecurityManager.logger.debug("Checking permission: {}", clientpermission);
		if (clientpermission instanceof MenuPermission) {
			checkMenuPermission((MenuPermission) clientpermission);
		} else if (clientpermission instanceof ApplicationPermission) {
			checkApplicationPermission(clientpermission);
		} else if (clientpermission instanceof TableFormPermission) {
			checkTableFormPermission((TableFormPermission) clientpermission);
		} else if (clientpermission instanceof FormPermission) {
			checkFormPermission((FormPermission) clientpermission);
		} else if (clientpermission instanceof FMPermission) {
			checkFormManagerPermission((FMPermission) clientpermission);
		} else if (clientpermission instanceof TreePermission) {
			checkTreePermission((TreePermission) clientpermission);
		}
	}

	protected void checkTreePermission(final TreePermission treePermission) throws NotInPeriodException,
	GeneralSecurityException {
		final Map treepermissions = (Map) this.permissions.get(ClientSecurityManager.TREE_ID);
		if (treepermissions != null) {
			final String treeName = treePermission.getTree();
			if (treeName == null) {
				ClientSecurityManager.logger.warn("Tree has not an id.");
				return;
			}
			final List nodepermissions = (List) treepermissions.get(treeName);
			if (nodepermissions != null) {
				for (int i = 0; i < nodepermissions.size(); i++) {
					final TreePermission fp = (TreePermission) nodepermissions.get(i);
					if (fp.getAttribute().equals((treePermission).getAttribute())) {
						if (fp.getPermissionName().equalsIgnoreCase((treePermission).getPermissionName())) {
							if (!fp.hasPermission()) {
								if (fp.isPeriodRestricted()) {
									throw new NotInPeriodException("Permission " + treePermission.toString()
									+ " denied. Not in allowed period");
								}
								throw new GeneralSecurityException(
										"Permission " + treePermission.toString() + " denied");
							}
						}
					}
				}
			}
		}
	}

	protected void checkFormManagerPermission(final FMPermission formManagerpermission) throws NotInPeriodException,
	GeneralSecurityException {
		final Map formmanagerpermissions = (Map) this.permissions.get(ClientSecurityManager.FM_ID);
		if (formmanagerpermissions != null) {
			final String gfName = formManagerpermission.getFMId();
			if (gfName == null) {
				ClientSecurityManager.logger.warn("FormManager has not an id. ");
				return;
			}
			final List formpermissions = (List) formmanagerpermissions.get(gfName);
			if (formpermissions != null) {
				for (int i = 0; i < formpermissions.size(); i++) {
					final FMPermission fp = (FMPermission) formpermissions.get(i);
					if (fp.getPermissionName().equalsIgnoreCase((formManagerpermission).getPermissionName())) {
						if (fp.getAttribute().equals((formManagerpermission).getAttribute())) {
							if (!fp.hasPermission()) {
								if (fp.isPeriodRestricted()) {
									throw new NotInPeriodException("Permission " + formManagerpermission.toString()
									+ " denied. Not in allowed period");
								}
								throw new GeneralSecurityException(
										"Permission " + formManagerpermission.toString() + " denied");
							}
						}
					}
				}
			}
		}
	}

	protected void checkFormPermission(final FormPermission formPermission) throws NotInPeriodException,
	GeneralSecurityException {
		final Map formpermissions = (Map) this.permissions.get(ClientSecurityManager.FORM_ID);
		if (formpermissions != null) {
			final String archiveName = formPermission.getArchiveName();
			if (archiveName == null) {
				ClientSecurityManager.logger.warn("Form doesn't have an archive name.");
				return;
			}
			final List componentpermissions = (List) formpermissions.get(archiveName);
			if (componentpermissions != null) {
				for (int i = 0; i < componentpermissions.size(); i++) {
					final FormPermission fp = (FormPermission) componentpermissions.get(i);
					if (fp.getAttribute().equals((formPermission).getAttribute())) {
						if (fp.getPermissionName().equalsIgnoreCase((formPermission).getPermissionName())) {
							if (!fp.hasPermission()) {
								if (fp.isPeriodRestricted()) {
									throw new NotInPeriodException("Permission " + formPermission.toString()
									+ " denied. Not in allowed period");
								}
								throw new GeneralSecurityException(
										"Permission " + formPermission.toString() + " denied");
							}
						}
					}
				}
			}
		}
	}

	protected void checkTableFormPermission(final TableFormPermission tableFormPermission) throws NotInPeriodException,
	GeneralSecurityException {
		final Map formPermissions = (Map) this.permissions.get(ClientSecurityManager.FORM_ID);
		if (formPermissions != null) {
			final String archiveName = tableFormPermission.getArchiveName();
			if (archiveName == null) {
				ClientSecurityManager.logger.warn("Form doesn't have an archive name");
				return;
			}
			final List componentpermissions = (List) formPermissions.get(archiveName);
			if (componentpermissions != null) {
				for (int i = 0; i < componentpermissions.size(); i++) {
					final Object o = componentpermissions.get(i);
					if (!(o instanceof TableFormPermission)) {
						continue;
					}
					final TableFormPermission fp = (TableFormPermission) componentpermissions.get(i);
					if (fp.getAttribute().equals(tableFormPermission.getAttribute())) {
						if (fp.getPermissionName().equalsIgnoreCase(tableFormPermission.getPermissionName())) {
							if ((fp.getColumnName() != null)
									&& fp.getColumnName()
									.equalsIgnoreCase((tableFormPermission)
											.getColumnName())
									&& (fp.getType() != null)
									&& fp.getType().equalsIgnoreCase((tableFormPermission).getType())) {
								if (!fp.hasPermission()) {
									if (fp.isPeriodRestricted()) {
										throw new NotInPeriodException("Permission " + tableFormPermission.toString()
										+ " denied. Not in allowed period");
									}
									throw new GeneralSecurityException("Permission " + fp.toString() + " denied");
								}
							} else if ((fp.getType() != null) && fp.getType()
									.equalsIgnoreCase((tableFormPermission).getType()) && (fp.getColumnName() == null)) {
								if (!fp.hasPermission()) {
									if (fp.isPeriodRestricted()) {
										throw new NotInPeriodException("Permission " + tableFormPermission.toString()
										+ " denied. Not in allowed period");
									}
									throw new GeneralSecurityException(
											"Permission " + tableFormPermission.toString() + " denied");
								}
							}
						}
					}
				}
			}
		}
	}

	protected void checkApplicationPermission(final ClientPermission clientpermission) throws GeneralSecurityException {
		// Finds in application permissions
		final List applicationpermissions = (List) this.permissions.get(ClientSecurityManager.APPLICATION_ID);
		if ((applicationpermissions == null) || applicationpermissions.isEmpty()) {
			return;
		}
		// Ahora, si hay permisos miramos si existe el que nos piden
		for (int i = 0; i < applicationpermissions.size(); i++) {
			final ApplicationPermission mP = (ApplicationPermission) applicationpermissions.get(i);
			if (mP.getPermissionName().equalsIgnoreCase(clientpermission.getPermissionName())) {
				if (!mP.hasPermission()) {
					throw new GeneralSecurityException("Permission " + clientpermission.toString() + " denied");
				}
			}
		}
	}

	protected void checkMenuPermission(final MenuPermission menupermission) throws NotInPeriodException,
	GeneralSecurityException {
		// Finds in menu permissions
		final List menupermissions = (List) this.permissions.get(ClientSecurityManager.MENU_ID);
		if ((menupermissions == null) || menupermissions.isEmpty()) {
			return;
		}
		// Finds in menu permissions
		for (int i = 0; i < menupermissions.size(); i++) {
			final MenuPermission mP = (MenuPermission) menupermissions.get(i);
			if (mP.getAttribute().equals(menupermission.getAttribute())) {
				// Checks restricted condition
				if (mP.getPermissionName().equalsIgnoreCase(menupermission.getPermissionName())) {
					if (!mP.hasPermission()) {
						if (mP.isPeriodRestricted()) {
							throw new NotInPeriodException(
									"Permission " + menupermission.toString() + " denied. Not in allowed period");
						}
						throw new GeneralSecurityException("Permission " + menupermission.toString() + " denied");
					}
				}
			}
		}
	}

}
