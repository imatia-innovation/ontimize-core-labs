package com.ontimize.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component to manage reports requests. All report descriptions are stored in a properties
 *
 * @deprecated
 */

@Deprecated
public class Reports implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Reports.class);

	public static boolean DEBUG = false;

	Properties prop = null;

	Frame frame = null;

	Map reportKeysValues = new Hashtable();

	String reportKey = "";

	public Reports(final URL urlPropertiesFile, final Frame parentFrame) {
		this.frame = parentFrame;
		// read the properties
		final URL uRLProp = urlPropertiesFile;
		if (uRLProp == null) {
			if (Reports.DEBUG) {
				Reports.logger.debug("URL report properties file is null");
			}
		} else {
			// Read the properties
			this.prop = new Properties();
			try {
				this.prop.load(uRLProp.openStream());
			} catch (final Exception e) {
				Reports.logger.error(this.getClass().toString() + e.getMessage(), e);
			}
		}
	}

	/**
	 * Get the selected report key. If no report is selected then return an empty string
	 */
	public String selectedReport() {
		// Show the keys
		final JDialog d = new JDialog(this.frame, true);
		this.reportKey = "";
		final Collection c = this.prop.values();
		if (c.isEmpty()) {
			return this.reportKey;
		}
		final JList list = new JList(new Vector(c));
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent evento) {
				if (evento.getClickCount() == 2) {
					if (list.getSelectedIndex() >= 0) {
						// Get the object, that is the description and search in
						// the
						// keys to request the report
						final Object oDescription = list.getSelectedValue();
						if (oDescription == null) {
							return;
						} else {
							final String descr = oDescription.toString();
							final Enumeration enumKeys = Reports.this.prop.keys();
							while (enumKeys.hasMoreElements()) {
								final Object oKey = enumKeys.nextElement();
								final Object oValue = Reports.this.prop.get(oKey);
								if (oValue.equals(descr)) {
									d.dispose();
									Reports.this.reportKey = oKey.toString();
									break;
								}
							}
						}
					}
				}
			}
		});
		final JScrollPane scroll = new JScrollPane(list);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.getContentPane().add(scroll);
		d.pack();
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		d.setLocation((dim.width - d.getSize().width) / 2, (dim.height - d.getSize().height) / 2);
		d.setVisible(true);
		return this.reportKey;
	}

	@Override
	public void free() {
		this.frame = null;
		this.prop.clear();
		if (Reports.DEBUG) {
			Reports.logger.debug("Informes Liberado");
		}
	}

}
