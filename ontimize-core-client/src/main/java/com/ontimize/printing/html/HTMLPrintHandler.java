package com.ontimize.printing.html;

/**
 * Class to print HTML documents. This class tries to separate the document in different pages in an
 * appropriate way
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLPrintHandler {

	private static final Logger logger = LoggerFactory.getLogger(HTMLPrintHandler.class);

	public static boolean DEBUG = true;

	public static boolean DEBUG2 = true;

	protected boolean printJobAccessEnabled = true;

	protected PageFormat pf;

	private HTMLPrintHandler() {
	}

	private static void debug(final String s) {
		if (HTMLPrintHandler.DEBUG) {
			HTMLPrintHandler.logger.debug(s);
		}
	}

	private static void debug2(final String s) {
		if (HTMLPrintHandler.DEBUG2) {
			HTMLPrintHandler.logger.debug(s);
		}
	}

	protected static Rectangle getViewRect(final View view, final float f, final float f_21_) {
		final Rectangle rectangle = new Rectangle();
		view.setSize(f, f_21_);
		rectangle.width = (int) Math.max((long) Math.ceil(view.getMinimumSpan(0)), (long) f);
		rectangle.height = (int) Math.min((long) Math.ceil(view.getPreferredSpan(1)), 2147483647L);
		view.setSize(rectangle.width, rectangle.height);
		if (view.getView(0) instanceof BoxView) {
			final BoxView boxview = (BoxView) view.getView(0);
			rectangle.width = boxview.getWidth();
			rectangle.height = boxview.getHeight();
		} else {
			rectangle.height = (int) Math.min((long) Math.ceil(view.getPreferredSpan(1)), 2147483647L);
		}
		return rectangle;
	}

	class HTMLPrintable implements Printable {

		boolean scaleToFit;

		JFrame frame;

		JEditorPane editor;

		List transforms;

		List clips;

		public HTMLPrintable(final JFrame f, final boolean bool) {
			this.scaleToFit = bool;
			this.frame = f;
			this.editor = (JEditorPane) f.getContentPane();
		}

		public List createTransforms(final JEditorPane jeditorpane, final PageFormat pageformat) {
			int i = 0;
			final List vector = new Vector();
			double d = 0.0;
			double d_0_ = 0.0;
			double d_1_ = 0.0;
			double d_2_ = 1.0;
			final View view = jeditorpane.getUI().getRootView(jeditorpane);
			final Rectangle rectangle = HTMLPrintHandler.getViewRect(view, (float) pageformat.getImageableWidth(),
					(float) pageformat.getImageableHeight());
			HTMLPrintHandler.debug("viewRec=" + rectangle);
			final Insets insets = jeditorpane.getInsets();
			this.frame.setBounds(0, 0, rectangle.width + insets.left + insets.right,
					rectangle.height + insets.top + insets.bottom);
			this.frame.setVisible(true);
			if (this.scaleToFit) {
				if (rectangle.getWidth() > pageformat.getImageableWidth()) {
					d_2_ = pageformat.getImageableWidth() / rectangle.getWidth();
				}
				HTMLPrintHandler.debug("scale=" + d_2_ + " ImageableWidth=" + pageformat.getImageableWidth() + " width="
						+ rectangle.getWidth());
			}
			final Rectangle2D.Double var_double = new Rectangle2D.Double(0.0, 0.0, pageformat.getImageableWidth(),
					pageformat.getImageableHeight() / d_2_);
			HTMLPrintHandler.debug("printRec=" + var_double);
			final Position.Bias[] biases = new Position.Bias[1];
			for (;;) {
				HTMLPrintHandler.debug("preparing page=" + i + " curHeight=" + d);
				final double d_3_ = var_double.getHeight() + d;
				final int i_4_ = view.viewToModel(0.0F, (float) d_3_, rectangle, biases);
				HTMLPrintHandler.debug2("point=" + i_4_);
				try {
					final Shape shape = view.modelToView(i_4_, rectangle, biases[0]);
					final Rectangle2D rectangle2d = shape.getBounds2D();
					HTMLPrintHandler.debug2("pointRec=" + rectangle2d);
					d_1_ = d;
					d_0_ = rectangle2d.getY() - 1.0;
					HTMLPrintHandler.debug2("Starting height=" + d_0_);
					if (d_3_ >= (rectangle2d.getY() + rectangle2d.getHeight())) {
						d_0_ = (rectangle2d.getY() + rectangle2d.getHeight()) - 1.0;
						HTMLPrintHandler.debug2("Adjusted height=" + d_0_);
					}
					double d_5_ = rectangle2d.getY();
					double d_6_ = (rectangle2d.getY() + rectangle2d.getHeight()) - 1.0;
					double d_7_ = rectangle2d.getX() + 20.0;
					double d_8_ = 0.0;
					double d_9_ = 0.0;
					Rectangle2D rectangle2d_10_ = rectangle2d;
					while (!(d_7_ > (pageformat.getImageableWidth() * d_2_))) {
						final int i_11_ = view.viewToModel((float) d_7_, (float) d_3_, rectangle, biases);
						final Shape shape_12_ = view.modelToView(i_11_, rectangle, biases[0]);
						final Rectangle2D rectangle2d_13_ = shape_12_.getBounds2D();
						if (rectangle2d_10_.equals(rectangle2d_13_) || (rectangle2d_13_.getX() < d_7_)) {
							d_7_ += 20.0;
						} else {
							HTMLPrintHandler.debug2("pointRec2=" + rectangle2d_13_);
							d_8_ = rectangle2d_13_.getY();
							d_9_ = (rectangle2d_13_.getY() + rectangle2d_13_.getHeight()) - 1.0;
							if (d_9_ > d_5_) {
								if (d_9_ > d_6_) {
									if (d_9_ < d_3_) {
										d_0_ = d_9_;
										d_6_ = d_9_;
										if (d_8_ < d_5_) {
											d_5_ = d_8_;
										}
										HTMLPrintHandler.debug2("Adjust height to testheight " + d_0_);
									} else if (d_8_ > d_6_) {
										d_0_ = d_8_ - 1.0;
										d_6_ = d_9_;
										d_5_ = d_8_;
										HTMLPrintHandler.debug2("new base component " + d_0_);
									} else if (d_8_ < d_5_) {
										d_0_ = d_8_ - 1.0;
										d_5_ = d_8_;
										d_6_ = d_9_;
										HTMLPrintHandler
										.debug2("test height > maxheight. Adjust height testY - 1 " + d_0_);
									} else {
										d_0_ = d_5_ - 1.0;
										d_6_ = d_9_;
										HTMLPrintHandler
										.debug2("test height > maxheight. Adjust height baseY - 1 " + d_0_);
									}
								} else if (d_6_ < d_3_) {
									d_0_ = d_6_;
									if (d_8_ < d_5_) {
										d_5_ = d_8_;
									}
									HTMLPrintHandler.debug2("baseHeight ok " + d_0_);
								} else if (d_5_ <= d_8_) {
									d_0_ = d_5_ - 1.0;
									HTMLPrintHandler.debug2("baseHeight too long - height ok" + d_0_);
								} else {
									d_0_ = d_8_ - 1.0;
									d_5_ = d_8_;
									HTMLPrintHandler.debug2("baseHeight too long - use testY - 1 " + d_0_);
								}
							}
							rectangle2d_10_ = rectangle2d_13_;
							d_7_ = rectangle2d_10_.getX() + 20.0;
						}
					}
					final PageTransform pagetransform = new PageTransform();
					pagetransform.translate(pageformat.getImageableX(), pageformat.getImageableY());
					HTMLPrintHandler.debug("t.translate=" + pagetransform);
					pagetransform.translate(-(double) insets.left * d_2_, -(insets.top + d_1_) * d_2_);
					HTMLPrintHandler.debug("t.translate=" + pagetransform);
					pagetransform.scale(d_2_, d_2_);
					HTMLPrintHandler.debug("t.scale=" + pagetransform);
					pagetransform.setHeight(d_0_ + insets.top);
					vector.add(i, pagetransform);
					d = d_0_ + 1.0;
					HTMLPrintHandler.debug("Setting curHeight=" + d);
					i++;
					if (d >= rectangle.getHeight()) {
						break;
					}
				} catch (final BadLocationException badlocationexception) {
					HTMLPrintHandler.logger.trace(null, badlocationexception);
					break;
				}
			}
			return vector;
		}

		@Override
		public int print(final Graphics graphics, final PageFormat pageformat, final int i) {
			final Graphics2D graphics2d = (Graphics2D) graphics;
			this.editor.setDropTarget(null);
			if (this.transforms == null) {
				this.transforms = this.createTransforms(this.editor, pageformat);
			}
			HTMLPrintHandler.debug("\n\n\nPrinting page=" + i);
			if (i >= this.transforms.size()) {
				return 1;
			}
			if (graphics2d.getClip() == null) {
				HTMLPrintHandler.debug("Graphics clip=null");
				final Rectangle2D.Double var_double = new Rectangle2D.Double(pageformat.getImageableX(),
						pageformat.getImageableY(), pageformat.getImageableWidth(),
						pageformat.getImageableHeight());
				graphics2d.setClip(var_double);
			}
			HTMLPrintHandler.debug("Graphics tansform=" + graphics2d.getTransform());
			HTMLPrintHandler.debug("Graphics clip=" + graphics2d.getClip());
			graphics2d.transform((AffineTransform) this.transforms.get(i));
			HTMLPrintHandler.debug("Graphics tansform=" + graphics2d.getTransform());
			HTMLPrintHandler.debug("Graphics clip=" + graphics2d.getClip());
			Shape shape = graphics2d.getClip();
			Rectangle2D rectangle2d = shape.getBounds2D();
			final double d = ((PageTransform) this.transforms.get(i)).getHeight();
			final double d_14_ = (rectangle2d.getY() + rectangle2d.getHeight()) - 1.0 - d;
			if (d_14_ > 0.0) {
				HTMLPrintHandler.debug("Graphics adjusted height=" + d_14_);
				final Rectangle2D.Double var_double = new Rectangle2D.Double(rectangle2d.getX(), rectangle2d.getY(),
						rectangle2d.getWidth(), rectangle2d.getHeight() - d_14_);
				graphics2d.clip(var_double);
				shape = graphics2d.getClip();
				rectangle2d = shape.getBounds2D();
				HTMLPrintHandler.debug("Graphics tansform=" + graphics2d.getTransform());
				HTMLPrintHandler.debug("Graphics clip=" + graphics2d.getClip());
			}
			if (rectangle2d.getY() < d) {
				this.editor.paint(graphics2d);
			} else {
				return 1;
			}
			return 0;
		}

	}

	class PageTransform extends AffineTransform {

		private double height;

		public double getHeight() {
			return this.height;
		}

		public void setHeight(final double d) {
			this.height = d;
		}

	}

	class Print1dot2 extends Thread {

		PageFormat pf;

		PrinterJob job;

		JFrame frame;

		JEditorPane editor;

		Print1dot2(final PrinterJob printerjob, final JEditorPane editorPane) {
			this.setDaemon(true);
			this.editor = editorPane;
			this.pf = HTMLPrintHandler.this.getPF();
			this.job = printerjob;
		}

		@Override
		public void run() {
			try {
				if (this.job.printDialog()) {
					this.frame = new JFrame();
					this.frame.setContentPane(this.editor);
					this.startPrinting();
				}
			} catch (final Exception exception) {
				HTMLPrintHandler.logger.error(null, exception);
			}
		}

		public void startPrinting() {
			if (this.job != null) {
				try {
					Thread.sleep(1000L);
				} catch (final InterruptedException interruptedexception) {
					HTMLPrintHandler.logger.error(null, interruptedexception);
				}
				this.job.setPrintable(new HTMLPrintable(this.frame, true),
						this.pf == null ? this.job.defaultPage() : this.pf);
				try {
					this.job.print();
				} catch (final PrinterException printerexception) {
					HTMLPrintHandler.logger.error(null, printerexception);
				}
			}
		}

	}

	class PageDialog extends Thread {

		PrinterJob job;

		PageFormat pf;

		PageDialog(final PrinterJob printerjob) {
			this.setDaemon(true);
			this.pf = HTMLPrintHandler.this.getPF();
			this.job = printerjob;
		}

		@Override
		public void run() {
			try {
				final PageFormat pageformat = this.job.pageDialog(this.pf == null ? this.job.defaultPage() : this.pf);
				if (this.pf != pageformat) {
					this.pf = pageformat;
					HTMLPrintHandler.this.setPF(this.pf);
				}
			} catch (final Exception exception) {
				HTMLPrintHandler.logger.error(null, exception);
			}
		}

	}

	public void pageSetup(final PrinterJob printerjob) {
		new PageDialog(printerjob).start();
	}

	public void print(final JEditorPane editorPane) {
		PrinterJob printerjob = null;
		if (this.printJobAccessEnabled) {
			try {
				printerjob = PrinterJob.getPrinterJob();
			} catch (final SecurityException securityexception) {
				HTMLPrintHandler.logger.error(null, securityexception);
				this.printJobAccessEnabled = false;
			}
		}
		if (printerjob != null) {
			new Print1dot2(printerjob, editorPane).start();
		}
	}

	public PageFormat getPF() {
		synchronized (this) {
			return this.pf;
		}
	}

	public void setPF(final PageFormat pageformat) {
		synchronized (this) {
			this.pf = pageformat;
		}
	}

	/**
	 * Prints the document with the specified html code. Here the document codebase is not established
	 * then relative paths, like images, do not appear
	 * @param html
	 * @throws Exception
	 */
	public synchronized static void printDocument(final String html) throws Exception {
		HTMLPrintHandler.printDocument(html, null);
	}

	public synchronized static void printDocument(final String html, final URL base) throws Exception {
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setDoubleBuffered(false);
		final HTMLEditorKit editorKit = new HTMLEditorKit();
		final HTMLDocument doc = new HTMLDocument();
		if (base != null) {
			doc.setBase(base);
		}
		editorPane.setEditorKit(editorKit);
		editorPane.setDocument(doc);
		editorPane.read(new StringReader(html), null);
		// Start the printin job
		final HTMLPrintHandler ph = new HTMLPrintHandler();
		ph.print(editorPane);
	}

	public synchronized static void printDocument(final URL page) {
	}

	public synchronized static void printDocument(final File f) throws Exception {
		HTMLPrintHandler.printDocument(f, null);
	}

	public synchronized static void printDocument(final File f, final URL base) throws Exception {
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setDoubleBuffered(false);
		final HTMLEditorKit editorKit = new HTMLEditorKit();
		final HTMLDocument doc = new HTMLDocument();
		if (base != null) {
			doc.setBase(base);
		}
		editorPane.setEditorKit(editorKit);
		editorPane.setDocument(doc);
		editorPane.read(new FileReader(f), null);
		// Start the printing job
		final HTMLPrintHandler ph = new HTMLPrintHandler();
		ph.print(editorPane);
	}

	public static void main(final String args[]) throws Exception {
		final JFileChooser fc = new JFileChooser();
		final int iOption = fc.showOpenDialog(null);
		if (iOption == JFileChooser.APPROVE_OPTION) {
			final File f = fc.getSelectedFile();
			HTMLPrintHandler.printDocument(f);
		}
	}

}
