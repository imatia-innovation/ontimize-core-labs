package com.ontimize.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.images.ImageManager;

/**
 * @version 1.0
 * @deprecated
 */
@Deprecated
public class ButtonBar extends JToolBar implements FormComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ButtonBar.class);

	protected int alignment = GridBagConstraints.NORTHWEST;

	protected ImageIcon bgImage = null;

	protected boolean rollover = true;

	protected Button hideButton = null;

	private boolean hide = false;

	protected ImageIcon leftImage = null;

	protected ImageIcon rightImage = null;

	private final EmptyBorder margin = new EmptyBorder(4, 5, 4, 5);

	public ButtonBar(final Map parameters) {
		super();
		this.init(parameters);
		this.setFloatable(false);
		if (this.bgImage != null) {
			Border b = this.getBorder();
			if (b == null) {
				b = new EtchedBorder(EtchedBorder.LOWERED);
			}
			this.setBorder(new CompoundBorder(b, this.margin));
		}
		this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		if (this.alignment == GridBagConstraints.NORTHWEST) {
			this.hideButton = new Button(new Hashtable());
		} else if (this.alignment == GridBagConstraints.NORTHEAST) {
			this.hideButton = new Button(new Hashtable());
		}
		if (this.hideButton != null) {
			final ImageIcon iconL = ImageManager.getIcon(ImageManager.LEFT_ARROW);
			if (iconL != null) {
				this.leftImage = iconL;
			} else {
				ButtonBar.logger.debug(ImageManager.LEFT_ARROW + " hasn't been found");
			}

			final ImageIcon iconR = ImageManager.getIcon(ImageManager.RIGHT_ARROW);
			if (iconR != null) {
				this.rightImage = iconR;
			} else {
				ButtonBar.logger.debug(ImageManager.RIGHT_ARROW + " hasn't been found");
			}
			this.hideButton.setFocusPainted(false);
			this.hideButton.setMargin(new Insets(0, 0, 0, 0));
			if (this.alignment == GridBagConstraints.NORTHEAST) {
				this.hideButton.setIcon(this.rightImage);
			} else if (this.alignment == GridBagConstraints.NORTHWEST) {
				this.hideButton.setIcon(this.leftImage);
			}
			this.add(this.hideButton, null);
			this.hideButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ButtonBar.this.hide = !ButtonBar.this.hide;
					if (ButtonBar.this.hide) {
						final Border b = ButtonBar.this.getBorder();
						if (b instanceof CompoundBorder) {
							ButtonBar.this.setBorder(((CompoundBorder) b).getOutsideBorder());
						}
						// If it is hide and on the right then icon to the left
						if (ButtonBar.this.alignment == GridBagConstraints.NORTHEAST) {
							ButtonBar.this.hideButton.setIcon(ButtonBar.this.leftImage);
						} else if (ButtonBar.this.alignment == GridBagConstraints.NORTHWEST) {
							ButtonBar.this.hideButton.setIcon(ButtonBar.this.rightImage);
						}
					} else {
						final Border b = ButtonBar.this.getBorder();
						if (!(b instanceof CompoundBorder)) {
							if (ButtonBar.this.bgImage != null) {
								ButtonBar.this.setBorder(new CompoundBorder(b, ButtonBar.this.margin));
							}
						}
						if (ButtonBar.this.alignment == GridBagConstraints.NORTHEAST) {
							ButtonBar.this.hideButton.setIcon(ButtonBar.this.rightImage);
						} else if (ButtonBar.this.alignment == GridBagConstraints.NORTHWEST) {
							ButtonBar.this.hideButton.setIcon(ButtonBar.this.leftImage);
						}
					}
					for (int i = 0; i < ButtonBar.this.getComponentCount(); i++) {
						final Component c = ButtonBar.this.getComponent(i);
						if (!c.equals(ButtonBar.this.hideButton)) {
							if (ButtonBar.this.hide) {
								c.setVisible(false);
							} else {
								c.setVisible(true);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void init(final Map parameters) {
		final Object rollover = parameters.get("rollover");
		if (rollover == null) {
			this.rollover = true;
		} else {
			if (rollover.toString().equalsIgnoreCase("no")) {
				this.rollover = false;
			} else {
				this.rollover = true;
			}
		}
		final Object bg = parameters.get("bg");
		if (bg != null) {

			this.bgImage = ImageManager.getIcon(bg.toString());

			if (this.bgImage == null) {
				ButtonBar.logger.debug(this.getClass().toString() + ". Image hasn't been found: " + bg.toString());
				ButtonBar.logger.warn("{}. Image hasn't been found: {}", this.getClass().toString(), bg.toString());
			}
		}
		final Object orientation = parameters.get("orientation");
		if (orientation != null) {
			if (orientation.toString().equalsIgnoreCase("v")) {
				this.setOrientation(SwingConstants.VERTICAL);
			} else {
				this.setOrientation(SwingConstants.HORIZONTAL);
			}
		} else {
			this.setOrientation(SwingConstants.HORIZONTAL);
		}

		// Parameter: Alignment
		final Object oAlign = parameters.get("align");
		if (oAlign == null) {
		} else {
			if (oAlign.equals("right")) {
				this.alignment = GridBagConstraints.NORTHEAST;
			} else {
				if (oAlign.equals("left")) {
					this.alignment = GridBagConstraints.NORTHWEST;
				} else {
					this.alignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, this.alignment,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// Image
		if (this.bgImage != null) {
			g.drawImage(this.bgImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

	@Override
	public void add(final Component c, final Object con) {
		if ((this.getComponentCount() > 0) && (this.bgImage != null)) {
			this.addSeparator();
		}
		super.add(c, con);
		if (c instanceof Button) {
			((Button) c).setRollover(true);
			((Button) c).setMargin(new Insets(0, 0, 0, 0));
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
