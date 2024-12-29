package com.ontimize.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.login.AliasCertPair;
import com.ontimize.jee.common.util.Base64Utils;
import com.ontimize.security.provider.SecurityProvider;

public final class CertificateUtils {

	private static final Logger logger = LoggerFactory.getLogger(CertificateUtils.class);

	public static final String PROVIDERS = "com/ontimize/security/provider/providers.properties";

	public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";

	public static final String END_CERTIFICATE = "\n-----END CERTIFICATE-----\n";

	public static final String USER_CERTIFICATE = "-----USER_CERTIFICATE-----";

	public static final String SIGNED_TOKEN = "-----SIGNED_TOKEN-----";

	public static final String TOKEN = "-----TOKEN-----";

	public static final String TOKEN_ERROR = "TOKEN_ERROR";

	public static final String SIGNED_ERROR = "SIGNED_ERROR";

	public static final String TRUST_CA_ERROR = "TRUST_CA_ERROR";

	public static final String TRUST_CRL_ERROR = "TRUST_CRL_ERROR";

	public static final String VALIDITY_ERROR = "VALIDITY_ERROR";

	public static final String DNIe = "DNIe";

	public static final String DNIe_CN = "CN";

	public static final String DNIe_G = "G";

	public static final String DNIe_SN = "SN";

	public static final String DNIe_SERIALNUMBER = "SERIALNUMBER";

	public static final String DNIe_C = "C";

	protected static int OFFSET = 3;

	protected static String OFFSET_STRING = "e1f6468168wefwec5we1654wefwef2165467664654efwedf4efd64ef6";

	public static String providers = CertificateUtils.PROVIDERS;

	public static String beginCertificateToken = CertificateUtils.USER_CERTIFICATE;

	private static KeyStore smcks = null;

	// Installed providers
	private static Map providerInstalled = new Hashtable();

	public static void installCertProviders() {

		InputStream is = CertificateUtils.class.getClassLoader().getResourceAsStream(CertificateUtils.providers);
		// check whether file
		if (is == null) {
			try {
				is = new FileInputStream(new File(CertificateUtils.providers));
			} catch (final FileNotFoundException e) {
				CertificateUtils.logger.error(null, e);
			}
		}
		if (is != null) {
			Properties p = new Properties();
			try {
				p.load(is);
			} catch (final Exception ex) {
				if (ApplicationManager.DEBUG) {
					CertificateUtils.logger.error(null, ex);
				} else {
					CertificateUtils.logger.trace(null, ex);
				}
				p = null;
			}
			if (p != null) {
				final Enumeration en = p.keys();
				while (en.hasMoreElements()) {
					final Object key = en.nextElement();
					final Object value = p.get(key);
					try {
						final Class c = Class.forName(value.toString());
						final Object obj = c.newInstance();
						if (obj instanceof SecurityProvider) {
							final Provider prov = ((SecurityProvider) obj).getProvider();
							CertificateUtils.installProvider(key.toString(), prov);
						}
					} catch (final Exception ex) {
						if (ApplicationManager.DEBUG) {
							CertificateUtils.logger.error(null, ex);
						} else {
							CertificateUtils.logger.trace(null, ex);
						}
					}
				}
			}
		}
	}

	private CertificateUtils() {
	}

	public static void installProvider(final String key, final Provider provider) {
		if (provider == null) {
			return;
		}
		Security.addProvider(provider);
		CertificateUtils.providerInstalled.put(key.toString(), provider);
	}

	public static Enumeration getProvidersInstalled() {
		return Collections.enumeration(CertificateUtils.providerInstalled.keySet());
	}

	public static Provider getProviderInstalled(final String key) {
		return (Provider) CertificateUtils.providerInstalled.get(key);
	}

	public static boolean isProviderInstalled(final String key) {
		return CertificateUtils.providerInstalled.containsKey(key);
	}

