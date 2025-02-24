package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

public class MemoryMonitorComponent extends JPanel implements FormComponent, IdentifiedElement, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(MemoryMonitorComponent.class);

	protected class MemoryMonitor extends JPanel {

		public Surface surf;

		JPanel controls;

		boolean doControls;

		JTextField tf;

		public MemoryMonitor() {
			this.setLayout(new BorderLayout());
			this.setBorder(new TitledBorder(new EtchedBorder(), "Memory Monitor"));
			this.add(this.surf = new Surface());
			this.controls = new JPanel();
			this.controls.setPreferredSize(new Dimension(270, 240));
			final Font font = new Font("serif", Font.PLAIN, 10);
			JLabel label = new JLabel("Sample Rate");
			label.setFont(font);
			label.setForeground(Color.black);
			this.controls.add(label);
			this.tf = new JTextField("1000");
			this.tf.setPreferredSize(new Dimension(45, 20));
			this.controls.add(this.tf);
			this.controls.add(label = new JLabel("ms"));
			label.setFont(font);
			label.setForeground(Color.black);

			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					MemoryMonitor.this.removeAll();
					if (MemoryMonitor.this.doControls = !MemoryMonitor.this.doControls) {
						MemoryMonitor.this.surf.stop();
						MemoryMonitor.this.add(MemoryMonitor.this.controls);
					} else {
						try {
							MemoryMonitor.this.surf.sleepAmount = Long
									.parseLong(MemoryMonitor.this.tf.getText().trim());
						} catch (final Exception ex) {
							MemoryMonitorComponent.logger.trace(null, ex);
						}
						MemoryMonitor.this.surf.start();
						MemoryMonitor.this.add(MemoryMonitor.this.surf);
					}
					MemoryMonitor.this.validate();
					MemoryMonitor.this.repaint();
				}
			});
		}

		public void setCanvasPreferredSize(final int w, final int h) {
			this.surf.setPreferredSize(new Dimension(w, h));
			this.controls.setPreferredSize(new Dimension(w, h));
			this.revalidate();
		}

		public class Surface extends JPanel implements Runnable {

			public Thread thread;

			public long sleepAmount = 1000;

			private int w, h;

			private BufferedImage buffer;

			private Graphics2D big;

			private final Font font = new Font("Times New Roman", Font.PLAIN, 11);

			private final Runtime r = Runtime.getRuntime();

			private int columnInc;

			private int pts[];

			private int ptNum;

			private int ascent, descent;

			private float freeMemory, totalMemory, maxUsed;

			private final Rectangle graphOutlineRect = new Rectangle();

			private final Rectangle2D mfRect = new Rectangle2D.Float();

			private final Rectangle2D memoryUserRectangle = new Rectangle2D.Float();

			private final Line2D graphLine = new Line2D.Float();

			private final Color graphColor = new Color(46, 139, 87);

			private final Color mfColor = new Color(0, 100, 0);

			private String usedStr;

			private boolean draw = false;

			public Surface() {
				this.setBackground(Color.black);
				this.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(final MouseEvent e) {
						if (Surface.this.thread == null) {
							Surface.this.start();
						} else {
							Surface.this.stop();
						}
					}
				});
				this.setPreferredSize(new Dimension(270, 240));
			}

			@Override
			public Dimension getMinimumSize() {
				return this.getPreferredSize();
			}

			@Override
			public Dimension getPreferredSize() {
				return super.getPreferredSize();
			}

			private void readMemoryData() {
				this.freeMemory = this.r.freeMemory();
				this.totalMemory = this.r.totalMemory();
				this.maxUsed = Math.max(this.totalMemory - this.freeMemory, this.maxUsed);
			}

			@Override
			public void paint(final Graphics g) {

				if (this.big == null) {
					return;
				}

				this.big.setBackground(this.getBackground());
				this.big.clearRect(0, 0, this.w, this.h);

				// .. Draw allocated and used strings ..
				this.big.setColor(Color.green);
				this.big.drawString(String.valueOf((int) this.totalMemory / 1024) + "K alloc", 4.0f,
						this.ascent + 0.5f);
				this.usedStr = String.valueOf((int) (this.totalMemory - this.freeMemory) / 1024) + "K used";
				this.big.drawString(this.usedStr, 4, this.h - this.descent);

				final String maxUsedStr = String.valueOf((int) this.maxUsed / 1024) + "K max used";
				this.big.setColor(Color.red);
				this.big.drawString(maxUsedStr,
						this.getWidth() - 4 - this.big.getFontMetrics(this.big.getFont()).stringWidth(maxUsedStr),
						this.ascent + 0.5f);

				// Calculate remaining size
				final float ssH = this.ascent + this.descent;
				final float remainingHeight = this.h - (ssH * 2) - 0.5f;
				final float blockHeight = remainingHeight / 10;
				final float blockWidth = 20.0f;

				// .. Memory Free ..
				this.big.setColor(this.mfColor);
				final int MemUsage = (int) ((this.freeMemory / this.totalMemory) * 10);
				int i = 0;
				for (; i < MemUsage; i++) {
					this.mfRect.setRect(5, ssH + (i * blockHeight), blockWidth, blockHeight - 1);
					this.big.fill(this.mfRect);
				}

				// .. Memory Used ..
				this.big.setColor(Color.green);
				for (; i < 10; i++) {
					this.memoryUserRectangle.setRect(5, ssH + (i * blockHeight), blockWidth, blockHeight - 1);
					this.big.fill(this.memoryUserRectangle);
				}

				// .. Draw History Graph ..
				this.big.setColor(this.graphColor);
				final int graphX = 30;
				final int graphY = (int) ssH;
				final int graphW = this.w - graphX - 5;
				final int graphH = (int) remainingHeight;
				this.graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
				this.big.draw(this.graphOutlineRect);

				final int graphRow = graphH / 10;

				// .. Draw row ..
				for (int j = graphY; j <= (graphH + graphY); j += graphRow) {
					this.graphLine.setLine(graphX, j, graphX + graphW, j);
					this.big.draw(this.graphLine);
				}

				// .. Draw animated column movement ..
				final int graphColumn = graphW / 15;

				if (this.columnInc == 0) {
					this.columnInc = graphColumn;
				}

				for (int j = graphX + this.columnInc; j < (graphW + graphX); j += graphColumn) {
					this.graphLine.setLine(j, graphY, j, graphY + graphH);
					this.big.draw(this.graphLine);
				}

				--this.columnInc;

				if (this.pts == null) {
					this.pts = new int[graphW];
					this.ptNum = 0;
				} else if (this.pts.length != graphW) {
					int tmp[] = null;
					if (this.ptNum < graphW) {
						tmp = new int[this.ptNum];
						System.arraycopy(this.pts, 0, tmp, 0, tmp.length);
					} else {
						tmp = new int[graphW];
						System.arraycopy(this.pts, this.pts.length - tmp.length, tmp, 0, tmp.length);
						this.ptNum = tmp.length - 2;
					}
					this.pts = new int[graphW];
					System.arraycopy(tmp, 0, this.pts, 0, tmp.length);
				} else {
					this.big.setColor(Color.yellow);
					this.pts[this.ptNum] = (int) (graphY + (graphH * (this.freeMemory / this.totalMemory)));
					for (int j = (graphX + graphW) - this.ptNum, k = 0; k < this.ptNum; k++, j++) {
						if (k != 0) {
							if (this.pts[k] != this.pts[k - 1]) {
								this.big.drawLine(j - 1, this.pts[k - 1], j, this.pts[k]);
							} else {
								this.big.fillRect(j, this.pts[k], 1, 1);
							}
						}
					}
					if ((this.ptNum + 2) == this.pts.length) {
						// throw out oldest point
						for (int j = 1; j < this.ptNum; j++) {
							this.pts[j - 1] = this.pts[j];
						}
						--this.ptNum;
					} else {
						this.ptNum++;
					}
				}
				g.drawImage(this.buffer, 0, 0, this);
			}

			public synchronized void start() {
				if (this.thread == null) {
					this.thread = new Thread(this);
					this.thread.setPriority(Thread.MAX_PRIORITY);
					this.thread.setName("MemoryMonitor");
					this.thread.start();
				}
				this.draw = true;
				this.notify();
			}

			public synchronized void stop() {
				this.draw = false;
				this.notify();
			}

			@Override
			public void run() {

				while (true) {
					synchronized (this) {
						while (!this.draw) {
							try {
								this.wait();
							} catch (final Exception e) {
								MemoryMonitorComponent.logger.trace(null, e);
							}
						}
					}
					if (this.draw) {
						final Dimension d = this.getSize();
						if ((d.width != this.w) || (d.height != this.h)) {
							this.w = d.width;
							this.h = d.height;
							this.buffer = (BufferedImage) this.createImage(this.w, this.h);
							this.big = this.buffer.createGraphics();
							this.big.setFont(this.font);
							final FontMetrics fm = this.big.getFontMetrics(this.font);
							this.ascent = fm.getAscent();
							this.descent = fm.getDescent();
						}
						this.readMemoryData();

						this.repaint();
						try {
							Thread.sleep(this.sleepAmount);
						} catch (final InterruptedException e) {
							MemoryMonitorComponent.logger.trace(null, e);
						}
					}
				}
			}

		}

	}

	protected MemoryMonitor monitor = null;

	protected String attribute = null;

	public MemoryMonitorComponent(final Map parameters) {
		this.monitor = new MemoryMonitor();
		this.setLayout(new BorderLayout());
		this.add(this.monitor);
		this.init(parameters);
	}

	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 0.01, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	public void setCanvasPreferredSize(final int w, final int h) {
		if (this.monitor != null) {
			this.monitor.setCanvasPreferredSize(w, h);
		}
	}

	public void start() {
		if (this.monitor != null) {
			this.monitor.surf.start();
		}
	}

	public void stop() {
		if (this.monitor != null) {
			this.monitor.surf.stop();
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public void initPermissions() {
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
