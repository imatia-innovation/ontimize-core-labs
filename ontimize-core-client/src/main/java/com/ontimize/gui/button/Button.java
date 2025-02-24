package com.ontimize.gui.button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.HasHelpIdComponent;
import com.ontimize.gui.SecureElement;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * The class to create a button whose text changes in function of language.
 */

public class Button extends JButton implements com.ontimize.gui.field.FormComponent, AccessForm, Freeable,
MouseListener, SecureElement, HasHelpIdComponent {

	private static final Logger logger = LoggerFactory.getLogger(Button.class);

	public static final String INSETS = "insets";

	public static final String MARGIN = "margin";

	public static final String HIGHLIGHT = "highlight";

	public static final String OPAQUE = "opaque";

	public static final String BORDERVISIBLE = "bordervisible";

	public static final String PAINTFOCUS = "paintfocus";

	public static final String PRESSEDICON = "pressedicon";

	public static final String DISABLEDICON = "disabledicon";

	public static final String ROLLOVERICON = "rollovericon";

	public static final String ICON = "icon";

	public static final String ICONALIGN = "iconalign";

	public static final String TIP = "tip";

	public static final String TEXT = "text";

	public static final String KEY = "key";

	public static final String VALIGN = "valign";

	public static final String ALIGN = "align";

	public static final String EXPAND = "expand";

	public static final String DIM = "dim";

	/**
	 * The text to show in button. By default, null.
	 */
	protected String text = null;

	/**
	 * The alignment. By default, centered.
	 */
	protected int alignment = GridBagConstraints.NORTH;

	/**
	 * The vertical alignment. By default, at top.
	 */
	protected int alignmentV = GridBagConstraints.NORTH;

	/**
	 * The key to manages the button. By default, null.
	 */
	protected String buttonKey = null;

	/**
	 * Condition about focusable. By default, true.
	 */
	protected boolean focusable = true;

	/**
	 * The tooltip key.
	 */
	protected String tooltip = null;

	/**
	 * A condition to check if tooltip is specified. By default, false.
	 */
	protected boolean specifiedTooltip = false;

	/**
	 * The roll over condition. By default, false.
	 */
	protected boolean rollover = false;

	/**
	 * A reference to resource bundle file. By default, null.
	 */
	protected ResourceBundle resourcesFileName = null;

	/**
	 * A reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * A visible permission reference. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * A enable permission reference. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * The label size. By default, -1.
	 */
	protected int labelSize = -1;

	/**
	 * The preferred height. By default, -1.
	 */
	protected int preferredHeight = -1;

	/**
	 * The font size. By default, 12 pt.
	 */
	protected int fontSize = 12;

	/**
	 * The font color. By default, black.
	 */
	protected Color fontColor = Color.black;

	/**
	 * The bold font condition. By default, false.
	 */
	protected boolean bold = false;

	/**
	 * The text to show when key pressed from keyboard. By default, null.
	 */
	protected String keyStrokeText = null;

	/**
	 * The help identifier. By default, null.
	 */
	protected String helpId = null;

	/**
	 * The icon reference. By default, null.
	 */
	protected String icon = null;

	/**
	 * The reference to icon with alt key pressed. By default, null.
	 */
	protected String altIcon = null;

	/**
	 * The reference to text with alt key pressed. By default, null.
	 */
	protected String alttext = null;

	/**
	 * The reference to tip with alt key pressed. By default, null.
	 */

	protected String altTip = null;

	protected boolean expand = false;

	protected boolean dimtext = false;

	protected double weighty = 0;

	protected double weightx = 0;

	protected Insets insets = new Insets(1, 1, 1, 1);

	/**
	 * The class constructor. It inits the parameters, sets text and updates tips and adds a mouse
	 * listener.
	 * <p>
	 * @param parameters the Map with parameters from XML definition.
	 */
	public Button(final Map parameters) {
		super("");
		this.init(parameters);
		super.setText(this.text);
		this.updateTip();
		this.addMouseListener(this);
		this.installHelpId();
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			// Specifies the component alignment.
			int totalAlignment = this.alignment;
			switch (this.alignmentV) {
			case GridBagConstraints.NORTH:
				totalAlignment = this.alignment;

				break;
			case GridBagConstraints.CENTER:
				switch (this.alignment) {
				case GridBagConstraints.NORTH:
					totalAlignment = GridBagConstraints.CENTER;

					break;
				case GridBagConstraints.NORTHEAST:
					totalAlignment = GridBagConstraints.EAST;

					break;
				case GridBagConstraints.NORTHWEST:
					totalAlignment = GridBagConstraints.WEST;
					break;
				default:
					break;
				}
				break;
			case GridBagConstraints.SOUTH:
				switch (this.alignment) {
				case GridBagConstraints.NORTH:
					totalAlignment = GridBagConstraints.SOUTH;
					break;
				case GridBagConstraints.NORTHEAST:
					totalAlignment = GridBagConstraints.SOUTHEAST;
					break;
				case GridBagConstraints.NORTHWEST:
					totalAlignment = GridBagConstraints.SOUTHWEST;

					break;
				default:
					break;
				}
				break;
			default:
				totalAlignment = this.alignment;
				break;
			}

			int fill = GridBagConstraints.NONE;

			if (this.expand && this.dimtext) {
				fill = GridBagConstraints.BOTH;
				this.weighty = 1.0;
				this.weightx = 1.0;
			} else if (this.expand) {
				this.weighty = 1.0;
				fill = GridBagConstraints.VERTICAL;
			} else if (this.dimtext) {
				this.weightx = 1.0;
				fill = GridBagConstraints.HORIZONTAL;
			}

			return new GridBagConstraints(0, 0, 1, 1, this.weightx, this.weighty, totalAlignment, fill, this.insets, 0,
					0);
		} else {
			return null;
		}
	}

	/**
	 * Gets the button attribute.
	 * <p>
	 * @return the button key
	 */
	public Object getAttribute() {
		return this.buttonKey;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters initialization parameters from XML file:<br>
	 *        <br>
	 *
	 *        <p>
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td>empty string</td>
	 *        <td>no</td>
	 *        <td>Initial text is shown in button, before locale is established. By default, an empty
	 *        string.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>left, right, center</td>
	 *        <td>center</td>
	 *        <td>no</td>
	 *        <td>The button alignment.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>key</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The reference to the button. It is used by form to return a button reference, so it
	 *        must be unique in a FORM.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>tip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The key used by bundle to translation. So, It must be present in bundle file.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>labelsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The size of label</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>fontsize</td>
	 *        <td></td>
	 *        <td>12</td>
	 *        <td>no</td>
	 *        <td>The font size.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>fontcolor</td>
	 *        <td></td>
	 *        <td>black</td>
	 *        <td>no</td>
	 *        <td>The font color.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>bold</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The condition to use bold font.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>raised/lowered</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border definition style.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>iconalign</td>
	 *        <td><i>left, right,top, bottom</td>
	 *        <td>left</td>
	 *        <td>no</td>
	 *        <td>Alignment for icon with respect to the text. Only important when icon and text are
	 *        both presents.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>mnemonic</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The mnemonic for button.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>rollover</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The roll-over condition.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>helpid</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td></td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>height</td>
	 *        <td></td>
	 *        <td>-1</td>
	 *        <td>no</td>
	 *        <td>Preferred height in pixels for button.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>disabledicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The icon for button when is disabled</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>textalign</td>
	 *        <td><i>left, center, right</td>
	 *        <td>center</td>
	 *        <td>no</td>
	 *        <td>The text alignment.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>alticon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Auxiliary icon when setAltMode is enabled.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>alttext</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Auxiliary text when setAltMode is enabled.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>alttip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Auxiliary tip when setAltMode is enabled.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opacity condition for buttons.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>bordervisible</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Visibility of button border.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>font</td>
	 *        <td>A string like : 'Arial-BOLD-18' (See #Font.decode())</td>
	 *        <td>The default font for system</td>
	 *        <td>no</td>
	 *        <td>Font for button text.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>paintfocus</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td></td>
	 *        <td>Sets the focus paint property. See
	 *        {@link AbstractButton#setFocusPainted(boolean)}.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>highlight</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the highlight property when mouse is entered. See
	 *        {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires
	 *        opaque='no'.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>pressedicon</td>
	 *        <td>'yes' or the path to icon: <br>
	 *        <ul>
	 *        <li>- 'yes': i.e. if 'button_name' is the name of button icon, it is mandatory that exists
	 *        a button called 'button_name<b>_pressed</b>' that will be showed when it is pressed.<br>
	 *        <li>- the path to the icon to show.</td>
	 *        </ul>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The button to show when it is pressed.</td>
	 *        </tr>
	 *
	 *        </table>
	 *        <br>
	 *        <B>-Associations:</B> <br>
	 *
	 *        <table x:str border=1 cellpadding=0 cellspacing=0 width=278 style='border-collapse: *
	 *        collapse;table-layout:fixed;width:209pt'>
	 *        <col width=198 style= 'mso-width-source:userset;mso-width-alt:7241;width:149pt'>
	 *        <col width=80 style='width:60pt'>
	 *
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'><b>Key</b></td> <td align=right
	 *        x:num><b>Code</b></td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 width=198 style='height:12.75pt;width:149pt'>VK_0</td> <td align=right
	 *        width=80 style='width:60pt' x:num>48</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_1</td> <td align=right x:num>49</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_2</td> <td align=right x:num>50</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_3</td> <td align=right x:num>51</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_4</td> <td align=right x:num>52</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_5</td> <td align=right x:num>53</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_6</td> <td align=right x:num>54</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_7</td> <td align=right x:num>55</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_8</td> <td align=right x:num>56</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_9</td> <td align=right x:num>57</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_A</td> <td align=right x:num>65</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ACCEPT</td> <td align=right x:num>30</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ADD</td> <td align=right x:num>107</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_AGAIN</td> <td align=right x:num>65481</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ALL_CANDIDATES</td> <td align=right x:num>256</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ALPHANUMERIC</td> <td align=right x:num>240</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ALT</td> <td align=right x:num>18</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ALT_GRAPH</td> <td align=right x:num>65406</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_AMPERSAND</td> <td align=right x:num>150</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ASTERISK</td> <td align=right x:num>151</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_AT</td> <td align=right x:num>512</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_B</td> <td align=right x:num>66</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_BACK_QUOTE</td> <td align=right x:num>192</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_BACK_SLASH</td> <td align=right x:num>92</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_BACK_SPACE</td> <td align=right x:num>8</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_BRACELEFT</td> <td align=right x:num>161</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_BRACERIGHT</td> <td align=right x:num>162</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_C</td> <td align=right x:num>67</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CANCEL</td> <td align=right x:num>3</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CAPS_LOCK</td> <td align=right x:num>20</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CIRCUMFLEX</td> <td align=right x:num>514</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CLEAR</td> <td align=right x:num>12</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CLOSE_BRACKET</td> <td align=right x:num>93</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CODE_INPUT</td> <td align=right x:num>258</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_COLON</td> <td align=right x:num>513</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_COMMA</td> <td align=right x:num>44</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_COMPOSE</td> <td align=right x:num>65312</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CONTROL</td> <td align=right x:num>17</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CONVERT</td> <td align=right x:num>28</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_COPY</td> <td align=right x:num>65485</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_CUT</td> <td align=right x:num>65489</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_D</td> <td align=right x:num>68</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_ABOVEDOT</td> <td align=right x:num>134</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_ABOVERING</td> <td align=right x:num>136</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_ACUTE</td> <td align=right x:num>129</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_BREVE</td> <td align=right x:num>133</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_CARON</td> <td align=right x:num>138</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_CEDILLA</td> <td align=right x:num>139</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_CIRCUMFLEX</td> <td align=right
	 *        x:num>130</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_DIAERESIS</td> <td align=right x:num>135</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_DOUBLEACUTE</td> <td align=right
	 *        x:num>137</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_GRAVE</td> <td align=right x:num>128</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_IOTA</td> <td align=right x:num>141</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_MACRON</td> <td align=right x:num>132</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_OGONEK</td> <td align=right x:num>140</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_SEMIVOICED_SOUN<span style=
	 *        'display:none'>D</span></td> <td align=right x:num>143</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_TILDE</td> <td align=right x:num>131</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DEAD_VOICED_SOUND</td> <td align=right
	 *        x:num>142</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DECIMAL</td> <td align=right x:num>110</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DELETE</td> <td align=right x:num>127</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DIVIDE</td> <td align=right x:num>111</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DOLLAR</td> <td align=right x:num>515</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_DOWN</td> <td align=right x:num>40</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_E</td> <td align=right x:num>69</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_END</td> <td align=right x:num>35</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ENTER</td> <td align=right x:num>10</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_EQUALS</td> <td align=right x:num>61</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ESCAPE</td> <td align=right x:num>27</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_EURO_SIGN</td> <td align=right x:num>516</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_EXCLAMATION_MARK</td> <td align=right
	 *        x:num>517</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F</td> <td align=right x:num>70</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F1</td> <td align=right x:num>112</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F10</td> <td align=right x:num>121</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F11</td> <td align=right x:num>122</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F12</td> <td align=right x:num>123</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F13</td> <td align=right x:num>61440</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F14</td> <td align=right x:num>61441</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F15</td> <td align=right x:num>61442</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F16</td> <td align=right x:num>61443</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F17</td> <td align=right x:num>61444</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F18</td> <td align=right x:num>61445</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F19</td> <td align=right x:num>61446</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F2</td> <td align=right x:num>113</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F20</td> <td align=right x:num>61447</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F21</td> <td align=right x:num>61448</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F22</td> <td align=right x:num>61449</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F23</td> <td align=right x:num>61450</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F24</td> <td align=right x:num>61451</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F3</td> <td align=right x:num>114</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F4</td> <td align=right x:num>115</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F5</td> <td align=right x:num>116</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F6</td> <td align=right x:num>117</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F7</td> <td align=right x:num>118</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F8</td> <td align=right x:num>119</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_F9</td> <td align=right x:num>120</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_FINAL</td> <td align=right x:num>24</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_FIND</td> <td align=right x:num>65488</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_FULL_WIDTH</td> <td align=right x:num>243</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_G</td> <td align=right x:num>71</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_GREATER</td> <td align=right x:num>160</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_H</td> <td align=right x:num>72</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_HALF_WIDTH</td> <td align=right x:num>244</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_HELP</td> <td align=right x:num>156</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_HIRAGANA</td> <td align=right x:num>242</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_HOME</td> <td align=right x:num>36</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_I</td> <td align=right x:num>73</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_INPUT_METHOD_ON_OFF</td> <td align=right
	 *        x:num>263</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_INSERT</td> <td align=right x:num>155</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_INVERTED_EXCLAMATION_<span style=
	 *        'display:none'>MARK</span></td> <td align=right x:num>518</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_J</td> <td align=right x:num>74</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_JAPANESE_HIRAGANA</td> <td align=right
	 *        x:num>260</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_JAPANESE_KATAKANA</td> <td align=right
	 *        x:num>259</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_JAPANESE_ROMAN</td> <td align=right x:num>261</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_K</td> <td align=right x:num>75</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KANA</td> <td align=right x:num>21</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KANA_LOCK</td> <td align=right x:num>262</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KANJI</td> <td align=right x:num>25</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KATAKANA</td> <td align=right x:num>241</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KP_DOWN</td> <td align=right x:num>225</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KP_LEFT</td> <td align=right x:num>226</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KP_RIGHT</td> <td align=right x:num>227</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_KP_UP</td> <td align=right x:num>224</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_L</td> <td align=right x:num>76</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_LEFT</td> <td align=right x:num>37</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_LEFT_PARENTHESIS</td> <td align=right
	 *        x:num>519</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_LESS</td> <td align=right x:num>153</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_M</td> <td align=right x:num>77</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_META</td> <td align=right x:num>157</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_MINUS</td> <td align=right x:num>45</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_MODECHANGE</td> <td align=right x:num>31</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_MULTIPLY</td> <td align=right x:num>106</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_N</td> <td align=right x:num>78</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NONCONVERT</td> <td align=right x:num>29</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUM_LOCK</td> <td align=right x:num>144</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMBER_SIGN</td> <td align=right x:num>520</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD0</td> <td align=right x:num>96</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD1</td> <td align=right x:num>97</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD2</td> <td align=right x:num>98</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD3</td> <td align=right x:num>99</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD4</td> <td align=right x:num>100</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD5</td> <td align=right x:num>101</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD6</td> <td align=right x:num>102</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD7</td> <td align=right x:num>103</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD8</td> <td align=right x:num>104</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_NUMPAD9</td> <td align=right x:num>105</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_O</td> <td align=right x:num>79</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_OPEN_BRACKET</td> <td align=right x:num>91</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_P</td> <td align=right x:num>80</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PAGE_DOWN</td> <td align=right x:num>34</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PAGE_UP</td> <td align=right x:num>33</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PASTE</td> <td align=right x:num>65487</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PAUSE</td> <td align=right x:num>19</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PERIOD</td> <td align=right x:num>46</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PLUS</td> <td align=right x:num>521</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PREVIOUS_CANDIDATE</td> <td align=right
	 *        x:num>257</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PRINTSCREEN</td> <td align=right x:num>154</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_PROPS</td> <td align=right x:num>65482</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_Q</td> <td align=right x:num>81</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_QUOTE</td> <td align=right x:num>222</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_QUOTEDBL</td> <td align=right x:num>152</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_R</td> <td align=right x:num>82</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_RIGHT</td> <td align=right x:num>39</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_RIGHT_PARENTHESIS</td> <td align=right
	 *        x:num>522</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_ROMAN_CHARACTERS</td> <td align=right
	 *        x:num>245</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_S</td> <td align=right x:num>83</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SCROLL_LOCK</td> <td align=right x:num>145</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SEMICOLON</td> <td align=right x:num>59</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SEPARATER</td> <td align=right x:num>108</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SHIFT</td> <td align=right x:num>16</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SLASH</td> <td align=right x:num>47</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SPACE</td> <td align=right x:num>32</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_STOP</td> <td align=right x:num>65480</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_SUBTRACT</td> <td align=right x:num>109</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_T</td> <td align=right x:num>84</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_TAB</td> <td align=right x:num>9</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_U</td> <td align=right x:num>85</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_UNDEFINED</td> <td align=right x:num>0</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_UNDERSCORE</td> <td align=right x:num>523</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_UNDO</td> <td align=right x:num>65483</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_UP</td> <td align=right x:num>38</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_V</td> <td align=right x:num>86</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_W</td> <td align=right x:num>87</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_X</td> <td align=right x:num>88</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_Y</td> <td align=right x:num>89</td>
	 *        </tr>
	 *        <tr height=17 style='height:12.75pt'>
	 *        <td height=17 style='height:12.75pt'>VK_Z</td> <td align=right x:num>90</td>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        <tr height=0 style='display:none'>
	 *        <td width=198 style='width:149pt'></td>
	 *        <td width=80 style='width:60pt'></td>
	 *        </tr>
	 *        </table>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		this.setRollover(parameters);
		this.setFocusable(parameters);
		this.setHelp(parameters);
		this.setText(parameters);
		this.setAlignment(parameters);
		this.setVerticalAlign(parameters);
		this.setTip(parameters);
		this.setKey(parameters);
		this.setIconAlign(parameters);
		this.setTextAlign(parameters);
		this.setIcon(parameters);
		this.setMargin(parameters);
		this.insets = ParseUtils.getMargin((String) parameters.get(Button.INSETS), this.insets);
		this.setLabelSize(parameters);
		this.setHeight(parameters);
		this.setMnemonic(parameters);
		this.setBold(parameters);
		this.setBorder(parameters);
		this.setFontsize(parameters);
		this.setFontColor(parameters);
		this.setAltIcon(parameters);
		this.setAltText(parameters);
		this.setAltTip(parameters);
		this.setOpaque(parameters);
		this.setBorderVisible(parameters);
		// Parameter expand
		this.expand = ParseUtils.getBoolean((String) parameters.get(Button.EXPAND), false);
		this.setDim(parameters);
		this.setFont(ParseUtils.getFont((String) parameters.get("font"), this.getFont()));
		this.setFocusPainted(ParseUtils.getBoolean((String) parameters.get(Button.PAINTFOCUS), true));
		// pressedicon can be an icon path or 'yes' (to say not delete this
		// parameter).
		// If it is 'yes' then an icon with the same name that the icon
		// parameter + _pressed must be exist
		this.setPressedIcon(parameters);
		this.setdisabledIcon(parameters);
		this.setRollOverIcon(parameters);
		this.installHighlight(parameters);

	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setRollover(final Map parameters) {
		final Object rollover = parameters.get("rollover");
		if (rollover == null) {
			this.setRollover(false);
		} else {
			if (rollover.toString().equalsIgnoreCase("yes")) {
				this.setRollover(true);
			} else {
				this.setRollover(false);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFocusable(final Map parameters) {
		final Object focusable = parameters.get("focusable");
		if (focusable == null) {
		} else {
			if (focusable.toString().equalsIgnoreCase("no")) {
				this.focusable = false;
				Button.logger.debug("{} Focusable: no", this.getKey());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHelp(final Map parameters) {
		final Object helpid = parameters.get("helpid");
		if (helpid != null) {
			this.helpId = helpid.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setText(final Map parameters) {
		// Parameter 'text'
		final Object oButtonText = parameters.get(Button.TEXT);
		if (oButtonText == null) {
		} else {
			this.text = oButtonText.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAlignment(final Map parameters) {
		// Parameter 'alignment'
		final Object oAlignment = parameters.get(Button.ALIGN);
		if (oAlignment == null) {
			this.alignment = GridBagConstraints.NORTH;
		} else {
			if (oAlignment.equals("left")) {
				this.alignment = GridBagConstraints.NORTHWEST;
			} else {
				if (oAlignment.equals("right")) {
					this.alignment = GridBagConstraints.NORTHEAST;
				} else {
					this.alignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setVerticalAlign(final Map parameters) {
		final Object vAlign = parameters.get(Button.VALIGN);
		if (vAlign == null) {
		} else {
			if (vAlign.equals("center")) {
				this.alignmentV = GridBagConstraints.CENTER;
			} else {
				if (vAlign.equals("bottom")) {
					this.alignmentV = GridBagConstraints.SOUTH;
				} else {
					this.alignmentV = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTip(final Map parameters) {
		// Parameter 'tip'
		final Object tip = parameters.get(Button.TIP);
		if (tip == null) {
			this.tooltip = this.text;
		} else {
			this.tooltip = tip.toString();
			this.specifiedTooltip = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setKey(final Map parameters) {
		// Parameter 'key'
		final Object oKey = parameters.get(Button.KEY);
		if (oKey == null) {
			Button.logger.warn("'key' parameter is required");
		} else {
			this.buttonKey = oKey.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setIconAlign(final Map parameters) {
		// Parameter 'iconalign'
		final Object iconalign = parameters.get(Button.ICONALIGN);
		if (iconalign == null) {
			this.setHorizontalTextPosition(SwingConstants.RIGHT);
		} else {
			if (iconalign.toString().equalsIgnoreCase("right")) {
				this.setHorizontalTextPosition(SwingConstants.LEFT);
			} else if (iconalign.toString().equalsIgnoreCase("top")) {
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setVerticalTextPosition(SwingConstants.BOTTOM);
			} else if (iconalign.toString().equalsIgnoreCase("bottom")) {
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setVerticalTextPosition(SwingConstants.TOP);
			} else {
				this.setHorizontalTextPosition(SwingConstants.RIGHT);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextAlign(final Map parameters) {
		// Parameter 'textalign'
		final Object textalign = parameters.get("textalign");
		if (textalign == null) {
			this.setHorizontalAlignment(SwingConstants.CENTER);
		} else {
			if (textalign.toString().equalsIgnoreCase("right")) {
				this.setHorizontalAlignment(SwingConstants.RIGHT);
			} else if (textalign.toString().equalsIgnoreCase("left")) {
				this.setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				this.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setIcon(final Map parameters) {
		// Parameter 'icon'
		final Object icon = parameters.get(Button.ICON);
		if (icon == null) {
			this.setMargin(new Insets(2, 5, 2, 5));
		} else {
			final String sIconFile = icon.toString();
			this.icon = sIconFile;
			if (sIconFile.equals("images/search.png") || sIconFile.equals("images/insert.png")
					|| sIconFile.equals("images/update.png")
					|| sIconFile.equals("images/delete_document.png")) {
				final ImageIcon imIcon = ImageManager.getIcon(sIconFile);
				if (imIcon != null) {
					this.setIcon(imIcon);
					this.setMargin(new Insets(2, 4, 2, 4));
				}
			} else {
				final ImageIcon buttonIcon = ImageManager.getIcon(sIconFile);
				if (buttonIcon != null) {
					this.setIcon(buttonIcon);
					this.setMargin(new Insets(2, 4, 2, 4));
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMargin(final Map parameters) {
		final Object margin = parameters.get(Button.MARGIN);
		if (margin != null) {
			try {
				this.setMargin(ApplicationManager.parseInsets((String) margin));
			} catch (final Exception e) {
				Button.logger.error("{}: 'margin' paramater", this.buttonKey, e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelSize(final Map parameters) {
		final Object labelSize = parameters.get("labelsize");
		if (labelSize != null) {
			try {
				final Integer tamInteger = new Integer(labelSize.toString());
				this.labelSize = tamInteger.intValue();
			} catch (final Exception e) {
				Button.logger.error("Error:'labelsize' parameter: {} ", labelSize.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHeight(final Map parameters) {
		final Object height = parameters.get("height");
		if (height == null) {
		} else {
			try {
				this.preferredHeight = Integer.parseInt(height.toString());
			} catch (final Exception e) {
				Button.logger.error("Error:'height' parameter: {} ", height.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMnemonic(final Map parameters) {
		final Object mnemonic = parameters.get("mnemonic");
		if ((mnemonic != null) && !mnemonic.equals("")) {
			try {
				final int mCode = Integer.parseInt(mnemonic.toString());
				this.setMnemonic(mCode);
			} catch (final Exception e) {
				Button.logger.error("Error:'mnemonic' parameter: {} ", mnemonic.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBold(final Map parameters) {
		final Object bold = parameters.get("bold");
		if (bold != null) {
			if (bold.equals("yes")) {
				this.bold = true;
			} else {
				this.bold = false;
			}
		}
		if (this.bold) {
			this.setBold(this.bold);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorder(final Map parameters) {
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.equals("raised")) {
				this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals("lowered")) {
				this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontsize(final Map parameters) {
		final Object fontsize = parameters.get("fontsize");
		if (fontsize != null) {
			try {
				this.fontSize = Integer.parseInt(fontsize.toString());
				this.setFontSize(this.fontSize);
			} catch (final Exception e) {
				Button.logger.error("Error:'fontsize' parameter: {} ", fontsize.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontColor(final Map parameters) {
		final Object fontcolor = parameters.get("fontcolor");
		if (fontcolor != null) {
			try {
				this.fontColor = ColorConstants.parseColor(fontcolor.toString());
				this.setFontColor(this.fontColor);
			} catch (final Exception e) {
				Button.logger.error("Error:'fontcolor' parameter: {} ", fontcolor.toString(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltIcon(final Map parameters) {
		final Object alticon = parameters.get("alticon");
		if (alticon != null) {
			this.altIcon = alticon.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltText(final Map parameters) {
		final Object alttext = parameters.get("alttext");
		if (alttext != null) {
			this.alttext = alttext.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltTip(final Map parameters) {
		final Object alttip = parameters.get("alttip");
		if (alttip != null) {
			this.altTip = alttip.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOpaque(final Map parameters) {
		if (!ParseUtils.getBoolean((String) parameters.get(Button.OPAQUE), true)) {
			this.setOpaque(false);
			this.setContentAreaFilled(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderVisible(final Map parameters) {
		if (!ParseUtils.getBoolean((String) parameters.get(Button.BORDERVISIBLE), true)) {
			this.setBorderPainted(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDim(final Map parameters) {
		final String dim = ParseUtils.getString((String) parameters.get(Button.DIM), "no");
		if ("text".equalsIgnoreCase(dim)) {
			this.dimtext = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setPressedIcon(final Map parameters) {
		final Icon pressedIcon = ParseUtils.getPressedImageIcon((String) parameters.get(Button.PRESSEDICON),
				(String) parameters.get(Button.ICON), null);
		if (pressedIcon != null) {
			this.setPressedIcon(pressedIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setdisabledIcon(final Map parameters) {
		final Icon disabledIcon = ParseUtils.getDisabledImageIcon((String) parameters.get(Button.DISABLEDICON),
				(String) parameters.get(Button.ICON), null);
		if (disabledIcon != null) {
			this.setDisabledIcon(disabledIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setRollOverIcon(final Map parameters) {
		final Icon rolloverIcon = ParseUtils.getRolloverImageIcon((String) parameters.get(Button.ROLLOVERICON),
				(String) parameters.get(Button.ICON), null);

		if (rolloverIcon != null) {
			this.setRolloverIcon(rolloverIcon);
		}
	}

	protected void installHighlight(final Map params) {
		if (!ParseUtils.getBoolean((String) params.get(Button.OPAQUE), true)
				&& ParseUtils.getBoolean((String) params.get(Button.HIGHLIGHT), false)) {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					if (Button.this.isEnabled()) {
						Button.this.setOpaque(true);
						Button.this.setContentAreaFilled(true);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					Button.this.setOpaque(false);
					Button.this.setContentAreaFilled(false);
				}
			});
		}
	}

	/**
	 * Gets the button key.
	 * <p>
	 * @return the buttonkey parameter
	 */
	public String getKey() {
		return this.buttonKey;
	}

	/**
	 * Gets the text to translate.
	 * <p>
	 * @return the List with text and tooltip to translate
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		if (this.text != null) {
			v.add(this.text);
		}
		if (this.tooltip != null) {
			v.add(this.tooltip);
		}
		if (v.size() > 0) {
			return v;
		}
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resourcesFileName = resources;
		String sLocaleText = null;
		try {
			final String sTextKey = this.altMode ? this.alttext : this.text;
			if (resources != null) {
				sLocaleText = resources.getString(sTextKey);
			}
			if (sLocaleText != null) {
				super.setText(sLocaleText);
			}
		} catch (final Exception e) {
			Button.logger.debug("{}", e.getMessage(), e);
		}
		this.updateTip();
	}

	/**
	 * Sets the locale component.
	 * <p>
	 * @param l the locale
	 */
	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	/**
	 * Sets the text button. if text exists in resources file name, button text will be that file
	 * equivalence.
	 * <p>
	 * @param sText the text string.
	 */
	@Override
	public void setText(final String sText) {
		this.text = sText;
		if (!this.specifiedTooltip) {
			this.tooltip = sText;
		}
		try {
			if (this.resourcesFileName != null) {
				super.setText(this.resourcesFileName.getString(sText));
			} else {
				super.setText(sText);
			}
			if (this.resourcesFileName != null) {
				this.setToolTipText(this.getTextWithKeyStroke(this.resourcesFileName.getString(this.tooltip)));
			} else {
				this.setToolTipText(this.getTextWithKeyStroke(this.tooltip));
			}
		} catch (final Exception e) {
			super.setText(sText);
			this.setToolTipText(this.getTextWithKeyStroke(this.tooltip));
			Button.logger.debug("{}", e.getMessage(), e);
		}
	}

	/**
	 * Gets the keystroke text.
	 * <p>
	 * @param text the basic button text
	 * @return the text to show
	 */
	protected String getTextWithKeyStroke(final String text) {
		if (this.keyStrokeText == null) {
			return text;
		} else {
			return text + " (" + this.keyStrokeText + ")";
		}
	}

	/**
	 * Gets the text button. If parameter <code>translated</code> is true, text button will be
	 * internationalized. In other case, returns the key.
	 * <p>
	 * @param translated the condition to get text
	 * @return the text button
	 */
	public String getText(final boolean translated) {
		if (translated) {
			return this.getText();
		} else {
			return this.text;
		}
	}

	@Override
	public void free() {
		this.resourcesFileName = null;
		this.parentForm = null;
		this.visiblePermission = null;
		this.enabledPermission = null;
		this.fontColor = null;
		this.insets = null;
		Button.logger.trace("Invoke free method");
	}

	@Override
	public void setBorderPainted(final boolean border) {
		super.setBorderPainted(border);
	}

	/**
	 * Sets rollover.
	 * <p>
	 * @param rollover the roll-over condition
	 */
	public void setRollover(final boolean rollover) {
		this.rollover = rollover;
		if (rollover) {
			this.setBorderPainted(false);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		if ((this.rollover) && this.isEnabled()) {
			this.setBorderPainted(true);
		}
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (this.rollover) {
			this.setBorderPainted(false);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	public Form getParentForm() {
		return this.parentForm;
	}

	@Override
	public void setVisible(final boolean vis) {
		if (vis) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(vis);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		super.setEnabled(enabled);
	}

	@Override
	public void repaint() {
		super.repaint();
		final Container c = this.getParent();
		if (c != null) {
			c.repaint();
		}
	}

	/**
	 * Checks the visible permission condition.
	 * <p>
	 * @return the condition about visibility permissions
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.buttonKey != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.buttonKey, true);
				}
			}
			try {
				// Checks to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Button.logger.error("{}", e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the enabled permission condition.
	 * <p>
	 * @return the enable permission condition.
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.buttonKey != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.buttonKey, true);
				}
			}
			try {
				// Checks to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Button.logger.error("{}", e.getMessage(), e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					Button.logger.error(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Initialize permissions.
	 */
	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if (this.preferredHeight >= 0) {
			d.height = this.preferredHeight;
		}
		if (this.labelSize <= 0) {
			return d;
		}

		final FontMetrics fm = this.getFontMetrics(this.getFont().deriveFont(Font.PLAIN));
		int newWidth = this.labelSize * fm.charWidth('A');
		newWidth = newWidth + 20;
		d.width = newWidth;
		return d;
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (this.bold) {
			this.setBold(this.bold);
		}
	}

	/**
	 * Sets the keystroke text and updates the tip.
	 * <p>
	 * @param keyStrokeText the keystroke text
	 */
	public void setKeyStrokeText(final String keyStrokeText) {
		this.keyStrokeText = keyStrokeText;
		this.updateTip();
	}

	/**
	 * Updates the tip.
	 * <p>
	 *
	 * @see #setToolTipText(String)
	 */
	protected void updateTip() {
		final String tipKey = this.altMode ? this.altTip : this.tooltip;
		if (tipKey == null) {
			return;
		}
		try {
			if (this.resourcesFileName != null) {
				this.setToolTipText(this.getTextWithKeyStroke(this.resourcesFileName.getString(tipKey)));
			} else {
				this.setToolTipText(this.getTextWithKeyStroke(tipKey));
			}
		} catch (final Exception e) {
			this.setToolTipText(this.getTextWithKeyStroke(tipKey));
			Button.logger.debug("{}", e.getMessage(), e);
		}
	}

	/**
	 * Sets the bold condition.
	 * <p>
	 * @param bold the bold condition.
	 */
	public void setBold(final boolean bold) {
		try {
			if (bold) {
				this.setFont(this.getFont().deriveFont(Font.BOLD));
			} else {
				this.setFont(this.getFont().deriveFont(Font.PLAIN));
			}
			this.bold = bold;
		} catch (final Exception e) {
			Button.logger.error(" Error establishing bold {}", e.getMessage());
			Button.logger.debug("{}", e.getMessage(), e);
		}
	}

	/**
	 * Sets the font size.
	 * <p>
	 * @param fontSize the font size
	 */
	public void setFontSize(final int fontSize) {
		try {
			this.setFont(this.getFont().deriveFont((float) fontSize));
		} catch (final Exception e) {
			Button.logger.error("Error establishing font size {}", e.getMessage());
			Button.logger.debug("{}", e.getMessage(), e);
		}
	}

	/**
	 * Sets the font color.
	 * <p>
	 * @param fontColor the font color
	 */
	public void setFontColor(final Color fontColor) {
		try {
			this.setForeground(fontColor);
		} catch (final Exception e) {
			Button.logger.error("Error establishing font color {}", e.getMessage());
			Button.logger.debug("{}", e.getMessage(), e);
		}
	}

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	/**
	 * Checks the restricted value.
	 * <p>
	 * @return the restricted condition
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Gets the help id string.
	 * <p>
	 * @return the help id string
	 */
	@Override
	public String getHelpIdString() {
		if (this.helpId != null) {
			return this.helpId;
		}
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		return className + "HelpId";
	}

	/**
	 * Installs a help id.
	 * <p>
	 *
	 * @see HelpUtilities#setHelpIdString(Component, String)
	 */
	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			Button.logger.error("{}", e.getMessage(), e);
			return;
		}
	}

	/**
	 * Sets the tooltip to a button.
	 * <p>
	 * @param key the button key
	 */
	public void setTooltipKey(final String key) {
		this.tooltip = key;
		this.specifiedTooltip = true;
		this.updateTip();
	}

	@Override
	public boolean isFocusTraversable() {
		return this.focusable;
	}

	@Override
	public boolean isRequestFocusEnabled() {
		if (!this.focusable) {
			return false;
		} else {
			return super.isRequestFocusEnabled();
		}
	}

	/**
	 * The alt mode state condition. By default, false.
	 */
	protected boolean altMode = false;

	/**
	 * Sets the alt mode state.
	 * <p>
	 * @param mode the mode condition
	 */
	public void setAltMode(final boolean mode) {
		this.altMode = mode;

		final String iconKey = mode ? this.altIcon : this.icon;
		final String textKey = mode ? this.alttext : this.text;
		if (iconKey != null) {
			this.setIcon(ApplicationManager.getIcon(iconKey));
		}
		if (textKey != null) {
			this.setText(textKey);
		}
		this.updateTip();
	}

	/**
	 * Checks the alt mode state.
	 * <p>
	 * @return the condition
	 */
	public boolean isAltMode() {
		return this.altMode;
	}

}
