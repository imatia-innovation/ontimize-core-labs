package com.ontimize.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;

/**
 * Implements a 'splash window', or a window without buttons bar to show images, messages, etc.
 */
public class TopWindow extends JWindow implements Internationalization, ISplash {

	private static final Logger logger = LoggerFactory.getLogger(TopWindow.class);

	JLabel label = null;

	ResourceBundle resources = null;

	JLabel labelIm = null;

	boolean repaintWindow = true;

	JLabel labelIm2 = null;

	protected int repaintTime = 400;

	protected RepaintThread repaintThread = new RepaintThread();

	class RepaintThread extends Thread {

		public RepaintThread() {
			this.setPriority(Thread.MAX_PRIORITY);
		}

		@Override
		public void run() {
			while (TopWindow.this.repaintWindow) {
				try {
					if (EventQueue.isDispatchThread()) {
						TopWindow.this.update();
					} else {
						try {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									TopWindow.this.update();
								}
							});
						} catch (final Exception eIW) {
							TopWindow.logger.trace(null, eIW);
						}
					}
					Thread.sleep(TopWindow.this.repaintTime);
				} catch (final Exception e) {
					TopWindow.logger.trace(null, e);
				}
			}
		}

	};

	public TopWindow(final ResourceBundle res) {
		this.resources = res;
		this.label = new JLabel("");
		this.label.setHorizontalTextPosition(SwingConstants.CENTER);
		this.label.setHorizontalTextPosition(SwingConstants.LEFT);
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane()
		.add(this.label, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		final EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
		this.getRootPane().setBackground(Color.lightGray);
		this.getRootPane().setOpaque(true);
		this.getRootPane().setBorder(border);
		this.pack();
		this.label.setForeground(Color.blue);
		this.center();
	}

	protected void center() {
		// Center the window
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
	}

	public TopWindow(final String message) {
		this.label = new JLabel(message);
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane()
		.add(this.label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		final EtchedBorder borde = new EtchedBorder(EtchedBorder.RAISED);
		this.getRootPane().setBackground(Color.lightGray);
		this.getRootPane().setOpaque(true);
		this.getRootPane().setBorder(borde);
		this.pack();
		this.label.setForeground(Color.blue);
		this.center();
	}

	public TopWindow(final String message, final ResourceBundle res) {
		this.resources = res;
		try {
			if (this.resources != null) {
				this.label = new JLabel(res.getString(message));
			} else {
				this.label = new JLabel(message);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				TopWindow.logger.debug(null, e);
			} else {
				TopWindow.logger.trace(null, e);
			}
			this.label = new JLabel(message);
		}
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane()
		.add(this.label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		final EtchedBorder border = new EtchedBorder(EtchedBorder.RAISED);
		this.getRootPane().setBackground(Color.lightGray);
		this.getRootPane().setOpaque(true);
		this.getRootPane().setBorder(border);
		this.pack();
		this.label.setForeground(Color.blue);
		this.center();
	}

	public TopWindow(final String message, final ImageIcon image, final ImageIcon image2) {
		this(message, null, image, image2);
	}

	public TopWindow(final Frame owner, final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2,
			final Border border) {
		this((Window) owner, message, res, image, image2, border);
	}

	public TopWindow(final Window owner, final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2,
			final Border border) {
		super(owner);
		this.resources = res;
		if (message != null) {
			try {
				if (this.resources != null) {
					this.label = new JLabel(res.getString(message));
				} else {
					this.label = new JLabel(message);
				}
			} catch (final Exception e) {
				TopWindow.logger.trace(null, e);
				this.label = new JLabel(message);
			}
		}
		if (image != null) {
			this.labelIm2 = new JLabel(image);
			this.labelIm2.setOpaque(true);
		}

		this.labelIm = new JLabel(image2);
		this.labelIm.setOpaque(true);
		if (this.label != null) {
			this.label.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		if (this.label != null) {
			this.label.setHorizontalTextPosition(SwingConstants.LEFT);
		}
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane()
		.add(this.labelIm, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (this.label != null) {
			this.getContentPane()
			.add(this.label, new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(10, 10, 10, 5), 0, 0));
		}
		if (this.labelIm2 != null) {
			this.getContentPane()
			.add(this.labelIm2, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		this.getRootPane().setBackground(Color.lightGray);
		this.getRootPane().setOpaque(true);
		this.getRootPane().setBorder(border);
		if (this.label != null) {
			this.label.setForeground(Color.blue);
		}
		if ((this.label == null) && (this.labelIm2 == null)) {
			this.setSize(new Dimension(image2.getIconWidth(), image2.getIconHeight()));
		} else if (this.label == null) {
			this.setSize(new Dimension(image2.getIconWidth(), image2.getIconHeight() + image.getIconHeight()));
		} else {
			this.pack();
		}
		this.center();
	}

	public TopWindow(final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2, final Border border) {
		this.resources = res;
		if (message != null) {
			try {
				if (this.resources != null) {
					this.label = new JLabel(res.getString(message));
				} else {
					this.label = new JLabel(message);
				}
			} catch (final Exception e) {
				TopWindow.logger.trace(null, e);
				this.label = new JLabel(message);
			}
		}
		if (image != null) {
			this.labelIm2 = new JLabel(image);
			this.labelIm2.setOpaque(true);
		}
		this.labelIm = new JLabel(image2);
		this.labelIm.setOpaque(true);
		if (this.label != null) {
			this.label.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		if (this.label != null) {
			this.label.setHorizontalTextPosition(SwingConstants.LEFT);
		}

		this.getContentPane().setLayout(new GridBagLayout());

		this.getContentPane()
		.add(this.labelIm, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (this.label != null) {
			this.getContentPane()
			.add(this.label, new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(10, 10, 10, 5), 0, 0));
		}
		if (this.labelIm2 != null) {
			this.getContentPane()
			.add(this.labelIm2, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		this.getRootPane().setBackground(Color.lightGray);
		this.getRootPane().setOpaque(true);
		this.getRootPane().setBorder(border);
		if (this.label != null) {
			this.label.setForeground(Color.blue);
		}
		if ((this.label == null) && (this.labelIm2 == null)) {
			this.setSize(new Dimension(image2.getIconWidth(), image2.getIconHeight()));
		} else if (this.label == null) {
			this.setSize(new Dimension(image2.getIconWidth(), image2.getIconHeight() + image.getIconHeight()));
		} else {
			this.pack();
		}
		this.center();
	}

	public TopWindow(final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2) {
		this(message, res, image, image2, new EtchedBorder(EtchedBorder.RAISED));
	}

	public TopWindow(final Window owner, final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2) {
		this(owner, message, res, image, image2, new EtchedBorder(EtchedBorder.RAISED));
	}

	public TopWindow(final Frame owner, final String message, final ResourceBundle res, final ImageIcon image, final ImageIcon image2) {
		this((Window) owner, message, res, image, image2, new EtchedBorder(EtchedBorder.RAISED));
	}

	public TopWindow(final JPanel panel) {
	}

	@Override
	public void show() {
		if (SwingUtilities.isEventDispatchThread()) {
			super.show();
			this.update();
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					TopWindow.this.setVisible(true);
					TopWindow.this.update();
				}
			});
		}
	}

	@Override
	public void setRepaintTime(final int repaintTime) {
		this.repaintTime = repaintTime;
	}

	/**
	 * repaint specifies that the window must be painted each <code>repaintTime</code> milliseconds
	 * using a high priority thread, but this painting does not happen if the event dispatcher thread is
	 * busy
	 */
	@Override
	public void show(final boolean repaint) {
		if (SwingUtilities.isEventDispatchThread()) {
			super.show();
			this.update();
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					TopWindow.this.show();
					TopWindow.this.update();
				}
			});
		}
		if (repaint) {
			this.repaintWindow = true;
			if ((this.repaintThread == null) || !this.repaintThread.isAlive()) {
				this.repaintThread = new RepaintThread();
				this.repaintThread.start();
			}

		}
	}

	/**
	 * Show the window during the specified time. If this time is 0, then shows the window until the
	 * next call to the hide method. If the window is already visible then does nothing
	 * @param milliseconds
	 */
	public void show(final int milliseconds) {
		if (!this.isVisible()) {
			if (SwingUtilities.isEventDispatchThread()) {
				super.show();
				this.update();
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						TopWindow.super.show();
						TopWindow.this.update();
					}
				});
			}
			final Thread t = new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(milliseconds);
					} catch (final Exception e) {
						TopWindow.logger.trace(null, e);
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							TopWindow.super.hide();
							TopWindow.this.repaintWindow = false;
						}
					});
				}
			};
			t.start();
		}
	}

	@Override
	public void hide() {
		this.repaintWindow = false;
		Thread.yield();
		try {
			if (this.repaintThread != null) {
				this.repaintThread.join(100);
			}
		} catch (final InterruptedException e) {
			TopWindow.logger.error(null, e);
		}
		if (SwingUtilities.isEventDispatchThread()) {
			super.hide();
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					TopWindow.super.hide();
					TopWindow.this.repaintWindow = false;
				}
			});
		}
	}

	/**
	 * Update the text in the window if this window shows a text, in other case does nothing.
	 * @param text
	 */
	@Override
	public void updateText(final String text) {
		if (!this.repaintWindow) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						if (TopWindow.this.resources != null) {
							TopWindow.this.label.setText(TopWindow.this.resources.getString(text));
						} else {
							TopWindow.this.label.setText(text);
						}
					} catch (final Exception e) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							TopWindow.logger.debug(null, e);
						} else {
							TopWindow.logger.trace(null, e);
						}
						TopWindow.this.label.setText(text);
					}
					TopWindow.this.label.paintImmediately(TopWindow.this.label.getBounds());
				}
			});
		} else {
			try {
				if (this.resources != null) {
					this.label.setText(this.resources.getString(text));
				} else {
					this.label.setText(text);
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					TopWindow.logger.debug(null, e);
				} else {
					TopWindow.logger.trace(null, e);
				}
				this.label.setText(text);
			}
			this.label.paintImmediately(this.label.getBounds());
		}
	}

	public void updateIcon(final ImageIcon icon) {
		if (!this.repaintWindow) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						TopWindow.this.label.setIcon(icon);
					} catch (final Exception e) {
						TopWindow.logger.trace(null, e);
					}
					TopWindow.this.pack();
					TopWindow.this.center();
				}
			});
		} else {
			try {
				this.label.setIcon(icon);
			} catch (final Exception e) {
				TopWindow.logger.trace(null, e);
			}
			TopWindow.this.pack();
			this.center();
		}
	}

	/**
	 * Repaint the window
	 */
	public void update() {
		TopWindow.this.getRootPane()
		.paintImmediately(0, 0, TopWindow.this.getRootPane().getWidth(), TopWindow.this.getRootPane().getHeight());
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resources = res;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.label.getText());
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void pack() {
		super.pack();
	}

	public void setTextColor(final Color color) {
		if (this.label != null) {
			this.label.setForeground(color);
		}
	}

	public static TopWindow createSearchingWindow(final ResourceBundle res) {
		final TopWindow t = new TopWindow(res);
		t.updateText("performing_query");
		t.updateIcon(new ImageIcon(t.getClass().getResource("images/searching.png")));
		return t;
	}

	public static TopWindow createWorkingWindow(final ResourceBundle res) {
		final TopWindow t = new TopWindow(res);
		t.updateText("M_TRABAJANDO");
		t.updateIcon(new ImageIcon(t.getClass().getResource("images/working2.png")));
		return t;

	}

}
