package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

public class NavigationMenu extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(NavigationMenu.class);

	protected static final String IMAGEBASEPATH = "imagebasepath";

	protected static final String CENTERIMAGE = "centerimage";

	protected static final String IMG = "img";

	protected static final String FIXIMG = "fiximg";

	protected static final String FIXIMGPOSITION = "fiximgposition";

	protected static final String MOSAIC = "mosaic";

	protected static final String BASICRENDERER = "basicrenderer";

	//
	// Constants used to specify a position.
	//
	/**
	 * Compass-direction North (up).
	 */
	public static final String NORTH = "north";

	/**
	 * Compass-direction north-east (upper right).
	 */
	public static final String NORTH_EAST = "north_east";

	/**
	 * Compass-direction east (right).
	 */
	public static final String EAST = "east";

	/**
	 * Compass-direction south-east (lower right).
	 */
	public static final String SOUTH_EAST = "south_east";

	/**
	 * Compass-direction south (down).
	 */
	public static final String SOUTH = "south";

	/**
	 * Compass-direction south-west (lower left).
	 */
	public static final String SOUTH_WEST = "south_west";

	/**
	 * Compass-direction west (left).
	 */
	public static final String WEST = "west";

	/**
	 * Compass-direction north west (upper left).
	 */
	public static final String NORTH_WEST = "north_west";

	/**
	 * Identification of the "NavigationMenu" XML tag.
	 */
	protected static final String ROOT_XML = "NavigationMenu";

	/**
	 * Identification of the "MenuGroup" XML tag.
	 */
	protected static final String MENUGROUP = "MenuGroup";

	/**
	 * Identification of the "header" XML tag.
	 */
	protected static final String HEADER = "header";

	/**
	 * Identification of the "options" XML tag.
	 */
	protected static final String OPTIONS = "options";

	/**
	 * Identification of the "icons" XML tag.
	 */
	protected static final String ICONS = "icons";

	/**
	 * Identification of the "x" XML tag.
	 */
	protected static final String X = "x";

	/**
	 * Identification of the "y" XML tag.
	 */
	protected static final String Y = "y";

	/**
	 * Identification of the "width" XML tag.
	 */
	protected static final String WIDTH = "width";

	/**
	 * Identification of the "height" XML tag.
	 */
	protected static final String HEIGHT = "height";

	/**
	 * Identification of the "bgheader" XML tag.
	 */
	protected static final String BGHEADER = "bgHeader";

	/**
	 * Identification of the "bgbody" XML tag.
	 */
	protected static final String BGBODY = "bgBody";

	/**
	 * Identification of the "fg" XML tag.
	 */
	protected static final String FOREGROUND = "fg";

	/**
	 * Identification of the "fgheader" XML tag.
	 */
	protected static final String FOREGROUNDHEADER = "fgheader";

	/**
	 * Identification of the "bordercolor" XML tag.
	 */
	protected static final String BORDERCOLOR = "bordercolor";

	/**
	 * Identification of the "menuitemclass" XML tag.
	 */
	protected static final String MENUITEMCLASS = "menuitemclass";

	/**
	 * Identification of the "headerheight" XML tag.
	 */
	protected static final String HEADERHEIGHT = "headerheight";

	/**
	 * Identification of the "borderclass" XML tag.
	 */
	protected static final String BORDERCLASS = "borderclass";

	/**
	 * Identification of the "opaque" XML tag.
	 */
	protected static final String OPAQUE = "opaque";

	/**
	 * Identification of the "dragenable" XML tag.
	 */
	protected static final String DRAGENABLE = "dragenabled";

	/**
	 * Identification of the "dragallmenu" XML tag.
	 */
	protected static final String DRAGALLMENU = "dragallmenu";

	/**
	 * Identification of the "separator" XML tag.
	 */
	protected static final String SEPARATOR = "separator";

	/**
	 * Identification of the "border" XML tag.
	 */
	protected static final String BORDER = "border";

	protected static final String MENU_GROUP_HEADER_ICON = "headericon";

	protected static final String MENUGROUPCLASS = "menugroupclass";

	private static final Class[] params = { String.class, String[].class, ImageIcon[].class, int.class, int.class,
			int.class, int.class, Color.class, Color.class, Color.class,
			Color.class };

	/**
	 * Message error.
	 */
	protected static final String ERROR_MESSAGE = "Parameter not found: ";

	public static final String GRAY = "gray";

	public static final String BLACK = "black";

	public static final String BLUE = "blue";

	public static final String RED = "red";

	public static final String YELLOW = "yellow";

	public static final String CYAN = "cyan";

	public static final String DARKGRAY = "darkgray";

	public static final String GREEN = "green";

	public static final String LIGHTGRAY = "lightgray";

	public static final String MAGENTA = "magenta";

	public static final String ORANGE = "orange";

	public static final String PINK = "pink";

	public static final String WHITE = "white";

	public static final String DARKBLUE = "darkblue";

	/**
	 * List that contains all the menuGroups contained into the NavigatioMenu.
	 */
	protected List menuList = new ArrayList();

	/**
	 * Background image of the NavigationMenu.
	 */
	protected java.awt.Image bgImage = null;

	/**
	 * Background image if it is wanted to fit into the NavigationMenu. It could be used as a centered
	 * corporative logo.
	 */
	protected java.awt.Image fixImage = null;

	/**
	 * Position to place fixImage. It could be: north, south, east, west, north_east, south_east,
	 * north_west, south_west.
	 */
	protected int fixImagePosition = -1;

	/**
	 * Parameter that indicates if it is wanted to center the background image into the NavigationMenu.
	 */
	protected boolean centerImage = false;

	protected boolean mosaic = false;

	/**
	 * String that contains the base search route of all the images and icons of the NavigationMenu.
	 */
	protected String imageBasePath = null;

	/**
	 * Width of the NavigationMenu background image.
	 */
	protected int width = -1;

	/**
	 * Height of the NavigationMenu background image.
	 */
	protected int height = -1;

	/**
	 * Parameter that indicates if basic renderer is used.
	 */
	protected static boolean useBasicRenderer = false;

	/**
	 * Renderer of the MenuItem.
	 */
	protected MenuItemRenderer menuItemRenderer = new DefaultMenuItemRenderer();

	/**
	 * Renderer of the Header.
	 */
	protected MenuHeaderRenderer menuHeaderRenderer = new DefaultMenuHeaderRenderer();

	/**
	 * Constructs a new NavigationMenu specifying the background image and its dimensions.
	 * @param bgImage The background image of the NavigationMenu.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 */
	public NavigationMenu(final java.awt.Image bgImage, final int width, final int height) {
		this.bgImage = bgImage;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a new NavigationMenu specifying the background image, the logo image and its
	 * dimensions.
	 * @param bgImage The background image of the NavigationMenu.
	 * @param imageLogo The logo image of the NavigationMenu.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 */
	public NavigationMenu(final java.awt.Image bgImage, final java.awt.Image imageLogo, final int width, final int height) {
		this.bgImage = bgImage;
		this.fixImage = imageLogo;
		this.width = width;
		this.height = height;
	}

	/**
	 * Default constructor. It sets null layout to the NavigationMenu.
	 * @throws Exception
	 */
	public NavigationMenu() throws Exception {
		this.setLayout(null);
	}

	/**
	 * Constructs a new NavigationMenu. The characteristics and parameters are read from an XML
	 * Document.
	 * @param xml The XML Document that contains the configuration parameters.
	 * @throws Exception
	 */
	public NavigationMenu(final URL xml) throws Exception {
		this.setLayout(null);
		this.parse(xml);
	}

	/**
	 * Establishes the MenuItem renderer.
	 * @param menuItemRenderer The MenuItem renderer.
	 */
	public void setMenuItemRenderer(final MenuItemRenderer menuItemRenderer) {
		this.menuItemRenderer = menuItemRenderer;

		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroup mg = (MenuGroup) this.menuList.get(i);
			mg.setMenuItemRenderer(menuItemRenderer);
		}
	}

	/**
	 * Establishes the MenuHeader renderer.
	 * @param menuHeaderRenderer The MenuHeader renderer.
	 */
	public void setMenuHeaderRenderer(final MenuHeaderRenderer menuHeaderRenderer) {
		this.menuHeaderRenderer = menuHeaderRenderer;

		for (int i = 0; i < this.menuList.size(); i++) {
			final MenuGroup mg = (MenuGroup) this.menuList.get(i);
			mg.setMenuHeaderRenderer(menuHeaderRenderer);
		}
	}

	/**
	 * This method obtains the preferred size of the background image of the NavigationMenu.
	 * @return a <code>Dimension</code> object with the background image dimensions.
	 */
	@Override
	public Dimension getPreferredSize() {
		if ((this.width >= 0) && (this.height >= 0)) {
			return new Dimension(this.width, this.height);
		} else if (this.bgImage != null) {
			return new Dimension(this.bgImage.getWidth(this), this.bgImage.getHeight(this));
		} else {
			return new Dimension(10, 10);
		}
	}

	@Override
	public Component getComponent(final int n) {
		return super.getComponent(n);
	}

	@Override
	public Component[] getComponents() {
		return super.getComponents();
	}

	/**
	 * This method adds the specified MenuGroup to the NavigationMenu.
	 * @param mg The MenuGroup object to be added.
	 */
	public void add(final MenuGroup mg) {
		super.add(mg);
		this.menuList.add(mg);
		mg.setMenuHeaderRenderer(this.menuHeaderRenderer);
		mg.setMenuItemRenderer(this.menuItemRenderer);
	}

	/**
	 * This method establishes the ResourceBundle of the NavigationMenu to all MenuGroups.
	 * @param bundle The ResourceBundle.
	 */
	public void setResourceBundle(final ResourceBundle bundle) {
		for (int i = 0; i < this.menuList.size(); i++) {
			((MenuGroup) this.menuList.get(i)).setResourceBundle(bundle);
		}
	}

	/**
	 * This method paints the NavigationMenu
	 * @param g The graphics.
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (this.bgImage != null) {

			if ((this.width >= 0) && (this.height >= 0)) {
				// Mosaic
				if (this.mosaic) {
					int xPos = 0;
					int yPos = 0;
					while (xPos < this.width) {
						while (yPos < this.height) {
							g.drawImage(this.bgImage, xPos, yPos, this.bgImage.getWidth(this),
									this.bgImage.getHeight(this), this);
							yPos = yPos + this.bgImage.getHeight(this);
						}
						xPos = xPos + this.bgImage.getWidth(this);
						yPos = 0;
					}
				} else {
					g.drawImage(this.bgImage, 0, 0, this.width, this.height, this);
				}
			} else {
				// Mosaic
				if (this.mosaic) {
					int xPos = 0;
					int yPos = 0;
					while (xPos < this.getWidth()) {
						while (yPos < this.getHeight()) {
							g.drawImage(this.bgImage, xPos, yPos, this.bgImage.getWidth(this),
									this.bgImage.getHeight(this), this);
							yPos = yPos + this.bgImage.getHeight(this);
						}
						xPos = xPos + this.bgImage.getWidth(this);
						yPos = 0;
					}
				} else {
					g.drawImage(this.bgImage, 0, 0, this.getWidth(), this.getHeight(), 0, 0,
							this.bgImage.getWidth(this), this.bgImage.getHeight(this), this);
				}
			}
		}

		if (this.fixImage != null) {
			if (this.centerImage) {
				final int fixImageWidth = this.fixImage.getWidth(null);
				final int fixImageHeight = this.fixImage.getHeight(null);
				final int navigationWidth = this.getWidth();
				final int navigationHeight = this.getHeight();
				final int posX = (navigationWidth - fixImageWidth) / 2;
				final int posY = (navigationHeight - fixImageHeight) / 2;
				g.drawImage(this.fixImage, posX < 0 ? 0 : posX, posY < 0 ? 0 : posY, this.fixImage.getWidth(this),
						this.fixImage.getHeight(this), this);
			} else if (this.fixImagePosition != -1) {
				this.paintFixImageToPosition(g);
			} else {
				g.drawImage(this.fixImage, 0, 0, this.fixImage.getWidth(this), this.fixImage.getHeight(this), this);
			}
		}
	}

	protected void paintFixImageToPosition(final Graphics g) {
		if ((this.fixImage != null) && (this.fixImagePosition != -1)) {
			final int fixImageWidth = this.fixImage.getWidth(this);
			final int fixImageHeight = this.fixImage.getHeight(this);
			final int navigationWidth = this.getWidth();
			final int navigationHeight = this.getHeight();

			int posX = 0;
			int posY = 0;
			switch (this.fixImagePosition) {
			case SwingConstants.NORTH:
				posX = (navigationWidth - fixImageWidth) / 2;
				posY = 0;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.SOUTH:
				posX = (navigationWidth - fixImageWidth) / 2;
				posY = navigationHeight - fixImageHeight;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.WEST:
				posX = 0;
				posY = (navigationHeight - fixImageHeight) / 2;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.EAST:
				posX = navigationWidth - fixImageWidth;
				posY = (navigationHeight - fixImageHeight) / 2;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.NORTH_EAST:
				posX = navigationWidth - fixImageWidth;
				posY = 0;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.SOUTH_WEST:
				posX = 0;
				posY = navigationHeight - fixImageHeight;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			case SwingConstants.SOUTH_EAST:
				posX = navigationWidth - fixImageWidth;
				posY = navigationHeight - fixImageHeight;
				g.drawImage(this.fixImage, posX, posY, fixImageWidth, fixImageHeight, this);
				break;
			default:// NORTH_WEST
			g.drawImage(this.fixImage, 0, 0, fixImageWidth, fixImageHeight, this);
			break;
			}
		}
	}

	/**
	 * This method parses the XML Document configuration to obtain all the parameters.
	 * @param xml The XML Document configuration.
	 * @throws Exception
	 */
	protected void parse(final URL xml) throws Exception {
		final DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(xml.openStream());
		final NodeList list = doc.getElementsByTagName(NavigationMenu.ROOT_XML);
		Node root = null;
		if (list.getLength() == 1) {
			root = list.item(0);
			final NamedNodeMap attributes = root.getAttributes();
			String imgUrl = null;
			imgUrl = this.parseAndSetBackgroundImage(attributes, imgUrl);
			if (imgUrl != null) {
				this.parseAndSetNodeHeight(attributes);
				this.parseAndSetNodeWidth(attributes);
			}
			this.parseAndSetFixedImage(attributes);
			this.parseAndSetFixImagePosition(attributes);
			this.parseAndSetCenterImagePosition(attributes);
			this.parseAndSetMosaic(attributes);
			this.parseAndSetImageBasePath(attributes);
			this.parseAndSetBasicRenderer(attributes);

		}
		if (root == null) {
			throw new IllegalArgumentException("Error in xml: " + NavigationMenu.ROOT_XML);
		}

		final NodeList listMenu = root.getChildNodes();

		for (int i = 0; i < listMenu.getLength(); i++) {
			final Node node = listMenu.item(i);
			if (this.isTag(node) && NavigationMenu.MENUGROUP.equals(node.getNodeName())) {
				final MenuGroup mG = this.createMenuGroup(node);
				this.add(mG);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetBasicRenderer(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.BASICRENDERER) != null) {
			NavigationMenu.useBasicRenderer = ParseUtils
					.getBoolean(attributes.getNamedItem(NavigationMenu.BASICRENDERER).getNodeValue(), false);
			if (NavigationMenu.useBasicRenderer) {
				this.setMenuHeaderRenderer(new BasicMenuHeaderRenderer());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetImageBasePath(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.IMAGEBASEPATH) != null) {
			final String imageBasePath = attributes.getNamedItem(NavigationMenu.IMAGEBASEPATH).getNodeValue();
			if (imageBasePath != null) {
				this.imageBasePath = imageBasePath;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetMosaic(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.MOSAIC) != null) {
			final String sMosaic = attributes.getNamedItem(NavigationMenu.MOSAIC).getNodeValue();
			if ((sMosaic != null) && (sMosaic.equalsIgnoreCase("yes") || sMosaic.equalsIgnoreCase("true"))) {
				this.mosaic = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetCenterImagePosition(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.CENTERIMAGE) != null) {
			final String center = attributes.getNamedItem(NavigationMenu.CENTERIMAGE).getNodeValue();
			if ((center != null) && (center.equalsIgnoreCase("yes") || center.equalsIgnoreCase("true"))) {
				this.centerImage = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetFixImagePosition(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.FIXIMGPOSITION) != null) {
			final String imgPosition = attributes.getNamedItem(NavigationMenu.FIXIMGPOSITION).getNodeValue();
			if (imgPosition != null) {
				if (NavigationMenu.NORTH.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.NORTH;
				} else if (NavigationMenu.NORTH_EAST.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.NORTH_EAST;
				} else if (NavigationMenu.SOUTH.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.SOUTH;
				} else if (NavigationMenu.SOUTH_WEST.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.SOUTH_WEST;
				} else if (NavigationMenu.SOUTH_EAST.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.SOUTH_EAST;
				} else if (NavigationMenu.EAST.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.EAST;
				} else if (NavigationMenu.WEST.equals(imgPosition)) {
					this.fixImagePosition = SwingConstants.WEST;
				} else {
					// Default value...
					this.fixImagePosition = SwingConstants.NORTH_WEST;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetFixedImage(final NamedNodeMap attributes) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.FIXIMG) != null) {
			final String imgUrl2 = attributes.getNamedItem(NavigationMenu.FIXIMG).getNodeValue();
			if (imgUrl2 != null) {
				final URL url = ImageManager.getIconURL(imgUrl2);
				this.fixImage = Toolkit.getDefaultToolkit().getImage(url);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetNodeWidth(final NamedNodeMap attributes) throws DOMException {
		final Node nodeWidth = attributes.getNamedItem(NavigationMenu.WIDTH);
		if (nodeWidth != null) {
			final String width = nodeWidth.getNodeValue();
			try {
				this.width = Integer.parseInt(width);
			} catch (final Exception e) {
				NavigationMenu.logger.trace(null, e);
				this.width = -1;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @throws DOMException
	 */
	protected void parseAndSetNodeHeight(final NamedNodeMap attributes) throws DOMException {
		final Node nodeHeight = attributes.getNamedItem(NavigationMenu.HEIGHT);
		if (nodeHeight != null) {
			final String height = nodeHeight.getNodeValue();
			try {
				this.height = Integer.parseInt(height);
			} catch (final Exception e) {
				NavigationMenu.logger.trace(null, e);
				this.height = -1;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #parse(URL)}
	 * @param attributes
	 * @param imgUrl
	 * @return
	 * @throws DOMException
	 */
	protected String parseAndSetBackgroundImage(final NamedNodeMap attributes, String imgUrl) throws DOMException {
		if (attributes.getNamedItem(NavigationMenu.IMG) != null) {
			imgUrl = attributes.getNamedItem(NavigationMenu.IMG).getNodeValue();
			if (imgUrl != null) {
				final URL url = ImageManager.getIconURL(imgUrl);
				this.bgImage = Toolkit.getDefaultToolkit().getImage(url);
			}
		}
		return imgUrl;
	}

	/**
	 * This method checks if the given node is an Element node of a DOM document or not.
	 * @param node The node to be checked.
	 * @return a <code>boolean</code>.
	 */
	public boolean isTag(final Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return true;
		} else {
			return false;
		}
	}

	protected Node getNode(final NamedNodeMap attributes, final String name) {
		Node currentNode = attributes.getNamedItem(name);
		if (currentNode == null) {
			currentNode = attributes.getNamedItem(name.toLowerCase());
		}
		return currentNode;
	}

	/**
	 * This method analyzes a "MenuGroup" node of the XML Document configuration to obtain all the
	 * parameter to build a new MenuGroup into the NavigationMenu.
	 * @param node The "MenuGroup" node of the XML Document configuration.
	 * @return a <code>MenuGroup</code> object.
	 * @throws Exception
	 */
	protected MenuGroup createMenuGroup(final Node node) throws Exception {
		final NamedNodeMap attributes = node.getAttributes();

		Node currentNode = this.getNode(attributes, NavigationMenu.HEADER);
		String header = null;
		if (currentNode != null) {
			header = currentNode.getNodeValue();
		} else {
			throw new IllegalArgumentException(NavigationMenu.HEADER + " parameter is mandatory");
		}

		String[] opts = null;
		currentNode = this.getNode(attributes, NavigationMenu.OPTIONS);
		if (currentNode != null) {
			final String options = currentNode.getNodeValue();
			final List li = new ArrayList();
			final StringTokenizer tokens = new StringTokenizer(options, ";");
			while (tokens.hasMoreTokens()) {
				li.add(tokens.nextToken());
			}
			opts = (String[]) li.toArray(new String[li.size()]);
		} else {
			throw new IllegalArgumentException(NavigationMenu.OPTIONS + " parameter is mandatory");
		}

		ImageIcon[] icons = null;
		currentNode = this.getNode(attributes, NavigationMenu.ICONS);
		if (currentNode != null) {
			final String sIcons = currentNode.getNodeValue();
			final List li = new ArrayList();
			final StringTokenizer tokens = new StringTokenizer(sIcons, ";");
			while (tokens.hasMoreTokens()) {
				if (this.imageBasePath != null) {
					final String str = this.imageBasePath + tokens.nextToken();
					li.add(str);
				} else {
					li.add(tokens.nextToken());
				}
			}
			icons = new ImageIcon[li.size()];
			for (int i = 0; i < li.size(); i++) {
				try {
					icons[i] = ImageManager.getIcon((String) li.get(i));
				} catch (final Exception e) {
					NavigationMenu.logger.error("Resource not found " + li.get(i), e);
				}
			}
		} else {
			icons = new ImageIcon[0];
		}

		currentNode = this.getNode(attributes, NavigationMenu.X);
		if (currentNode == null) {
			throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.X);
		}
		final int x = Integer.parseInt(currentNode.getNodeValue());

		currentNode = this.getNode(attributes, NavigationMenu.Y);
		if (currentNode == null) {
			throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.Y);
		}
		final int y = Integer.parseInt(currentNode.getNodeValue());

		currentNode = this.getNode(attributes, NavigationMenu.WIDTH);
		if (currentNode == null) {
			throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.WIDTH);
		}
		final int width = Integer.parseInt(currentNode.getNodeValue());

		currentNode = this.getNode(attributes, NavigationMenu.HEIGHT);
		if (currentNode == null) {
			throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.HEIGHT);
		}
		final int height = Integer.parseInt(currentNode.getNodeValue());

		Color bgHeader = Color.green;
		try {
			currentNode = this.getNode(attributes, NavigationMenu.BGHEADER);

			if (currentNode == null) {
				throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.BGHEADER);
			}
			bgHeader = NavigationMenu.parseColor(currentNode.getNodeValue());
		} catch (final Exception e) {
			NavigationMenu.logger.error(null, e);
		}

		Color bgBody = Color.white;
		try {
			currentNode = this.getNode(attributes, NavigationMenu.BGBODY);
			if (currentNode == null) {
				throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.BGBODY);
			}
			bgBody = NavigationMenu.parseColor(currentNode.getNodeValue());
		} catch (final Exception e) {
			NavigationMenu.logger.error(null, e);
		}

		Color fg = Color.black;
		try {
			currentNode = this.getNode(attributes, NavigationMenu.FOREGROUND);
			if (currentNode == null) {
				throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.FOREGROUND);
			}
			fg = NavigationMenu.parseColor(currentNode.getNodeValue());
		} catch (final Exception e) {
			NavigationMenu.logger.error(null, e);
		}

		Color border = Color.white;
		try {
			currentNode = this.getNode(attributes, NavigationMenu.BORDERCOLOR);
			if (currentNode == null) {
				throw new IllegalArgumentException(NavigationMenu.ERROR_MESSAGE + NavigationMenu.BORDERCOLOR);
			}
			border = NavigationMenu.parseColor(currentNode.getNodeValue());
		} catch (final Exception e) {
			NavigationMenu.logger.error(null, e);
		}

		return this.createMenuGroupInstance(header, opts, icons, x, y, width, height, bgHeader, bgBody, fg, border);
	}

	/**
	 * This method instantiates a new MenuGroup object with the specified parameters.
	 * @param header String with the text to display into the Header of the MenuGroup.
	 * @param opts String Array with the identifier of each MenuItem of the MenuGroup.
	 * @param icons ImageIcon Array with the icons to each MenuItem of the MenuGroup.
	 * @param x The coordinate x of the MenuGroup into the NavigationMenu.
	 * @param y The coordinate y of the MenuGroup into the NavigationMenu.
	 * @param width The width of the MenuGroup.
	 * @param height The absolute height of the MenuGroup. The header height is included into this
	 *        height.
	 * @param bgHeader The color of the MenuGroup Header.
	 * @param bgBody The color of the background MenuGroup.
	 * @param fg The color of the MenuGroup font.
	 * @param border The color of the border of the MenuGroup.
	 * @return a <code>MenuGroup</code> object.
	 * @throws Exception
	 */
	protected MenuGroup createMenuGroupInstance(final String header, final String[] opts, final ImageIcon[] icons, final int x, final int y,
			final int width, final int height, final Color bgHeader, final Color bgBody, final Color fg,
			final Color border) throws Exception {

		Class cMenuGroup = null;
		try {
			cMenuGroup = Class.forName("com.ontimize.gui.field.NavigationMenu" + "$" + NavigationMenu.MENUGROUP);
		} catch (final Exception e) {
			NavigationMenu.logger.error(null, e);
		}
		final Constructor constructors = cMenuGroup.getConstructor(NavigationMenu.params);
		final Object ob = constructors
				.newInstance(new Object[] { header, opts, icons, new Integer(x), new Integer(y), new Integer(width),
						new Integer(height), bgHeader, bgBody, fg, border });
		return (MenuGroup) ob;
	}

	/**
	 * This method converts the specified RGBColor into a Color object.
	 * @param rgb String specifying a RGB color separated by semicolon "R;G;B". That is "102;23;44"
	 * @return a <code>Color</code>.
	 * @throws Exception
	 */
	public static Color colorRGBToColor(final String rgb) throws Exception {
		final StringTokenizer st = new StringTokenizer(rgb, ";");
		if (st.countTokens() != 3) {
			throw new Exception("Invalid values");
		}
		final int r = Integer.parseInt(st.nextToken());
		final int g = Integer.parseInt(st.nextToken());
		final int b = Integer.parseInt(st.nextToken());
		return new Color(r, g, b);
	}

	/**
	 * This method converts the specified RGBColor into a Color object.
	 * @param rgb String specifying a RGB color expressed into Hexadecimal form "#". That is "#ffffff"
	 * @return a <code>Color</code>.
	 * @throws Exception
	 */
	public static Color colorRGBHexToColor(final String rgb) throws Exception {
		if ((rgb.length() != 7) || !rgb.startsWith("#")) {
			throw new Exception("Invalid values " + rgb + " not hexadecimal color");
		}
		final String rH = rgb.substring(1, 3);
		final String gH = rgb.substring(3, 5);
		final String bH = rgb.substring(5, 7);
		final int r = Integer.parseInt(rH, 16);
		final int g = Integer.parseInt(gH, 16);
		final int b = Integer.parseInt(bH, 16);
		return new Color(r, g, b);
	}

	/**
	 * This method analyzes a String that specifies a color and returns a Color object.
	 * @param color String with the color.
	 * @return a <code>Color</code> object.
	 * @throws Exception
	 */
	public static Color parseColor(final String color) throws Exception {
		if (color == null) {
			throw new IllegalArgumentException("Color can't be parsed. Null String.");
		}
		if (color.startsWith("#")) {
			final Color c = NavigationMenu.colorRGBHexToColor(color);
			return c;
		} else if (color.indexOf(";") >= 0) {
			final Color c = NavigationMenu.colorRGBToColor(color);
			return c;
		} else {
			final Color c = ColorConstants.colorNameToColor(color);
			return c;
		}
	}

	/**
	 * This method returns if it is wanted to center the background image. If it is true the background
	 * image is centered.
	 * @return a <code>boolean</code>.
	 */
	public boolean isCenterImage() {
		return this.centerImage;
	}

	/**
	 * This method establishes that the background image have to be centered into the NavigationMenu.
	 * @param centerImage
	 */
	public void setCenterImage(final boolean centerImage) {
		this.centerImage = centerImage;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Public static classes.
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Class that contains each Menu displayed into the NavigationMenu.
	 *
	 * @author Imatia Innovation.
	 */
	public static class MenuGroup extends JComponent {

		/**
		 * The location's route to the default MenuItem Class.
		 */
		protected static final String DEFAULT_MENUITEM_CLASS = "com.ontimize.gui.field.NavigationMenu$MenuItem";

		/**
		 * Default color of the background body.
		 */
		public static final Color DEFAULT_BGBODY = new Color(222, 222, 222);

		/**
		 * Default color of the background header.
		 */
		public static final Color DEFAULT_BGHEADER = new Color(67, 67, 67);

		/**
		 * Default color of the Font.
		 */
		public static Color DEFAULT_FG = new Color(10, 27, 40);

		/**
		 * Default color of the Header Font.
		 */
		public static final Color DEFAULT_FGHEADER = new Color(255, 255, 255);

		/**
		 * Default border color of the border of the Menu Group.
		 */
		public static final Color DEFAULT_BORDERCOLOR = new Color(150, 150, 150);

		/**
		 * Default color when an item is selected.
		 */
		public static Color DEFAULT_SELECTIONCOLOR = new Color(237, 166, 2);

		/**
		 * Default color of the Font when an item is selected.
		 */
		public static Color DEFAULT_FGSELECTIONCOLOR = new Color(255, 255, 255);

		/**
		 * Number of pixels of separation between the Header and the items of the Menu. By default it is set
		 * to 5 pixels.
		 */
		protected int optionsOffset = 0;

		/**
		 * String that contains the location's route of the MenuItem Class. If MenuItem Class is overridden
		 * this variable must contain the location route of it.
		 */
		protected String menuItemClass = null;

		/**
		 * The ActionListener assigned to the MenuGroup.
		 */
		protected ActionListener actionListener = null;

		/**
		 * The height of each item in pixels.
		 */
		protected int itemHeight = 0;

		/**
		 * Color of the Header of the Menu.
		 */
		protected Color bgHeader = null;

		/**
		 * Color of the background of the Menu.
		 */
		protected Color bgBody = null;

		/**
		 * Color of the font of the Menu.
		 */
		protected Color foregroundColor = null;

		/**
		 * Color of the font of the Menu Header.
		 */
		protected Color foregroundColorHeader = null;

		/**
		 * String with the text that will be displayed into the Header of the Menu.
		 */
		protected String header = null;

		/**
		 * Color of the border of the Menu.
		 */
		protected Color borderColor = null;

		/**
		 * Border Object with the border of the Menu.
		 */
		protected Border border = null;

		/**
		 * String with the style of the border of the Menu.
		 */
		protected String borderString = null;

		/**
		 * String that contains the selected Option into the Menu.
		 */
		protected String selectedOption = null;

		/**
		 * Parameter that allows to configure the Menu opaque or not. If it is true the Menu will be
		 * displayed opaque. By default it is set to true.
		 */
		protected boolean opaque = false;

		/**
		 * Parameter that allows to drag the Menu or not. If it is true the Menu is possible to drag. By
		 * default it is set to true.
		 */
		protected boolean dragEnabled = true;

		/**
		 * Parameter that allows to drag all Menu. by default only menu header is draggable.
		 */
		protected boolean dragAllMenuEnabled = false;

		/**
		 * Parameter that displays a separator between the MenuItems of the MenuGroup.
		 */
		protected boolean separator = true;

		/**
		 * Parameter that allows to configure if the MenuGroup has or not border.
		 */
		protected boolean hasBorder = false;

		/**
		 * The Resource Bundle assigned to the Menu.
		 */
		protected ResourceBundle bundle = null;

		/**
		 * Array with all the MenuItems of the Menu.
		 */
		protected MenuItem[] menuItem = null;

		/**
		 * The default height of the Header in pixels.
		 */
		protected static int DEFAULT_HEADER_HEIGHT = 39;

		/**
		 * Variable that contains the header height of the Menu.
		 */
		protected int headerheight = MenuGroup.DEFAULT_HEADER_HEIGHT;

		/**
		 * The renderer of each MenuItem of the Menu.
		 */
		protected MenuItemRenderer menuItemRenderer = null;

		/**
		 * The renderer of each MenuHeader of the Menu.
		 */
		protected MenuHeaderRenderer menuHeaderRenderer = null;

		/**
		 * Icon of the Header that indicates if the Menu Group could be dragged.
		 */
		protected ImageIcon icon = null;

		/**
		 * Constructs a new Menu Group with the specified parameters.
		 * @param header String with the text to display into the Header of the MenuGroup.
		 * @param options String Array with the identifier of each MenuItem of the MenuGroup.
		 * @param icons ImageIcon Array with the icons to each MenuItem of the MenuGroup.
		 * @param x The coordinate x of the MenuGroup into the NavigationMenu.
		 * @param y The coordinate y of the MenuGroup into the NavigationMenu.
		 * @param w The width of the MenuGroup.
		 * @param h The absolute height of the MenuGroup. The header height is included into this height.
		 * @param bgH The color of the MenuGroup Header.
		 * @param bgB The color of the background MenuGroup.
		 * @param fg The color of the MenuGroup font.
		 * @param borderColor The color of the border of the MenuGroup. Use
		 *        {@link #MenuGroup(Map parameters)}
		 * @deprecated
		 */
		@Deprecated
		public MenuGroup(final String header, final String[] options, final ImageIcon[] icons, final int x, final int y, final int w, final int h, final Color bgH,
				final Color bgB, final Color fg, final Color borderColor) {

			this.setBounds(x, y, w, h);

			this.header = header;
			this.bgBody = bgB;
			this.bgHeader = bgH;
			this.foregroundColor = fg;
			this.borderColor = borderColor;
			if (borderColor != null) {
				final LineBorder border = new LineBorder(this.borderColor, 3);
				this.setBorder(border);
			}
			this.installMouseHandler();
			this.setOpaque(true);

			final Map param = new Hashtable();
			param.put(NavigationMenu.OPTIONS, options);
			param.put(NavigationMenu.ICONS, icons);
			this.createMenuItems(param);

			this.setMenuGroupBounds(x, y, w, h);
			this.setOpaque(false);

		}

		/**
		 * Constructs a new MenuGroup with the specified parameters.
		 * @param parameters the <code>Hashtable</code> with the whole parameters to configure the
		 *        MenuGroup.
		 *        <p>
		 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
		 *        <tr>
		 *        <td><b>attribute</td>
		 *        <td><b>values</td>
		 *        <td><b>default</td>
		 *        <td><b>required</td>
		 *        <td><b>meaning</td>
		 *        </tr>
		 *        <tr>
		 *        <td>header</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>String with the text to display into the Header of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>headerheight</td>
		 *        <td></td>
		 *        <td>35</td>
		 *        <td>no</td>
		 *        <td>Height of the Header of the MenuGroup.</td>
		 *        </tr>
		 *        <td>bgheader</td>
		 *        <td>Color: "214;222;230" or "#ffffff"</td>
		 *        <td>#434343</td>
		 *        <td>no</td>
		 *        <td>The color of the MenuGroup Header.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>options</td>
		 *        <td><i>"mangarusers;manageraccounts..."</td>
		 *        <td></td>
		 *        <td>yes</td>
		 *        <td>String Array with the identifier of each MenuItem of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>icons</td>
		 *        <td><i>"/buttonsbar/icon1.png;/buttonsbar/icon2.png..."</td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>ImageIcon Array with the icons to each MenuItem of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>x</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>The coordinate x of the MenuGroup into the NavigationMenu.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>y</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>The coordinate y of the MenuGroup into the NavigationMenu.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>width</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>yes</td>
		 *        <td>The width of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>height</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>The absolute height of the MenuGroup. The header height is included into this
		 *        height.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>bgbody</td>
		 *        <td>Color: "214;222;230" or "#ffffff"</td>
		 *        <td>#dedede</td>
		 *        <td>no</td>
		 *        <td>The color of the background MenuGroup.</td>
		 *        </tr>
		 *        <td>fg</td>
		 *        <td>Color: "214;222;230" or "#ffffff"</td>
		 *        <td>#434343</td>
		 *        <td>no</td>
		 *        <td>The color of the foreground MenuGroup.</td>
		 *        </tr>
		 *        <td>bordercolor</td>
		 *        <td>Color: "214;222;230" or "#ffffff"</td>
		 *        <td>#f0f0f0</td>
		 *        <td>no</td>
		 *        <td>The color of the MenuGroup border.</td>
		 *        </tr>
		 *        <td>border</td>
		 *        <td>"yes/no" or "true/false"</td>
		 *        <td>true</td>
		 *        <td>no</td>
		 *        <td>Specifies if the MenuGroup has border.</td>
		 *        </tr>
		 *        <td>borderclass</td>
		 *        <td></td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>String with the border style.</td>
		 *        </tr>
		 *        <td>opaque</td>
		 *        <td>"yes/no" or "true/false"</td>
		 *        <td>true</td>
		 *        <td>no</td>
		 *        <td>Specifies is the MenuGroup is opaque or not.</td>
		 *        </tr>
		 *        <td>dragenable</td>
		 *        <td>"yes/no" or "true/false"</td>
		 *        <td>true</td>
		 *        <td>no</td>
		 *        <td>Specifies if the MenuGroup could be dragged.</td>
		 *        </tr>
		 *        </tr>
		 *        <td>dragallmenu</td>
		 *        <td>"yes/no" or "true/false"</td>
		 *        <td>false</td>
		 *        <td>no</td>
		 *        <td>Specifies if all the MenuGroup could be dragged or only the header. By default, only
		 *        header is draggable.</td>
		 *        </tr>
		 *        <td>separator</td>
		 *        <td>"yes/no" or "true/false"</td>
		 *        <td>true</td>
		 *        <td>no</td>
		 *        <td>Specifies if a separator would be placed between MenuItems of the MenuGroup.</td>
		 *        </tr>
		 *        </table>
		 *
		 *
		 */
		public MenuGroup(final Map parameters) {
			int x = 0, y = 0, w = 0, h = 0;

			if (NavigationMenu.useBasicRenderer) {
				this.opaque = true;
				this.hasBorder = true;
				MenuGroup.DEFAULT_FG = new Color(67, 67, 67);
				MenuGroup.DEFAULT_SELECTIONCOLOR = new Color(205, 205, 205);
				MenuGroup.DEFAULT_FGSELECTIONCOLOR = new Color(114, 123, 124);
			}

			Object value = ApplicationManager.getParameterValue(NavigationMenu.HEADER, parameters);
			if (value != null) {
				this.header = (String) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.X, parameters);
			if (value != null) {
				x = Integer.parseInt((String) value);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.Y, parameters);
			if (value != null) {
				y = Integer.parseInt((String) value);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.WIDTH, parameters);
			if (value != null) {
				w = Integer.parseInt((String) value);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.HEIGHT, parameters);
			if (value != null) {
				h = Integer.parseInt((String) value);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.BGHEADER, parameters);
			if (value != null) {
				this.bgHeader = (Color) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.BGBODY, parameters);
			if (value != null) {
				this.bgBody = (Color) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.FOREGROUND, parameters);
			if (value != null) {
				this.foregroundColor = (Color) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.FOREGROUNDHEADER, parameters);
			if (value != null) {
				this.foregroundColorHeader = (Color) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.BORDERCOLOR, parameters);
			if (value != null) {
				this.borderColor = (Color) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.HEADERHEIGHT, parameters);
			if (value != null) {
				this.headerheight = Integer.parseInt((String) value);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.BORDERCLASS, parameters);
			if (value != null) {
				this.borderString = (String) value;
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.OPAQUE, parameters);
			if (value != null) {
				final String strOpaque = (String) value;
				this.opaque = ParseUtils.getBoolean(strOpaque, this.opaque);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.DRAGENABLE, parameters);
			if (value != null) {
				final String strDrag = (String) value;
				this.dragEnabled = ParseUtils.getBoolean(strDrag, this.dragEnabled);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.DRAGALLMENU, parameters);
			if (value != null) {
				final String strDrag = (String) value;
				this.dragAllMenuEnabled = ParseUtils.getBoolean(strDrag, this.dragAllMenuEnabled);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.SEPARATOR, parameters);
			if (value != null) {
				final String strSeparator = (String) value;
				this.separator = ParseUtils.getBoolean(strSeparator, this.separator);
			}

			value = ApplicationManager.getParameterValue(NavigationMenu.BORDER, parameters);
			if (value != null) {
				final String strBorder = (String) value;
				this.hasBorder = ParseUtils.getBoolean(strBorder, this.hasBorder);
			}

			if (this.hasBorder) {
				if (this.borderString == null) {
					LineBorder border = null;
					// RoundBorder border = null;
					if (this.borderColor != null) {
						border = new LineBorder(this.borderColor, 2);

					} else {
						border = new LineBorder(MenuGroup.DEFAULT_BORDERCOLOR, 2);
						// border = new RoundBorder(10,10,10,10);
						// border.setBorderColor(DEFAULT_BORDERCOLOR);
						// border.setShadowColor(Color.black);
					}
					this.setBorder(border);
				}
			} else {
				this.border = BorderFactory.createEmptyBorder();
			}

			this.icon = ParseUtils.getImageIcon(
					(String) ApplicationManager.getParameterValue(NavigationMenu.MENU_GROUP_HEADER_ICON, parameters),
					ImageManager.getIcon(ImageManager.BLACK_HAND));

			this.installMouseHandler();
			this.setOpaque(this.opaque);
			this.createMenuItems(parameters);
			if (h == 0) {
				this.itemHeight = 35;
			}
			this.setMenuGroupBounds(x, y, w, h);
		}

		/**
		 * This method allows to resize the size of the MenuGroup in function of the number of MenuItems
		 * that are visible. For example, if your Menu Group have configured four MenuItems but only two are
		 * visible, this method fits the MenuGroup size to these two MenuItems.
		 */
		public void reBound() {
			final Insets insets = this.getInsets();
			final int d = this.getItemHeight();
			int numVisible = 0;
			for (int i = 0; i < this.menuItem.length; i++) {
				if (this.menuItem[i].isVisible()) {
					numVisible++;
				}
			}
			int h = 0;
			if (numVisible > 0) {
				h = this.headerheight + insets.top + insets.bottom + this.optionsOffset + (d * numVisible);
			}
			this.setBounds(this.getX(), this.getY(), this.getWidth(), h);
		}

		/**
		 * This method establishes the size of the MenuGroup when it is created the first time.
		 * @param x The coordinate x of the MenuGroup.
		 * @param y The coordinate y of the MenuGroup.
		 * @param width The width of the MenuGroup.
		 * @param height The height of the MenuGroup.
		 */
		public void setMenuGroupBounds(final int x, final int y, final int width, final int height) {

			final Insets insets = this.getInsets();
			int d = 0;
			// Calculating the height of each MenuItem.
			if ((this.itemHeight == 0) && (height != 0)) {
				if (this.menuItem.length == 0) {
					d = height - insets.top - insets.bottom - this.headerheight - this.optionsOffset;
				} else {
					d = (height - insets.top - insets.bottom - this.headerheight - this.optionsOffset)
							/ this.menuItem.length;
				}
				this.setItemHeight(d);
			} else {
				d = this.getItemHeight();
			}
			// Checking which MenuItem are visible.
			int numVisible = 0;
			for (int i = 0; i < this.menuItem.length; i++) {
				if (this.menuItem[i].isVisible()) {
					numVisible++;
				}
			}
			final int h = this.headerheight + insets.top + insets.bottom + this.optionsOffset + (d * numVisible);
			this.setBounds(x, y, width, h);
		}

		/**
		 * This method creates all the MenuItems of the MenuGroup.
		 * @param parameters the <code>Hashtable</code> with the whole parameters to configure the MenuItem.
		 *        <p>
		 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
		 *        <tr>
		 *        <td><b>attribute</td>
		 *        <td><b>values</td>
		 *        <td><b>default</td>
		 *        <td><b>required</td>
		 *        <td><b>meaning</td>
		 *        </tr>
		 *        <tr>
		 *        <td>options</td>
		 *        <td><i>"mangarusers;manageraccounts..."</td>
		 *        <td></td>
		 *        <td>yes</td>
		 *        <td>String Array with the identifier of each MenuItem of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>icons</td>
		 *        <td><i>"/buttonsbar/icon1.png;/buttonsbar/icon2.png..."</td>
		 *        <td></td>
		 *        <td>no</td>
		 *        <td>ImageIcon Array with the icons to each MenuItem of the MenuGroup.</td>
		 *        </tr>
		 *        <tr>
		 *        <td>menuitemclass</td>
		 *        <td></td>
		 *        <td><i>"com.ontimize.gui.field.NavigationMenu$MenuItem"</td>
		 *        <td>no</td>
		 *        <td>The class of the MenuItem</td>
		 *        </tr>
		 *        </table>
		 *
		 */
		protected void createMenuItems(final Map parameters) {
			String[] options = null;
			ImageIcon[] icons = null;
			if (parameters.containsKey(NavigationMenu.OPTIONS)) {
				options = (String[]) parameters.get(NavigationMenu.OPTIONS);
			}
			if (parameters.containsKey(NavigationMenu.ICONS)) {
				icons = (ImageIcon[]) parameters.get(NavigationMenu.ICONS);
			}
			if (parameters.containsKey(NavigationMenu.MENUITEMCLASS)) {
				this.menuItemClass = (String) parameters.get(NavigationMenu.MENUITEMCLASS);
			} else {
				this.menuItemClass = MenuGroup.DEFAULT_MENUITEM_CLASS;
			}

			this.menuItem = new MenuItem[options.length];
			try {
				final Class itemClass = Class.forName(this.menuItemClass);
				for (int i = 0; i < options.length; i++) {
					final Constructor constructor = itemClass.getConstructor(new Class[] { String.class, ImageIcon.class });
					this.menuItem[i] = (MenuItem) constructor.newInstance(new Object[] { options[i], icons[i] });
				}
			} catch (final Exception ex) {
				NavigationMenu.logger.error(null, ex);
			}
		}

		/**
		 * This method returns an Array of MenuItem objects with all the MenuItem of the MenuGroup.
		 * @return a <code>Array</code> of MenuItem.
		 */
		public MenuItem[] getMenuItem() {
			return this.menuItem;
		}

		/**
		 * This method sets the MenuItems of the MenuGroup.
		 * @param menuItem Array of MenuItem objects.
		 */
		public void setMenuItem(final MenuItem[] menuItem) {
			this.menuItem = menuItem;
		}

		/**
		 * This method returns the height of each MenuItem of the MenuGroup.
		 * @return a <code>int</code> with the MenuItem height.
		 */
		public int getItemHeight() {
			return this.itemHeight;
		}

		/**
		 * This method establishes the height of the MenuItem of the MenuGroup. All the MenuItem have the
		 * same height.
		 * @param itemHeight
		 */
		public void setItemHeight(final int itemHeight) {
			this.itemHeight = itemHeight;
		}

		/**
		 * This method returns the coordinate x of the MenuGroup.
		 * @return a <code>int</code> with the coordinate x of the MenuGroup.
		 */
		@Override
		public int getX() {
			return this.getBounds().x;
		}

		/**
		 * This method returns the coordinate y of the MenuGroup.
		 * @return a <code>int</code> with the coordinate y of the MenuGroup.
		 */
		@Override
		public int getY() {
			return this.getBounds().y;
		}

		/**
		 * This method sets the coordinates x,y of the MenuGroup.
		 * @param x Int with the coordinate x of the MenuGroup.
		 * @param y Int with the coordinate y of the MenuGroup.
		 */
		public void setXY(final int x, final int y) {
			final Rectangle r = this.getBounds();
			r.x = x;
			r.y = y;
			this.setBounds(r);
		}

		/**
		 * This method allows to move the MenuGroup the specified distance into the coordinate x and y since
		 * the current position of the MenuGroup.
		 * @param despX Int with the displacement into the coordinate x.
		 * @param despY Int with the displacement into the coordinate y.
		 */
		@Override
		public void move(final int despX, final int despY) {
			final Rectangle r = this.getBounds();

			r.x = r.x + despX;
			r.y = r.y + despY;
			if (r.x < 0) {
				r.x = 0;
			}
			if (r.y < 0) {
				r.y = 0;
			}
			final int parentH = this.getParent().getHeight();
			final int parentW = this.getParent().getWidth();
			if ((r.x + r.width) > parentW) {
				r.x = parentW - r.width;
			}
			if ((r.y + r.height) > parentH) {
				r.y = parentH - r.height;
			}
			this.setBounds(r);
		}

		/**
		 * This method establishes the specified ActionListener to the MenuGroup.
		 * @param l The ActionListener.
		 */
		public void setActionListener(final ActionListener l) {
			this.actionListener = l;
		}

		/**
		 * This method establishes the specified MenuItemRenderer to the MenuGroup.
		 * @param menuItemRenderer The MenuItemRenderer.
		 */
		public void setMenuItemRenderer(final MenuItemRenderer menuItemRenderer) {
			this.menuItemRenderer = menuItemRenderer;
		}

		/**
		 * This method establishes the specified MenuHeaderRenderer to the MenuGroup.
		 * @param menuHeaderRenderer The MenuHeaderRenderer.
		 */
		public void setMenuHeaderRenderer(final MenuHeaderRenderer menuHeaderRenderer) {
			this.menuHeaderRenderer = menuHeaderRenderer;
		}

		/**
		 * This method returns a String with the text of the MenuGroup Header.
		 * @return a <code>String</code> with the MenuGroup Header text.
		 */
		public String getHeader() {
			return this.header;
		}

		/**
		 * This method establishes the ResourceBundle to the MenuGroup.
		 * @param bundle The ResourceBundle.
		 */
		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
			this.repaint();
		}

		/**
		 * This method installs the Handler of Mouse Listeners. The MouseMotionListener and MouseListerner
		 * are added to the MenuGroup.
		 */
		protected void installMouseHandler() {
			this.addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseMoved(final MouseEvent e) {
					MenuGroup.this.selectedOption = MenuGroup.this.getOptionAt(e.getX(), e.getY());
					if (MenuGroup.this.selectedOption != null) {
						e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					} else {
						e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					MenuGroup.this.repaint();
				}
			});
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					final MenuItem item = MenuGroup.this.getMenuAt(e.getX(), e.getY());
					if (item != null) {
						item.actionPerformed(new ActionEvent(MenuGroup.this, 0, item.getManager()));
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					MenuGroup.this.selectedOption = null;
					MenuGroup.this.repaint();
				}
			});
		}

		/**
		 * This method establishes the paint options to paint each MenuItem of the MenuGroup using the
		 * MenuItemRenderer assigned to the MenuGroup.
		 * @param g The Graphics of the MenuGroup.
		 */
		protected void paintOptions(final Graphics g) {
			g.setFont(new Font("Verdana", Font.PLAIN, 10));
			final Insets insets = this.getInsets();
			final int d = this.getItemHeight();

			int yoffset = this.headerheight + insets.top + this.optionsOffset;
			for (int i = 0; i < this.menuItem.length; i++) {
				boolean selected = false;
				if (this.menuItem[i].getManager().equals(this.selectedOption)) {
					selected = true;
				}
				boolean enabled = true;
				if (!this.menuItem[i].isEnabled()) {
					enabled = false;
				}

				this.menuItemRenderer.setResourceBundle(this.bundle);
				g.translate(insets.left, yoffset);
				final Component c = this.menuItemRenderer.getMenuItemRendererComponent(this, this.menuItem[i], selected,
						enabled);
				c.setSize(this.getWidth() - insets.left - insets.right, d);
				c.paint(g);
				g.translate(-insets.left, -yoffset);

				yoffset = yoffset + d;
			}
		}

		/**
		 * This method paints the MenuGroup.
		 * @param g The Graphics of NavigationMenu.
		 */
		@Override
		protected void paintComponent(final Graphics g) {

			final Insets insets = this.getInsets();
			if ((this.bgBody != null) && (this.opaque)) {
				g.setColor(this.bgBody);
				g.fillRect(insets.left, insets.top, this.getWidth() - insets.left - insets.right,
						this.getHeight() - insets.top - insets.bottom);
			} else if ((this.bgBody == null) && (this.opaque)) {
				g.setColor(MenuGroup.DEFAULT_BGBODY);
				g.fillRect(insets.left, insets.top, this.getWidth() - insets.left - insets.right,
						this.getHeight() - insets.top - insets.bottom);
			}

			if ((this.headerheight != 0) && (this.header != null)) {

				this.menuHeaderRenderer.setResourceBundle(this.bundle);
				final Component c = this.menuHeaderRenderer.getMenuHeaderRendererComponent(this);
				c.setSize(this.getWidth() - insets.left - insets.right, this.getHeaderHeight());
				c.paint(g);

			} else {
				this.optionsOffset = 0;
			}

			if (this.menuItem != null) {
				this.paintOptions(g);
			}

		}

		@Override
		public boolean isVisible() {
			boolean anyVisible = false;
			for (int m = 0; m < this.menuItem.length; m++) {
				if (this.menuItem[m].isVisible()) {
					anyVisible = true;
					return true;
				}
			}

			return anyVisible;
		}

		/**
		 * This method checks if the specified coordinates x and y belongs to any MenuItem of the MenuGroup,
		 * and if it is true, the method returns the MenuItem object selected.
		 * @param x int of the coordinate x.
		 * @param y int of the coordinate y.
		 * @return a <code>MenuItem</code> selected.
		 */
		protected MenuItem getMenuAt(final int x, final int y) {
			final Insets insets = this.getInsets();
			final int d = (this.getHeight() - insets.top - insets.bottom - this.headerheight - this.optionsOffset)
					/ this.menuItem.length;
			int yoffset = this.headerheight + this.getInsets().top + this.optionsOffset;
			for (int i = 0; i < this.menuItem.length; i++) {
				if (!this.menuItem[i].isVisible()) {
					continue;
				}
				if ((y >= yoffset) && (y < (yoffset + d))) {
					return this.menuItem[i];
				}
				yoffset = yoffset + d;
			}
			return null;
		}

		/**
		 * This method checks if the specified coordinates x and y belongs to any MenuItem of the MenuGroup,
		 * and if it is true, the method returns the identifier of the MenuItem selected.
		 * @param x int of the coordinate x.
		 * @param y int of the coordinate y.
		 * @return a <code>String</code> with the MenuItem identifier.
		 */
		protected String getOptionAt(final int x, final int y) {
			final Insets insets = this.getInsets();
			final int d = (this.getHeight() - insets.top - insets.bottom - this.headerheight - this.optionsOffset)
					/ this.menuItem.length;
			int yoffset = this.headerheight + this.getInsets().top + this.optionsOffset;
			for (int i = 0; i < this.menuItem.length; i++) {
				if (!this.menuItem[i].isVisible()) {
					continue;
				}
				if ((y >= yoffset) && (y < (yoffset + d))) {
					return this.menuItem[i].getManager();
				}
				yoffset = yoffset + d;
			}
			return null;
		}

		/**
		 * This method checks if the specified coordinates x and y belongs to any MenuItem of the MenuGroup,
		 * and if it is true, the method returns the index of the MenuItem selected.
		 * @param x int of the coordinate x.
		 * @param y int of the coordinate y.
		 * @return a <code>int</code> with the MenuItem index.
		 */
		protected int getOptionIndex(final int x, final int y) {
			final Insets insets = this.getInsets();
			final int d = this.getItemHeight();
			int yoffset = this.headerheight + this.getInsets().top + this.optionsOffset;
			if (y < (insets.top + this.headerheight)) {
				return -2;
			}
			for (int i = 0; i < this.menuItem.length; i++) {
				if (!this.menuItem[i].isVisible()) {
					continue;
				}
				if ((y >= yoffset) && (y < (yoffset + d))) {
					return i;
				}
				yoffset = yoffset + d;
			}
			return -1;
		}

		public int getHeaderHeight() {
			return this.headerheight;
		}

		public Color getBgHeader() {
			return this.bgHeader;
		}

		@Override
		public boolean isOpaque() {
			return this.opaque;
		}

		public Color getForegroundColorHeader() {
			return this.foregroundColorHeader;
		}

	}

	/**
	 * Class that contains each Item displayed into the MenuGroup of the NavigationMenu.
	 *
	 * @author Imatia Innovation.
	 */
	public static class MenuItem implements ActionListener {

		/**
		 * Identifier of the Item (String).
		 */
		protected String manager;

		/**
		 * ImageIcon of the Item.
		 */
		protected ImageIcon icon;

		/**
		 * Boolean that determines if the Item will be visible or not. By default it is set to true.
		 */
		protected boolean visible = true;

		/**
		 * Boolean that determines if the Item will be enabled or not. By default it is set to true.
		 */
		protected boolean enabled = true;

		/**
		 * Constructs a new MenuItem specifying the identifier and the ImageIcon of the MenuItem.
		 * @param manager String with the identifier of the MenuItem.
		 * @param icon ImageIcon with the icon of the MenuItem.
		 */
		public MenuItem(final String manager, final ImageIcon icon) {
			super();
			this.icon = icon;
			this.manager = manager;
		}

		/**
		 * Constructs a new MenuItem specifying the identifier and the ImageIcon of the MenuItem.
		 * @param manager String with the identifier of the MenuItem.
		 * @param icon ImageIcon with the icon of the MenuItem.
		 * @param visible Boolean that sets the visibility of the MenuItem.
		 * @param enabled Boolean that sets the availability of the MenuItme.
		 */
		public MenuItem(final String manager, final ImageIcon icon, final boolean visible, final boolean enabled) {
			this.manager = manager;
			this.icon = icon;
			this.visible = visible;
			this.enabled = enabled;
		}

		/**
		 * This method returns if the MenuItem is visible.
		 * @return a <code>boolean</code> with the attribute visible.
		 */
		public boolean isVisible() {
			return this.visible;
		}

		/**
		 * This method sets the visibility of the MenuItem.
		 * @param visible boolean that indicates if the MenuItem is visible. If it is true, the MenuItem
		 *        will be visible.
		 */
		public void setVisible(final boolean visible) {
			this.visible = visible;
		}

		/**
		 * This method returns if the MenuItem is enabled.
		 * @return a <code>boolean</code> with the attribute enabled.
		 */
		public boolean isEnabled() {
			return this.enabled;
		}

		/**
		 * This method sets the availability of the MenuItem.
		 * @param enabled boolean that indicates if the MenuItem is enabled. If it is true, the MenuItem
		 *        will be enabled.
		 * @deprecated Use {@link #setEnabled(boolean)}
		 */
		@Deprecated
		public void setEnable(final boolean enabled) {
			this.setEnabled(enabled);
		}

		public void setEnabled(final boolean enabled) {
			this.enabled = enabled;
		}

		/**
		 * This method returns a String with the identifier of the MenuItem.
		 * @return a <code>String</code> with the MenuItem identifier.
		 */
		public String getManager() {
			return this.manager;
		}

		/**
		 * This method establishes the String identifier of the MenuItem.
		 * @param manager String with the identifier.
		 */
		public void setManager(final String manager) {
			this.manager = manager;
		}

		/**
		 * This method returns an ImageIcon with the icon of the MenuItem.
		 * @return an <code>ImageIcon</code> with the MenuItem icon.
		 */
		public ImageIcon getIcon() {
			return this.icon;
		}

		/**
		 * This method sets the icon of the MenuItem.
		 * @param icon ImageIcon to the icon.
		 */
		public void setIcon(final ImageIcon icon) {
			this.icon = icon;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
		}

	}

	/**
	 * Interface MenuHeaderRenderer.
	 *
	 * @author Imatia Innovation.
	 *
	 */
	public interface MenuHeaderRenderer extends Internationalization {

		/**
		 * This method returns the renderer to the Header of the MenuGroup.
		 * @param group The MenuGroup where the Header is contained.
		 * @return a <code>Component</code>.
		 */
		public Component getMenuHeaderRendererComponent(MenuGroup group);

	}

	/**
	 * Interface MenuItemRenderer.
	 *
	 * @author Imatia Innovation.
	 *
	 */
	public interface MenuItemRenderer extends Internationalization {

		/**
		 * This method returns the renderer to the MenuItem of the MenuGroup.
		 * @param group The MenuGroup where the MenuItem is contained.
		 * @param item The MenuItem to be applied the renderer.
		 * @param selected Boolean that indicates if the MenuItem is selected or not.
		 * @param enabled Boolean that indicates if the MenuItem is enabled or not.
		 * @return a <code>Component</code>.
		 */
		public Component getMenuItemRendererComponent(MenuGroup group, MenuItem item, boolean selected,
				boolean enabled);

	}

	/**
	 * Class that determines a MenuHeader renderer by default.
	 *
	 * @author Imatia Innovation.
	 */
	public static class DefaultMenuHeaderRenderer extends JComponent
	implements MenuHeaderRenderer, Internationalization {

		public static String LEFT_IMAGE = "leftimage";

		public static String CENTER_IMAGE = "centerimage";

		public static String RIGHT_IMAGE = "rightimage";

		public static String LEFTIMAGE_URL = "navigator/menuGroupHeaderLeft.png";

		public static String CENTERIMAGE_URL = "navigator/menuGroupHeaderCenter.png";

		public static String RIGHTIMAGE_URL = "navigator/menuGroupHeaderRight.png";

		protected Image leftImage;

		protected Image centerImage;

		protected Image rightImage;

		public DefaultMenuHeaderRenderer() {
			this.setOpaque(false);

			ImageIcon im = ImageManager.getIcon(DefaultMenuHeaderRenderer.LEFTIMAGE_URL);
			if (im != null) {
				this.leftImage = im.getImage();
			}

			im = ImageManager.getIcon(DefaultMenuHeaderRenderer.CENTERIMAGE_URL);
			if (im != null) {
				this.centerImage = im.getImage();
			}

			im = ImageManager.getIcon(DefaultMenuHeaderRenderer.RIGHTIMAGE_URL);
			if (im != null) {
				this.rightImage = im.getImage();
			}

		}

		/**
		 * The ResourceBundle of the Header renderer.
		 */
		protected ResourceBundle bundle = null;

		protected MenuGroup menuGroup;

		@Override
		public Component getMenuHeaderRendererComponent(final MenuGroup group) {
			this.menuGroup = group;
			return this;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);

			if (this.menuGroup != null) {
				final String header = this.menuGroup.getHeader();
				if (header != null) {
					final Map images = this.getImagesForMenuGroupHeader(header);
					if ((images != null) && !images.isEmpty()) {
						final Image left = (Image) images.get(DefaultMenuHeaderRenderer.LEFT_IMAGE);
						final Image center = (Image) images.get(DefaultMenuHeaderRenderer.CENTER_IMAGE);
						final Image right = (Image) images.get(DefaultMenuHeaderRenderer.RIGHT_IMAGE);

						final int leftWidth = left.getWidth(this);
						final int rightWidth = right.getWidth(this);
						final int centerWidth = this.getWidth() - (leftWidth + rightWidth + this.menuGroup.getInsets().right);

						g.drawImage(left, this.menuGroup.getInsets().left, 0, left.getWidth(this), left.getHeight(this),
								this);
						g.drawImage(center, left.getWidth(this), 0, centerWidth, center.getHeight(this), this);
						g.drawImage(right, leftWidth + centerWidth, 0, right.getWidth(this), right.getHeight(this),
								this);
					} else {
						if ((this.menuGroup.getBgHeader() != null) && this.menuGroup.isOpaque()) {
							g.setColor(this.menuGroup.getBgHeader());
							g.fillRect(this.menuGroup.getInsets().left, this.menuGroup.getInsets().top,
									this.getWidth() - this.menuGroup.getInsets().left
									- this.menuGroup.getInsets().right,
									this.menuGroup.getHeaderHeight());
						} else if ((this.menuGroup.getBgHeader() == null) && this.menuGroup.isOpaque()) {
							g.setColor(MenuGroup.DEFAULT_BGHEADER);
							g.fillRect(this.menuGroup.getInsets().left, this.menuGroup.getInsets().top,
									this.getWidth() - this.menuGroup.getInsets().left
									- this.menuGroup.getInsets().right,
									this.menuGroup.getHeaderHeight());
						}
					}

					if (this.menuGroup.getForegroundColorHeader() != null) {
						g.setColor(this.menuGroup.getForegroundColorHeader());
					} else {
						g.setColor(MenuGroup.DEFAULT_FGHEADER);
					}
					String viewHeader = header;
					try {
						viewHeader = this.bundle != null ? this.bundle.getString(header) : header;
					} catch (final Exception e) {
						NavigationMenu.logger.trace(null, e);
					}
					viewHeader = viewHeader.toUpperCase();
					g.setFont(new Font("Arial", Font.BOLD, 14));
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.drawString(viewHeader, 50,
							((this.menuGroup.getHeaderHeight() + this.menuGroup.getInsets().top) / 2)
							+ (g.getFontMetrics().getAscent() / 2));

				}
			}
		}

		protected Map getImagesForMenuGroupHeader(final String header) {

			final Map images = new Hashtable();
			try {

				if (this.leftImage != null) {
					images.put(DefaultMenuHeaderRenderer.LEFT_IMAGE, this.leftImage);
				}
				if (this.centerImage != null) {
					images.put(DefaultMenuHeaderRenderer.CENTER_IMAGE, this.centerImage);
				}
				if (this.rightImage != null) {
					images.put(DefaultMenuHeaderRenderer.RIGHT_IMAGE, this.rightImage);
				}
				return images;
			} catch (final Exception e) {
				NavigationMenu.logger.error(null, e);
			}
			return null;
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale arg0) {
		}

		/**
		 * This method establishes the ResourceBundle to the renderer.
		 */
		@Override
		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

	}

	/**
	 * Class that determines a MenuItem renderer by default.
	 *
	 * @author Imatia Innovation.
	 */
	public static class BasicMenuHeaderRenderer extends JComponent implements MenuHeaderRenderer, Internationalization {

		public BasicMenuHeaderRenderer() {
			this.setOpaque(true);
		}

		/**
		 * The ResourceBundle of the Header renderer.
		 */
		protected ResourceBundle bundle = null;

		protected MenuGroup menuGroup;

		@Override
		public Component getMenuHeaderRendererComponent(final MenuGroup group) {
			this.menuGroup = group;
			return this;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);

			if (this.menuGroup != null) {
				final String header = this.menuGroup.getHeader();
				if (header != null) {
					if ((this.menuGroup.getBgHeader() != null) && this.menuGroup.isOpaque()) {
						g.setColor(this.menuGroup.getBgHeader());
						g.fillRect(this.menuGroup.getInsets().left, this.menuGroup.getInsets().top,
								this.menuGroup.getWidth() - this.menuGroup.getInsets().left
								- this.menuGroup.getInsets().right,
								this.menuGroup.getHeaderHeight());
					} else if ((this.menuGroup.getBgHeader() == null) && this.menuGroup.isOpaque()) {
						g.setColor(MenuGroup.DEFAULT_BGHEADER);
						g.fillRect(this.menuGroup.getInsets().left, this.menuGroup.getInsets().top,
								this.menuGroup.getWidth() - this.menuGroup.getInsets().left
								- this.menuGroup.getInsets().right,
								this.menuGroup.getHeaderHeight());
					}

					if (this.menuGroup.getForegroundColorHeader() != null) {
						g.setColor(this.menuGroup.getForegroundColorHeader());
					} else {
						g.setColor(MenuGroup.DEFAULT_FGHEADER);
					}
					String viewHeader = header;
					try {
						viewHeader = this.bundle != null ? this.bundle.getString(header) : header;
					} catch (final Exception e) {
						NavigationMenu.logger.trace(null, e);
					}
					viewHeader = viewHeader.toUpperCase();
					g.setFont(new Font("Arial", Font.BOLD, 14));
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.drawString(viewHeader,
							(this.menuGroup.getWidth() / 2) - (g.getFontMetrics().stringWidth(viewHeader) / 2),
							((this.menuGroup.getHeaderHeight() + this.menuGroup.getInsets().top) / 2)
							+ (g.getFontMetrics().getAscent() / 2));

					try {
						if (this.menuGroup.dragEnabled) {
							final Image img = this.menuGroup.icon.getImage();
							g.drawImage(img, this.getWidth() - (2 * img.getWidth(this)),
									((this.menuGroup.getHeaderHeight() + this.menuGroup.getInsets().top) / 2)
									- (img.getHeight(this) / 2),
									this);
						}
					} catch (final Exception e) {
						NavigationMenu.logger.error(null, e);
					}
				}
			}
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale arg0) {
		}

		/**
		 * This method establishes the ResourceBundle to the renderer.
		 */
		@Override
		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

	}

	/**
	 * Class that determines a MenuItem renderer by default.
	 *
	 * @author Imatia Innovation.
	 */
	public static class DefaultMenuItemRenderer extends JLabel implements MenuItemRenderer, Internationalization {

		public static int leftIconMargin = 10;

		/**
		 * The ResourceBundle of the MenuItem renderer.
		 */
		protected ResourceBundle bundle = null;

		public Color getSelectionColor() {
			return MenuGroup.DEFAULT_SELECTIONCOLOR;
		}

		@Override
		public Component getMenuItemRendererComponent(final MenuGroup group, final MenuItem item, final boolean selected,
				final boolean enabled) {

			final Color selectionColor = this.getSelectionColor();

			if (selected) {
				this.setOpaque(true);
				if (selectionColor != null) {
					this.setBackground(selectionColor);
					this.setBorder(BorderFactory.createMatteBorder(0, DefaultMenuItemRenderer.leftIconMargin, 0, 0,
							selectionColor));
				} else {
					this.setBackground(Color.GRAY);
					this.setBorder(BorderFactory.createMatteBorder(0, DefaultMenuItemRenderer.leftIconMargin, 0, 0,
							Color.GRAY));
				}
			} else {
				if (group.opaque && (group.bgBody != null)) {
					this.setBackground(group.bgBody);
					this.setBorder(BorderFactory.createMatteBorder(0, DefaultMenuItemRenderer.leftIconMargin, 0, 0,
							group.bgBody));
				} else if (group.opaque && (group.bgBody == null)) {
					this.setBackground(MenuGroup.DEFAULT_BGBODY);
					this.setBorder(BorderFactory.createMatteBorder(0, DefaultMenuItemRenderer.leftIconMargin, 0, 0,
							MenuGroup.DEFAULT_BGBODY));
				} else {
					this.setOpaque(group.opaque);
					this.setBorder(BorderFactory.createEmptyBorder(0, DefaultMenuItemRenderer.leftIconMargin, 0, 0));
				}
			}
			if (item.getManager() != null) {
				this.setText(ApplicationManager.getTranslation(item.getManager(), this.bundle));
			}

			if (enabled) {
				this.setIcon(item.getIcon());
				if (selected) {
					this.setForeground(MenuGroup.DEFAULT_FGSELECTIONCOLOR);
				} else {
					if (group.foregroundColor != null) {
						this.setForeground(group.foregroundColor);
					} else {
						this.setForeground(MenuGroup.DEFAULT_FG);
					}
				}

			} else {
				final Image grayImage = GrayFilter.createDisabledImage(item.getIcon().getImage());
				final ImageIcon iIcon = new ImageIcon(grayImage);
				this.setIcon(iIcon);
				this.setForeground(Color.GRAY);
			}
			this.setIconTextGap(18);
			this.setFont(new Font("Arial", Font.BOLD, 14));

			return this;
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale arg0) {
		}

		/**
		 * This method establishes the ResourceBundle to the renderer.
		 */
		@Override
		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

	}

}