	public static String encrypt(final String password) {
		try {
			return encrypt(password, CertificateUtils.OFFSET_STRING, CertificateUtils.OFFSET);
		} catch (final Exception ex) {
			CertificateUtils.logger.error(null, ex);
			return null;
		}
	}


	public static byte[] encrypt(final byte[] bytes, final String key)
			throws IllegalArgumentException, UnsupportedEncodingException {
		if ((bytes == null) || (bytes.length == 0)) {
			throw new IllegalArgumentException(
					"Error: invalid string. If can not be null and the lenght must be greater than 0");
		}
		final byte[] res = new byte[bytes.length];
		final byte[] llave = key.getBytes("ISO-8859-1");
		for (int i = 0; i < bytes.length; i++) {
			final byte b = bytes[i];
			byte bRes = b;
			for (int j = 0; j < llave.length; j++) {
				bRes = (byte) (bRes ^ llave[j]);
			}
			res[i] = bRes;
		}
		return res;
	}

	public static String encrypt(final String password, final String key, final int number)
			throws IllegalArgumentException, UnsupportedEncodingException {
		if ((password == null) || (password.length() == 0)) {
			throw new IllegalArgumentException(
					"Error: invalid string. If can not be null and the lenght must be greater than 0");
		}
		final byte[] bytes = password.getBytes();
		final byte[] res = new byte[bytes.length];
		final byte[] llave = key.getBytes("ISO-8859-1");
		for (int i = 0; i < bytes.length; i++) {
			final byte b = bytes[i];
			byte bRes = b;
			for (int j = 0; j < llave.length; j++) {
				bRes = (byte) (bRes ^ llave[(j + number) % llave.length]);
			}
			res[i] = bRes;
		}
		return new String(res);
	}

	/*
	 * public static KeyStore installPKCS11(Provider pkcs11Provider) throws Exception {
	 * Security.addProvider(pkcs11Provider); return KeyStore.getInstance("PKCS11"); }
	 */

	public static String encodeCertificate(final Certificate cert) {
		try {
			// Get the encoded form which is suitable for exporting
			final byte[] buf = cert.getEncoded();
			final StringBuilder os = new StringBuilder();
			// Write in text form
			os.append(CertificateUtils.BEGIN_CERTIFICATE);
			os.append(Base64Utils.getBase64JV().getEncoder().encodeByteArrayToString(buf));
			os.append(CertificateUtils.END_CERTIFICATE);
			return os.toString();
		} catch (final Exception e) {
			CertificateUtils.logger.trace(null, e);
			return null;
		}
	}

	public static Certificate decodeCertificate(final String certificate) throws Exception {
		final ByteArrayInputStream bIn = new ByteArrayInputStream(certificate.getBytes());
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return cf.generateCertificate(bIn);
	}

	public static String createTokenToSend(final Certificate cert, final String token, final String signedToken) {
		final StringBuilder sb = new StringBuilder();
		try {
			sb.append(CertificateUtils.USER_CERTIFICATE);
			sb.append(CertificateUtils.encodeCertificate(cert));
			sb.append(CertificateUtils.TOKEN);
			sb.append(token);
			sb.append(CertificateUtils.SIGNED_TOKEN);
			sb.append(signedToken);
		} catch (final Exception ex) {
			CertificateUtils.logger.error(null, ex);
			return null;
		}
		return sb.toString();
	}

	public static Map parseTokenReceived(final String token) throws Exception {
		final Map h = new Hashtable();
		if (!token.startsWith(CertificateUtils.USER_CERTIFICATE)) {
			return null;
		}
		String tk = token.substring(CertificateUtils.USER_CERTIFICATE.length());
		int i = tk.indexOf(CertificateUtils.TOKEN);
		if (i == -1) {
			return null;
		}
		final String stringCert = tk.substring(0, i);
		h.put(CertificateUtils.USER_CERTIFICATE, CertificateUtils.decodeCertificate(stringCert));
		tk = tk.substring(i + CertificateUtils.TOKEN.length());
		i = tk.indexOf(CertificateUtils.SIGNED_TOKEN);
		if (i == -1) {
			return null;
		}
		h.put(CertificateUtils.TOKEN, tk.substring(0, i));
		h.put(CertificateUtils.SIGNED_TOKEN, tk.substring(i + CertificateUtils.SIGNED_TOKEN.length()));
		return h;
	}

