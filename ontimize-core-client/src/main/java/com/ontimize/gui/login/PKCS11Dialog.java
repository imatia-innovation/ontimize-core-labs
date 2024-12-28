package com.ontimize.gui.login;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.security.CertificateUtils;
import com.ontimize.security.provider.SunPKCS11Wrapper;

public class PKCS11Dialog extends EJDialog {

	public static final String UNIX_MAC_LIBRARY_NAME = "opensc-pkcs11.so";

	public static final String WINDOWS_LIBRARY_NAME = "UsrPkcs11.dll";

	public static final String WIN_32_DEFAULT_LIBRARY_LOCATION = "C:/Windows/System32/";

	public static final String WIN_64_DEFAULT_LIBRARY_LOCATION = "C:/Windows/SysWOW64/";

	public static final String UNIX_DEFAULT_LIBRARY_LOCATION = "/usr/lib/";

	public static final String MAC_OS_X_DEFAULT_LIBRARY_LOCATION = "/Library/OpenSC/";

	public static String pkcs11LibraryLocation;

	public static String pkcs11LibraryName;

	public static String filterLibraryExtension;

	public String selectedLibraryPath;

	public String selectedLibraryDirectory;

	public static final String HELP_TEXT_KEY = "pkcsdialog.help";

	public static final String HELP_TEXT_TIP_KEY = "pkcsdialog.help.tip";

	public static final String HELP_TITLE = "pkcsdialog.title";

	public static final String MODULE_FILE = "pkcsdialog.module.file";

	private static PKCS11Dialog pf = null;

	private static boolean accepted = false;

	protected Button butChoosePKCS = null;

	protected Button cancelButton = null;

	protected Button okButton = null;

	protected JTextField textLibrary = new JTextField();

	protected JLabel labelHelp = new JLabel();

	protected JFileChooser chooserPKCS = new JFileChooser();

	public PKCS11Dialog(final Frame owner) {
		super(owner, true);
		this.configureFields();
		this.configureLayout();
	}

