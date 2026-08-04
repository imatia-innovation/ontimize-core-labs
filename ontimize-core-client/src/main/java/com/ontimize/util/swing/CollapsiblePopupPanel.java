package com.ontimize.util.swing;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.TableButton;

public class CollapsiblePopupPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(CollapsiblePopupPanel.class);

    transient Component invoker;

    transient Popup popup;

    private int desiredLocationX, desiredLocationY;

    transient Frame frame;

    protected JPanel innerComponent;

    protected JViewport viewPort = null;

    protected boolean deployedState = false;

    public int customWidth = -1;

    public int minWidth = -1;

    protected Timer timer = null;

    protected long cycleStart = 0;

    protected int deployTime = 200;

    protected MouseGrabber mouseGrabber;

    public static Color backgroundColor = new Color(0.0f, 0.0f, 0.0f, 0.30f);

    public CollapsiblePopupPanel() {
        this.innerComponent = new JPanel(new FlowLayout(FlowLayout.LEFT)) {

            @Override
            public String getName() {
                return "TableButtonPanel";
            }
        };
        this.innerComponent.setBorder(new EtchedBorder(2));
        this.innerComponent.setOpaque(true);
        this.viewPort = new JViewport();
        this.viewPort.setView(this.innerComponent);
        this.viewPort.setOpaque(false);
        this.setOpaque(false);

        super.setLayout(new GridBagLayout());

        super.addImpl(this.viewPort, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0), -1);

        final ActionListener target = new ActionListener() {

            protected boolean start = false;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final long currentTime = System.currentTimeMillis();
                long totalTime = currentTime - CollapsiblePopupPanel.this.cycleStart;
                if ((totalTime > CollapsiblePopupPanel.this.deployTime) && !this.start) {
                    CollapsiblePopupPanel.this.cycleStart = currentTime;
                    totalTime = 0;
                    this.start = true;
                }

                float fraction = (float) totalTime / CollapsiblePopupPanel.this.deployTime;
                fraction = Math.min(1.0f, fraction);
                CollapsiblePopupPanel.this.calculatedCustomWidth(fraction, true);

                if (Float.compare(fraction, 1) == 0) {
                    CollapsiblePopupPanel.this.timer.stop();
                    this.start = false;
                }
            }
        };

        this.timer = new Timer(35, target);
        this.timer.setInitialDelay(0);

        this.installListeners();
    }

    protected void installListeners() {
        this.mouseGrabber = new MouseGrabber();
    }

    public Component[] getInnerComponents() {
        return this.innerComponent.getComponents();
    }

    @Override
    public void setLayout(final LayoutManager mgr) {
        if (this.innerComponent != null) {
            this.innerComponent.setLayout(mgr);
        }
    }

    public void doActionDeploy(final boolean animate) {
        if (animate) {
            if (this.timer != null) {
                this.timer.stop();
                this.setDeploy(!this.isDeploy());
                this.timer.start();
            }
        }
    }

    public boolean isDeploy() {
        return this.deployedState;
    }

    public void setDeploy(final boolean deploy) {
        this.deployedState = deploy;
    }

    @Override
    protected void addImpl(final Component comp, final Object constraints, final int index) {
        if (this.innerComponent != null) {
            this.innerComponent.add(comp, constraints, index);
        }
    }

    protected void calculatedCustomWidth(final float fraction, final boolean animated) {
        if (animated) {
            if (this.deployedState) {
                this.customWidth = (int) (this.viewPort.getView().getPreferredSize().width * fraction)
                        + this.getInsets().left + this.getInsets().right;
            } else {
                this.customWidth = (int) (this.viewPort.getView().getPreferredSize().width * (1 - fraction))
                        + this.getInsets().left;
                this.minWidth = this.customWidth;
            }

            final Window w = SwingUtilities.getWindowAncestor(this);

            if (w != null) {
                w.setBounds(w.getX(), w.getY(), this.customWidth, w.getHeight());
                this.validate();
            }
        }
        // else {
        // if (deployedState) {
        // customWidth = (int) viewPort.getView().getPreferredSize().width +
        // getInsets().left + getInsets().right;
        // } else {
        // customWidth = (int) getInsets().left
        // +collapsibleLabel.getPreferredSize().width + (orientation ==
        // VERTICAL_ORIENTATION ? 10 : 0);
        // minWidth = customWidth;
        // }
        // }

    }

    public Component getInvoker() {
        return this.invoker;
    }

    @Override
    public void setVisible(final boolean visible) {
        // logger.debug("Visible: " + visible);
        if (visible) {
            this.popup = this.getPopup();
            // Window w =
            // SwingUtilities.getWindowAncestor(popup.getComponent());
            this.popup.show();
            this.doActionDeploy(true);
            // firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);
        } else {
            this.popup.hide();
            this.popup = null;
            this.setDeploy(false);
        }
    }

    @Override
    public void setLocation(final int x, final int y) {
        final int oldX = this.desiredLocationX;
        final int oldY = this.desiredLocationY;

        this.desiredLocationX = x;
        this.desiredLocationY = y;
        if ((this.popup != null) && ((x != oldX) || (y != oldY))) {
            this.popup = this.getPopup();
        }
    }

    public void show(final Component invoker, final int x, final int y) {
        this.setInvoker(invoker);
        final Frame newFrame = CollapsiblePopupPanel.getFrame(invoker);
        if (newFrame != this.frame) {
            // Use the invoker's frame so that events
            // are propagated properly
            if (newFrame != null) {
                this.frame = newFrame;
                if (this.popup != null) {
                    this.setVisible(false);
                }
            }
        }
        Point invokerOrigin;
        if (invoker != null) {
            invokerOrigin = invoker.getLocationOnScreen();

            // To avoid integer overflow
            long lx, ly;
            lx = (long) invokerOrigin.x + (long) x;
            ly = (long) invokerOrigin.y + (long) y;
            if (lx > Integer.MAX_VALUE) {
                lx = Integer.MAX_VALUE;
            }
            if (lx < Integer.MIN_VALUE) {
                lx = Integer.MIN_VALUE;
            }
            if (ly > Integer.MAX_VALUE) {
                ly = Integer.MAX_VALUE;
            }
            if (ly < Integer.MIN_VALUE) {
                ly = Integer.MIN_VALUE;
            }

            this.setLocation((int) lx, (int) ly);
        } else {
            this.setLocation(x, y);
        }
        this.setVisible(true);
    }

    private static Frame getFrame(final Component c) {
        Component w = c;

        while (!(w instanceof Frame) && (w != null)) {
            w = w.getParent();
        }
        return (Frame) w;
    }

    public void setInvoker(final Component invoker) {
        final Component oldInvoker = this.invoker;
        this.invoker = invoker;
        if (this.invoker != null) {
            this.mouseGrabber.grabWindow(this.invoker);
        }
        if (oldInvoker != this.invoker) {
            // ui.uninstallUI(this);
            // ui.installUI(this);
        }
        this.invalidate();
    }

    Point adjustPopupLocationToFitScreen(final int xposition, final int yposition) {
        final Point p = new Point(xposition, yposition);

        // if(popupPostionFixDisabled ||
        // GraphicsEnvironment.isHeadless())
        // return p;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenBounds;
        GraphicsConfiguration gc = null;
        // Try to find GraphicsConfiguration, that includes mouse
        // pointer position
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        for (int i = 0; i < gd.length; i++) {
            if (gd[i].getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                final GraphicsConfiguration dgc = gd[i].getDefaultConfiguration();
                if (dgc.getBounds().contains(p)) {
                    gc = dgc;
                    break;
                }
            }
        }

        // If not found and we have invoker, ask invoker about his gc
        if ((gc == null) && (this.getInvoker() != null)) {
            gc = this.getInvoker().getGraphicsConfiguration();
        }

        if (gc != null) {
            // If we have GraphicsConfiguration use it to get
            // screen bounds
            screenBounds = gc.getBounds();
        } else {
            // If we don't have GraphicsConfiguration use primary screen
            screenBounds = new Rectangle(toolkit.getScreenSize());
        }

        Dimension size;

        size = this.getPreferredSize();

        // Use long variables to prevent overflow
        final long pw = (long) p.x + (long) size.width;
        final long ph = (long) p.y + (long) size.height;

        if (pw > (screenBounds.x + screenBounds.width)) {
            p.x = (screenBounds.x + screenBounds.width) - size.width;
        }

        if (ph > (screenBounds.y + screenBounds.height)) {
            p.y = (screenBounds.y + screenBounds.height) - size.height;
        }

        /*
         * Change is made to the desired (X,Y) values, when the PopupMenu is too tall OR too wide for the
         * screen
         */
        if (p.x < screenBounds.x) {
            p.x = screenBounds.x;
        }
        if (p.y < screenBounds.y) {
            p.y = screenBounds.y;
        }

        return p;
    }

    private static PopupFactory popupFactory;

    private Popup getPopup() {
        final Popup oldPopup = this.popup;

        if (oldPopup != null) {
            oldPopup.hide();
        }
        if (CollapsiblePopupPanel.popupFactory == null) {
            CollapsiblePopupPanel.popupFactory = new PopupFactory();
            try {
                final Method m = PopupFactory.class.getDeclaredMethod("setPopupType", new Class[] { int.class });
                m.setAccessible(true);
                m.invoke(CollapsiblePopupPanel.popupFactory, new Object[] { 2 });
            } catch (final Exception e) {
                CollapsiblePopupPanel.logger.error(null, e);
            }
        }

        // adjust the location of the popup
        final Point p = this.adjustPopupLocationToFitScreen(this.desiredLocationX, this.desiredLocationY);
        this.desiredLocationX = p.x;
        this.desiredLocationY = p.y;

        this.popup = CollapsiblePopupPanel.popupFactory.getPopup(this.getInvoker(), this, this.desiredLocationX,
                this.desiredLocationY);
        // try {
        // Method m = Popup.class.getDeclaredMethod("getComponent", null);
        // m.setAccessible(true);
        // Object window = m.invoke(this.popup, null);
        //
        // if (window instanceof Window) {
        // // AWTUtilities.setWindowOpaque((Window) window, false);
        // }
        // } catch (Exception e) {
        // logger.error(null,e);
        // }

        this.popup.show();
        return this.popup;
    }

    protected class MouseGrabber implements AWTEventListener, ComponentListener, WindowListener, PopupMenuListener {

        protected Window grabbedWindow;

        public void grabWindow(final Component invoker) {

			final Toolkit tk = Toolkit.getDefaultToolkit();

			tk.addAWTEventListener(
					this,
					AWTEvent.MOUSE_EVENT_MASK
							| AWTEvent.MOUSE_MOTION_EVENT_MASK
							| AWTEvent.MOUSE_WHEEL_EVENT_MASK
							| AWTEvent.WINDOW_EVENT_MASK);

			this.grabbedWindow = (invoker instanceof Window)
					? (Window) invoker
					: SwingUtilities.getWindowAncestor(invoker);

            if (this.grabbedWindow != null) {
				this.grabbedWindow.addComponentListener(this);
				this.grabbedWindow.addWindowListener(this);
            }
        }

        public void ungrabWindow() {
            final Toolkit tk = Toolkit.getDefaultToolkit();
            // The grab should be removed
            java.security.AccessController.doPrivileged(new java.security.PrivilegedAction() {

                @Override
                public Object run() {
                    tk.removeAWTEventListener(MouseGrabber.this);
                    return null;
                }
            });
        }

        public void realUngrabWindow() {

            if (this.grabbedWindow != null) {

				this.grabbedWindow.removeComponentListener(this);
				this.grabbedWindow.removeWindowListener(this);

                this.grabbedWindow = null;
            }
        }

        @Override
        public void eventDispatched(final AWTEvent ev) {

			if ((ev instanceof MouseEvent) &&
					(ev.getSource() instanceof IMenuButton)) {

                final MouseEvent me = (MouseEvent) ev;
                final Component src = me.getComponent();

				if (isInPopup(src) &&
						me.getID() == MouseEvent.MOUSE_CLICKED) {

					realUngrabWindow();
					ungrabWindow();

                    CollapsiblePopupPanel.this.processMouseEvent(me);

                    final JPopupMenu menu = ((IMenuButton) src).getMenu();

                    if (menu != null) {
                        menu.setVisible(false);
						menu.addPopupMenuListener(this);
                    }
                    return;
                }
            }

            if (!(ev instanceof MouseEvent)) {
                return;
            }

            final MouseEvent me = (MouseEvent) ev;
            final Component src = me.getComponent();

            switch (me.getID()) {

                case MouseEvent.MOUSE_CLICKED:
					if (isInPopup(src)) {
                        CollapsiblePopupPanel.this.processMouseEvent(me);
						cancelPopupMenu();
                        return;
                    }
                    break;

                case MouseEvent.MOUSE_PRESSED:
					if (!isInPopup(src)) {
						cancelPopupMenu();
                    }
                    break;
            }
        }

        boolean isInPopup(final Component src) {
            for (Component c = src; c != null; c = c.getParent()) {
                if ((c instanceof Applet) || (c instanceof Window)) {
                    break;
                } else if (c instanceof CollapsiblePopupPanel) {
                    return true;
                } else if (c instanceof IMenuButton) {
                    return true;
                }
            }
            return false;
        }

        protected void cancelPopupMenu() {
            this.ungrabWindow();
            this.realUngrabWindow();
            CollapsiblePopupPanel.this.setVisible(false);
        }

        @Override
        public void componentHidden(final ComponentEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void componentMoved(final ComponentEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void componentResized(final ComponentEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void componentShown(final ComponentEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void windowClosed(final WindowEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void windowClosing(final WindowEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void windowDeactivated(final WindowEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void windowIconified(final WindowEvent e) {
            this.cancelPopupMenu();
        }

        @Override
        public void windowOpened(final WindowEvent e) {
        }

        @Override
        public void windowDeiconified(final WindowEvent e) {
        }

        @Override
        public void windowActivated(final WindowEvent e) {
        }

        boolean visiblePopup = false;

        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
            this.visiblePopup = true;
        }

        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            if (this.visiblePopup) {
                this.visiblePopup = false;
                ((JPopupMenu) e.getSource()).removePopupMenuListener(this);
                this.cancelPopupMenu();
            }
        }

        @Override
        public void popupMenuCanceled(final PopupMenuEvent e) {
            if (this.visiblePopup) {
                this.visiblePopup = false;
                ((JPopupMenu) e.getSource()).removePopupMenuListener(this);
                this.cancelPopupMenu();

            }
        }

    }

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException e1) {
            CollapsiblePopupPanel.logger.error(null, e1);
        } catch (final InstantiationException e1) {
            CollapsiblePopupPanel.logger.error(null, e1);
        } catch (final IllegalAccessException e1) {
            CollapsiblePopupPanel.logger.error(null, e1);
        } catch (final UnsupportedLookAndFeelException e1) {
            CollapsiblePopupPanel.logger.error(null, e1);
        }

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        final JButton openButton = new JButton();
        openButton.setText("+ ...Abre botones... +");
        frame.getContentPane().add(openButton);

        final CollapsiblePopupPanel panel = new CollapsiblePopupPanel();

        final JButton buttonCopy = new TableButton();
        final ImageIcon copyIcon = ImageManager.getIcon(ImageManager.COPY);

        buttonCopy.setMargin(new Insets(0, 0, 0, 0));
        buttonCopy.setIcon(new ImageIcon(copyIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT)));
        panel.add(buttonCopy);

        final JButton buttonExcelExport = new TableButton();
        buttonExcelExport.setMargin(new Insets(0, 0, 0, 0));
        buttonExcelExport
            .setIcon(new ImageIcon(copyIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT)));
        panel.add(buttonExcelExport);

        final JButton buttonHTMLExport = new TableButton();
        buttonHTMLExport.setMargin(new Insets(0, 0, 0, 0));
        buttonHTMLExport
            .setIcon(new ImageIcon(copyIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT)));
        panel.add(buttonHTMLExport);

        final JButton buttonPrint = new TableButton();
        buttonPrint.setMargin(new Insets(0, 0, 0, 0));
        buttonPrint.setIcon(new ImageIcon(copyIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT)));
        panel.add(buttonPrint);

        // JLabel label = new JLabel("Estoy en un popup....");
        // label.setFont(ParseUtils.getFont("ArialRegular-PLAIN-30",
        // label.getFont()));
        // panel.add(label);
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                panel.show((JButton) e.getSource(), 0, ((JButton) e.getSource()).getHeight());
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

}