	public static String getSignedToken(final String token, final Certificate cert, final PrivateKey sk) {
		try {
			final Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(sk);
			sig.update(token.getBytes());
			final byte[] signatureBytes = sig.sign();
			return Base64Utils.getBase64JV().getEncoder().encodeByteArrayToString(signatureBytes);
		} catch (final Exception ex) {
			CertificateUtils.logger.error(null, ex);
		}
		return null;
	}

	public static Certificate[] loadCerfificatesFromURL(final String url) throws Exception {
		final InputStream is = CertificateUtils.class.getClassLoader().getResourceAsStream(url);
		return CertificateUtils.loadCerfificatesFromStream(is);
	}

	public static Certificate[] loadCerfificatesFromStream(final InputStream is) throws Exception {
		final Properties prop = new Properties();
		prop.load(is);
		return CertificateUtils.loadCerfificatesFromProperties(prop);
	}

	public static Certificate[] loadCerfificatesFromProperties(final Properties properties) throws Exception {
		return (java.security.cert.Certificate[]) CertificateUtils.loadCertificatesFromProperies(properties, false);
	}

	public static Object loadCerfificatesFromDir(final List dirlist, final boolean crl) throws Exception {
		if (dirlist == null) {
			if (crl) {
				return new CRL[0];
			} else {
				return new Certificate[0];
			}
		}
		final List certList = new ArrayList();
		for (int i = 0; i < dirlist.size(); i++) {
			final String currentDir = dirlist.get(i).toString();
			final File dir = new File(currentDir);
			if (dir.isFile()) {
				final Object certificate = CertificateUtils.loadCertificateFromFile(currentDir, crl);
				if (certificate != null) {
					certList.add(certificate);
				}
			}
			if (dir.isDirectory()) {
				final File[] fileList = dir.listFiles();
				for (int j = 0; j < fileList.length; j++) {
					final Object certificate = CertificateUtils.loadCertificateFromFile(currentDir, crl);
					if (certificate != null) {
						certList.add(certificate);
					}
				}
			}
		}
		if (crl) {
			return certList.toArray(new java.security.cert.CRL[certList.size()]);
		} else {
			return certList.toArray(new java.security.cert.Certificate[certList.size()]);
		}
	}

	public static Object loadCertificateFromFile(final String filePath, final boolean crl) throws Exception {
		final File file = new File(filePath);
		return CertificateUtils.loadCertificateFromFile(file, crl);
	}

	public static Object loadCertificateFromFile(final File file, final boolean crl) throws Exception {
		if (file.exists()) {
			FileInputStream fisCert = null;
			try {
				fisCert = new FileInputStream(file);
				final CertificateFactory cf = CertificateFactory.getInstance("X.509");
				try {
					if (crl) {
						return cf.generateCRL(fisCert);
					} else {
						return cf.generateCertificate(fisCert);
					}
				} catch (final CertificateException ex) {
					CertificateUtils.logger.error(
							CertificateUtils.class.getName() + ": Error loading certificate: " + file.getAbsolutePath(),
							ex);
					CertificateUtils.logger.error(
							"NOTE: .crt file must start with text: '-----BEGIN CERTIFICATE-----'. You must remove previous initial human-readable info added automatically to be valid.");
				}
			} catch (final Exception e) {
				CertificateUtils.logger.error(null, e);
			} finally {
				try {
					if (fisCert != null) {
						fisCert.close();
					}
				} catch (final Exception e3) {
					CertificateUtils.logger.error(null, e3);
				}
			}
		}
		return null;
	}

