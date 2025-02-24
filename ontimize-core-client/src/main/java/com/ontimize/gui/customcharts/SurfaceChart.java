package com.ontimize.gui.customcharts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.Pair;
import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;

/**
 * Panel with a custom <code>Canvas</code> used for surface in charts.
 *
 * @author Imatia Innovation
 */
public class SurfaceChart extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(SurfaceChart.class);

	public static boolean DEBUG = true;

	protected SurfaceCanvas canvas;

	private static final int INIT_CALC_DIV = 20;

	private static final int INIT_DISP_DIV = 20;

	private static final int MIN_X = -3;

	private static final int MIN_Y = -3;

	private static final int MIN_Z = -3;

	private static final int MAX_X = 3;

	private static final int MAX_Y = 3;

	private static final int MAX_Z = 3;

	public static final int WIREFRAME = 0;

	public static final int NORENDER = 1;

	public static final int SPECTRUM = 2;

	public static final int GRAYSCALE = 3;

	public static final int DUALSHADE = 4;

	protected static class ControlPanel extends JPanel {

		JTextField tfDispDivisions = new JTextField();

		JTextField tfCalcDivisions = new JTextField();

		JTextField tfXMin = new JTextField();

		JTextField tfXMax = new JTextField();

		JTextField tfYMin = new JTextField();

		JTextField tfYMax = new JTextField();

		JTextField tfZMin = new JTextField();

		JTextField tfZMax = new JTextField();

		JLabel lDispDivisions = new JLabel("Disp.Div. ");

		JLabel lCalcDivisions = new JLabel("Calc.Div. ");

		JLabel lXMin = new JLabel("X Min ");

		JLabel lXMax = new JLabel("X Max ");

		JLabel lYMin = new JLabel("Y Min ");

		JLabel lYMax = new JLabel("Y Max ");

		JLabel lZMin = new JLabel("Z Min ");

		JLabel lZMax = new JLabel("Z Max ");

		JCheckBox grid = new JCheckBox("Grid");

		JCheckBox labelXY = new JCheckBox("Label XY");

		JCheckBox labelZ = new JCheckBox("Label Z");

		JCheckBox mesh = new JCheckBox("Mesh");

		JComboBox combo = new JComboBox(
				new Object[] { "WIREFRAME", "HIDDEN", "COLOR LEVELS", "GRAY SCALE", "DUAL SHADE" });

		SurfaceChart chart = null;

		public ControlPanel(final SurfaceChart ch) {
			super(new GridBagLayout());
			this.chart = ch;
			final JPanel pAux = new JPanel(new GridLayout(0, 2));

			pAux.add(this.lCalcDivisions);
			pAux.add(this.tfCalcDivisions);
			pAux.add(this.lDispDivisions);
			pAux.add(this.tfDispDivisions);

			pAux.add(this.lXMin);
			pAux.add(this.tfXMin);
			pAux.add(this.lXMax);
			pAux.add(this.tfXMax);

			pAux.add(this.lYMin);
			pAux.add(this.tfYMin);
			pAux.add(this.lYMax);
			pAux.add(this.tfYMax);

			pAux.add(this.lZMin);
			pAux.add(this.tfZMin);
			pAux.add(this.lZMax);
			pAux.add(this.tfZMax);

			this.add(pAux, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

			this.add(this.combo, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTH,
					GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

			this.add(new JPanel(), new GridBagConstraints(0, 10, 1, 1, 1, 1, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

			this.initControlPanel();
			this.combo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ControlPanel.this.chart.setPlotMode(ControlPanel.this.combo.getSelectedIndex());
				}
			});

			final FocusAdapter f = new FocusAdapter() {

				@Override
				public void focusLost(final FocusEvent e) {
					if (e.isTemporary()) {
						return;
					}
					try {
						final JTextField t = (JTextField) e.getSource();
						final float f = Float.parseFloat(t.getText());
						if (e.getSource() == ControlPanel.this.tfXMin) {
							if (Float.compare(f, ControlPanel.this.chart.getXMin()) != 0) {
								ControlPanel.this.chart.canvas.setRanges(f, ControlPanel.this.chart.getXMax(),
										ControlPanel.this.chart.getYMin(),
										ControlPanel.this.chart.getYMax());
								ControlPanel.this.chart.recalculateExpression();
							}
						} else if (e.getSource() == ControlPanel.this.tfXMax) {
							if (Float.compare(f, ControlPanel.this.chart.getXMax()) != 0) {

								ControlPanel.this.chart.canvas.setRanges(ControlPanel.this.chart.getXMin(), f,
										ControlPanel.this.chart.getYMin(),
										ControlPanel.this.chart.getYMax());
								ControlPanel.this.chart.recalculateExpression();
							}
						}

						else if (e.getSource() == ControlPanel.this.tfYMax) {
							if (Float.compare(f, ControlPanel.this.chart.getYMax()) != 0) {
								ControlPanel.this.chart.canvas.setRanges(ControlPanel.this.chart.getXMin(),
										ControlPanel.this.chart.getXMax(), ControlPanel.this.chart.getYMin(),
										f);
								ControlPanel.this.chart.recalculateExpression();
							}
						}

						else if (e.getSource() == ControlPanel.this.tfYMin) {
							if (Float.compare(f, ControlPanel.this.chart.getYMin()) != 0) {

								ControlPanel.this.chart.canvas.setRanges(ControlPanel.this.chart.getXMin(),
										ControlPanel.this.chart.getXMax(), f,
										ControlPanel.this.chart.getYMax());
								ControlPanel.this.chart.recalculateExpression();
							}
						}

						else if (e.getSource() == ControlPanel.this.tfZMax) {
							if (Float.compare(f, ControlPanel.this.chart.getZMax()) != 0) {
								ControlPanel.this.chart.canvas.setRanges(ControlPanel.this.chart.getXMin(),
										ControlPanel.this.chart.getXMax(), ControlPanel.this.chart.getYMin(),
										ControlPanel.this.chart.getYMax(), ControlPanel.this.chart.getZMin(), f);
								ControlPanel.this.chart.recalculateExpression();
							}
						}

						else if (e.getSource() == ControlPanel.this.tfZMin) {
							if (Float.compare(f, ControlPanel.this.chart.getZMin()) != 0) {
								ControlPanel.this.chart.canvas.setRanges(ControlPanel.this.chart.getXMin(),
										ControlPanel.this.chart.getXMax(), ControlPanel.this.chart.getYMin(),
										ControlPanel.this.chart.getYMax(), f, ControlPanel.this.chart.getZMax());
								ControlPanel.this.chart.recalculateExpression();
							}
						}

						else if (e.getSource() == ControlPanel.this.tfDispDivisions) {
							ControlPanel.this.chart.setDispDivisions((int) f);
						}

						else if (e.getSource() == ControlPanel.this.tfCalcDivisions) {
							ControlPanel.this.chart.canvas.setCalcDivisions((int) f);
							ControlPanel.this.chart.recalculateExpression();
						}

					} catch (final Exception ex) {
						SurfaceChart.logger.error(null, ex);
						JOptionPane.showMessageDialog(ControlPanel.this.tfXMin, ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};

			this.tfXMax.addFocusListener(f);
			this.tfXMin.addFocusListener(f);

			this.tfYMax.addFocusListener(f);
			this.tfYMin.addFocusListener(f);

			this.tfZMax.addFocusListener(f);
			this.tfZMin.addFocusListener(f);

			this.tfDispDivisions.addFocusListener(f);
			this.tfCalcDivisions.addFocusListener(f);
		}

		protected void initControlPanel() {
			this.tfXMax.setText(String.valueOf(this.chart.canvas.xmax));
			this.tfXMin.setText(String.valueOf(this.chart.canvas.xmin));

			this.tfYMax.setText(String.valueOf(this.chart.canvas.ymax));
			this.tfYMin.setText(String.valueOf(this.chart.canvas.ymin));

			this.tfZMax.setText(String.valueOf(this.chart.canvas.zmax));
			this.tfZMin.setText(String.valueOf(this.chart.canvas.zmin));

			this.tfCalcDivisions.setText(String.valueOf(this.chart.canvas.calc_divisions));
			this.tfDispDivisions.setText(String.valueOf(this.chart.canvas.displayDivisions));

			this.combo.setSelectedIndex(this.chart.getPlotMode());
		}

	}

	public SurfaceChart() {
		this(40, 40, -10, 10, -10, 10, -10, 10, "1/sqrt(x*3+2*y^3)");
	}

	public SurfaceChart(final int calDiv, final int dispDiv, final float xmin, final float xmax, final float ymin, final float ymax, final float zmin,
			final float zmax) {
		this(calDiv, dispDiv, xmin, xmax, ymin, ymax, zmin, zmax, null);
	}

	public SurfaceChart(final int calDiv, final int dispDiv, final float xmin, final float xmax, final float ymin, final float ymax, final float zmin, final float zmax,
			final String expr) {
		this.setLayout(new BorderLayout());
		this.init(expr, xmin, xmax, ymin, ymax, zmin, zmax, calDiv, dispDiv);

		this.add(this.canvas, BorderLayout.CENTER);
	}

	public void recalculateExpression() {
		if (this.canvas != null) {
			this.canvas.recalculateExpression();
		}
	}

	public void setExpression(final String exp) {
		if (this.canvas != null) {
			this.canvas.calculate(exp);
		}
	}

	@Override
	public void setBackground(final Color c) {
		super.setBackground(c);
		if (this.canvas != null) {
			this.canvas.setBackground(c);
		}
	}

	public void setPlotMode(final int p) {
		this.canvas.setPlotMode(p);
	}

	public void setBoxed(final boolean b) {
		this.canvas.setBoxed(b);
	}

	public void setMesh(final boolean b) {
		this.canvas.setMesh(b);
	}

	public void setScaleBox(final boolean b) {
		this.canvas.setScaleBox(b);
	}

	public void setDisplayXY(final boolean b) {
		this.canvas.setDisplayXY(b);
	}

	public void setDisplayZ(final boolean b) {
		this.canvas.setDisplayZ(b);
	}

	public void setDisplayGrids(final boolean b) {
		this.canvas.setDisplayGrids(b);
	}

	private void init(final String expr, final float xmin, final float xmax, final float ymin, final float ymax, final float zmin, final float zmax,
			final int calc_divisions, final int display_divisions) {

		this.setFont(new Font("Helvetica", 0, 11));
		this.setBackground(Color.lightGray);
		this.setLayout(new BorderLayout());
		this.canvas = new SurfaceCanvas(calc_divisions, display_divisions, xmin, xmax, ymin, ymax, zmin, zmax);

		this.canvas.calculate(expr);
		this.repaint();
	}

	public void update() {
		this.canvas.destroyImage();
		this.canvas.repaint();
	}

	public boolean isBoxed() {
		return this.canvas.isBoxed;
	}

	public boolean isMesh() {
		return this.canvas.isMesh;
	}

	public boolean isScaleBox() {
		return this.canvas.isScaleBox;
	}

	public boolean isDisplayXY() {
		return this.canvas.isDisplayXY;
	}

	public boolean isDisplayZ() {
		return this.canvas.isDisplayZ;
	}

	public boolean isDisplayGrids() {
		return this.canvas.isDisplayGrids;
	}

	public int getPlotMode() {
		return this.canvas.plot_mode;
	}

	public int getCalcDivisions() {
		return this.canvas.calc_divisions;
	}

	public float getZMin() {
		return this.canvas.zmin;
	}

	public float getZMax() {
		return this.canvas.zmax;
	}

	public float getXMin() {
		return this.canvas.xmin;
	}

	public float getXMax() {
		return this.canvas.xmax;
	}

	public float getYMin() {
		return this.canvas.ymin;
	}

	public float getYMax() {
		return this.canvas.ymax;
	}

	public int getDispDivisions() {
		int i = this.canvas.displayDivisions;
		if (i > this.canvas.calc_divisions) {
			i = this.canvas.calc_divisions;
		}
		for (; (this.canvas.calc_divisions % i) != 0; i++) {
			;
		}
		return i;
	}

	public void setDispDivisions(final int d) {
		this.canvas.setDisplayDivisions(d);
	}

	public void setCalcDivisions(final int d) {
		this.canvas.setCalcDivisions(d);
	}

	protected static class SurfaceVertex {

		SurfaceVertex(final float f, final float f1, final float f2) {
			this.x = f;
			this.y = f1;
			this.z = f2;
			this.project_index = -1;
		}

		public boolean isInvalid() {
			return Float.isNaN(this.z);
		}

		public Point projection() {
			if (this.project_index != SurfaceVertex.master_project_index) {
				this.projection = SurfaceVertex.projector.project(this.x, this.y,
						((this.z - SurfaceVertex.zmin) * SurfaceVertex.zfactor) - 10F);
				this.project_index = SurfaceVertex.master_project_index;
			}
			return this.projection;
		}

		public void transform() {
			this.x = this.x / SurfaceVertex.projector.getXScaling();
			this.y = this.y / SurfaceVertex.projector.getYScaling();
			this.z = (((SurfaceVertex.zmax - SurfaceVertex.zmin)
					* ((this.z / SurfaceVertex.projector.getZScaling()) + 10F)) / 20F) + SurfaceVertex.zmin;
		}

		static void invalidate() {
			SurfaceVertex.master_project_index++;
		}

		static void setProjector(final Projector projector1) {
			SurfaceVertex.projector = projector1;
		}

		static void setZRange(final float f, final float f1) {
			SurfaceVertex.zmin = f;
			SurfaceVertex.zmax = f1;
			SurfaceVertex.zfactor = 20F / (f1 - f);
		}

		float x;

		float y;

		float z;

		private Point projection;

		private int project_index;

		private static Projector projector;

		private static float zmin;

		private static float zmax;

		private static float zfactor;

		private static int master_project_index;

	}

	protected static class SurfaceCanvas extends JComponent implements MouseListener, MouseMotionListener {

		public static float MAX_ZOOM = 100F;

		public static float MIN_ZOOM = 2F;

		private Image Buffer = new java.awt.image.BufferedImage(10, 10, java.awt.image.BufferedImage.TYPE_3BYTE_BGR);

		private Graphics BufferGC = this.Buffer.getGraphics();

		private boolean image_drawn;

		private final Projector projector;

		private SurfaceVertex vertex[][];

		private boolean critical;

		private int prevwidth;

		private int prevheight;

		private float color;

		private SurfaceVertex cop;

		private int plot_mode;

		private int calc_divisions;

		private int displayDivisions;

		private boolean isBoxed = true;

		private boolean isMesh = true;

		private boolean isScaleBox = true;

		private boolean isDisplayXY = true;

		private boolean isDisplayZ = true;

		private boolean isDisplayGrids = true;

		private float xmin;

		private float xmax;

		private float ymin;

		private float ymax;

		private float zmin;

		private float zmax;

		private final int WIREFRAME = 0;

		private final int NORENDER = 1;

		private final int SPECTRUM = 2;

		private final int GRAYSCALE = 3;

		private final int DUALSHADE = 4;

		private final int TOP = 0;

		private final int CENTER = 1;

		private final int UPPER = 1;

		private final int COINCIDE = 0;

		private final int LOWER = -1;

		// private boolean is_data_available;
		private boolean dragged;

		private int click_x;

		private int click_y;

		private int factor_x;

		private int factor_y;

		private int t_x;

		private int t_y;

		private int t_z;

		float color_factor;

		private final int poly_x[];

		private final int poly_y[];

		private final SurfaceVertex upperpart[];

		private final SurfaceVertex lowerpart[];

		private final SurfaceVertex values1[];

		private final SurfaceVertex values2[];

		private final Point testpoint[];

		protected class ControlMenu extends JPopupMenu {

			protected JCheckBoxMenuItem mBoxed = new JCheckBoxMenuItem("Boxed");

			protected JCheckBoxMenuItem mMesh = new JCheckBoxMenuItem("Show Mesh");

			protected JCheckBoxMenuItem mGrid = new JCheckBoxMenuItem("Show Grid");

			protected JCheckBoxMenuItem mDisplayXY = new JCheckBoxMenuItem("Display XY");

			protected JCheckBoxMenuItem mDisplayZ = new JCheckBoxMenuItem("Display Z");

			protected JRadioButtonMenuItem w = new JRadioButtonMenuItem("Wireframe");

			protected JRadioButtonMenuItem h = new JRadioButtonMenuItem("Hidden");

			protected JRadioButtonMenuItem cl = new JRadioButtonMenuItem("Color Levels");

			protected JRadioButtonMenuItem gs = new JRadioButtonMenuItem("Gray Scale");

			protected JRadioButtonMenuItem ds = new JRadioButtonMenuItem("Dual Shade");

			protected ButtonGroup grupo = new ButtonGroup();

			protected JMenu m = new JMenu("Type");

			protected JMenuItem changeRange = new JMenuItem("Change Range");

			protected JMenuItem changeExpr = new JMenuItem("Change Expression");

			protected class RangeDialog extends JDialog {

				JTextField tfDispDivisions = new JTextField();

				JTextField tfCalcDivisions = new JTextField();

				JTextField tfXMin = new JTextField();

				JTextField tfXMax = new JTextField();

				JTextField tfYMin = new JTextField();

				JTextField tfYMax = new JTextField();

				JTextField tfZMin = new JTextField();

				JTextField tfZMax = new JTextField();

				JLabel lDispDivisions = new JLabel("Disp.Div. ");

				JLabel lCalcDivisions = new JLabel("Calc.Div. ");

				JLabel lXMin = new JLabel("X Min ");

				JLabel lXMax = new JLabel("X Max ");

				JLabel lYMin = new JLabel("Y Min ");

				JLabel lYMax = new JLabel("Y Max ");

				JLabel lZMin = new JLabel("Z Min ");

				JLabel lZMax = new JLabel("Z Max ");

				JButton bOK = new JButton("Ok");

				JButton bCancel = new JButton("application.cancel");

				public RangeDialog(final Frame f) {
					super(f, "Range", true);
					this.init();
				}

				public RangeDialog(final Dialog d) {
					super(d, "Range", true);
					this.init();
				}

				protected void initControlPanel() {
					this.tfXMax.setText(String.valueOf(SurfaceCanvas.this.xmax));
					this.tfXMin.setText(String.valueOf(SurfaceCanvas.this.xmin));

					this.tfYMax.setText(String.valueOf(SurfaceCanvas.this.ymax));
					this.tfYMin.setText(String.valueOf(SurfaceCanvas.this.ymin));

					this.tfZMax.setText(String.valueOf(SurfaceCanvas.this.zmax));
					this.tfZMin.setText(String.valueOf(SurfaceCanvas.this.zmin));

					this.tfCalcDivisions.setText(String.valueOf(SurfaceCanvas.this.calc_divisions));
					this.tfDispDivisions.setText(String.valueOf(SurfaceCanvas.this.displayDivisions));
				}

				@Override
				public void setVisible(final boolean b) {
					if (b) {
						this.initControlPanel();
					}
					super.setVisible(b);
				}

				protected void init() {
					final JPanel pAux = new JPanel(new GridLayout(0, 4));
					pAux.setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

					pAux.add(this.lCalcDivisions);
					pAux.add(this.tfCalcDivisions);
					pAux.add(this.lDispDivisions);
					pAux.add(this.tfDispDivisions);

					pAux.add(this.lXMin);
					pAux.add(this.tfXMin);
					pAux.add(this.lXMax);
					pAux.add(this.tfXMax);

					pAux.add(this.lYMin);
					pAux.add(this.tfYMin);
					pAux.add(this.lYMax);
					pAux.add(this.tfYMax);

					pAux.add(this.lZMin);
					pAux.add(this.tfZMin);
					pAux.add(this.lZMax);
					pAux.add(this.tfZMax);

					final JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
					this.getContentPane().add(p, BorderLayout.SOUTH);
					p.add(this.bOK);
					p.add(this.bCancel);

					this.getContentPane().add(pAux, BorderLayout.CENTER);
					this.initControlPanel();

					this.bOK.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							try {
								JTextField t = RangeDialog.this.tfXMin;
								float f = Float.parseFloat(t.getText());
								boolean bXYChange = false;
								final float xmi = f;
								if (Double.compare(f, SurfaceCanvas.this.getXMin()) != 0) {
									bXYChange = true;
								}

								t = RangeDialog.this.tfXMax;
								f = Float.parseFloat(t.getText());
								final float xma = f;
								if (Double.compare(f, SurfaceCanvas.this.getXMax()) != 0) {
									bXYChange = true;
								}

								t = RangeDialog.this.tfYMax;
								f = Float.parseFloat(t.getText());
								final float yma = f;
								if (Double.compare(f, SurfaceCanvas.this.getYMax()) != 0) {
									bXYChange = true;
								}

								t = RangeDialog.this.tfYMin;
								f = Float.parseFloat(t.getText());
								final float ymi = f;
								if (Double.compare(f, SurfaceCanvas.this.getYMin()) != 0) {
									bXYChange = true;
								}

								t = RangeDialog.this.tfZMax;
								f = Float.parseFloat(t.getText());
								final float zma = f;
								if (Double.compare(f, SurfaceCanvas.this.getZMax()) != 0) {
									bXYChange = true;
								}

								t = RangeDialog.this.tfZMin;
								f = Float.parseFloat(t.getText());
								final float zmi = f;
								if (Double.compare(f, SurfaceCanvas.this.getZMin()) != 0) {
									bXYChange = true;

								}

								t = RangeDialog.this.tfCalcDivisions;
								f = Float.parseFloat(t.getText());
								boolean cambioCD = false;
								if ((int) f != SurfaceCanvas.this.calc_divisions) {
									SurfaceCanvas.this.setCalcDivisions((int) f);
									cambioCD = true;
								}
								if (bXYChange) {
									SurfaceCanvas.this.setRanges(xmi, xma, ymi, yma, zmi, zma);
								}
								if (bXYChange || cambioCD) {
									SurfaceCanvas.this.recalculateExpression();
								}

								t = RangeDialog.this.tfDispDivisions;
								f = Float.parseFloat(t.getText());
								if ((int) f != SurfaceCanvas.this.displayDivisions) {
									SurfaceCanvas.this.setDisplayDivisions((int) f);
								}

								RangeDialog.this.setVisible(false);

							} catch (final Exception ex) {
								SurfaceChart.logger.error(null, ex);
								JOptionPane.showMessageDialog(RangeDialog.this.tfXMin, ex.getMessage(), "Error",
										JOptionPane.ERROR_MESSAGE);
							}

						}
					});

					this.bCancel.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							RangeDialog.this.setVisible(false);
						}
					});

				}

			};

			RangeDialog rd = null;

			public ControlMenu() {
				final ActionListener l = new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (e.getSource() == ControlMenu.this.mBoxed) {
							SurfaceCanvas.this.setBoxed(ControlMenu.this.mBoxed.isSelected());
						} else if (e.getSource() == ControlMenu.this.mMesh) {
							SurfaceCanvas.this.setMesh(ControlMenu.this.mMesh.isSelected());
						} else if (e.getSource() == ControlMenu.this.mGrid) {
							SurfaceCanvas.this.setDisplayGrids(ControlMenu.this.mGrid.isSelected());
						} else if (e.getSource() == ControlMenu.this.mDisplayXY) {
							SurfaceCanvas.this.setDisplayXY(ControlMenu.this.mDisplayXY.isSelected());
						} else if (e.getSource() == ControlMenu.this.mDisplayZ) {
							SurfaceCanvas.this.setDisplayZ(ControlMenu.this.mDisplayZ.isSelected());
						}
					}
				};
				this.mBoxed.addActionListener(l);
				this.mMesh.addActionListener(l);
				this.mGrid.addActionListener(l);
				this.mDisplayXY.addActionListener(l);
				this.mDisplayZ.addActionListener(l);
				this.add(this.mBoxed);
				this.add(this.mMesh);
				this.add(this.mGrid);
				this.add(this.mDisplayXY);
				this.add(this.mDisplayZ);

				this.grupo.add(this.w);
				this.grupo.add(this.h);
				this.grupo.add(this.cl);
				this.grupo.add(this.gs);
				this.grupo.add(this.ds);

				this.add(this.m);
				this.m.add(this.w);
				this.m.add(this.h);
				this.m.add(this.cl);
				this.m.add(this.gs);
				this.m.add(this.ds);

				this.addSeparator();
				this.add(this.changeRange);
				this.add(this.changeExpr);

				final ActionListener aListenerType = new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (e.getSource() == ControlMenu.this.w) {
							SurfaceCanvas.this.setPlotMode(0);
						} else if (e.getSource() == ControlMenu.this.h) {
							SurfaceCanvas.this.setPlotMode(1);
						} else if (e.getSource() == ControlMenu.this.cl) {
							SurfaceCanvas.this.setPlotMode(2);
						} else if (e.getSource() == ControlMenu.this.gs) {
							SurfaceCanvas.this.setPlotMode(3);
						} else if (e.getSource() == ControlMenu.this.ds) {
							SurfaceCanvas.this.setPlotMode(4);
						}
					}
				};
				this.w.addActionListener(aListenerType);
				this.h.addActionListener(aListenerType);
				this.cl.addActionListener(aListenerType);
				this.gs.addActionListener(aListenerType);
				this.ds.addActionListener(aListenerType);

				this.changeRange.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (ControlMenu.this.rd == null) {
							final Window w = SwingUtilities.getWindowAncestor(SurfaceCanvas.this);
							if (w instanceof Dialog) {
								ControlMenu.this.rd = new RangeDialog((Dialog) w);
							} else {
								ControlMenu.this.rd = new RangeDialog((Frame) w);
							}
							ControlMenu.this.rd.pack();
							ControlMenu.this.center(ControlMenu.this.rd);
						}
						ControlMenu.this.rd.setVisible(true);
					}
				});

				this.changeExpr.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						final String s = JOptionPane.showInputDialog(SurfaceCanvas.this, "Expression?", "Change Expression",
								JOptionPane.QUESTION_MESSAGE);
						if ((s != null) && (s.length() > 0)) {
							SurfaceCanvas.this.setExpression(s);
						}
					}
				});
			}

			public void center(final Window f) {
				final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (d.width / 2) - (f.getWidth() / 2);
				int y = (d.height / 2) - (f.getHeight() / 2);
				if (x < 0) {
					x = 0;
				}
				if (y < 0) {
					y = 0;
				}
				if (x > d.width) {
					x = 0;
				}
				if (y > d.height) {
					y = 0;
				}
				f.setLocation(x, y);
			}

			protected boolean allowChangeExpression = true;

			public void setAllowChangeExpressionEnabled(final boolean b) {
				this.allowChangeExpression = b;
			}

			@Override
			public void show(final Component c, final int x, final int y) {
				this.mBoxed.setSelected(SurfaceCanvas.this.isBoxed);
				this.mMesh.setSelected(SurfaceCanvas.this.isMesh);
				this.mGrid.setSelected(SurfaceCanvas.this.isDisplayGrids);
				this.mDisplayXY.setSelected(SurfaceCanvas.this.isDisplayXY);
				this.mDisplayZ.setSelected(SurfaceCanvas.this.isDisplayZ);
				if (SurfaceCanvas.this.plot_mode == 0) {
					this.w.setSelected(true);
				} else if (SurfaceCanvas.this.plot_mode == 1) {
					this.h.setSelected(true);
				} else if (SurfaceCanvas.this.plot_mode == 2) {
					this.cl.setSelected(true);
				} else if (SurfaceCanvas.this.plot_mode == 3) {
					this.gs.setSelected(true);
				} else if (SurfaceCanvas.this.plot_mode == 4) {
					this.ds.setSelected(true);
				}
				this.changeExpr.setVisible(this.allowChangeExpression);
				super.show(c, x, y);
			}

		}

		protected boolean popupMenuEnabled = true;

		protected ControlMenu popupMenu = new ControlMenu();

		public void setPopupMenuEnabled(final boolean b) {
			this.popupMenuEnabled = b;
		}

		public void setAllowChangeExpression(final boolean b) {
			this.popupMenu.setAllowChangeExpressionEnabled(b);
		}

		float getXMax() {
			return this.xmax;
		}

		float getXMin() {
			return this.xmin;
		}

		float getYMax() {
			return this.ymax;
		}

		float getYMin() {
			return this.ymin;
		}

		float getZMax() {
			return this.zmax;
		}

		float getZMin() {
			return this.zmin;
		}

		private final Color colorBase = new Color(202, 220, 222);

		public SurfaceCanvas(final int calDiv, final int dispDiv, final float xmin, final float xmax, final float ymin, final float ymax, final float zmin,
				final float zmax) {
			this.calc_divisions = calDiv;
			this.displayDivisions = dispDiv;
			this.xmax = xmax;
			this.ymax = ymax;
			this.zmax = zmax;
			this.xmin = xmin;
			this.ymin = ymin;
			this.zmin = zmin;
			this.poly_x = new int[9];
			this.poly_y = new int[9];
			this.upperpart = new SurfaceVertex[8];
			this.lowerpart = new SurfaceVertex[8];
			this.values1 = new SurfaceVertex[4];
			this.values2 = new SurfaceVertex[4];
			this.testpoint = new Point[5];
			this.image_drawn = false;

			this.prevwidth = this.prevheight = 0;
			this.projector = new Projector();
			this.projector.setDistance(70F);
			this.projector.set2DScaling(15F);
			this.projector.setRotationAngle(125F);
			this.projector.setElevationAngle(10F);

			this.vertex = new SurfaceVertex[2][];
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.setBackground(Color.white);
			this.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(final ComponentEvent e) {
					final Rectangle bounds = SurfaceCanvas.this.getBounds();
					if ((bounds.width > 0) && (bounds.height > 0)) {
						if ((bounds.width != SurfaceCanvas.this.prevwidth)
								|| (bounds.height != SurfaceCanvas.this.prevheight)) {
							if (SurfaceChart.DEBUG) {
								SurfaceChart.logger.debug("New image size: " + bounds.width + "x" + bounds.height);
							}
							SurfaceVertex.setProjector(SurfaceCanvas.this.projector);
							SurfaceCanvas.this.projector
							.setProjectionArea(new Rectangle(0, 0, bounds.width, bounds.height));
							SurfaceCanvas.this.image_drawn = false;
							SurfaceCanvas.this.Buffer = SurfaceCanvas.this.createImage(bounds.width, bounds.height);
							if (SurfaceCanvas.this.BufferGC != null) {
								SurfaceCanvas.this.BufferGC.dispose();
							}
							SurfaceCanvas.this.BufferGC = SurfaceCanvas.this.Buffer.getGraphics();
							SurfaceCanvas.this.prevwidth = bounds.width;
							SurfaceCanvas.this.prevheight = bounds.height;
						}

					}
					SurfaceCanvas.this.repaint();

				}
			});
			this.setPreferredSize(new Dimension(550, 550));
		}

		public void destroyImage() {
			this.image_drawn = false;
		}

		public void update() {
			this.destroyImage();
			this.paintImmediately(this.getBounds());
		}

		public void setRanges(final float f, final float f1, final float f2, final float f3) {
			this.xmin = f;
			this.xmax = f1;
			this.ymin = f2;
			this.ymax = f3;
		}

		public void setRanges(final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
			this.xmin = f;
			this.xmax = f1;
			this.ymin = f2;
			this.ymax = f3;
			this.zmin = f4;
			this.zmax = f5;
		}

		public void setValuesArray(final SurfaceVertex asurfacevertex[][]) {
			this.vertex = asurfacevertex;
			this.update();
		}

		protected String expression = null;

		public void setExpression(final String s) {
			this.expression = s;
			this.recalculateExpression();
		}

		public void setBoxed(final boolean b) {
			this.isBoxed = b;
			this.update();
		}

		public void setDisplayDivisions(final int d) {
			this.displayDivisions = d;
			this.update();
		}

		public void setCalcDivisions(final int d) {
			this.calc_divisions = d;
			this.recalculateExpression();
		}

		@Override
		public void setBackground(final Color c) {
			super.setBackground(c);
			this.update();
		}

		public void setMesh(final boolean b) {
			this.isMesh = b;
			this.update();
		}

		public void setScaleBox(final boolean b) {
			this.isScaleBox = b;
			this.update();
		}

		public void setDisplayXY(final boolean b) {
			this.isDisplayXY = b;
			this.update();
		}

		public void setDisplayZ(final boolean b) {
			this.isDisplayZ = b;
			this.update();
		}

		public void setDisplayGrids(final boolean b) {
			this.isDisplayGrids = b;
			this.update();
		}

		@Override
		public void mousePressed(final MouseEvent event) {
			if (SurfaceChart.DEBUG) {
				SurfaceChart.logger.debug("pressed");
			}
			final int i = event.getX();
			final int j = event.getY();
			this.click_x = i;
			this.click_y = j;

		}

		@Override
		public void mouseReleased(final MouseEvent event) {
			if (event.getModifiers() == InputEvent.META_MASK) {
				if (this.popupMenuEnabled) {
					this.popupMenu.show(SurfaceCanvas.this, event.getX(), event.getY());
				}
				return;
			}
			if (SurfaceChart.DEBUG) {
				SurfaceChart.logger.debug("released");
			}
			if (this.dragged) {
				this.destroyImage();
				this.repaint();
				this.dragged = false;
			}

		}

		@Override
		public void mouseDragged(final MouseEvent event) {
			try {
				if (event.getModifiers() == InputEvent.META_MASK) {
					return;
				}
				SurfaceVertex.setProjector(this.projector);
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (SurfaceChart.DEBUG) {
					SurfaceChart.logger.debug("dragg");
				}
				final long t = System.currentTimeMillis();
				final int i = event.getX();
				final int j = event.getY();
				if (event.isControlDown()) {
					this.projector.set2D_xTranslation((this.projector.get2D_xTranslation() + i) - this.click_x);
					this.projector.set2D_yTranslation((this.projector.get2D_yTranslation() + j) - this.click_y);
				} else if (event.isShiftDown()) {
					float f1 = this.projector.get2DScaling() + ((j - this.click_y) * 0.5F);
					if (f1 > SurfaceCanvas.MAX_ZOOM) {
						f1 = SurfaceCanvas.MAX_ZOOM;
					}
					if (f1 < SurfaceCanvas.MIN_ZOOM) {
						f1 = SurfaceCanvas.MIN_ZOOM;
					}
					this.projector.set2DScaling(f1);
				} else {
					float f2;
					for (f2 = this.projector.getRotationAngle() + (i - this.click_x); f2 > 360F; f2 -= 360F) {
						;
					}
					for (; f2 < 0.0F; f2 += 360F) {
						;
					}
					this.projector.setRotationAngle(f2);
					f2 = this.projector.getElevationAngle() + (j - this.click_y);
					if (f2 > 90F) {
						f2 = 90F;
					} else if (f2 < 0.0F) {
						f2 = 0.0F;
					}
					this.projector.setElevationAngle(f2);
				}
				this.image_drawn = false;

				this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
				this.click_x = i;
				this.click_y = j;

				if (SurfaceChart.DEBUG) {
					SurfaceChart.logger.debug("dragg time" + (System.currentTimeMillis() - t));
				}
			} catch (final Exception e) {
				SurfaceChart.logger.trace(null, e);
			} finally {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
		}

		private Rectangle drawBoundingBox() {
			int xmi = 0;
			int xma = 0;
			int ymi = 0;
			int yma = 0;
			SurfaceVertex.setProjector(this.projector);
			final Point point = this.projector.project(this.factor_x * 10, this.factor_y * 10, 10F);
			this.BufferGC.setColor(Color.black);
			Point point1 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, 10F);
			this.BufferGC.drawLine(point.x, point.y, point1.x, point1.y);
			xmi = Math.min(point.x, point1.x);
			xma = Math.max(point.x, point1.x);
			ymi = Math.min(point.y, point1.y);
			yma = Math.max(point.y, point1.y);
			point1 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, 10F);
			this.BufferGC.drawLine(point.x, point.y, point1.x, point1.y);
			xmi = Math.min(xmi, point1.x);
			xma = Math.max(xma, point1.x);
			ymi = Math.min(ymi, point1.y);
			yma = Math.max(yma, point1.y);
			point1 = this.projector.project(this.factor_x * 10, this.factor_y * 10, -10F);
			this.BufferGC.drawLine(point.x, point.y, point1.x, point1.y);
			xmi = Math.min(xmi, point1.x);
			xma = Math.max(xma, point1.x);
			ymi = Math.min(ymi, point1.y);
			yma = Math.max(yma, point1.y);
			return new Rectangle(xmi, ymi, xma - xmi, yma - ymi);
		}

		private void drawBase(final Graphics g, final int ai[], final int ai1[]) {
			SurfaceVertex.setProjector(this.projector);
			Point point = this.projector.project(-10F, -10F, -10F);

			ai[0] = point.x;
			ai1[0] = point.y;
			point = this.projector.project(-10F, 10F, -10F);
			ai[1] = point.x;
			ai1[1] = point.y;
			point = this.projector.project(10F, 10F, -10F);
			ai[2] = point.x;
			ai1[2] = point.y;
			point = this.projector.project(10F, -10F, -10F);
			ai[3] = point.x;
			ai1[3] = point.y;
			ai[4] = ai[0];
			ai1[4] = ai1[0];
			this.setAndFillWhenNotPlotMode(g, ai, ai1);
			g.setColor(Color.black);
			g.drawPolygon(ai, ai1, 5);

		}

		private void drawBoxGridsTicksLabels(final Graphics g, final boolean flag) {
			boolean flag1 = false;
			boolean flag2 = false;
			final int ai[] = new int[5];
			final int ai1[] = new int[5];
			if (this.projector == null) {
				return;
			}
			SurfaceVertex.setProjector(this.projector);
			if (flag) {
				this.drawBase(g, ai, ai1);

				Point point = this.projector.project(0.0F, 0.0F, -10F);
				ai[0] = point.x;
				ai1[0] = point.y;
				point = this.projector.project(10.5F, 0.0F, -10F);
				g.drawLine(ai[0], ai1[0], point.x, point.y);
				this.setOutputStringForFirstPoint(g, ai, ai1, point);
				point = this.projector.project(0.0F, 11.5F, -10F);
				g.drawLine(ai[0], ai1[0], point.x, point.y);
				this.setOutputStringForSecondPoint(g, ai, ai1, point);
				point = this.projector.project(0.0F, 0.0F, 10.5F);
				g.drawLine(ai[0], ai1[0], point.x, point.y);
				this.outString(g, (int) (1.05D * (point.x - ai[0])) + ai[0],
						(int) (1.05D * (point.y - ai1[0])) + ai1[0], "z", 1, 1);
				return;
			}

			this.factor_x = this.factor_y = 1;
			Point point1 = this.projector.project(0.0F, 0.0F, -10F);
			ai[0] = point1.x;
			point1 = this.projector.project(10.5F, 0.0F, -10F);
			flag2 = point1.x > ai[0];
			int i = point1.y;
			point1 = this.projector.project(-10.5F, 0.0F, -10F);
			if (point1.y > i) {
				this.factor_x = -1;
				flag2 = point1.x > ai[0];
			}
			point1 = this.projector.project(0.0F, 10.5F, -10F);
			flag1 = point1.x > ai[0];
			i = point1.y;
			point1 = this.projector.project(0.0F, -10.5F, -10F);
			if (point1.y > i) {
				this.factor_y = -1;
				flag1 = point1.x > ai[0];
			}
			this.setAxesScale();
			this.drawBase(g, ai, ai1);

			this.drawBoxGridsTicksLabelsInner(g, ai, ai1);

			this.drawBoxGridsTicksLabelsFor(g, flag1, flag2);

		}

		protected void drawBoxGridsTicksLabelsFor(final Graphics g, final boolean flag1, final boolean flag2) {
			for (int j = -9; j <= 9; j++) {
				if (this.isDisplayXY || this.isDisplayGrids) {
					this.drawAccordingToDisplayXYORDisplayGrids(g, flag1, flag2, j);
				}
				if (this.isDisplayXY) {
					Point point13 = this.projector.project(0.0F, this.factor_y * 14, -10F);
					this.outString(g, point13.x, point13.y, "X", 1, 0);
					point13 = this.projector.project(this.factor_x * 14, 0.0F, -10F);
					this.outString(g, point13.x, point13.y, "Y", 1, 0);
				}
				if ((this.isDisplayZ || (this.isDisplayGrids && this.isBoxed))
						&& (!this.isDisplayGrids || ((j % (this.t_z / 2)) == 0) || this.isDisplayZ)) {
					Point point6;
					Point point14;
					if (this.isBoxed && this.isDisplayGrids && ((j % this.t_z) == 0)) {
						point6 = this.projector.project(-this.factor_x * 10, -this.factor_y * 10, j);
						point14 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, j);
					} else {
						if ((j % this.t_z) == 0) {
							point6 = this.projector.project(-this.factor_x * 10, this.factor_y * 9.5F, j);
						} else {
							point6 = this.projector.project(-this.factor_x * 10, this.factor_y * 9.8F, j);
						}
						point14 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, j);
					}
					g.drawLine(point6.x, point6.y, point14.x, point14.y);
					if (this.isDisplayZ) {
						this.setOutputStringAccordingToDisplayZAndFirstFlag(g, flag1, j);
					}
					if (this.isDisplayGrids && this.isBoxed && ((j % this.t_z) == 0)) {
						point6 = this.projector.project(-this.factor_x * 10, -this.factor_y * 10, j);
						point14 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, j);
					} else {
						if ((j % this.t_z) == 0) {
							point6 = this.projector.project(this.factor_x * 9.5F, -this.factor_y * 10, j);
						} else {
							point6 = this.projector.project(this.factor_x * 9.8F, -this.factor_y * 10, j);
						}
						point14 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, j);
					}
					g.drawLine(point6.x, point6.y, point14.x, point14.y);
					if (this.isDisplayZ) {
						this.setOutputStringAccordingToDisplayZAndSecondFlag(g, flag2, j);
					}
					if (this.isDisplayGrids && this.isBoxed) {
						this.drawAccordingToBoxedANDDisplayGrid(g, j);
					}
				}
			}
		}

		protected void drawBoxGridsTicksLabelsInner(final Graphics g, final int[] ai, final int[] ai1) {
			if (this.isBoxed) {
				Point point2 = this.projector.project(-this.factor_x * 10, -this.factor_y * 10, -10F);
				ai[0] = point2.x;
				ai1[0] = point2.y;
				point2 = this.projector.project(-this.factor_x * 10, -this.factor_y * 10, 10F);
				ai[1] = point2.x;
				ai1[1] = point2.y;
				point2 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, 10F);
				ai[2] = point2.x;
				ai1[2] = point2.y;
				point2 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, -10F);
				ai[3] = point2.x;
				ai1[3] = point2.y;
				ai[4] = ai[0];
				ai1[4] = ai1[0];
				this.setAndFillWhenNotPlotMode(g, ai, ai1);
				g.setColor(Color.black);
				g.drawPolygon(ai, ai1, 5);
				point2 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, 10F);
				ai[2] = point2.x;
				ai1[2] = point2.y;
				point2 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, -10F);
				ai[3] = point2.x;
				ai1[3] = point2.y;
				ai[4] = ai[0];
				ai1[4] = ai1[0];
				this.setAndFillWhenNotPlotMode(g, ai, ai1);
				g.setColor(Color.black);
				g.drawPolygon(ai, ai1, 5);
			} else if (this.isDisplayZ) {
				Point point3 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, -10F);
				ai[0] = point3.x;
				ai1[0] = point3.y;
				point3 = this.projector.project(this.factor_x * 10, -this.factor_y * 10, 10F);
				g.drawLine(ai[0], ai1[0], point3.x, point3.y);
				point3 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, -10F);
				ai[0] = point3.x;
				ai1[0] = point3.y;
				point3 = this.projector.project(-this.factor_x * 10, this.factor_y * 10, 10F);
				g.drawLine(ai[0], ai1[0], point3.x, point3.y);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param ai
		 * @param ai1
		 * @param point
		 */
		protected void setOutputStringForSecondPoint(final Graphics g, final int[] ai, final int[] ai1, final Point point) {
			if (point.x < ai[0]) {
				this.outString(g, (int) (1.05D * (point.x - ai[0])) + ai[0],
						(int) (1.05D * (point.y - ai1[0])) + ai1[0], "y", 2, 0);
			} else {
				this.outString(g, (int) (1.05D * (point.x - ai[0])) + ai[0],
						(int) (1.05D * (point.y - ai1[0])) + ai1[0], "y", 0, 0);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param ai
		 * @param ai1
		 * @param point
		 */
		protected void setOutputStringForFirstPoint(final Graphics g, final int[] ai, final int[] ai1, final Point point) {
			if (point.x < ai[0]) {
				this.outString(g, (int) (1.05D * (point.x - ai[0])) + ai[0],
						(int) (1.05D * (point.y - ai1[0])) + ai1[0], "x", 2, 0);
			} else {
				this.outString(g, (int) (1.05D * (point.x - ai[0])) + ai[0],
						(int) (1.05D * (point.y - ai1[0])) + ai1[0], "x", 0, 0);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param flag2
		 * @param j
		 */
		protected void setOutputStringAccordingToDisplayZAndSecondFlag(final Graphics g, final boolean flag2, final int j) {
			final Point point15 = this.projector.project(this.factor_x * 10.5F, -this.factor_y * 10, j);
			if ((j % this.t_z) == 0) {
				if (flag2) {
					this.outString(g, point15.x, point15.y,
							Float.toString((((j + 10) / 20F) * (this.zmax - this.zmin)) + this.zmin), 0, 1);
				} else {
					this.outString(g, point15.x, point15.y,
							Float.toString((((j + 10) / 20F) * (this.zmax - this.zmin)) + this.zmin), 2, 1);
				}
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param flag1
		 * @param j
		 */
		protected void setOutputStringAccordingToDisplayZAndFirstFlag(final Graphics g, final boolean flag1, final int j) {
			Point point14;
			point14 = this.projector.project(-this.factor_x * 10, this.factor_y * 10.5F, j);
			if ((j % this.t_z) == 0) {
				if (flag1) {
					this.outString(g, point14.x, point14.y,
							Float.toString((((j + 10) / 20F) * (this.zmax - this.zmin)) + this.zmin), 0, 1);
				} else {
					this.outString(g, point14.x, point14.y,
							Float.toString((((j + 10) / 20F) * (this.zmax - this.zmin)) + this.zmin), 2, 1);
				}
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param j
		 */
		protected void drawAccordingToBoxedANDDisplayGrid(final Graphics g, final int j) {
			if ((j % this.t_y) == 0) {
				final Point point7 = this.projector.project(-this.factor_x * 10, j, -10F);
				final Point point16 = this.projector.project(-this.factor_x * 10, j, 10F);
				g.drawLine(point7.x, point7.y, point16.x, point16.y);
			}
			if ((j % this.t_x) == 0) {
				final Point point8 = this.projector.project(j, -this.factor_y * 10, -10F);
				final Point point17 = this.projector.project(j, -this.factor_y * 10, 10F);
				g.drawLine(point8.x, point8.y, point17.x, point17.y);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param flag1
		 * @param flag2
		 * @param j
		 */
		protected void drawAccordingToDisplayXYORDisplayGrids(final Graphics g, final boolean flag1, final boolean flag2, final int j) {
			if (!this.isDisplayGrids || ((j % (this.t_y / 2)) == 0) || this.isDisplayXY) {
				final Point point4 = this.setPoint4(j);
				final Point point9 = this.projector.project(this.factor_x * 10, j, -10F);
				g.drawLine(point4.x, point4.y, point9.x, point9.y);
				if (((j % this.t_y) == 0) && this.isDisplayXY) {
					this.setOutputStringAccordingToFlag(g, flag2, j);
				}
			}
			if (!this.isDisplayGrids || ((j % (this.t_x / 2)) == 0) || this.isDisplayXY) {
				final Point point5 = this.setPoint5(j);
				final Point point11 = this.projector.project(j, this.factor_y * 10, -10F);
				g.drawLine(point5.x, point5.y, point11.x, point11.y);
				this.setOutputStringAccordingToDisplayXY(g, flag1, j);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param flag1
		 * @param j
		 */
		protected void setOutputStringAccordingToDisplayXY(final Graphics g, final boolean flag1, final int j) {
			if (((j % this.t_x) == 0) && this.isDisplayXY) {
				final Point point12 = this.projector.project(j, this.factor_y * 10.5F, -10F);
				if (flag1) {
					this.outString(g, point12.x, point12.y,
							Float.toString((((j + 10) / 20F) * (this.xmax - this.xmin)) + this.xmin), 0, 0);
				} else {
					this.outString(g, point12.x, point12.y,
							Float.toString((((j + 10) / 20F) * (this.xmax - this.xmin)) + this.xmin), 2, 0);
				}
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param j
		 * @return
		 */
		protected Point setPoint5(final int j) {
			Point point5;
			if (this.isDisplayGrids && ((j % this.t_x) == 0)) {
				point5 = this.projector.project(j, -this.factor_y * 10, -10F);
			} else if ((j % this.t_x) != 0) {
				point5 = this.projector.project(j, this.factor_y * 9.8F, -10F);
			} else {
				point5 = this.projector.project(j, this.factor_y * 9.5F, -10F);
			}
			return point5;
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param flag2
		 * @param j
		 */
		protected void setOutputStringAccordingToFlag(final Graphics g, final boolean flag2, final int j) {
			final Point point10 = this.projector.project(this.factor_x * 10.5F, j, -10F);
			if (flag2) {
				this.outString(g, point10.x, point10.y,
						Float.toString((((j + 10) / 20F) * (this.ymax - this.ymin)) + this.ymin), 0, 0);
			} else {
				this.outString(g, point10.x, point10.y,
						Float.toString((((j + 10) / 20F) * (this.ymax - this.ymin)) + this.ymin), 2, 0);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param j
		 * @return
		 */
		protected Point setPoint4(final int j) {
			Point point4;
			if (this.isDisplayGrids && ((j % this.t_y) == 0)) {
				point4 = this.projector.project(-this.factor_x * 10, j, -10F);
			} else if ((j % this.t_y) != 0) {
				point4 = this.projector.project(this.factor_x * 9.8F, j, -10F);
			} else {
				point4 = this.projector.project(this.factor_x * 9.5F, j, -10F);
			}
			return point4;
		}

		/**
		 * Method used to reduce the complexity of {@link #drawBoxGridsTicksLabels(Graphics, boolean)}
		 * @param g
		 * @param ai
		 * @param ai1
		 */
		protected void setAndFillWhenNotPlotMode(final Graphics g, final int[] ai, final int[] ai1) {
			if (this.plot_mode != 0) {
				if (this.plot_mode == 1) {
					g.setColor(Color.lightGray);
				} else {
					g.setColor(this.colorBase);
				}
				g.fillPolygon(ai, ai1, 4);
			}
		}

		private void setAxesScale() {
			SurfaceVertex.setProjector(this.projector);
			if (!this.isScaleBox) {
				this.projector.setScaling(1.0F);
				this.t_x = this.t_y = this.t_z = 4;
				return;
			}
			float f = this.xmax - this.xmin;
			float f1 = this.ymax - this.ymin;
			float f2 = this.zmax - this.zmin;
			float f3;
			byte byte0;
			if (f < f1) {
				if (f1 < f2) {
					byte0 = 3;
					f3 = f2;
				} else {
					byte0 = 2;
					f3 = f1;
				}
			} else if (f < f2) {
				byte0 = 3;
				f3 = f2;
			} else {
				byte0 = 1;
				f3 = f;
			}
			f /= f3;
			f1 /= f3;
			f2 /= f3;
			if ((f < 0.3F) || ((f1 < 0.3F) && (f2 < 0.3F))) {
				switch (byte0) {
				default:
					break;

				case 1: // '\001'
				if (f1 < f2) {
					f1 /= f2;
					f2 = 1.0F;
				} else {
					f2 /= f1;
					f1 = 1.0F;
				}
				break;

				case 2: // '\002'
					if (f < f2) {
						f /= f2;
						f2 = 1.0F;
					} else {
						f2 /= f;
						f = 1.0F;
					}
					break;

				case 3: // '\003'
					if (f1 < f) {
						f1 /= f;
						f = 1.0F;
					} else {
						f /= f1;
						f1 = 1.0F;
					}
					break;
				}
			}
			if (f < 0.3F) {
				f = 1.0F;
			}
			this.projector.setXScaling(f);
			if (f1 < 0.3F) {
				f1 = 1.0F;
			}
			this.projector.setYScaling(f1);
			if (f2 < 0.3F) {
				f2 = 1.0F;
			}
			this.projector.setZScaling(f2);
			if (f < 0.5F) {
				this.t_x = 8;
			} else {
				this.t_x = 4;
			}
			if (f1 < 0.5F) {
				this.t_y = 8;
			} else {
				this.t_y = 4;
			}
			if (f2 < 0.5F) {
				this.t_z = 8;
				return;
			} else {
				this.t_z = 4;
				return;
			}
		}

		private void outString(final Graphics g, final int i, int j, final String s, final int k, final int l) {
			switch (l) {
			case 0: // '\0'
				j += g.getFontMetrics(g.getFont()).getAscent();
				break;

			case 1: // '\001'
				j += g.getFontMetrics(g.getFont()).getAscent() / 2;
				break;
			}
			switch (k) {
			case 0: // '\0'
				g.drawString(s, i, j);
				return;

			case 2: // '\002'
				g.drawString(s, i - g.getFontMetrics(g.getFont()).stringWidth(s), j);
				return;

			case 1: // '\001'
				g.drawString(s, i - (g.getFontMetrics(g.getFont()).stringWidth(s) / 2), j);
				return;
			}
		}

		private void plot_plane(final SurfaceVertex asurfacevertex[], final int i) {
			Color color1 = Color.black;
			if (i < 3) {
				return;
			}
			SurfaceVertex.setProjector(this.projector);
			int j = 0;
			float f = 0.0F;
			boolean flag = asurfacevertex[0].z < this.zmin;
			boolean flag2 = !flag && (asurfacevertex[0].z <= this.zmax);
			int l = 1;
			for (int k = 0; k < i; k++) {
				final boolean flag1 = asurfacevertex[l].z < this.zmin;
				final boolean flag3 = !flag1 && (asurfacevertex[l].z <= this.zmax);
				if (flag2 || flag3 || (flag ^ flag1)) {
					if (!flag2) {
						float f1;
						if (flag) {
							f1 = this.zmin;
						} else {
							f1 = this.zmax;
						}
						final float f3 = (f1 - asurfacevertex[l].z) / (asurfacevertex[k].z - asurfacevertex[l].z);
						final float f5 = (f3 * (asurfacevertex[k].x - asurfacevertex[l].x)) + asurfacevertex[l].x;
						final float f7 = (f3 * (asurfacevertex[k].y - asurfacevertex[l].y)) + asurfacevertex[l].y;
						Point point;
						if (flag) {
							point = this.projector.project(f5, f7, -10F);
						} else {
							point = this.projector.project(f5, f7, 10F);
						}
						this.poly_x[j] = point.x;
						this.poly_y[j] = point.y;
						j++;
						f += f1;
					}
					if (flag3) {
						final Point point1 = asurfacevertex[l].projection();
						this.poly_x[j] = point1.x;
						this.poly_y[j] = point1.y;
						j++;
						f += asurfacevertex[l].z;
					} else {
						float f2;
						if (flag1) {
							f2 = this.zmin;
						} else {
							f2 = this.zmax;
						}
						final float f4 = (f2 - asurfacevertex[k].z) / (asurfacevertex[l].z - asurfacevertex[k].z);
						final float f6 = (f4 * (asurfacevertex[l].x - asurfacevertex[k].x)) + asurfacevertex[k].x;
						final float f8 = (f4 * (asurfacevertex[l].y - asurfacevertex[k].y)) + asurfacevertex[k].y;
						Point point2;
						if (flag1) {
							point2 = this.projector.project(f6, f8, -10F);
						} else {
							point2 = this.projector.project(f6, f8, 10F);
						}
						this.poly_x[j] = point2.x;
						this.poly_y[j] = point2.y;
						j++;
						f += f2;
					}
				}
				l = (l + 1) % i;
				flag2 = flag3;
				flag = flag1;
			}

			if (j > 0) {
				switch (this.plot_mode) {
				default:
					break;

				case 1: // '\001'
				this.BufferGC.setColor(Color.lightGray);
				break;

				case 2: // '\002'
					f = 0.8F - (((f / j) - this.zmin) * this.color_factor);
					this.BufferGC.setColor(new Color(Color.HSBtoRGB(f, 1.0F, 1.0F)));
					break;

				case 3: // '\003'
					f = ((f / j) - this.zmin) * this.color_factor;
					this.BufferGC.setColor(Color.getHSBColor(0.0F, 0.0F, f));
					if (f < 0.3F) {
						color1 = new Color(0.6F, 0.6F, 0.6F);
					}
					break;

				case 4: // '\004'
					f = (((f / j) - this.zmin) * this.color_factor) + 0.4F;
					this.BufferGC.setColor(Color.getHSBColor(this.color, 0.7F, f));
					break;
				}
				this.BufferGC.fillPolygon(this.poly_x, this.poly_y, j);
				this.BufferGC.setColor(color1);
				if (this.isMesh || (this.plot_mode == 1)) {
					this.poly_x[j] = this.poly_x[0];
					this.poly_y[j] = this.poly_y[0];
					j++;
					this.BufferGC.drawPolygon(this.poly_x, this.poly_y, j);
				}
			}
		}

		private void split_plot_plane(final SurfaceVertex asurfacevertex[], final SurfaceVertex asurfacevertex1[]) {
			byte byte0 = 0;
			int i = 0;
			int j = 0;
			boolean flag = true;
			boolean flag1 = false;
			int k = 0;
			int l = 0;
			for (int i1 = 0; i1 <= 4; i1++) {
				if (asurfacevertex[k].z < asurfacevertex1[k].z) {
					flag = false;
					if (byte0 == 0) {
						byte0 = 1;
						this.upperpart[i++] = asurfacevertex1[k];
					} else if (byte0 != 1) {
						final float f = (asurfacevertex[k].z - asurfacevertex1[k].z)
								/ (((asurfacevertex[k].z - asurfacevertex1[k].z) + asurfacevertex1[l].z)
										- asurfacevertex[l].z);
						float f2;
						float f4;
						if (Double.compare(asurfacevertex[k].x, asurfacevertex[l].x) == 0) {
							f4 = (f * (asurfacevertex[l].y - asurfacevertex[k].y)) + asurfacevertex[k].y;
							f2 = asurfacevertex[k].x;
						} else {
							f2 = (f * (asurfacevertex[l].x - asurfacevertex[k].x)) + asurfacevertex[k].x;
							f4 = asurfacevertex[k].y;
						}
						final float f6 = (f * (asurfacevertex1[l].z - asurfacevertex1[k].z)) + asurfacevertex1[k].z;
						this.upperpart[i++] = this.lowerpart[j++] = new SurfaceVertex(f2, f4, f6);
						this.upperpart[i++] = asurfacevertex1[k];
						byte0 = 1;
					} else {
						this.upperpart[i++] = asurfacevertex1[k];
					}
				} else if (asurfacevertex[k].z > asurfacevertex1[k].z) {
					flag = false;
					if (byte0 == 0) {
						byte0 = -1;
						this.lowerpart[j++] = asurfacevertex1[k];
					} else if (byte0 != -1) {
						final float f1 = (asurfacevertex[k].z - asurfacevertex1[k].z)
								/ (((asurfacevertex[k].z - asurfacevertex1[k].z) + asurfacevertex1[l].z)
										- asurfacevertex[l].z);
						float f3;
						float f5;
						if (Double.compare(asurfacevertex[k].x, asurfacevertex[l].x) == 0) {
							f5 = (f1 * (asurfacevertex[l].y - asurfacevertex[k].y)) + asurfacevertex[k].y;
							f3 = asurfacevertex[k].x;
						} else {
							f3 = (f1 * (asurfacevertex[l].x - asurfacevertex[k].x)) + asurfacevertex[k].x;
							f5 = asurfacevertex[k].y;
						}
						final float f7 = (f1 * (asurfacevertex1[l].z - asurfacevertex1[k].z)) + asurfacevertex1[k].z;
						this.lowerpart[j++] = this.upperpart[i++] = new SurfaceVertex(f3, f5, f7);
						this.lowerpart[j++] = asurfacevertex1[k];
						byte0 = -1;
					} else {
						this.lowerpart[j++] = asurfacevertex1[k];
					}
				} else {
					this.upperpart[i++] = asurfacevertex1[k];
					this.lowerpart[j++] = asurfacevertex1[k];
					byte0 = 0;
				}
				l = k;
				k = (k + 1) % 4;
			}

			if (flag) {
				this.plot_plane(asurfacevertex, 4);
				return;
			}
			if (this.critical) {
				flag1 = false;
			} else if (Double.compare(asurfacevertex[1].x, asurfacevertex[2].x) == 0) {
				flag1 = (((((asurfacevertex[2].z - asurfacevertex[3].z) * (this.cop.x - asurfacevertex[3].x))
						/ (asurfacevertex[2].x - asurfacevertex[3].x)) + asurfacevertex[3].z
						+ (((asurfacevertex[2].z - asurfacevertex[1].z) * (this.cop.y - asurfacevertex[1].y))
								/ (asurfacevertex[2].y - asurfacevertex[1].y))
						+ asurfacevertex[1].z)
						- asurfacevertex[2].z) > this.cop.z;
			} else {
				flag1 = (((((asurfacevertex[2].z - asurfacevertex[1].z) * (this.cop.x - asurfacevertex[1].x))
						/ (asurfacevertex[2].x - asurfacevertex[1].x)) + asurfacevertex[1].z
						+ (((asurfacevertex[2].z - asurfacevertex[3].z) * (this.cop.y - asurfacevertex[3].y))
								/ (asurfacevertex[2].y - asurfacevertex[3].y))
						+ asurfacevertex[3].z)
						- asurfacevertex[2].z) > this.cop.z;
			}
			if (j < 3) {
				if (flag1) {
					this.color = 0.7F;
					this.plot_plane(this.upperpart, i);
					this.color = 0.2F;
					this.plot_plane(asurfacevertex, 4);
					return;
				} else {
					this.color = 0.2F;
					this.plot_plane(asurfacevertex, 4);
					this.color = 0.7F;
					this.plot_plane(this.upperpart, i);
					return;
				}
			}
			if (i < 3) {
				if (flag1) {
					this.color = 0.2F;
					this.plot_plane(asurfacevertex, 4);
					this.color = 0.7F;
					this.plot_plane(this.lowerpart, j);
					return;
				} else {
					this.color = 0.7F;
					this.plot_plane(this.lowerpart, j);
					this.color = 0.2F;
					this.plot_plane(asurfacevertex, 4);
					return;
				}
			}
			if (flag1) {
				this.color = 0.7F;
				this.plot_plane(this.upperpart, i);
				this.color = 0.2F;
				this.plot_plane(asurfacevertex, 4);
				this.color = 0.7F;
				this.plot_plane(this.lowerpart, j);
				return;
			} else {
				this.color = 0.7F;
				this.plot_plane(this.lowerpart, j);
				this.color = 0.2F;
				this.plot_plane(asurfacevertex, 4);
				this.color = 0.7F;
				this.plot_plane(this.upperpart, i);
				return;
			}
		}

		private boolean plottable(final SurfaceVertex asurfacevertex[]) {
			return !asurfacevertex[0].isInvalid() && !asurfacevertex[1].isInvalid() && !asurfacevertex[2].isInvalid()
					&& !asurfacevertex[3].isInvalid();
		}

		private void plot_area(int i, final int j, int k, final int l, int i1, final int j1) {
			i *= this.calc_divisions + 1;
			i1 *= this.calc_divisions + 1;
			k *= this.calc_divisions + 1;
			int k1 = i;
			for (int l1 = j; l1 != l;) {

				if (((k1 + l1 + j1) >= this.vertex[0].length) || ((k1 + l1 + j1) < 0)) {
					break;
				}

				this.values1[1] = this.vertex[0][k1 + l1];
				this.values1[2] = this.vertex[0][k1 + l1 + j1];

				for (; k1 != k; k1 += i1) {
					Thread.yield();
					if (((k1 + i1 + l1 + j1) >= this.vertex[0].length) || ((k1 + i1 + l1) >= this.vertex[0].length)
							|| ((k1 + i1 + l1 + j1) < 0) || ((k1 + i1 + l1) < 0)) {
						break;
					}
					this.values1[0] = this.values1[1];
					this.values1[1] = this.vertex[0][k1 + i1 + l1];
					this.values1[3] = this.values1[2];
					this.values1[2] = this.vertex[0][k1 + i1 + l1 + j1];

					if (this.plot_mode == 4) {
						this.color = 0.2F;
					}
					if (this.plottable(this.values1)) {
						this.plot_plane(this.values1, 4);
					}

					if (this.plottable(this.values1)) {
						this.plot_plane(this.values1, 4);
					}
				}

				l1 += j1;
				k1 = i;
			}

		}

		private void plot_surface() {
			this.image_drawn = false;
			SurfaceVertex.setProjector(this.projector);
			float f = this.zmin;
			float f1 = this.zmax;

			if (f >= f1) {
				SurfaceChart.logger.debug("error zmax < zmin");
				this.zmax = this.zmin + 1;
				f1 = this.zmax;
				f = this.zmin;
			}

			final int k1 = this.displayDivisions;
			final int l1 = this.calc_divisions / k1;
			Thread.yield();
			this.zmax = f1;
			if (SurfaceChart.DEBUG) {
				SurfaceChart.logger.debug("regenerating ...");
			}
			this.color_factor = 0.8F / (this.zmax - this.zmin);
			if (this.plot_mode == 4) {
				this.color_factor *= 0.6F;
			}
			this.BufferGC.setColor(this.getBackground());
			this.BufferGC.fillRect(1, 1, this.bounds().width - 2, this.bounds().height - 2);
			this.BufferGC.draw3DRect(0, 0, this.bounds().width - 1, this.bounds().height - 1, false);
			this.drawBoxGridsTicksLabels(this.BufferGC, false);
			SurfaceVertex.setZRange(this.zmin, this.zmax);
			if ((this.vertex == null) || (this.vertex[0] == null)) {
				this.image_drawn = true;
				return;
			}
			final float f2 = this.projector.getDistance() * this.projector.getCosElevationAngle();
			this.cop = new SurfaceVertex(f2 * this.projector.getSinRotationAngle(),
					f2 * this.projector.getCosRotationAngle(),
					this.projector.getDistance() * this.projector.getSinElevationAngle());
			this.cop.transform();
			final boolean flag = this.cop.x > 0.0F;
			final boolean flag1 = this.cop.y > 0.0F;
			this.critical = false;
			int i;
			int k;
			int l;

			if (flag) {
				k = 0;
				l = this.calc_divisions;
				i = l1;
			} else {
				k = this.calc_divisions;
				l = 0;
				i = -l1;
			}
			int j;
			int i1;
			int j1;
			if (flag1) {
				i1 = 0;
				j1 = this.calc_divisions;
				j = l1;
			} else {
				i1 = this.calc_divisions;
				j1 = 0;
				j = -l1;
			}
			if ((this.cop.x > 10F) || (this.cop.x < -10F)) {
				if ((this.cop.y > 10F) || (this.cop.y < -10F)) {
					this.plot_area(k, i1, l, j1, i, j);
				} else {
					final int i2 = (int) (((this.cop.y + 10F) * k1) / 20F) * l1;
					this.plot_area(k, 0, l, i2, i, l1);
					this.plot_area(k, this.calc_divisions, l, i2, i, -l1);
				}
			} else if ((this.cop.y > 10F) || (this.cop.y < -10F)) {
				final int j2 = (int) (((this.cop.x + 10F) * k1) / 20F) * l1;
				this.plot_area(0, i1, j2, j1, l1, j);
				this.plot_area(this.calc_divisions, i1, j2, j1, -l1, j);
			} else {
				final int k2 = (int) (((this.cop.x + 10F) * k1) / 20F) * l1;
				final int l2 = (int) (((this.cop.y + 10F) * k1) / 20F) * l1;
				this.critical = true;
				this.plot_area(0, 0, k2, l2, l1, l1);
				this.plot_area(0, this.calc_divisions, k2, l2, l1, -l1);
				this.plot_area(this.calc_divisions, 0, k2, l2, -l1, l1);
				this.plot_area(this.calc_divisions, this.calc_divisions, k2, l2, -l1, -l1);
			}
			this.image_drawn = true;
			if (SurfaceChart.DEBUG) {
				SurfaceChart.logger.debug("completed");
			}

			this.checkIfDrawBounding();

		}

		private void plot_wireframe() {

			float f4 = 0.0F;
			float f5 = 0.0F;
			float f6 = 0.0F;
			SurfaceVertex.setProjector(this.projector);
			Point point = new Point(0, 0);
			Point point1 = new Point(0, 0);
			this.image_drawn = false;

			final Pair<Float, Float> pair = this.compareMayorMinor();

			final float f = pair.getFirst();
			final float f1 = pair.getSecond();

			final int l = this.displayDivisions;
			int i1 = this.calc_divisions / l;
			i1 = this.checkIfZero(i1);
			this.zmin = f;
			this.zmax = f1;

			this.BufferGC.setColor(this.getBackground());
			this.BufferGC.fillRect(1, 1, this.bounds().width - 2, this.bounds().height - 2);
			this.BufferGC.draw3DRect(0, 0, this.bounds().width - 1, this.bounds().height - 1, false);
			Thread.yield();
			this.drawBoxGridsTicksLabels(this.BufferGC, false);
			this.BufferGC.setColor(Color.black);
			SurfaceVertex.setZRange(this.zmin, this.zmax);
			if ((this.vertex == null) || (this.vertex[0] == null)) {
				this.image_drawn = true;
				return;
			}
			for (int l1 = 0; l1 < this.vertex.length; l1++) {
				if (((l1 != 0) || true) && ((l1 != 1) || true)) {
					int i = 0;
					int j = 0;
					int k = 0;
					for (int j1 = 0; i <= this.calc_divisions; j1 = (j1 + 1) % i1, i++) {
						boolean flag2 = true;
						if (j1 == 0) {
							while (j <= this.calc_divisions) {
								Thread.yield();
								if (k >= this.vertex[0].length) {
									j++;
									k++;
									continue;
								}
								final float f2 = this.vertex[l1][k].z;
								boolean flag4 = Float.isNaN(f2);
								boolean flag;
								if (!flag4) {
									if (f2 < this.zmin) {
										flag = true;
										final float f7 = (this.zmin - f6) / (f2 - f6);
										point = this.projector.project((f7 * (this.vertex[l1][k].x - f4)) + f4,
												(f7 * (this.vertex[l1][k].y - f5)) + f5, -10F);
									} else if (f2 > this.zmax) {
										flag = true;
										final float f8 = (this.zmax - f6) / (f2 - f6);
										point = this.projector.project((f8 * (this.vertex[l1][k].x - f4)) + f4,
												(f8 * (this.vertex[l1][k].y - f5)) + f5, 10F);
									} else {
										flag = false;
										point = this.vertex[l1][k].projection();
									}

									final Pair<Point, Boolean> pair1 = this.returnPointAndFlag(flag2, flag, j1, point1, f4,
											f5, f6, l1, k, f2, flag4);
									point1 = pair1.getFirst();
									flag4 = pair1.getSecond();
								} else {
									flag = true;
								}
								this.drawLine1(point, point1, j, flag4);
								point1 = point;
								flag2 = flag;
								f4 = this.vertex[l1][k].x;
								f5 = this.vertex[l1][k].y;
								f6 = f2;
								j++;
								k++;
							}
						} else {
							k += this.calc_divisions + 1;
						}
						j = 0;
					}

					i = 0;
					j = 0;
					k = 0;
					for (int k1 = 0; j <= this.calc_divisions; k1 = (k1 + 1) % i1) {
						boolean flag3 = true;
						if (k1 == 0) {
							while (i <= this.calc_divisions) {
								Thread.yield();
								if (k >= this.vertex[0].length) {
									i++;
									k += this.calc_divisions + 1;
									continue;
								}
								final float f3 = this.vertex[l1][k].z;
								boolean flag5 = Float.isNaN(f3);
								boolean flag1;
								if (!flag5) {
									if (f3 < this.zmin) {
										flag1 = true;
										final float f11 = (this.zmin - f6) / (f3 - f6);
										point = this.projector.project((f11 * (this.vertex[l1][k].x - f4)) + f4,
												(f11 * (this.vertex[l1][k].y - f5)) + f5, -10F);
									} else if (f3 > this.zmax) {
										flag1 = true;
										final float f12 = (this.zmax - f6) / (f3 - f6);
										point = this.projector.project((f12 * (this.vertex[l1][k].x - f4)) + f4,
												(f12 * (this.vertex[l1][k].y - f5)) + f5, 10F);
									} else {
										flag1 = false;
										point = this.vertex[l1][k].projection();
									}
									if (flag3 && !flag1 && (i != 0)) {
										point1 = this.setPoint1Calc(f4, f5, f6, point1, l1, k, f3);
									} else {
										flag5 = flag1 && flag3;
									}
								} else {
									flag1 = true;
								}
								this.drawLine1(point, point1, i, flag5);
								point1 = point;
								flag3 = flag1;
								f4 = this.vertex[l1][k].x;
								f5 = this.vertex[l1][k].y;
								f6 = f3;
								i++;
								k += this.calc_divisions + 1;
							}
						}
						i = 0;
						k = ++j;
					}

				}
			}

			this.image_drawn = true;

			this.checkIfDrawBounding();

		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param flag2
		 * @param flag
		 * @param j
		 * @param point1
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param l1
		 * @param k
		 * @param f2
		 * @param flag4
		 * @return
		 */
		protected Pair<Point, Boolean> returnPointAndFlag(final boolean flag2, final boolean flag, final int j, Point point1, final float f4,
				final float f5, final float f6, final int l1, final int k, final float f2, boolean flag4) {
			if (flag2 && !flag && (j != 0)) {
				point1 = this.setPoint1(f4, f5, f6, point1, l1, k, f2);
			} else {
				flag4 = flag && flag2;
			}

			return new Pair<Point, Boolean>(point1, flag4);
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 *
		 */
		protected Pair<Float, Float> compareMayorMinor() {
			float f = this.zmin;
			float f1 = this.zmax;

			if (f >= f1) {
				SurfaceChart.logger.debug("error zmax < zmin");
				this.zmax = this.zmin + 1;
				f1 = this.zmax;
				f = this.zmin;
			}

			return new Pair<Float, Float>(f, f1);
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param i1
		 * @return
		 */
		protected int checkIfZero(int i1) {
			if (i1 == 0) {
				i1 = 1;
			}
			return i1;
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 *
		 */
		protected void checkIfDrawBounding() {
			if (this.isBoxed) {
				this.drawBoundingBox();
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param point
		 * @param point1
		 * @param j
		 * @param flag4
		 */
		protected void drawLine1(final Point point, final Point point1, final int j, final boolean flag4) {
			if (!flag4 && (j != 0)) {
				this.BufferGC.drawLine(point1.x, point1.y, point.x, point.y);
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param point1
		 * @param l1
		 * @param k
		 * @param f3
		 * @return
		 */
		protected Point setPoint1Calc(final float f4, final float f5, final float f6, Point point1, final int l1, final int k, final float f3) {
			if (f6 > this.zmax) {
				point1 = this.setPoint1withFloat(f4, f5, f6, l1, k, f3);
			} else if (f6 < this.zmin) {
				point1 = this.setPoint1WithFloat3(f4, f5, f6, l1, k, f3);
			}
			return point1;
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param l1
		 * @param k
		 * @param f3
		 * @return
		 */
		protected Point setPoint1WithFloat3(final float f4, final float f5, final float f6, final int l1, final int k, final float f3) {
			Point point1;
			final float f14 = (this.zmin - f3) / (f6 - f3);
			point1 = this.projector.project((f14 * (f4 - this.vertex[l1][k].x)) + this.vertex[l1][k].x,
					(f14 * (f5 - this.vertex[l1][k].x)) + this.vertex[l1][k].y, -10F);
			return point1;
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param point1
		 * @param l1
		 * @param k
		 * @param f2
		 * @return
		 */
		protected Point setPoint1(final float f4, final float f5, final float f6, Point point1, final int l1, final int k, final float f2) {
			if (f6 > this.zmax) {
				point1 = this.setPoint1withFloat(f4, f5, f6, l1, k, f2);
			} else if (f6 < this.zmin) {
				point1 = this.setPoint1WithFloat2(f4, f5, f6, l1, k, f2);
			}
			return point1;
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param l1
		 * @param k
		 * @param f2
		 * @return
		 */
		protected Point setPoint1WithFloat2(final float f4, final float f5, final float f6, final int l1, final int k, final float f2) {
			Point point1;
			final float f10 = (this.zmin - f2) / (f6 - f2);
			point1 = this.projector.project((f10 * (f4 - this.vertex[l1][k].x)) + this.vertex[l1][k].x,
					(f10 * (f5 - this.vertex[l1][k].y)) + this.vertex[l1][k].y, -10F);
			return point1;
		}

		/**
		 * Method used to reduce the complexity of {@link #plot_wireframe()}
		 * @param f4
		 * @param f5
		 * @param f6
		 * @param l1
		 * @param k
		 * @param f2
		 * @return
		 */
		protected Point setPoint1withFloat(final float f4, final float f5, final float f6, final int l1, final int k, final float f2) {
			Point point1;
			final float f9 = (this.zmax - f2) / (f6 - f2);
			point1 = this.projector.project((f9 * (f4 - this.vertex[l1][k].x)) + this.vertex[l1][k].x,
					(f9 * (f5 - this.vertex[l1][k].y)) + this.vertex[l1][k].y, 10F);
			return point1;
		}

		@Override
		public void paintComponent(final Graphics g) {
			final long t = System.currentTimeMillis();
			if ((this.bounds().width <= 0) || (this.bounds().height <= 0)) {
				return;
			}
			if (this.image_drawn && (this.Buffer != null)) {
				g.drawImage(this.Buffer, 0, 0, this);
				if (SurfaceChart.DEBUG) {
					SurfaceChart.logger.debug(" paint image time:  " + (System.currentTimeMillis() - t));
				}
			} else {
				this.calculate();
				if (SurfaceChart.DEBUG) {
					SurfaceChart.logger.debug("paint calculate time:  " + (System.currentTimeMillis() - t));
				}
			}

		}

		@Override
		public void update(final Graphics g) {
			this.paint(g);
		}

		public void recalculateExpression() {
			if (this.expression == null) {
				return;
			}
			this.calculate(this.expression);
		}

		public void calculate(final String function) {
			try {
				this.expression = function;
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				final float f6 = this.xmin;
				final float f7 = this.xmax;
				final float f8 = this.ymin;
				final float f9 = this.ymax;

				this.setRanges(f6, f7, f8, f9);
				int percent = 0;
				if (SurfaceChart.DEBUG) {
					SurfaceChart.logger.debug("parsing ...");
				}
				final boolean flag = function != null;
				MathExpressionParser parser = null;
				if (flag) {

					parser = MathExpressionParserFactory.getInstance();
					parser.addStandardFunctions();
					parser.addStandardConstants();
					parser.addVariable("x", 0.0);
					parser.addVariable("y", 0.0);

					parser.setTraverse(true);
					parser.parseExpression(function);
					if (parser.hasError()) {
						SurfaceChart.logger.debug("Expression error : " + parser.getErrorInfo());
					}

					this.expression = function;
				} else {
					SurfaceChart.logger.debug("No function selected");
					return;
				}
				Thread.yield();

				final float f = (f7 - f6) / this.calc_divisions;
				final float f1 = (f9 - f8) / this.calc_divisions;
				final int l = (this.calc_divisions + 1) * (this.calc_divisions + 1);
				this.setValuesArray(null);
				SurfaceVertex asurfacevertex[][] = null;
				try {
					asurfacevertex = new SurfaceVertex[1][l];

				} catch (final OutOfMemoryError _ex) {
					SurfaceChart.logger.error("Not enough memory", _ex);
					return;
				} catch (final Exception exception) {
					SurfaceChart.logger.debug("Error: " + exception.toString(), exception);
					return;
				}
				float f11 = 1.0F / 0.0F;
				float f10 = -1.0F / 0.0F;

				this.destroyImage();
				int i = 0;
				int j = 0;
				int k = 0;
				float f2 = f6;
				float f3 = f8;
				final float f12 = 20F / (f7 - f6);
				final float f13 = 20F / (f9 - f8);
				while (i <= this.calc_divisions) {
					if (flag) {
						parser.addVariable("x", f2);
						parser.addVariable("y", f3);
					}

					while (j <= this.calc_divisions) {
						Thread.yield();
						if (flag) {
							float f4 = (float) parser.getValue();
							if (Float.isInfinite(f4)) {
								f4 = 0 / 0.0F;
							}
							if (!Float.isNaN(f4)) {
								if ((f4 > f11) || Float.isInfinite(f11)) {
									f11 = f4;
								} else if ((f4 < f10) || Float.isInfinite(f10)) {
									f10 = f4;
								}
							}
							asurfacevertex[0][k] = new SurfaceVertex(((f2 - f6) * f12) - 10F, ((f3 - f8) * f13) - 10F,
									f4);
						}

						j++;
						f3 += f1;
						if (flag) {
							parser.addVariable("y", f3);
						}

						k++;
						if (SurfaceChart.DEBUG) {
							final int p = (k * 100) / l;
							if (p >= (percent + 10)) {
								SurfaceChart.logger.debug("Calculating : " + p + " % completed");
								percent = p;
							}
						}
					}
					j = 0;
					f3 = f8;
					i++;
					f2 += f;
				}

				this.setValuesArray(asurfacevertex);
				this.paintImmediately(this.getBounds());
			} catch (final Exception ex) {
				SurfaceChart.logger.error(null, ex);
			} finally {
				this.setCursor(Cursor.getDefaultCursor());
			}
		}

		public void calculate() {
			final long t = System.currentTimeMillis();
			SurfaceVertex.invalidate();

			if (this.plot_mode == 0) {
				this.plot_wireframe();
			} else {
				this.plot_surface();
			}
			if (SurfaceChart.DEBUG) {
				SurfaceChart.logger.debug("Plot time: " + (System.currentTimeMillis() - t));
			}

			this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
		}

		public void setPlotMode(final int p) {
			this.plot_mode = p;
			this.update();
		}

	}

	public void setValuesArray(final SurfaceVertex asurfacevertex[][]) {
		this.canvas.setValuesArray(asurfacevertex);
	}

	public void setValuesArray(final Coordinate[] puntos) {
		this.setValuesArray(puntos, true, false);
	}

	class CoordenadaComp implements Comparable {

		Coordinate c = null;

		public CoordenadaComp(final Coordinate c) {
			this.c = c;
		}

		@Override
		public int compareTo(final Object o) {
			if (!(o instanceof CoordenadaComp)) {
				throw new IllegalArgumentException("...");
			}
			final CoordenadaComp p = this;
			final CoordenadaComp p2 = (CoordenadaComp) o;
			if ((o == null) || (p2.c == null)) {
				return 1;
			}
			if ((p.c == null) || (p.c.x < p2.c.x)) {
				return -1;
			} else if (Double.compare(p.c.x, p2.c.x) == 0) {
				if (p.c.y < p2.c.y) {
					return -1;
				} else if (p.c.y > p2.c.y) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 1;
			}
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	public void setValuesArray(final Coordinate[] points, final boolean autorangexy, final boolean autorangez) {
		// Array
		final SurfaceVertex[][] v = new SurfaceVertex[1][points.length];
		// Increasing y, then x

		final List c = new Vector();
		for (int i = 0; i < points.length; i++) {
			if (points[i] == null) {
				SurfaceChart.logger.debug("point " + i + " is NULL");
			}
			c.add(new CoordenadaComp(points[i]));
		}
		Collections.sort(c);

		float maxX = ((CoordenadaComp) c.get(0)).c.x;
		float maxY = ((CoordenadaComp) c.get(0)).c.y;
		float maxZ = ((CoordenadaComp) c.get(0)).c.z;

		float minX = ((CoordenadaComp) c.get(0)).c.x;
		float minY = ((CoordenadaComp) c.get(0)).c.y;
		float minZ = ((CoordenadaComp) c.get(0)).c.z;

		float x, y, z = 0;

		for (int i = 0; i < c.size(); i++) {
			x = ((CoordenadaComp) c.get(i)).c.x;
			y = ((CoordenadaComp) c.get(i)).c.y;
			z = ((CoordenadaComp) c.get(i)).c.z;
			if (x > maxX) {
				maxX = x;
			} else if (x < minX) {
				minX = x;
			}
			if (y > maxY) {
				maxY = y;
			} else if (y < minY) {
				minY = y;
			}
			if (z > maxZ) {
				maxZ = z;
			} else if (z < minZ) {
				minZ = z;
			}
		}

		final float f6 = autorangexy ? minX : this.getXMin();
		final float f7 = autorangexy ? maxX : this.getXMax();
		final float f8 = autorangexy ? minY : this.getYMin();
		final float f9 = autorangexy ? maxY : this.getYMax();
		final float f12 = 20F / (f7 - f6);
		final float f13 = 20F / (f9 - f8);
		double divisiones = Math.sqrt(points.length);
		divisiones = (int) (divisiones - 1);
		this.canvas.setCalcDivisions((int) divisiones);
		this.canvas.setDisplayDivisions((int) divisiones);
		SurfaceChart.logger.debug(String.valueOf(divisiones));
		this.canvas.setRanges(f6, f7, f8, f9, autorangez ? minZ : this.getZMin(), autorangez ? maxZ : this.getZMax());

		for (int i = 0; i < c.size(); i++) {
			v[0][i] = new SurfaceVertex(((((CoordenadaComp) c.get(i)).c.x - f6) * f12) - 10F,
					((((CoordenadaComp) c.get(i)).c.y - f8) * f13) - 10F,
					((CoordenadaComp) c.get(i)).c.z);
		}
		//
		this.canvas.setValuesArray(v);
	}

	public static void main(final String args[]) {
		final JFrame f = new JFrame();
		final SurfaceChart s = new SurfaceChart(50, 20, -60, 60, -100, 100, 0, 0.20f, "1/sqrt(x^2+2*y^2)");
		s.setPlotMode(SurfaceChart.SPECTRUM);
		final JButton expressionButton = new JButton("Insert expression");
		expressionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String ex = JOptionPane.showInputDialog(f, "Expression?");
				s.setExpression(ex);
			}
		});
		f.getContentPane().add(expressionButton, BorderLayout.SOUTH);
		final ControlPanel panelControles = new ControlPanel(s);
		f.getContentPane().add(panelControles, BorderLayout.WEST);
		f.getContentPane().add(s);
		f.pack();
		f.setVisible(true);

	}

}
