package com.ontimize.util.calc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Calculator extends JDialog implements com.ontimize.gui.i18n.Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(Calculator.class);

	protected static boolean DEBUG = false;

	protected ImageIcon icon = null;

	protected JButton zoomButton = null;

	protected boolean zoom = false;

	protected ResourceBundle resourceBundle = null;

	protected JPopupMenu popupMenu = new JPopupMenu();

	protected JMenuItem copy = new JMenuItem(Calculator.copyKey);

	class InternalJButton extends JButton {

		public InternalJButton(final String text) {
			super(text);
			this.setMargin(new Insets(4, 5, 4, 5));
		}

	}

	class DoubleNumberDocument extends PlainDocument implements com.ontimize.gui.i18n.Internationalization {

		DecimalFormatSymbols symbology = new DecimalFormatSymbols();

		NumberFormat formatter = NumberFormat.getInstance();

		Double doubleValue = null;

		public DoubleNumberDocument() {
			super();
			this.formatter.setMinimumFractionDigits(2);
			this.formatter.setMaximumFractionDigits(8);
		}

		@Override
		public void setResourceBundle(final ResourceBundle res) {

		}

		@Override
		public List getTextsToTranslate() {
			return new Vector();
		}

		@Override
		public void setComponentLocale(final Locale l) {
			this.symbology = new DecimalFormatSymbols(l);
			this.formatter = NumberFormat.getInstance(l);
			try {
				this.remove(0, this.getLength());
				if (this.doubleValue != null) {
					this.insertString(0, this.formatter.format(this.doubleValue.doubleValue()), null);
				}
			} catch (final Exception e) {
				Calculator.logger.error(null, e);
			}
		}

		public double getDouble() {
			return this.doubleValue.doubleValue();
		}

		@Override
		public void insertString(final int offset, final String value, final AttributeSet attributes) {
			try {
				if (this.getText(0, this.getLength()).equals("Error")) {
					this.remove(0, this.getLength());
				}
				if ((offset == 0) && value.equals("Error")) {
					super.insertString(0, value, attributes);
					return;
				}
			} catch (final Exception e) {
				Calculator.logger.trace(null, e);
			}
			// uses the system separator (. or ,)
			final char decimalSeparator = this.symbology.getDecimalSeparator();
			// first check:
			if (value.length() == 1) {
				// Checks that the character is numeric
				if (!Character.isDigit(value.charAt(0)) && (value.charAt(0) != decimalSeparator)) {
					Toolkit.getDefaultToolkit().beep();
					return;
				} else {
					// Only one decimal separator is allowed
					try {
						final int iLenght = this.getLength();
						boolean separatorAlreadyExist = false;
						final String sCurrentText = this.getText(0, iLenght);
						for (int i = 0; i < iLenght; i++) {
							if (sCurrentText.charAt(i) == decimalSeparator) {
								separatorAlreadyExist = true;
								break;
							}
						}
						if (((value.charAt(0) == decimalSeparator) && (offset == 0))
								|| ((value.charAt(0) == decimalSeparator) && separatorAlreadyExist)) {
							return;
						}
						super.insertString(offset, value, attributes);
						try {
							final StringBuilder newText = new StringBuilder(this.getText(0, this.getLength()));
							final Number number = this.formatter.parse(newText.toString());
							this.doubleValue = new Double(number.doubleValue());

						} catch (final Exception ex) {
							if (com.ontimize.gui.ApplicationManager.DEBUG) {
								Calculator.logger.debug(null, ex);
							} else {
								Calculator.logger.trace(null, ex);
							}
							return;
						}
					} catch (final Exception e) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							Calculator.logger.debug(null, e);
						} else {
							Calculator.logger.trace(null, e);
						}
					}
				}
			} else {
				// After insert checks that the result string is a number
				try {
					final StringBuilder sCurrentText = new StringBuilder(this.getText(0, this.getLength()));
					sCurrentText.insert(offset, value);
					try {
						final Number number = this.formatter.parse(sCurrentText.toString());
						try {
							this.remove(0, this.getLength());
							super.insertString(0, this.formatter.format(number).toString(), attributes);
							this.doubleValue = new Double(number.doubleValue());
						} catch (final BadLocationException e) {
							if (com.ontimize.gui.ApplicationManager.DEBUG) {
								Calculator.logger.debug(null, e);
							} else {
								Calculator.logger.trace(null, e);
							}
						}
					} catch (final ParseException ex) {
						if (com.ontimize.gui.ApplicationManager.DEBUG) {
							Calculator.logger.debug(null, ex);
						} else {
							Calculator.logger.trace(null, ex);
						}
						return;
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						Calculator.logger.debug(null, e);
					} else {
						Calculator.logger.trace(null, e);
					}
					return;
				}
			}
		}

	}

	public class ButtonListener implements ActionListener {

		private static final String DIGIT_0 = "0";

		private static final String DIGIT_1 = "1";

		private static final String DIGIT_2 = "2";

		private static final String DIGIT_3 = "3";

		private static final String DIGIT_4 = "4";

		private static final String DIGIT_5 = "5";

		private static final String DIGIT_6 = "6";

		private static final String DIGIT_7 = "7";

		private static final String DIGIT_8 = "8";

		private static final String DIGIT_9 = "9";

		private static final String SYMBOL_ADD = "+";

		private static final String SYMBOL_MINUS = "-";

		private static final String SYMBOL_MULTIPLY_BY = "*";

		private static final String SYMBOL_DIVIDED_BY = "/";

		private static final String SYMBOL_C = "C";

		private static final String SYMBOL_EQUALS = "=";

		private static final String SYMBOL_SQRT = "sqrt";

		private static final String SYMBOL_SIGNUM = "+/-";

		private static final String SYMBOL_ZOOM = "zoom";

		private static final String SYMBOL_INVERSE = "1/x";

		public ButtonListener() {
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (event.getSource() instanceof JButton) {
				final String actionCommand = ((JButton) event.getSource()).getActionCommand();
				if (DIGIT_0.equals(actionCommand)) {
					processDigitNumber(DIGIT_0);
				}

				if (DIGIT_1.equals(actionCommand)) {
					processDigitNumber(DIGIT_1);
				}

				if (DIGIT_2.equals(actionCommand)) {
					processDigitNumber(DIGIT_2);
				}

				if (DIGIT_3.equals(actionCommand)) {
					processDigitNumber(DIGIT_3);
				}

				if (DIGIT_4.equals(actionCommand)) {
					processDigitNumber(DIGIT_4);
				}

				if (DIGIT_5.equals(actionCommand)) {
					processDigitNumber(DIGIT_5);
				}

				if (DIGIT_6.equals(actionCommand)) {
					processDigitNumber(DIGIT_6);
				}

				if (DIGIT_7.equals(actionCommand)) {
					processDigitNumber(DIGIT_7);
				}

				if (DIGIT_8.equals(actionCommand)) {
					processDigitNumber(DIGIT_8);
				}

				if (DIGIT_9.equals(actionCommand)) {
					processDigitNumber(DIGIT_9);
				}

				if (SYMBOL_ADD.equals(actionCommand)) {
					processAddOperation();
				}

				if (SYMBOL_MINUS.equals(actionCommand)) {
					processSubstractOperation();
				}

				if (SYMBOL_MULTIPLY_BY.equals(actionCommand)) {
					processMultiplyByOperation();
				}

				if (SYMBOL_DIVIDED_BY.equals(actionCommand)) {
					processDividedByOperation();
				}

				if (actionCommand.equals(Calculator.this.separator)) {
					processSeparator();
				}

				if (SYMBOL_C.equals(actionCommand)) {
					processC();
				}

				if (SYMBOL_EQUALS.equals(actionCommand)) {
					processEquals();
				}

				if (SYMBOL_SQRT.equals(actionCommand)) {
					processSQRT();
				}

				if (SYMBOL_SIGNUM.equals(actionCommand)) {
					processSigNum();
				}

				if (SYMBOL_ZOOM.equals(actionCommand)) {
					processZoom();
				}

				if (SYMBOL_INVERSE.equals(actionCommand)) {
					processInverse();
				}
			}
		}

		protected void processInverse() {
			Calculator.this.operand = true;
			Calculator.this.currentOperation = Calculator.INVERSE;
			if (!Calculator.this.viewer.getText().equals("")) {
				Calculator.this.calculateResult();
			}
			Calculator.this.operand = false;
		}

		protected void processZoom() {
			// Change the window size to make the text field larger
			if (!Calculator.this.zoom) {
				Calculator.this.setSize(new Dimension(Calculator.this.getPreferredSize().width * 2,
						Calculator.this.getPreferredSize().height));
				Calculator.this.validate();
				Calculator.this.zoom = true;
			} else {
				Calculator.this.pack();
				Calculator.this.zoom = false;
			}
		}

		protected void processSigNum() {
			Calculator.this.operand = true;
			Calculator.this.currentOperation = Calculator.SIGNUM;
			if (!Calculator.this.viewer.getText().equals("")) {
				Calculator.this.calculateResult();
			}
			Calculator.this.operand = false;
		}

		protected void processSQRT() {
			Calculator.this.operand = true;
			Calculator.this.currentOperation = Calculator.SQRT;
			if (!Calculator.this.viewer.getText().equals("")) {
				Calculator.this.calculateResult();
			}
			Calculator.this.operand = false;
		}

		protected void processEquals() {
			Calculator.this.calculateResult();
			Calculator.this.operand = false;
		}

		protected void processC() {
			Calculator.this.operand = false;
			Calculator.this.result = false;
			Calculator.this.viewer.setText("");
		}

		protected void processSeparator() {
			try {
				if (Calculator.this.viewer.getDocument().getLength() == 0) {
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(), "0", null);
				}
				Calculator.this.viewer.getDocument()
				.insertString(Calculator.this.viewer.getDocument().getLength(), Calculator.this.separator, null);
			} catch (final Exception e) {
				Calculator.logger.trace(null, e);
			}
		}

		protected void processDividedByOperation() {
			// If this is the second operation then executes the
			// previous one
			if (Calculator.this.operand) {
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
					Calculator.this.currentOperation = Calculator.DIVISION;
				}
			} else {
				Calculator.this.currentOperation = Calculator.DIVISION;
				Calculator.this.operand = true;
				if (!Calculator.this.result) {
					Calculator.this.viewer.setText("");
				}
				Calculator.this.memorizedNumber = ((DoubleNumberDocument) Calculator.this.viewer.getDocument())
						.getDouble();
			}
		}

		protected void processMultiplyByOperation() {
			// If this is the second operation then executes the
			// previous one
			if (Calculator.this.operand) {
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
					Calculator.this.currentOperation = Calculator.PRODUCT;
				}
			} else {
				Calculator.this.currentOperation = Calculator.PRODUCT;
				Calculator.this.operand = true;
				if (!Calculator.this.result) {
					Calculator.this.viewer.setText("");
				}
				Calculator.this.memorizedNumber = ((DoubleNumberDocument) Calculator.this.viewer.getDocument())
						.getDouble();
			}
		}

		protected void processSubstractOperation() {
			// If it is the second operation then executes the previous
			// one
			if (Calculator.this.operand) {
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
					Calculator.this.currentOperation = Calculator.SUBTRACT;
				}
			} else {
				Calculator.this.currentOperation = Calculator.SUBTRACT;
				Calculator.this.operand = true;
				if (!Calculator.this.result) {
					Calculator.this.viewer.setText("");
				}
				Calculator.this.memorizedNumber = ((DoubleNumberDocument) Calculator.this.viewer.getDocument())
						.getDouble();
			}
		}

		protected void processAddOperation() {
			// If it is the second operation then executes the previous
			// one
			if (Calculator.this.operand) {
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
					Calculator.this.currentOperation = Calculator.SUM;
				}
			} else {
				Calculator.this.currentOperation = Calculator.SUM;
				Calculator.this.operand = true;
				if (!Calculator.this.result) {
					Calculator.this.viewer.setText("");
				}
				Calculator.this.memorizedNumber = ((DoubleNumberDocument) Calculator.this.viewer.getDocument())
						.getDouble();
			}
		}

		protected void processDigitNumber(final String digit) {
			if (Calculator.this.result) {
				Calculator.this.result = false;
				Calculator.this.viewer.setText("");
			}
			if (!Calculator.this.operand) {
				try {
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(), digit, null);
				} catch (final Exception e) {
					Calculator.logger.trace(null, e);
				}
			} else {
				try {
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(), digit, null);
				} catch (final Exception e) {
					Calculator.logger.trace(null, e);
				}
			}
		}

	}

	double memorizedNumber = 0.0;

	boolean operand = false;

	boolean result = false;

	int currentOperation = 0;

	private static final int SUM = 0;

	private static final int SUBTRACT = 1;

	private static final int PRODUCT = 2;

	private static final int DIVISION = 3;

	private static final int SQRT = 4;

	private static final int INVERSE = 5;

	private static final int SIGNUM = 6;

	JPanel containerPanel = new JPanel();

	JPanel viewerPanel = new JPanel(new BorderLayout(10, 10));

	JPanel buttonPanel = new JPanel();

	static String title = "Calculadora";

	static String copyKey = "Copiar";

	InternalJButton button0 = new InternalJButton("0");

	InternalJButton button1 = new InternalJButton("1");

	InternalJButton button2 = new InternalJButton("2");

	InternalJButton button3 = new InternalJButton("3");

	InternalJButton button4 = new InternalJButton("4");

	InternalJButton button5 = new InternalJButton("5");

	InternalJButton button6 = new InternalJButton("6");

	InternalJButton button7 = new InternalJButton("7");

	InternalJButton button8 = new InternalJButton("8");

	InternalJButton button9 = new InternalJButton("9");

	InternalJButton addButton = new InternalJButton("+");

	InternalJButton substractButton = new InternalJButton("-");

	InternalJButton multiplyButton = new InternalJButton("*");

	InternalJButton divideButton = new InternalJButton("/");

	InternalJButton dotButton = new InternalJButton(".");

	InternalJButton equalButton = new InternalJButton("=");

	InternalJButton deleteButton = new InternalJButton("C");

	InternalJButton sqrtButton = new InternalJButton("sqrt");

	InternalJButton inverseButton = new InternalJButton("1/x");

	InternalJButton signumButton = new InternalJButton("+/-");

	JTextField viewer = new JTextField();

	String separator = ".";

	public Calculator() {
		this((Frame) null);
	}

	public Calculator(final Frame parentFrame) {
		super(parentFrame, true);
		// Default locale
		final DecimalFormatSymbols df = new DecimalFormatSymbols();
		this.separator = new Character(df.getDecimalSeparator()).toString();
		this.dotButton.setText(this.separator);
		this.deleteButton.setForeground(Color.red);
		this.initUI();
		this.registerActionsAndKeyBindings();
	}

	protected void registerActionAndKeyBinding(final InputMap inMap, final ActionMap actMap, final JButton button, final String key,
			final int keyCodeA, final int keyCodeB, final Action action) {
		if (keyCodeA != -1) {
			final KeyStroke ks = KeyStroke.getKeyStroke(keyCodeA, 0, true);
			inMap.put(ks, key);
		}
		if (keyCodeB != -1) {
			final KeyStroke ksn = KeyStroke.getKeyStroke(keyCodeB, 0, true);
			inMap.put(ksn, key);
		}
		actMap.put(key, action);
		if (button != null) {
			button.setAction(action);
		}
	}

	protected void registerActionsAndKeyBindings() {
		final InputMap inMap = this.containerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actMap = this.containerPanel.getActionMap();

		// Number buttons
		this.registerActionAndKeyBinding(inMap, actMap, this.button0, "0", KeyEvent.VK_0, KeyEvent.VK_NUMPAD0,
				new ActionNumber("0"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button1, "1", KeyEvent.VK_1, KeyEvent.VK_NUMPAD1,
				new ActionNumber("1"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button2, "2", KeyEvent.VK_2, KeyEvent.VK_NUMPAD2,
				new ActionNumber("2"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button3, "3", KeyEvent.VK_3, KeyEvent.VK_NUMPAD3,
				new ActionNumber("3"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button4, "4", KeyEvent.VK_4, KeyEvent.VK_NUMPAD4,
				new ActionNumber("4"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button5, "5", KeyEvent.VK_5, KeyEvent.VK_NUMPAD5,
				new ActionNumber("5"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button6, "6", KeyEvent.VK_6, KeyEvent.VK_NUMPAD6,
				new ActionNumber("6"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button7, "7", KeyEvent.VK_7, KeyEvent.VK_NUMPAD7,
				new ActionNumber("7"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button8, "8", KeyEvent.VK_8, KeyEvent.VK_NUMPAD8,
				new ActionNumber("8"));
		this.registerActionAndKeyBinding(inMap, actMap, this.button9, "9", KeyEvent.VK_9, KeyEvent.VK_NUMPAD9,
				new ActionNumber("9"));

		// Operation buttons
		this.registerActionAndKeyBinding(inMap, actMap, this.addButton, "+", KeyEvent.VK_ADD, -1,
				new ActionOperator("+", Calculator.SUM));
		this.registerActionAndKeyBinding(inMap, actMap, this.substractButton, "-", KeyEvent.VK_MINUS,
				KeyEvent.VK_SUBTRACT, new ActionOperator("-", Calculator.SUBTRACT));
		this.registerActionAndKeyBinding(inMap, actMap, this.multiplyButton, "*", KeyEvent.VK_MULTIPLY, -1,
				new ActionOperator("*", Calculator.PRODUCT));
		this.registerActionAndKeyBinding(inMap, actMap, this.divideButton, "/", KeyEvent.VK_DIVIDE, -1,
				new ActionOperator("/", Calculator.DIVISION));
		this.registerActionAndKeyBinding(inMap, actMap, this.addButton, "+", KeyEvent.VK_ADD, -1,
				new ActionOperator("+", Calculator.SUM));

		// Other
		this.registerActionAndKeyBinding(inMap, actMap, null, "back", KeyEvent.VK_BACK_SPACE, -1,
				new DefaultCalculatorAction() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				try {
					if (Calculator.this.viewer.getDocument().getLength() > 0) {
						Calculator.this.viewer.getDocument()
						.remove(Calculator.this.viewer.getDocument().getLength() - 1, 1);
					}
				} catch (final BadLocationException e1) {
					Calculator.logger.error(null, e1);
				}
			}

		});

		// Decimal separator
		this.registerActionAndKeyBinding(inMap, actMap, this.dotButton, this.separator, KeyEvent.VK_COMMA,
				KeyEvent.VK_DECIMAL, new DefaultCalculatorAction(this.separator) {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				try {
					if (Calculator.this.viewer.getDocument().getLength() == 0) {
						Calculator.this.viewer.getDocument()
						.insertString(Calculator.this.viewer.getDocument().getLength(), "0", null);
					}
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(),
							Calculator.this.separator, null);
				} catch (final Exception ex) {
					Calculator.logger.trace(null, ex);
				}
			}

		});

		// Delete
		this.registerActionAndKeyBinding(inMap, actMap, this.deleteButton, "C", KeyEvent.VK_C, -1,
				new DefaultCalculatorAction("C") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				Calculator.this.operand = false;
				Calculator.this.result = false;
				Calculator.this.viewer.setText("");
			}
		});

		// Equals
		this.registerActionAndKeyBinding(inMap, actMap, this.equalButton, "=", KeyEvent.VK_ENTER, -1,
				new DefaultCalculatorAction("=") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				Calculator.this.calculateResult();
				Calculator.this.operand = false;
			}
		});

		// Close
		this.registerActionAndKeyBinding(inMap, actMap, null, "esc", KeyEvent.VK_ESCAPE, -1,
				new DefaultCalculatorAction("esc") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Calculator.this.setVisible(false);
			}
		});

		// Square root
		this.registerActionAndKeyBinding(inMap, actMap, this.sqrtButton, "sqrt", -1, -1,
				new DefaultCalculatorAction("sqrt") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				Calculator.this.operand = true;
				Calculator.this.currentOperation = Calculator.SQRT;
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
				}
				Calculator.this.operand = false;
			}
		});

		// Change the sign
		this.registerActionAndKeyBinding(inMap, actMap, this.signumButton, "+/-", -1, -1,
				new DefaultCalculatorAction("+/-") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				Calculator.this.operand = true;
				Calculator.this.currentOperation = Calculator.SIGNUM;
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
				}
				Calculator.this.operand = false;
			}
		});

		// Inverse
		this.registerActionAndKeyBinding(inMap, actMap, this.inverseButton, "1/x", -1, -1,
				new DefaultCalculatorAction("1/x") {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				Calculator.this.operand = true;
				Calculator.this.currentOperation = Calculator.INVERSE;
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
				}
				Calculator.this.operand = false;
			}
		});

		// Zoom
		final URL iconURL = this.getClass().getResource("images/calc.gif");
		ImageIcon icono = null;
		if (iconURL != null) {
			icono = new ImageIcon(iconURL);
		}
		this.registerActionAndKeyBinding(inMap, actMap, this.zoomButton, "zoom", -1, -1,
				new DefaultCalculatorAction("", icono) {

			@Override
			public void actionPerformed(final ActionEvent e) {
				super.actionPerformed(e);
				// Change the window size to make the text field larger.
				if (!Calculator.this.zoom) {
					Calculator.this.setSize(new Dimension(Calculator.this.getPreferredSize().width * 2,
							Calculator.this.getPreferredSize().height));
					Calculator.this.validate();
					Calculator.this.zoom = true;
				} else {
					Calculator.this.pack();
					Calculator.this.zoom = false;
				}
			}
		});

	}

	private void initUI() {
		this.viewer.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getClickCount() == 1) && (e.getModifiers() == InputEvent.META_MASK)) {
					Calculator.this.showPopupMenu(e.getX(), e.getY());
				}
			}
		});
		this.createPopupMenu();
		this.containerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.viewerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
		this.setContentPane(this.containerPanel);
		this.setTitle(Calculator.title);
		this.getContentPane().setLayout(new BorderLayout());
		this.buttonPanel.setLayout(new GridLayout(4, 5, 6, 6));
		this.viewer.setHorizontalAlignment(SwingConstants.RIGHT);
		this.viewer.setDocument(new DoubleNumberDocument());
		this.viewer.setEditable(false);

		final URL iconURL = this.getClass().getResource("images/calc.png");
		if (iconURL != null) {
			this.icon = new ImageIcon(iconURL);
			this.zoomButton = new JButton(this.icon);
			this.zoomButton.setMargin(new Insets(0, 0, 0, 0));
			this.viewerPanel.add(this.zoomButton, BorderLayout.WEST);
			this.zoomButton.setActionCommand("zoom");
			this.zoomButton.addActionListener(new ButtonListener());

		}

		this.buttonPanel.add(this.button7);
		this.buttonPanel.add(this.button8);
		this.buttonPanel.add(this.button9);
		this.buttonPanel.add(this.divideButton);
		this.buttonPanel.add(this.sqrtButton);

		this.buttonPanel.add(this.button4);
		this.buttonPanel.add(this.button5);
		this.buttonPanel.add(this.button6);
		this.buttonPanel.add(this.multiplyButton);
		this.buttonPanel.add(this.inverseButton);

		this.buttonPanel.add(this.button1);
		this.buttonPanel.add(this.button2);
		this.buttonPanel.add(this.button3);
		this.buttonPanel.add(this.substractButton);
		this.buttonPanel.add(this.signumButton);

		this.buttonPanel.add(this.deleteButton);
		this.buttonPanel.add(this.button0);
		this.buttonPanel.add(this.dotButton);
		this.buttonPanel.add(this.addButton);
		this.buttonPanel.add(this.equalButton);

		this.getContentPane().add(this.viewerPanel, BorderLayout.NORTH);
		this.viewerPanel.add(this.viewer);
		this.getContentPane().add(this.buttonPanel);

		this.setResizable(false);
		this.pack();

	}

	public void calculateResult() {
		if (this.operand) {
			if (!this.viewer.getText().equals("")) {
				try {
					switch (this.currentOperation) {
					case Calculator.SUM:
						this.add();
						break;
					case Calculator.SUBTRACT:
						this.substract();
						break;
					case Calculator.PRODUCT:
						this.multiply();
						break;
					case Calculator.DIVISION:
						this.divide();
						break;
					case Calculator.SIGNUM:
						this.signum();
						break;
					case Calculator.SQRT:
						this.square();
						break;
					case Calculator.INVERSE:
						this.inverse();
						break;
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						Calculator.logger.debug(null, e);
					} else {
						Calculator.logger.trace(null, e);
					}
					this.viewer.setText("Error");
					this.operand = false;
					this.result = false;
				}
			}
		}
	}

	public void add() throws Exception {
		this.result = true;
		// Sum the stored number and the current one
		double dOperationResult = 0.0;
		dOperationResult = this.memorizedNumber + ((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void multiply() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = this.memorizedNumber * ((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void substract() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = this.memorizedNumber - ((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void divide() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = this.memorizedNumber / ((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void inverse() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = 1 / ((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void square() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = Math.sqrt(((DoubleNumberDocument) this.viewer.getDocument()).getDouble());
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	public void signum() throws Exception {
		this.result = true;
		double dOperationResult = 0.0;
		dOperationResult = -((DoubleNumberDocument) this.viewer.getDocument()).getDouble();
		this.memorizedNumber = dOperationResult;
		if (!this.checkResult()) {
			throw new Exception("Error");
		}
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(dOperationResult));
	}

	private boolean checkResult() {
		if (Double.isNaN(this.memorizedNumber)) {
			return false;
		}
		if (Double.isInfinite(this.memorizedNumber)) {
			return false;
		}
		return true;
	}

	public double showCalculator() {
		this.setVisible(true);
		return this.memorizedNumber;
	}

	public double showCalculator(final double initialValue, int x, int y) {
		this.memorizedNumber = initialValue;
		this.viewer.setText(((DoubleNumberDocument) this.viewer.getDocument()).formatter.format(this.memorizedNumber));
		if ((x + this.getWidth()) > Toolkit.getDefaultToolkit().getScreenSize().width) {
			x = Toolkit.getDefaultToolkit().getScreenSize().width - this.getWidth();
		} else {
			if (x < 0) {
				x = 0;
			}
		}
		if ((y + this.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().height) {
			y = Toolkit.getDefaultToolkit().getScreenSize().height - this.getHeight();
		} else {
			if (y < 0) {
				y = 0;
			}
		}

		this.setLocation(x, y);
		this.setVisible(true);
		return this.memorizedNumber;
	}

	protected void createPopupMenu() {

		this.copy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final StringSelection s = new StringSelection(Calculator.this.viewer.getText());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
				} catch (final Exception ex) {
					Calculator.logger.error(null, ex);
				}
			}
		});
		this.popupMenu.add(this.copy);
		this.popupMenu.pack();
	}

	protected void showPopupMenu(final int x, final int y) {
		if (this.viewer.getText().equals("") || this.viewer.getText().equals("Error")) {
			this.copy.setEnabled(false);
		} else {
			this.copy.setEnabled(true);
		}
		this.popupMenu.show(this.viewer, x, y);
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resourceBundle = res;
		try {
			if (this.resourceBundle != null) {
				this.setTitle(this.resourceBundle.getString(Calculator.title));
			}
		} catch (final Exception e) {
			Calculator.logger.trace(null, e);
			this.setTitle(Calculator.title);
		}
		try {
			if (this.resourceBundle != null) {
				this.copy.setText(this.resourceBundle.getString(Calculator.copyKey));
			}
		} catch (final Exception e) {
			Calculator.logger.trace(null, e);
			this.copy.setText(Calculator.copyKey);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {

		final DoubleNumberDocument d = (DoubleNumberDocument) this.viewer.getDocument();
		this.separator = new Character(d.symbology.getDecimalSeparator()).toString();
		this.dotButton.setText(this.separator);
		d.setComponentLocale(l);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(Calculator.title);
		v.add(Calculator.copyKey);
		return v;
	}

	class ActionNumber extends DefaultCalculatorAction {

		protected String insertString;

		public ActionNumber(final String insertString) {
			super(insertString);
			this.insertString = insertString;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			super.actionPerformed(e);
			Calculator.this.viewer.requestFocus();
			if (Calculator.this.result) {
				Calculator.this.result = false;
				Calculator.this.viewer.setText("");
			}
			if (!Calculator.this.operand) {
				try {
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(), this.insertString, null);
				} catch (final Exception ex) {
					Calculator.logger.trace(null, ex);
				}
			} else {
				try {
					Calculator.this.viewer.getDocument()
					.insertString(Calculator.this.viewer.getDocument().getLength(), this.insertString, null);
				} catch (final Exception ex) {
					Calculator.logger.trace(null, ex);
				}
			}
		}

	}

	class ActionOperator extends DefaultCalculatorAction {

		protected int operator;

		public ActionOperator(final String text, final int operator) {
			super(text);
			this.operator = operator;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			super.actionPerformed(e);
			if (Calculator.this.operand) {
				if (!Calculator.this.viewer.getText().equals("")) {
					Calculator.this.calculateResult();
					Calculator.this.currentOperation = this.operator;
				}
			} else {
				Calculator.this.currentOperation = this.operator;
				Calculator.this.operand = true;
				if (!Calculator.this.result) {
					Calculator.this.viewer.setText("");
				}
				Calculator.this.memorizedNumber = ((DoubleNumberDocument) Calculator.this.viewer.getDocument())
						.getDouble();
			}
		}

	}

	class DefaultCalculatorAction extends AbstractAction {

		public DefaultCalculatorAction() {
			super();
		}

		public DefaultCalculatorAction(final String name, final Icon icon) {
			super(name, icon);
		}

		public DefaultCalculatorAction(final String name) {
			super(name);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			Calculator.this.viewer.requestFocus();
		}

	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JButton bto = new JButton(new AbstractAction("calc") {

			Calculator calc = new Calculator();

			@Override
			public void actionPerformed(final ActionEvent e) {
				this.calc.setVisible(true);
			}
		});

		bto.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				Calculator.logger.debug(e.toString());
			}
		});
		frame.getContentPane().add(bto);
		frame.pack();
		frame.setVisible(true);

	}

}