	private static Object loadCertificatesFromProperies(final Properties properties, final boolean crl) throws Exception {
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		if (cf == null) {
			CertificateUtils.logger.debug("X.509 CertificateFactory is null");
			return null;
		}
		final List l = new ArrayList();
		final Enumeration props = properties.keys();
		while (props.hasMoreElements()) {
			final String prop = (String) props.nextElement();
			final String cert = properties.getProperty(prop);
			final URL urlCert = CertificateUtils.class.getClassLoader().getResource(cert);
			if (urlCert == null) {
				CertificateUtils.logger.debug("Certificate not found in: " + cert);
				continue;
			}
			Certificate certificate = null;
			CRL crlcert = null;
			try {
				if (crl) {
					crlcert = cf.generateCRL(urlCert.openStream());
				} else {
					certificate = cf.generateCertificate(urlCert.openStream());
				}
			} catch (final Exception ex) {
				CertificateUtils.logger.debug("Reading certificate problems " + cert);
				CertificateUtils.logger.error(null, ex);
				certificate = null;
				crlcert = null;
			}
			if (crl && (crlcert != null)) {
				l.add(crlcert);
			}
			if (!crl && (certificate != null)) {
				l.add(certificate);
			}
		}
		if (crl) {
			return l.toArray(new java.security.cert.CRL[l.size()]);
		} else {
			return l.toArray(new java.security.cert.Certificate[l.size()]);
		}
	}

	public static CRL[] loadCRLFromURL(final String url) throws Exception {
		final InputStream is = CertificateUtils.class.getClassLoader().getResourceAsStream(url);
		return CertificateUtils.loadCRLFromStream(is);
	}

	public static CRL[] loadCRLFromStream(final InputStream is) throws Exception {
		final Properties prop = new Properties();
		prop.load(is);
		return CertificateUtils.loadCRLFromProperties(prop);
	}

	public static CRL[] loadCRLFromProperties(final Properties properties) throws Exception {
		return (java.security.cert.CRL[]) CertificateUtils.loadCertificatesFromProperies(properties, true);
	}

	public static boolean checkValidity(final Certificate cert) {
		boolean trusted = false;
		try {
			if (cert instanceof X509Certificate) {
				((X509Certificate) cert).checkValidity();
			}
			trusted = true;
		} catch (final Exception ex) {
			CertificateUtils.logger.trace(null, ex);
			trusted = false;
		}
		return trusted;
	}

	public static boolean isTrustCA(final Certificate cert, final java.security.cert.Certificate[] validCA) {
		boolean trusted = false;
		if (validCA == null) {
			return trusted;
		}
		for (int i = 0, a = validCA.length; (i < a) && !trusted; i++) {
			try {
				cert.verify(validCA[i].getPublicKey());
				trusted = true;
			} catch (final Exception ex) {
				CertificateUtils.logger.trace(null, ex);
				trusted = false;
			}
		}
		return trusted;
	}

	public static boolean isRevoked(final Certificate cert, final java.security.cert.CRL[] crls) {
		boolean revoked = false;
		if (crls == null) {
			return revoked;
		}
		for (int j = 0; (j < crls.length) && !revoked; j++) {
			if (crls[j].isRevoked(cert)) {
				CertificateUtils.logger.debug(" -> " + cert + " is Revoked");
				revoked = true;
			}
		}
		return revoked;
	}

	public static java.security.cert.Certificate importCertificate(final String filepath) {
		try {
			final FileInputStream is = new FileInputStream(new File(filepath));

			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			final java.security.cert.Certificate cert = cf.generateCertificate(is);
			return cert;
		} catch (final CertificateException e) {
			CertificateUtils.logger.error(null, e);
		} catch (final IOException e) {
			CertificateUtils.logger.error(null, e);
		}
		return null;
	}

	public static List getAliasCertPairFromKeyStore(final KeyStore ks, final String pin)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final List l = new ArrayList();
		if (ks == null) {
			return l;
		}
		try {
			ks.load(null, pin.toCharArray());
		} catch (final ProviderException e) {
			CertificateUtils.logger.trace(null, e);
			final KeyStore renewKs = CertificateUtils.getKeystoreInstance(true);
			if (renewKs != null) {
				renewKs.load(null, pin.toCharArray());
			}
		}

