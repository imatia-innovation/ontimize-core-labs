package com.ontimize.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;


public class JarUtil {

    protected static Properties data;
    static {
        final URL current = JarUtil.class.getResource("data.properties");
        data = new Properties();
        try {
            data.load(current.openStream());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static final String COMPONENT_NAME = "Component-name";

    public static final String ONTIMIZE_VERSION = "Ontimize-version-number";

    public static final String VERSION = "Version-number";

    public static final String BUILT = "Built-By";

    public static final String DATE = "Version-date";

    protected static class ManifestInfo extends HashMap {

        protected String componentName;

        public void setComponentName(final String name) {
            this.componentName = name;
        }

        public String getComponentName() {
            return this.componentName;
        }

    }

    protected static class Header extends JLabel {

        protected String componentName;

        protected int h2 = 0;

        public Header(final String name) {
            super(name);
            setFont(getFont().deriveFont(24F));
            setBackground(Color.black);
            setForeground(Color.white);
            setHorizontalAlignment(CENTER);
            setOpaque(true);
        }

        @Override
		public Dimension getPreferredSize() {
            final Dimension d = super.getPreferredSize();
            h2 = d.height / 6;
            d.height = d.height + h2;
            return d;
        }

        @Override
		protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Dimension d = getSize();
            final int h = d.height;
            final int h1 = h / 6;
            final Color c = g.getColor();
            g.setColor(Color.red);
            g.fillRect(0, h - h1, d.width, h1);
            g.setColor(c);
        }

    }


    protected static class Body extends JPanel {

        public Body(final ManifestInfo model) {
            setLayout(new GridBagLayout());
            final Iterator iterator = model.keySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                final Object currentKey = iterator.next();
                add(createTitle(currentKey.toString()), new GridBagConstraints(GridBagConstraints.RELATIVE, i, 1, 1, 1,
                        0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
                add(createValue(model.get(currentKey)), new GridBagConstraints(GridBagConstraints.RELATIVE, i, 1, 1, 1,
                        0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
                i++;
            }
            add(new JPanel(), new GridBagConstraints(GridBagConstraints.RELATIVE, i, 2, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }

        protected JLabel createTitle(final Object key) {
            JLabel label = null;
            if (data.containsKey(key)) {
                label = new JLabel(data.getProperty(key.toString()));
            } else {
				label = new JLabel(key.toString());
			}
            label.setOpaque(true);
            Font f = label.getFont().deriveFont(Font.BOLD);
            f = f.deriveFont(f.getSize() + 3F);
            label.setFont(f);

            return label;
        }

        protected JLabel createValue(final Object key) {
            final JLabel label = new JLabel(key.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            Font f = label.getFont();
            f = f.deriveFont(f.getSize() + 3F);
            label.setFont(f);
            // label.setFont(label.getFont().deriveFont(Font.BOLD));
            return label;
        }

    }

    public static ManifestInfo getManifest(final Component d) throws Exception {
        final ManifestInfo info = new ManifestInfo();
        final Manifest manifest = retrieveManifest();
        if (manifest != null) {
            try {
                final Enumeration keys = data.keys();
                while (keys.hasMoreElements()) {
                    final Object current = keys.nextElement();
                    final String currentValue = getAttribute(current, manifest).toString();
                    info.put(current, currentValue);
                }
                info.setComponentName(getAttribute(COMPONENT_NAME, manifest).toString());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else {
            info.put(BUILT, "Imatia Innovation S.L");
            info.put(VERSION, "666");
            info.put(ONTIMIZE_VERSION, "69");
            info.put(DATE, "12:30:24 19/02/2008");
            info.setComponentName("debug");
        }
        return info;
    }

    // protected static String getHtmlSource(){
    // URL urlHtml=JarUtil.class.getResource(TEMPLATE_PATH);
    // if (urlHtml!=null){
    // InputStream iS;
    // try {
    // iS = urlHtml.openStream();
    //
    // BufferedReader bR= new BufferedReader(new InputStreamReader(iS));
    // StringBuffer html=new StringBuffer();
    // try{
    // String str;
    // while ((str = bR.readLine()) != null) {
    // html.append(str);
    // }
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // bR.close();
    // return html.toString();
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    // }
    // return null;
    // }

    protected static String getAttribute(final Object key, final Manifest m) {
        final Attributes.Name aN = new Attributes.Name(key.toString());
        final Attributes ats = m.getMainAttributes();
        if (ats.containsKey(aN)) {
            return ats.getValue(key.toString());
        }
        return null;
    }

    protected static Manifest retrieveManifest() {
        Enumeration enumeration = null;
        String pattern = null;
        try {
            final URL url = JarUtil.class.getResource("");
            String packageName = JarUtil.class.getPackage().getName();

            packageName = packageName.replace('.', '/');
            final String path = url.getFile();
            final int index = path.lastIndexOf(packageName);
            if (index >= -1) {
                pattern = path.substring(0, index);
            }
            enumeration = JarUtil.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        if ((enumeration == null) || (pattern == null)) {
			return null;
		}

        try {
            while (enumeration.hasMoreElements()) {
                final URL url = (URL) enumeration.nextElement();
                final String path = url.getFile();
                if (pattern != null) {
                    if (path.indexOf(pattern) >= 0) {
                        final Manifest m = new Manifest(url.openStream());
                        return m;
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static class InformationDialog extends JFrame {

        protected JLabel lVersion = null;

        protected JLabel lHtml = null;

        protected JLabel iOntimize = null;

        protected JLabel tPanel = null;

        protected boolean hideFrame = false;


        class EAction extends AbstractAction {

            @Override
			public void actionPerformed(final ActionEvent e) {
                if (SwingUtilities.getWindowAncestor((Component) e.getSource()) instanceof InformationDialog) {
                    ((InformationDialog) SwingUtilities.getWindowAncestor((Component) e.getSource()))
                        .processWindowEvent(new WindowEvent(
                                (SwingUtilities.getWindowAncestor((Component) e.getSource())),
                                WindowEvent.WINDOW_CLOSING));
                }
            }

        }

        public static ImageIcon getImatiaIcon() {
            final URL url = JarUtil.class.getResource("iconimatia.gif");
            if (url == null) {
				return null;
			}
            final ImageIcon icon = new ImageIcon(url);
            return icon;
        }


        public InformationDialog(final boolean hideFrame) {
            this.hideFrame = hideFrame;
            final ActionMap aM = ((JComponent) getContentPane()).getActionMap();
            final InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

            aM.put("close", new EAction());
            inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");

            setTitle("Imatia Innovation");
            final ImageIcon iconImatia = getImatiaIcon();
            if (iconImatia != null) {
                setIconImage(iconImatia.getImage());
            }


            ((JComponent) this.getContentPane()).setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inMap);
            ((JComponent) getContentPane()).setActionMap(aM);

            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
				public void windowClosing(final WindowEvent e) {
                    if (InformationDialog.this.hideFrame) {
                        InformationDialog.this.setVisible(false);
                    } else {
						System.exit(0);
					}
                }
            });

            setResizable(false);
            getContentPane().setBackground(new Color(209, 209, 209));

            ManifestInfo info = null;
            try {
                info = getManifest(this);
            } catch (final Exception e1) {
                e1.printStackTrace();
            }

            String componentName = info.getComponentName();
            if (componentName == null) {
                componentName = "MANIFEST";
            }

            getContentPane().setLayout(new GridBagLayout());
            getContentPane().add(new Header(componentName), new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 0), 0, 0));
            getContentPane().add(new Body(info), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 2, 2, 0), 0, 0));
            getContentPane().setFocusable(true);
            getContentPane().requestFocus();
            pack();
            final Dimension d = getSize();
            if (d.width < 250) {
                final int increment = 250 - d.width;
                d.width = d.width + increment;
                d.height = d.height + increment;
            }
            setSize(d);
        }

    }

    public final static void main(final String[] arg) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // ImageIcon icono=ApplicationManager.getIcon("com/ontimize/gui/images/iconimatia.gif");
        // if (icono!=null){
        // JFrame f=new JFrame("borrar");
        // f.setIconImage(icono.getImage());
        //
        //
        // }
        final InformationDialog id = new InformationDialog(false);
        center(id);
        id.setVisible(true);
    }

    public static void center(final Window f) {
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = d.width / 2 - f.getWidth() / 2;
        int y = d.height / 2 - f.getHeight() / 2;
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

}