	protected void configureLayout() {
		this.setLayout(new GridBagLayout());
		this.add(this.labelHelp,
				new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 2, 2));
		this.add(this.butChoosePKCS, new GridBagConstraints(4, 1, 1, 1, 0.5, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		this.add(this.textLibrary, new GridBagConstraints(0, 1, 3, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
		this.add(this.okButton, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		this.add(this.cancelButton, new GridBagConstraints(2, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
	}

	public void configureFields() {
		this.butChoosePKCS = this.createChooserButton();
		this.butChoosePKCS.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int decision = PKCS11Dialog.this.chooserPKCS.showOpenDialog(PKCS11Dialog.this);
				if (JFileChooser.APPROVE_OPTION == decision) {
					PKCS11Dialog.this.selectedLibraryPath = PKCS11Dialog.this.chooserPKCS.getSelectedFile()
							.getAbsolutePath();
					PKCS11Dialog.this.selectedLibraryDirectory = PKCS11Dialog.this.selectedLibraryPath.substring(0,
							PKCS11Dialog.this.selectedLibraryPath.lastIndexOf(File.separator));
					PKCS11Dialog.this.textLibrary.setText(PKCS11Dialog.this.selectedLibraryPath);
					SunPKCS11Wrapper.pkcsConfigFile = MessageFormat.format(SunPKCS11Wrapper.DLL_LIBRARY_PATTERN,
							new Object[] { PKCS11Dialog.this.selectedLibraryPath });
				}
			}
		});
		if (PKCS11Dialog.isWindows()) {
			if (this.is64bit()) {
				PKCS11Dialog.pkcs11LibraryLocation = PKCS11Dialog.WIN_64_DEFAULT_LIBRARY_LOCATION;
				PKCS11Dialog.pkcs11LibraryName = PKCS11Dialog.WINDOWS_LIBRARY_NAME;
				PKCS11Dialog.filterLibraryExtension = ".dll";
			} else {
				PKCS11Dialog.pkcs11LibraryLocation = PKCS11Dialog.WIN_32_DEFAULT_LIBRARY_LOCATION;
				PKCS11Dialog.pkcs11LibraryName = PKCS11Dialog.WINDOWS_LIBRARY_NAME;
				PKCS11Dialog.filterLibraryExtension = ".dll";
			}
		} else if (PKCS11Dialog.isMac()) {
			PKCS11Dialog.pkcs11LibraryLocation = PKCS11Dialog.MAC_OS_X_DEFAULT_LIBRARY_LOCATION;
			PKCS11Dialog.pkcs11LibraryName = PKCS11Dialog.UNIX_MAC_LIBRARY_NAME;
			PKCS11Dialog.filterLibraryExtension = ".so";
		} else if (PKCS11Dialog.isUnix()) {
			PKCS11Dialog.pkcs11LibraryLocation = PKCS11Dialog.UNIX_DEFAULT_LIBRARY_LOCATION;
			PKCS11Dialog.pkcs11LibraryName = PKCS11Dialog.UNIX_MAC_LIBRARY_NAME;
			PKCS11Dialog.filterLibraryExtension = ".so";
		} else {
			PKCS11Dialog.pkcs11LibraryLocation = System.getProperty("user.dir");
			PKCS11Dialog.pkcs11LibraryName = "";
			PKCS11Dialog.filterLibraryExtension = "*.*";
		}
		this.chooserPKCS.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.chooserPKCS.addChoosableFileFilter(new LibraryFileFilter());
		this.chooserPKCS.setCurrentDirectory(new File(PKCS11Dialog.pkcs11LibraryLocation));

		this.okButton = this.createOKButton();
		this.okButton.setIcon(ApplicationManager.getDefaultOKIcon());
		this.okButton.setResourceBundle(ApplicationManager.getApplicationBundle());
		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CertificateUtils.installCertProviders();
				if (CertificateUtils.getProviderInstalled(CertificateUtils.DNIe) != null) {
					if ((ApplicationManager.getApplication() != null)
							&& (ApplicationManager.getApplication().getPreferences() != null)) {
						ApplicationManager.getApplication()
						.getPreferences()
						.setPreference(null, SunPKCS11Wrapper.PREFERENCE_PKCS11_MODULE,
								SunPKCS11Wrapper.pkcsConfigFile);
						ApplicationManager.getApplication().getPreferences().savePreferences();
					}
				}
				PKCS11Dialog.accepted = true;
				PKCS11Dialog.this.setVisible(false);
			}
		});

		this.cancelButton = this.createCancelButton();
		this.cancelButton.setIcon(ApplicationManager.getDefaultCancelIcon());
		this.cancelButton.setResourceBundle(ApplicationManager.getApplicationBundle());
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PKCS11Dialog.accepted = false;
				PKCS11Dialog.this.setVisible(false);
			}
		});

		this.textLibrary.setEditable(false);
		this.labelHelp.setIcon(ImageManager.getIcon(ImageManager.HELP));
	}

	private boolean is64bit() {
		if (PKCS11Dialog.isWindows()) {
			final String arch = System.getProperty("os.arch");
			return (arch != null) && (arch.indexOf("64") >= 0);
		}
		return false;
	}

	public Button createOKButton() {
		final Map p = new Hashtable();
		p.put(Button.KEY, "application.accept");
		p.put(Button.TEXT, "application.accept");
		p.put(Button.MARGIN, "2;1;2;1");
		p.put("height", "18");
		return new Button(p);
	}

	public Button createChooserButton() {
		final Map p = new Hashtable();
		p.put(Button.KEY, "choose.load.button");
		p.put(Button.TEXT, "pkcsdialog.load");
		p.put(Button.MARGIN, "2;1;2;1");
		p.put("height", "18");
		return new Button(p);
	}

	public Button createCancelButton() {
		final Map p = new Hashtable();
		p.put(Button.KEY, "application.cancel");
		p.put(Button.TEXT, "application.cancel");
		p.put(Button.MARGIN, "2;1;2;1");
		p.put("height", "18");
		return new Button(p);
	}

	public void setBundle(final ResourceBundle bundle) {
		this.setTitle(ApplicationManager.getTranslation(PKCS11Dialog.HELP_TITLE, bundle));
		this.labelHelp.setText(ApplicationManager.getTranslation(PKCS11Dialog.HELP_TEXT_KEY, bundle) + " "
				+ PKCS11Dialog.pkcs11LibraryName);
		this.labelHelp.setToolTipText(ApplicationManager.getTranslation(PKCS11Dialog.HELP_TEXT_TIP_KEY, bundle));
		this.okButton.setResourceBundle(bundle);
		this.butChoosePKCS.setResourceBundle(bundle);
		this.cancelButton.setResourceBundle(bundle);
	}

	public static boolean showDialog(final Frame owner, final ResourceBundle bundle) {
		if (PKCS11Dialog.pf == null) {
			PKCS11Dialog.pf = new PKCS11Dialog(owner);
			PKCS11Dialog.pf.pack();
		}
		PKCS11Dialog.accepted = false;
		ApplicationManager.center(PKCS11Dialog.pf);
		PKCS11Dialog.pf.setBundle(bundle);
		PKCS11Dialog.pf.setVisible(true);
		return PKCS11Dialog.accepted;
	}

	private static boolean isWindows() {
		final String os = System.getProperty("os.name").toLowerCase();
		// windows
		return os.indexOf("win") >= 0;
	}

	private static boolean isMac() {
		final String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return os.indexOf("mac") >= 0;
	}

	private static boolean isUnix() {
		final String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0) || (os.indexOf("nux") >= 0);
	}

	protected static class LibraryFileFilter extends javax.swing.filechooser.FileFilter {

		public LibraryFileFilter() {
		}

		@Override
		public String getDescription() {
			return PKCS11Dialog.filterLibraryExtension;
		}

		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				if ((PKCS11Dialog.pkcs11LibraryName == null) || (PKCS11Dialog.pkcs11LibraryName.length() == 0)) {
					return true;
				}
				if (file.getPath().indexOf(PKCS11Dialog.pkcs11LibraryName) != -1) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

}