		final Enumeration aliasesEnum = ks.aliases();
		while (aliasesEnum.hasMoreElements()) {
			final String alias = (String) aliasesEnum.nextElement();
			X509Certificate cert = null;
			try {
				cert = (X509Certificate) ks.getCertificate(alias);
				l.add(new AliasCertPair(alias, cert));
			} catch (final Exception ex) {
				CertificateUtils.logger.error(null, ex);
				cert = null;
			}
		}
		return l;
	}

	protected static String getInfo(final X509Certificate cert) {
		if (cert == null) {
			return "";
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(" getIssuerDN(): " + cert.getIssuerDN().getName());
			sb.append(" SubjectDN(): " + cert.getSubjectDN().getName());
			sb.append(" NotAfter: " + cert.getNotAfter());
			sb.append(" SerialNumber: " + cert.getSerialNumber());
			return sb.toString();
		}
	}

	private static String parseStringForTokenizer(final String s, final char searchChar, final char newChar, final char toggleChar) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		boolean change = false;
		for (int i = 0, a = s.length(); i < a; i++) {
			if ((s.charAt(i) == searchChar) && change) {
				sb.append(newChar);
				continue;
			}
			if (s.charAt(i) == toggleChar) {
				change = !change;
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public static Map getX509CertificateSubjectDNFields(final X509Certificate cert) {
		if (cert == null) {
			return null;
		}
		final String s = cert.getSubjectDN().getName();
		if (s == null) {
			return null;
		}
		final String parsed = CertificateUtils.parseStringForTokenizer(s, ',', '_', '"');
		final Map h = new Hashtable();
		final StringTokenizer st = new StringTokenizer(parsed, ",", false);
		while (st.hasMoreTokens()) {
			final String token = st.nextToken();
			final StringTokenizer st2 = new StringTokenizer(token, "=", false);
			if (st2.countTokens() == 2) {
				final String key = st2.nextToken().trim();
				final String value = st2.nextToken().trim();
				h.put(key, CertificateUtils.parseStringForTokenizer(value, '_', ',', '"'));
			}
		}
		return h;
	}

	public static String getX509CertificateSubjectDNFields(final X509Certificate cert, final String field) {
		final Map h = CertificateUtils.getX509CertificateSubjectDNFields(cert);
		if (h == null) {
			return null;
		}
		if (!h.containsKey(field)) {
			return null;
		}
		return h.get(field).toString();
	}

	public static KeyStore getKeystoreInstance() {
		return CertificateUtils.getKeystoreInstance(false);
	}

	public static KeyStore getKeystoreInstance(final boolean forceRenew) {
		if ((CertificateUtils.smcks != null) && !forceRenew) {
			return CertificateUtils.smcks;
		}
		try {
			return CertificateUtils.smcks = KeyStore.getInstance("PKCS11");
		} catch (final java.security.KeyStoreException ex) {
			CertificateUtils.smcks = null;
			MessageDialog.showMessage(ApplicationManager.getApplication().getFrame(),
					"abstractlogindialog.pkcs11notfound", JOptionPane.ERROR_MESSAGE,
					ApplicationManager.getApplicationBundle());
			if (ApplicationManager.DEBUG) {
				CertificateUtils.logger.error(null, ex);
			}
			return CertificateUtils.smcks;
		} catch (final Exception ex) {
			CertificateUtils.smcks = null;
			MessageDialog.showMessage(ApplicationManager.getApplication().getFrame(), ex.getMessage(),
					JOptionPane.ERROR_MESSAGE, ApplicationManager.getApplicationBundle());
			if (ApplicationManager.DEBUG) {
				CertificateUtils.logger.error(null, ex);
			}
			return CertificateUtils.smcks;
		}
	}

	/**
	 * Loads content into a keystore from file.
	 * @param keystoreFilepath
	 * @param keystorePassword
	 * @param alias
	 */
	public static void addToKeyStore(final String keystoreFilepath, final char[] keystorePassword, final String alias,
			final String keystoreType) {
		FileInputStream in = null;
		try {

			in = new FileInputStream(new File(keystoreFilepath));
			CertificateUtils.smcks = java.security.KeyStore.getInstance(keystoreType);
			CertificateUtils.smcks.load(in, keystorePassword);
		} catch (final Exception e) {
			CertificateUtils.logger.error(null, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
					CertificateUtils.logger.error(null, e);
				}
			}
		}
	}

	/**
	 * It returns in server locator revoked and accepted certificates. This method is called
	 * programmatically twice in remote locator to load revoked and accepted certificates.
	 * @param vCert Contains the list of places where user wants to load certificates: <br>
	 *        <br>
	 *        <ul>
	 *        <li>One unique .properties file included in project (keeps backward compatibility), e.g.
	 *        <i>com/ontimize/quickstart/server /certificate/cert.properties</i> . This properties
	 *        contains a structure like this: <br>
	 *        <br>
	 *        <i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AC_DNIE_001=com/ontimize/quickstart
	 *        /server/certificate/AC_DNIE_001.crt <br>
	 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AC_DNIE_002=com/ontimize/
	 *        quickstart/server/certificate/AC_DNIE_002.crt <br>
	 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AC_DNIE_003=com/ontimize/
	 *        quickstart/server/certificate/AC_DNIE_003.crt <br>
	 *        </i> <br>
	 *        <li>Complete path to a directory, e.g. <i>/home/cert</i>. With this format, all files
	 *        contained in this path will be scanned. Non-compatible certificate files will be
	 *        discarded.
	 *        <li>Indivual file path, e.g. <i>/home/cert/AC_DNIE_001.crt</i>. An individual certificate
	 *        will be loaded with this format.
	 *        </ul>
	 * @param crl when <code>true</code> loads and returns revoked certificates, else loads/returns
	 *        accepted certificates.
	 * @return the Certificate[]/CRL[] array with accepted/revoked certificates according to
	 *         <code>crl</code> parameter
	 * @throws Exception when occurs an error loading one of these file paths
	 *
	 * @since 5.2068EN-0.2EN
	 */
	public static Object loadCertificates(final Vector vCert, final boolean crl) throws Exception {
		Certificate[] base = null;
		CRL[] rvk = null;
		for (int i = 0; i < vCert.size(); i++) {
			final String url = vCert.get(i).toString();
			if (url.endsWith(".properties")) {
				// Backward compatibility. Certificates are defined into
				// properties in pairs: alias-certX.crt
				vCert.remove(i);
				if (crl) {
					rvk = CertificateUtils.loadCRLFromURL(url);
				} else {
					base = CertificateUtils.loadCerfificatesFromURL(url);
				}
				break;
			}
		}
		if (crl) {
			// revoked certificates loaded from directory or from a single file
			final CRL[] rvkDirCert = (CRL[]) CertificateUtils.loadCerfificatesFromDir(vCert, crl);
			final CRL[] rvkCert = new CRL[rvk.length + rvkDirCert.length];
			System.arraycopy(rvk, 0, rvkCert, 0, rvk.length);
			System.arraycopy(rvkDirCert, 0, rvkCert, rvk.length, rvkDirCert.length);
			return rvkCert;
		} else {
			// accepted certificates loaded from directory or from a single file
			final Certificate[] baseDirCert = (Certificate[]) CertificateUtils.loadCerfificatesFromDir(vCert, crl);
			final Certificate[] baseCert = new Certificate[base.length + baseDirCert.length];
			System.arraycopy(base, 0, baseCert, 0, base.length);
			System.arraycopy(baseDirCert, 0, baseCert, base.length, baseDirCert.length);
			return baseCert;
		}
	}

}
