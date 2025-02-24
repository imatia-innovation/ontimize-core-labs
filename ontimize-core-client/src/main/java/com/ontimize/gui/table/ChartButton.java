package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.chart.ChartUtilities_1_0;
import com.ontimize.chart.ChartVersionControl;
import com.ontimize.chart.IChartUtilities;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.util.swing.MenuButton;

public class ChartButton extends TableButtonSelection {

    private static final Logger logger = LoggerFactory.getLogger(ChartButton.class);

    protected JPopupMenu chartConfigMenu = null;

    protected ActionListener listener = null;

    protected ListenerDeleteItem listenerDelete = null;

    protected ExtendedJPopupMenu chartMenu = null;

    protected IChartUtilities chartUtilities = null;

    protected Table table;

    protected boolean loadButtonVisible = true;

    protected boolean saveButtonVisible = true;

    public ChartButton(Table pTable) {

        this.table = pTable;

        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChartButton.this.showDefaultChartDialog(null);
            }
        });

        this.addActionMenuListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChartButton.this.createChartConfigMenu();
                ChartButton.this.chartConfigMenu.show(ChartButton.this, 0, ChartButton.this.getHeight());
            }
        });
    }

    protected void showDefaultChartDialog(String config) {
        if (this.chartUtilities == null) {
            if (ChartVersionControl.isVersion_1_0()) {
                this.chartUtilities = new ChartUtilities_1_0(this.table);
            } else {
                try {
                    Class rootClass = Class.forName("com.ontimize.chart.ChartUtilities");
                    Class[] p = { Table.class };
                    Constructor constructorChart = rootClass.getConstructor(p);
                    Object[] parameters = { this.table };
                    this.chartUtilities = (IChartUtilities) constructorChart.newInstance(parameters);
                } catch (Exception e) {
                    ChartButton.logger.error(null, e);
                }
            }

            this.chartUtilities.setLoadButtonVisible(this.loadButtonVisible);
            this.chartUtilities.setSaveButtonVisible(this.saveButtonVisible);
        }

        if (config != null) {
            this.chartUtilities.showDefaultChartDialog(config);
        } else {
            this.chartUtilities.showDefaultChartDialog();
        }

    }

    protected void createChartConfigMenu() {
        if (this.chartConfigMenu == null) {
            this.chartConfigMenu = new JPopupMenu();
            this.setMenu(this.chartConfigMenu);
        }
        if (this.listener == null) {
            this.listener = new ListenerItem();
        }
        if (this.listenerDelete == null) {
            this.listenerDelete = new ListenerDeleteItem(this.table.getResourceBundle());
        }

        java.util.List list = this.getChartConfiguration();
        int originalSize = list.size();

        for (int i = this.chartConfigMenu.getComponentCount() - 1; i >= 0; i--) {
            Object o = this.chartConfigMenu.getComponent(i);
            if (o instanceof JPanel) {
                JPanel current = (JPanel) o;
                JButton item = (JButton) current.getComponent(0);
                String itemKey = item.getActionCommand();
                if (!list.contains(itemKey)) {
                    this.chartConfigMenu.remove(i);
                } else {
                    list.remove(itemKey);
                }
            } else {
                this.chartConfigMenu.remove(i);
            }
        }

        if (originalSize != 0) {
            for (int i = 0; i < list.size(); i++) {
                JPanel panel = new JPanel(new GridBagLayout());
                JButton item = new MenuButton((String) list.get(i));
                item.addActionListener(this.listener);
                panel.add(item, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                ImageIcon icon = ImageManager.getIcon(ImageManager.RECYCLER);
                item.setMargin(new Insets(0, 0, 0, 0));
                JButton delete = new MenuButton(icon);
                delete.setActionCommand((String) list.get(i));
                delete.addActionListener(this.listenerDelete);
                delete.setMargin(new Insets(0, 0, 0, 0));
                panel.add(delete,
                        new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.EAST,
                                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
                this.chartConfigMenu.add(panel);
            }
        } else {
            JLabel label = new JLabel(
                    ApplicationManager.getTranslation("table.no_custom_chart_stored", this.table.getResourceBundle()));
            this.chartConfigMenu.add(label);
        }
    }

    public String getCustomChartPreferenceKey() {
        Form f = this.table.getParentForm();
        return f != null
                ? BasicApplicationPreferences.TABLE_CONF_CHART_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
                        + this.table
                            .getEntityName()
                : BasicApplicationPreferences.TABLE_CONF_CHART_CONFIGURATIONS + "_" + this.table.getEntityName();
    }

    protected List getChartConfiguration() {
        List configurationList = new ArrayList();
        try {
            Application ap = ApplicationManager.getApplication();
            String preferenceKey = this.getCustomChartPreferenceKey();
            ApplicationPreferences prefs = ap.getPreferences();
            if ((preferenceKey != null) && (prefs != null)) {
                String p = prefs.getPreference(((ClientReferenceLocator) ap.getReferenceLocator()).getUser(),
                        preferenceKey);
                if (p != null) {
                    StringTokenizer st = new StringTokenizer(p, ";");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        int iIndex = token.indexOf(":");
                        if (iIndex > 0) {
                            String sName = token.substring(0, iIndex);
                            configurationList.add(sName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            ChartButton.logger.error(null, e);
        }
        return configurationList;
    }

    protected class ListenerItem implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if (o instanceof AbstractButton) {
                String command = ((AbstractButton) o).getActionCommand();
                ChartButton.this.showDefaultChartDialog(command);

            }
            ChartButton.this.chartConfigMenu.setVisible(false);
        }

    }

    protected class ListenerDeleteItem implements ActionListener {

        protected String DELETE_KEY = "chartutilities.delete_key";

        protected ResourceBundle bundle = null;

        public ListenerDeleteItem(ResourceBundle resource) {
            this.bundle = resource;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if (o instanceof AbstractButton) {
                int i = JOptionPane.showConfirmDialog((Component) o,
                        ApplicationManager.getTranslation(this.DELETE_KEY, this.bundle), "", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.OK_OPTION) {
                    String command = ((AbstractButton) o).getActionCommand();
                    this.deleteChartConfiguration(command);
                }
            }
            ChartButton.this.chartConfigMenu.setVisible(false);
        }

        public void setResourceBundle(ResourceBundle bundle) {
            this.bundle = bundle;
        }

        protected void deleteChartConfiguration(String conf) {
            try {
                Application ap = ApplicationManager.getApplication();
                String preferenceKey = ChartButton.this.getCustomChartPreferenceKey();
                ApplicationPreferences prefs = ap.getPreferences();
                if ((preferenceKey != null) && (prefs != null)) {
                    String p = prefs.getPreference(((ClientReferenceLocator) ap.getReferenceLocator()).getUser(),
                            preferenceKey);
                    String pout = "";
                    if (p != null) {
                        StringTokenizer st = new StringTokenizer(p, ";");
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            int iIndex = token.indexOf(":");
                            if (iIndex > 0) {
                                String sName = token.substring(0, iIndex);
                                if (!sName.equalsIgnoreCase(conf)) {
                                    pout += pout.length() == 0 ? token : ";" + token;
                                }
                            }
                        }
                        prefs.setPreference(((ClientReferenceLocator) ap.getReferenceLocator()).getUser(),
                                preferenceKey, pout);
                        prefs.savePreferences();
                    }
                }
            } catch (Exception e) {
                ChartButton.logger.error(null, e);
            }
        }

    }

    public IChartUtilities getChartUtilities() {
        return this.chartUtilities;
    }

    public void setLoadButtonVisible(boolean visible) {
        this.loadButtonVisible = visible;
        if (this.chartUtilities != null) {
            this.chartUtilities.setLoadButtonVisible(visible);
        }
    }

    public void setSaveButtonVisible(boolean visible) {
        this.saveButtonVisible = visible;
        if (this.chartUtilities != null) {
            this.chartUtilities.setSaveButtonVisible(visible);
        }
    }

}
