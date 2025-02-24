package com.ontimize.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

public class TipWindow extends JDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(TipWindow.class);

	public static String titleKey = "TIP_WINDOW_TITLE";

	public static String nextKey = "NEXT_TIP";

	public static String previousKey = "PREVIOUS_TIP";

	public static String closeKey = "close";

	public static final String ICON_ID = "#icon=";

	public static String TIPS_FILE = "com/ontimize/gui/resources/tips.properties";

	protected JTextPane textPaneTip = new JTextPane();

	protected JLabel titleTip = new JLabel();

	protected ImageIcon tipIcon = null;

	protected JButton nextButton = new JButton(TipWindow.nextKey);

	protected JButton previousButton = new JButton(TipWindow.previousKey);

	protected JButton closeButton = new JButton(TipWindow.closeKey);

	protected List sortedTips = new Vector();

	protected List tipDescriptions = new Vector();

	protected int currentTipIndex = 0;

	protected Locale locale = Locale.getDefault();

	private TipWindow(final Frame parent, final Locale l) throws Exception {
		super(parent, TipWindow.titleKey, true);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		final String lang = l.getLanguage();
		final String country = l.getCountry();
		final String variant = l.getVariant();
		String sFile = null;
		final String sDefaultFile = sFile = TipWindow.TIPS_FILE;
		if (lang.equalsIgnoreCase("") || country.equalsIgnoreCase("")) {
			sFile = sDefaultFile;
		} else {
			final String sFileName = TipWindow.TIPS_FILE.substring(0, TipWindow.TIPS_FILE.lastIndexOf("."));
			final String sExtension = TipWindow.TIPS_FILE.substring(TipWindow.TIPS_FILE.lastIndexOf("."));
			sFile = sFileName + "_" + lang + "_" + country;
			if ((variant != null) && (variant.length() > 0)) {
				sFile = sFile + "_" + variant;
			}
			sFile = sFile + sExtension;
		}
		URL urlProp = this.getClass().getClassLoader().getResource(sFile);
		if (urlProp == null) {
			urlProp = this.getClass().getResource(sDefaultFile);
			if (urlProp == null) {
				throw new Exception("File not found: " + sFile);
			}
		}
		final InputStreamReader in = new InputStreamReader(urlProp.openStream());
		final LineNumberReader ri = new LineNumberReader(in);
		String sLine = ri.readLine();
		while (sLine != null) {
			final StringTokenizer st = new StringTokenizer(sLine, ";");
			String sTitle = null;
			String descr = null;
			if (st.hasMoreTokens()) {
				sTitle = st.nextToken();
			} else {
				sTitle = "";
			}
			if (st.hasMoreTokens()) {
				descr = st.nextToken();
			} else {
				descr = "";
			}
			this.sortedTips.add(this.sortedTips.size(), sTitle);
			this.tipDescriptions.add(this.tipDescriptions.size(), descr);
			sLine = ri.readLine();
		}
		ri.close();
		this.locale = l;
		this.getContentPane().setLayout(new GridBagLayout());
		this.textPaneTip.setEditable(false);
		this.textPaneTip.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.titleTip.setFont(this.titleTip.getFont().deriveFont((float) 14));
		final ImageIcon tipsIcon = ImageManager.getIcon(ImageManager.TIPS);
		if (tipsIcon != null) {
			this.tipIcon = tipsIcon;
		}
		if (this.tipIcon != null) {
			this.getContentPane()
			.add(new JLabel(this.tipIcon),
					new GridBagConstraints(0, 0, 1, 2, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
							new Insets(15, 15, 15, 15), 0, 0));
		}
		this.getContentPane()
		.add(this.titleTip, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0));

		final JScrollPane scroll = new JScrollPane(this.textPaneTip);
		scroll.setBorder(new LineBorder(Color.black));
		this.getContentPane()
		.add(scroll, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 5), 0, 0));
		final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(this.previousButton);
		buttonsPanel.add(this.nextButton);
		buttonsPanel.add(this.closeButton);
		this.previousButton.setMargin(new Insets(2, 4, 2, 4));
		final ImageIcon prevIcon = ImageManager.getIcon(ImageManager.PREV);
		if (prevIcon != null) {
			this.previousButton.setIcon(prevIcon);
		}
		final ImageIcon nextIcon = ImageManager.getIcon(ImageManager.NEXT);
		if (nextIcon != null) {
			this.nextButton.setIcon(nextIcon);
		}
		this.nextButton.setMargin(new Insets(2, 4, 2, 4));
		this.closeButton.setMargin(new Insets(2, 4, 2, 4));
		this.getContentPane()
		.add(buttonsPanel,
				new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.loadTips();
		this.textPaneTip.setOpaque(true);
		this.textPaneTip.setBackground(new Color(255, 255, 214));
		try {
			this.setResourceBundle(ResourceBundle.getBundle("com.ontimize.gui.resources.tips_bundle", l));
		} catch (final Exception e) {
			TipWindow.logger.error(null, e);
		}
		// pack();
		this.setSize(445, 275);
		this.setResizable(false);
		this.center();
		this.installListener();
	}

	private void center() {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width / 2) - (this.getWidth() / 2), (d.height / 2) - (this.getHeight() / 2));
	}

	private void loadTips() {
		// Select one
		if (this.sortedTips.isEmpty()) {
			this.titleTip.setText("No se han encontrado tips");
		} else {
			final double rand = Math.random();
			final int index = (int) (rand * this.sortedTips.size());
			this.currentTipIndex = index;
			this.titleTip.setText(this.sortedTips.get(index).toString());
			this.processDescription(this.tipDescriptions.get(index).toString());
		}
	}

	public static final TipWindow createGUITipsWindow(final Frame parent, final Locale l) throws Exception {
		final TipWindow tipWindow = new TipWindow(parent, l);
		tipWindow.setVisible(true);
		return tipWindow;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		try {
			if (res != null) {
				this.previousButton.setText(res.getString(TipWindow.previousKey));
			}
		} catch (final Exception e) {
			TipWindow.logger.error(null, e);
		}
		try {
			if (res != null) {
				this.nextButton.setText(res.getString(TipWindow.nextKey));
			}
		} catch (final Exception e) {
			TipWindow.logger.error(null, e);
		}
		try {
			if (res != null) {
				this.closeButton.setText(res.getString(TipWindow.closeKey));
			}
		} catch (final Exception e) {
			TipWindow.logger.error(null, e);
		}
		try {
			if (res != null) {
				this.setTitle(res.getString(TipWindow.titleKey));
			}
		} catch (final Exception e) {
			TipWindow.logger.error(null, e);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		try {
			this.setResourceBundle(ResourceBundle.getBundle("com.ontimize.gui.resources.tips_bundle", l));
		} catch (final Exception e) {
			TipWindow.logger.trace(null, e);
		}
	}

	protected void installListener() {
		this.previousButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (TipWindow.this.currentTipIndex == 0) {
					TipWindow.this.currentTipIndex = TipWindow.this.sortedTips.size() - 1;
				} else {
					TipWindow.this.currentTipIndex--;
				}
				TipWindow.this.titleTip
				.setText(TipWindow.this.sortedTips.get(TipWindow.this.currentTipIndex).toString());
				TipWindow.this
				.processDescription(TipWindow.this.tipDescriptions.get(TipWindow.this.currentTipIndex).toString());
			}
		});
		this.nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (TipWindow.this.currentTipIndex == (TipWindow.this.sortedTips.size() - 1)) {
					TipWindow.this.currentTipIndex = 0;
				} else {
					TipWindow.this.currentTipIndex++;
				}
				TipWindow.this.titleTip
				.setText(TipWindow.this.sortedTips.get(TipWindow.this.currentTipIndex).toString());
				TipWindow.this
				.processDescription(TipWindow.this.tipDescriptions.get(TipWindow.this.currentTipIndex).toString());
			}
		});
		this.closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				TipWindow.this.setVisible(false);
			}
		});
	}

	/**
	 * Process the description string (icons...)
	 * @param description
	 */
	protected void processDescription(final String description) {
		int iconIdIndex = description.indexOf(TipWindow.ICON_ID);
		if (iconIdIndex >= 0) {
			final StringBuilder sb = new StringBuilder();
			final Map hIconsIndex = new Hashtable();
			String sNextString = description;
			while (iconIdIndex >= 0) {
				final String sPreviousString = sNextString.substring(0, iconIdIndex);
				sNextString = description.substring(iconIdIndex + TipWindow.ICON_ID.length(), description.length());
				final int padIndex = sNextString.indexOf("#");
				if (padIndex <= 0) {
					break;
				}
				final String iconString = sNextString.substring(0, padIndex);
				hIconsIndex.put(new Integer(iconIdIndex), iconString);
				sNextString = sNextString.substring(padIndex + 1, sNextString.length());
				// Add the text to the string buffer
				sb.append(sPreviousString);
				// For the next iteration use the next string
				iconIdIndex = sNextString.indexOf(TipWindow.ICON_ID);
			}
			sb.append(sNextString);
			// Set the text
			this.textPaneTip.setText(sb.toString());
			// Now put the icons
			final Enumeration enumKeys = Collections.enumeration(hIconsIndex.keySet());
			while (enumKeys.hasMoreElements()) {
				final Integer index = (Integer) enumKeys.nextElement();
				final String iconString = (String) hIconsIndex.get(index);
				// Create the icon
				final URL urlIco = this.getClass().getClassLoader().getResource(iconString);
				if (urlIco == null) {
					TipWindow.logger.debug(this.getClass().toString() + ". Icon not found: " + iconString);
				} else {
					this.textPaneTip.setEditable(true);
					this.textPaneTip.setCaretPosition(index.intValue());
					this.textPaneTip.insertIcon(new ImageIcon(urlIco));
					this.textPaneTip.setEditable(false);
				}
			}
		} else {
			this.textPaneTip.setText(description);
		}
	}

	private Style initStyle(final String style, final ImageIcon icon) {
		// Initialize some styles.
		final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		final Style regular = this.textPaneTip.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		final Style s = this.textPaneTip.addStyle(style, regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
		StyleConstants.setIcon(s, icon);
		return s;
	}

}
