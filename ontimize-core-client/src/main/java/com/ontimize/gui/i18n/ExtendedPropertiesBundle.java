package com.ontimize.gui.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.jee.common.gui.i18n.IDatabaseBundleManager;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.InitialContext;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.extend.EntitiesPropertiesParser;
import com.ontimize.jee.common.util.extend.OrderProperties;

public class ExtendedPropertiesBundle extends ResourceBundle implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(ExtendedPropertiesBundle.class);

	public static final String INCLUDE_KEY = "@include";

	private static Map cache = new Hashtable();

	protected static Map databaseCache;

	protected static Map extendedBundleCache = new Hashtable();

	protected static List<String> moduleResources = new ArrayList<String>();

	private static class CacheKey implements Serializable {

		protected String baseName = null;

		protected Locale locale = null;

		public CacheKey(final String baseName, final Locale l) {
			this.baseName = baseName;
			this.locale = l;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof CacheKey) {
				if (this.baseName.equals(((CacheKey) o).baseName) && this.locale.equals(((CacheKey) o).locale)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.baseName.hashCode() + this.locale.hashCode();
		}

		@Override
		public String toString() {
			return this.baseName + "_" + this.locale.toString();
		}

		public Locale getLocale() {
			return this.locale;
		}

	}

	private ResourceBundle[] res = null;

	private List cachedKeys = null;

	public ResourceBundle[] getBundles() {
		return this.res;
	}

	protected void setBundles(final ResourceBundle[] bundles) {
		this.res = bundles;
	}

	protected void addBundle(final ResourceBundle bundle) {
		final int size = this.res != null ? this.res.length : 0;
		final ResourceBundle[] result = new ResourceBundle[size + 1];
		if (this.res != null) {
			System.arraycopy(this.res, 0, result, 0, this.res.length);
		}
		result[size] = bundle;
		this.res = result;
	}

	protected void setBundles(final List bundles) {
		this.res = new ResourceBundle[bundles.size()];
		for (int i = 0; i < bundles.size(); i++) {
			this.res[i] = (ResourceBundle) bundles.get(i);
		}

	}

	private ExtendedPropertiesBundle(final List bundles) {
		this.res = new ResourceBundle[bundles.size()];
		for (int i = 0; i < bundles.size(); i++) {
			this.res[i] = (ResourceBundle) bundles.get(i);
		}
	}

	@Override
	public Locale getLocale() {
		if ((this.res == null) || (this.res.length == 0) || (this.res[0] == null)) {
			return super.getLocale();
		}
		return this.res[0].getLocale();
	}

	@Override
	public Enumeration getKeys() {
		if (this.cachedKeys == null) {
			final List keys = new Vector();
			for (int i = 0; i < this.res.length; i++) {
				final ResourceBundle r = this.res[i];
				if (r == null) {
					continue;
				}
				final Enumeration ks = r.getKeys();
				while (ks.hasMoreElements()) {
					final Object k = ks.nextElement();
					if (!keys.contains(k)) {
						keys.add(k);
					}
				}
			}
			this.cachedKeys = keys;
		}
		return Collections.enumeration(this.cachedKeys);
	}

	@Override
	public Object handleGetObject(final String key) {
		if ((key == null) || "".equals(key)) {
			return "";
		}

		for (final ResourceBundle r : this.res) {
			try {
				if (r.containsKey(key)) {
					return r.getObject(key);
				}
			} catch (final Exception e) {
				ExtendedPropertiesBundle.logger.trace(null, e);
			}
		}
		// Review comment svn - revision 2430.
		// TODO Review Xestre.
		return key;
		// return null;
	}


	public static ResourceBundle getExtendedBundle(final String baseName, final Locale l) {

		final CacheKey key = new CacheKey(baseName, l);
		if (ExtendedPropertiesBundle.cache.containsKey(key)) {
			ExtendedPropertiesBundle.logger.debug("Bundle {} in cache", baseName);
			final ExtendedPropertiesBundle extendedPropertiesBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache
					.get(key);
			return extendedPropertiesBundle;
		}
		final List v = ExtendedPropertiesBundle.getBundles(baseName, l);
		final ExtendedPropertiesBundle b = new ExtendedPropertiesBundle(v);
		ExtendedPropertiesBundle.cache.put(key, b);
		return b;
	}

	private static List getBundles(final String baseName, final Locale l) {
		final List v = new Vector();

		if (ExtendedPropertiesBundle.useDatabaseBundle) {
			final Object object = ExtendedPropertiesBundle.databaseCache.get(new CacheKey(baseName, l));
			if (object == null) {
				final Map allDatabaseResourceBundles = ExtendedPropertiesBundle.getAllDatabaseResourceBundles(l);
				if (allDatabaseResourceBundles != null) {
					ExtendedPropertiesBundle.databaseCache.putAll(allDatabaseResourceBundles);
				}
			}
		}

		final Object extendedBundle = ExtendedPropertiesBundle.extendedBundleCache.get(new CacheKey(baseName, l));
		if (extendedBundle == null) {
			final Map extendedResourceBundle = ExtendedPropertiesBundle.getExtendedResourceBundles(baseName, l);
			if (extendedResourceBundle != null) {
				ExtendedPropertiesBundle.extendedBundleCache.putAll(extendedResourceBundle);
			}
		}

		ExtendedPropertiesBundle.getBundles(baseName, l, v);
		// Include modules bundle...
		for (final String moduleBundle : ExtendedPropertiesBundle.moduleResources) {
			ExtendedPropertiesBundle.getBundles(moduleBundle, l, v);
		}
		return v;
	}

	private static void getBundles(final String baseName, final Locale l, final List dest) {
		ExtendedPropertiesBundle.logger.debug("Loading bundle: {}", baseName);
		// If the bundle is in cache then put it directly
		ResourceBundle bundle = null;
		final CacheKey key = new CacheKey(baseName, l);

		if (!ExtendedPropertiesBundle.cache.containsKey(key)) {
			try {
				if ((ExtendedPropertiesBundle.databaseCache != null)
						&& ExtendedPropertiesBundle.databaseCache.containsKey(key)) {
					dest.add(ExtendedPropertiesBundle.databaseCache.get(key));
				}
				if ((ExtendedPropertiesBundle.extendedBundleCache != null)
						&& ExtendedPropertiesBundle.extendedBundleCache.containsKey(key)) {
					dest.add(ExtendedPropertiesBundle.extendedBundleCache.get(key));
					ExtendedPropertiesBundle
					.checkImports((ResourceBundle) ExtendedPropertiesBundle.extendedBundleCache.get(key), l, dest);
				}
				bundle = ResourceBundle.getBundle(baseName, l);
			} catch (final Exception e) {
				ExtendedPropertiesBundle.logger.error("Error loading bundle: {}", e.getMessage(), e);
			}
		} else {
			ExtendedPropertiesBundle.logger.debug("Bundle {} in cache", key);
			bundle = (ResourceBundle) ExtendedPropertiesBundle.cache.get(key);
		}

		dest.add(bundle);
		ExtendedPropertiesBundle.checkImports(bundle, l, dest);

	}

	protected static void checkImports(final ResourceBundle bundle, final Locale l, final List dest) {
		if ((bundle instanceof PropertyResourceBundle) || (bundle instanceof ExtendedPropertiesResourceBundle)) {
			// Search the key '@include'
			try {
				final String sIncludeBundle = bundle.getString(ExtendedPropertiesBundle.INCLUDE_KEY);
				if ((sIncludeBundle != null) && (sIncludeBundle.indexOf(';') > 0)) {
					final StringTokenizer st = new StringTokenizer(sIncludeBundle, ";");
					while (st.hasMoreTokens()) {
						final String bundleI = st.nextToken();
						ExtendedPropertiesBundle.logger.debug("Including: {}", bundleI);
						ExtendedPropertiesBundle.getBundles(bundleI, l, dest);
						final Map extendedResourcesBundle = ExtendedPropertiesBundle.getExtendedResourceBundles(bundleI,
								l);
						// ExtendedPropertiesBundle.extendedBundleCache.putAll(extendedResourcesBundle);
						if (extendedResourcesBundle != null) {
							dest.add(extendedResourcesBundle.get(new CacheKey(bundleI, l)));
						}

					}
				} else {
					ExtendedPropertiesBundle.logger.debug("Including: {}", sIncludeBundle);
					ExtendedPropertiesBundle.getBundles(sIncludeBundle, l, dest);
				}
			} catch (final Exception e) {
				ExtendedPropertiesBundle.logger.trace("{}", e.getMessage(), e);
			}
		}
	}

	private static Map getExtendedResourceBundles(final String baseName, final Locale l) {
		Properties extendedBundleProperties = new Properties();

		final StringBuilder builder = new StringBuilder();
		final String baseNameWithSlashes = baseName.replace('.', '/');
		builder.append(baseNameWithSlashes);
		builder.append("_extends_");
		builder.append(l);
		builder.append(".properties");
		final String _extendsBundle = builder.toString();

		try {
			final Enumeration<URL> _extends = EntitiesPropertiesParser.class.getClassLoader()
					.getResources(builder.toString());

			if (_extends.hasMoreElements()) {

				final EntitiesPropertiesParser parser = new EntitiesPropertiesParser();
				final List<OrderProperties> extendsResourceBundle = new ArrayList<OrderProperties>();

				while (_extends.hasMoreElements()) {
					final Properties extendedBundle = new Properties();
					final URL urlStream = _extends.nextElement();
					final InputStream stream = urlStream.openStream();
					extendedBundle.clear();
					extendedBundle.load(stream);
					final int index = parser.parseExtendPropertiesOrder(extendedBundle);
					extendsResourceBundle.add(new OrderProperties(index, extendedBundle));
				}

				Collections.sort(extendsResourceBundle);
				Properties extendedBundle = new Properties();

				for (final OrderProperties entry : extendsResourceBundle) {
					extendedBundle = entry.getProperties();
					extendedBundleProperties = parser.parseProperties(extendedBundleProperties, extendedBundle);
					ExtendedPropertiesBundle.logger.debug(
							"Resource bundle \"{}\" with locale \"{}\" extended, Load order -> {}", baseName, l,
							entry.getIndex());
				}

				final Map toRet = new Hashtable();
				final ExtendedPropertiesResourceBundle extendedProperties = new ExtendedPropertiesResourceBundle(
						new Hashtable(extendedBundleProperties), l);
				toRet.put(new CacheKey(baseName, l), extendedProperties);

				return toRet;
			}
		} catch (final IOException e) {
			ExtendedPropertiesBundle.logger.error(
					"Resource bundle \"{}\" with locale \"{}\" can't be extended. ERROR: {}", baseName, l,
					e.getMessage(), e);
		}

		return null;
	}

	public static int getSizeInMemory() {
		final List vCounter = new Vector();
		final Enumeration enumKeys = Collections.enumeration(ExtendedPropertiesBundle.cache.keySet());
		int iSize = 0;
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final ResourceBundle ress = (ResourceBundle) ExtendedPropertiesBundle.cache.get(oKey);
			if (ress instanceof ExtendedPropertiesBundle) {
				for (int j = 0; j < ((ExtendedPropertiesBundle) ress).getBundles().length; j++) {
					final ResourceBundle resourceBundle = ((ExtendedPropertiesBundle) ress).getBundles()[j];
					if (resourceBundle == null) {
						continue;
					}
					boolean counted = false;
					for (int i = 0; i < vCounter.size(); i++) {
						if (resourceBundle == vCounter.get(i)) {
							counted = true;
						}
					}
					if (!counted) {
						final Enumeration keys = resourceBundle.getKeys();
						while (keys.hasMoreElements()) {
							final Object key = keys.nextElement();
							String sValue = resourceBundle.getString(key.toString());
							if (sValue == null) {
								sValue = "";
							}
							iSize = iSize + (key.toString().length() * 2) + (sValue.length() * 2);
						}
						vCounter.add(resourceBundle);
					}
				}
			} else {
				boolean counted = false;
				for (int i = 0; i < vCounter.size(); i++) {
					if (ress == vCounter.get(i)) {
						counted = true;
					}
				}
				if (!counted) {
					final Enumeration keys = ress.getKeys();
					while (keys.hasMoreElements()) {
						final Object key = keys.nextElement();
						String sValue = ress.getString(key.toString());
						if (sValue == null) {
							sValue = "";
						}
						iSize = iSize + (key.toString().length() * 2) + (sValue.length() * 2);
					}
					vCounter.add(ress);
				}
			}
		}
		return iSize;
	}

	protected static UtilReferenceLocator locator;

	protected static String dbBundleManagerName;

	protected static boolean useDatabaseBundle = false;

	public static void useDatabaseBundle(final UtilReferenceLocator locator, final String databaseBundleManagerName) {
		ExtendedPropertiesBundle.locator = locator;
		ExtendedPropertiesBundle.dbBundleManagerName = databaseBundleManagerName;

		ExtendedPropertiesBundle.databaseCache = new Hashtable();
		ExtendedPropertiesBundle.extendedBundleCache = new Hashtable();
		ExtendedPropertiesBundle.useDatabaseBundle = true;

		if (ExtendedPropertiesBundle.cache != null) {
			// Include the database bundle if exist in the existing resources
			final Enumeration keys = Collections.enumeration(ExtendedPropertiesBundle.cache.keySet());
			while (keys.hasMoreElements()) {
				final CacheKey key = (CacheKey) keys.nextElement();
				final Map allDatabaseResourceBundles = ExtendedPropertiesBundle
						.getAllDatabaseResourceBundles(key.locale);

				if (allDatabaseResourceBundles != null) {
					ExtendedPropertiesBundle.databaseCache.putAll(allDatabaseResourceBundles);
				}

				final ExtendedPropertiesBundle propBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache
						.remove(key);
				final List resourceBundleList = ExtendedPropertiesBundle.getBundles(key.baseName, key.locale);
				propBundle.setBundles(resourceBundleList);
				ExtendedPropertiesBundle.cache.put(key, propBundle);
			}
		}
	}

	// Obtener el primer locale para el init
	public static Locale getDefaultCacheLocale() {
		if (ExtendedPropertiesBundle.cache != null) {
			final Enumeration keys = Collections.enumeration(ExtendedPropertiesBundle.cache.keySet());
			while (keys.hasMoreElements()) {
				final CacheKey key = (CacheKey) keys.nextElement();
				return key.locale;
			}
		}
		return null;
	}

	protected static ResourceBundle getDatabaseBundle(final String resourcesFileName, final Locale locale) {
		try {
			if (ExtendedPropertiesBundle.useDatabaseBundle && (ExtendedPropertiesBundle.locator != null)
					&& (((EntityReferenceLocator) ExtendedPropertiesBundle.locator)
							.getSessionId() >= 0)) {
				final IDatabaseBundleManager remoteReference = (IDatabaseBundleManager) ExtendedPropertiesBundle.locator
						.getRemoteReference(ExtendedPropertiesBundle.dbBundleManagerName,
								((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				final ResourceBundle bundle = remoteReference.getBundle(resourcesFileName, locale,
						((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				if (bundle != null) {
					return bundle;
				}
			}
		} catch (final Exception e) {
			ExtendedPropertiesBundle.logger.error("{}", e.getMessage(), e);
		}
		return null;
	}

	protected static Map getAllDatabaseResourceBundles(final Locale locale) {
		try {
			if (ExtendedPropertiesBundle.useDatabaseBundle && (ExtendedPropertiesBundle.locator != null)
					&& (((EntityReferenceLocator) ExtendedPropertiesBundle.locator)
							.getSessionId() >= 0)) {

				Map bundles = null;
				if ((((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext() != null)
						&& ((ClientReferenceLocator) ExtendedPropertiesBundle.locator)
						.getInitialContext()
						.containsKey(InitialContext.ALL_RESOURCES_BUNDLES)) {
					bundles = (Map) ((ClientReferenceLocator) ExtendedPropertiesBundle.locator)
							.getInitialContext()
							.get(InitialContext.ALL_RESOURCES_BUNDLES);
				} else {
					final IDatabaseBundleManager remoteReference = (IDatabaseBundleManager) ExtendedPropertiesBundle.locator
							.getRemoteReference(ExtendedPropertiesBundle.dbBundleManagerName,
									((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
					bundles = remoteReference.getAllResourceBundles(locale,
							((EntityReferenceLocator) ExtendedPropertiesBundle.locator).getSessionId());
				}

				if ((bundles != null) && !bundles.isEmpty()) {
					final Enumeration keys = Collections.enumeration(bundles.keySet());
					final Map result = new Hashtable();
					while (keys.hasMoreElements()) {
						final String baseName = (String) keys.nextElement();
						result.put(new CacheKey(baseName, locale), bundles.get(baseName));
					}
					return result;
				}
			}
		} catch (final Exception e) {
			ExtendedPropertiesBundle.logger.error("{}", e.getMessage(), e);
		}
		return null;
	}

	public static void addModuleResourceBundle(final String resourceBundle) {
		if ((resourceBundle == null) || (resourceBundle.length() == 0)) {
			return;
		}

		final StringTokenizer st = new StringTokenizer(resourceBundle, ";");
		while (st.hasMoreTokens()) {
			final String bundle = st.nextToken();
			if (!ExtendedPropertiesBundle.moduleResources.contains(bundle)) {
				ExtendedPropertiesBundle.moduleResources.add(bundle);
				// Check if exits bundles already cache.
				final Enumeration<CacheKey> keys = Collections.enumeration(ExtendedPropertiesBundle.cache.keySet());
				while (keys.hasMoreElements()) {
					final CacheKey key = keys.nextElement();
					final ExtendedPropertiesBundle extendedPropertiesBundle = (ExtendedPropertiesBundle) ExtendedPropertiesBundle.cache
							.get(key);
					final ResourceBundle bundleToAdd = ResourceBundle.getBundle(bundle, key.getLocale());
					extendedPropertiesBundle.addBundle(bundleToAdd);
					ExtendedPropertiesBundle.logger.debug("{} {} has been added to {}", bundle, key.getLocale(), key);
				}
			} else {
				ExtendedPropertiesBundle.logger.warn("The {} bundle already exists in module resource list", bundle);
			}
		}
	}

	public static void reloadBundle() {
		if (ExtendedPropertiesBundle.cache != null) {
			ExtendedPropertiesBundle.cache.clear();
		}
		if (ExtendedPropertiesBundle.useDatabaseBundle) {
			ExtendedPropertiesBundle.databaseCache.clear();
		}

		if (ExtendedPropertiesBundle.extendedBundleCache != null) {
			ExtendedPropertiesBundle.extendedBundleCache.clear();
		}

		if ((((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext() != null)
				&& ((ClientReferenceLocator) ExtendedPropertiesBundle.locator)
				.getInitialContext()
				.containsKey(InitialContext.ALL_RESOURCES_BUNDLES)) {
			((ClientReferenceLocator) ExtendedPropertiesBundle.locator).getInitialContext()
			.remove(InitialContext.ALL_RESOURCES_BUNDLES);
		}

		final ResourceBundle extendedBundle = ExtendedPropertiesBundle.getExtendedBundle(
				((MainApplication) ApplicationManager.getApplication()).getResourcesFileName(),
				ApplicationManager.getApplication().getResourceBundle().getLocale());
		ApplicationManager.getApplication().setResourceBundle(extendedBundle);
	}

	public static boolean isUsingDatabaseBundle() {
		return ExtendedPropertiesBundle.useDatabaseBundle;
	}

	public static String getDbBundleManagerName() {
		return ExtendedPropertiesBundle.dbBundleManagerName;
	}

}
