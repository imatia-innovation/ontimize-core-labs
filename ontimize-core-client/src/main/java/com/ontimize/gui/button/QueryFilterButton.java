package com.ontimize.gui.button;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.IFilterElement;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.serializer.ISerializerManager;
import com.ontimize.jee.common.util.serializer.SerializerManagerFactory;
import com.ontimize.util.swing.MenuButton;

public class QueryFilterButton extends AbstractButtonSelection
implements InteractionManagerModeListener, ReferenceComponent {

	private static final Logger logger = LoggerFactory.getLogger(QueryFilterButton.class);

	protected static final String FORM_QUERY_FILTER_PREFERENCE_KEY = "form_query_filter";

	// Preferences format is: default~list~;name1;name2;name3;name4
	protected static final String PREFERENCE_DEFAULT_KEY = "default~";

	protected static final String PREFERENCE_LIST_KEY = "list~";

	protected static final String NO_RESULT_MESSAGE = "queryfilterbutton.no_result";

	protected static final String QUERY_FILTER_EXIST_QUESTION = "queryfilterbutton.query_filter_name_exists";

	protected static final String INSERT_FILTER_NAME_MESSAGE = "queryfilterbutton.insert_filter_name";

	protected static final String NO_DATA_FOR_FILTER_MESSAGE = "queryfilterbutton.no_data_for_filter_message";

	protected static final String DELETE_KEY = "queryfilterbutton.delete_key";

	protected JPopupMenu filterMenu = null;

	protected ItemListener itemListener = null;

	protected DefaultItemListener defaultItemListener = null;

	protected ItemDeleteListener itemDeleteListener = null;

	protected ItemSaveListener itemSaveListener = null;

	protected EntityReferenceLocator locator = null;

	protected JMenuItem insertMenuItem = null;

	protected ISerializerManager serializerManager = SerializerManagerFactory.getSerializerManager();

	public QueryFilterButton(final Map parameter) {
		super(parameter);
		this.jInit();
	}

	@Override
	public void init(final Map parameter) {
		super.init(parameter);
		if (this.icon == null) {
			this.icon = ImageManager.FUNNEL_NEW;
		}
		this.button.setIcon(ImageManager.getIcon(this.icon));
	}

	protected void jInit() {
		this.menuButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				QueryFilterButton.this.createFilterMenu();
				QueryFilterButton.this.filterMenu.show(QueryFilterButton.this.menuButton, 0,
						QueryFilterButton.this.menuButton.getHeight());
			}
		});
		this.itemSaveListener = new ItemSaveListener(this.bundle);
		this.addActionListener(this.itemSaveListener);
		this.setMargin(new Insets(3, 3, 4, 3));
	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (InteractionManager.QUERY == e.getInteractionManagerMode()) {
			final String defaultFilter = this.getDefaultQueryFilter();
			if (defaultFilter != null) {
				this.performFilter(defaultFilter);
			}
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}

	}

	protected class ItemListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof AbstractButton) {
				final String command = ((AbstractButton) o).getActionCommand();
				QueryFilterButton.this.performFilter(command);
			}
			QueryFilterButton.this.filterMenu.setVisible(false);
		}

	}

	protected class ItemSaveListener implements ActionListener {

		protected ResourceBundle bundle = null;

		public ItemSaveListener(final ResourceBundle resource) {
			this.bundle = resource;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object s = MessageDialog.showInputMessage(SwingUtilities.getWindowAncestor(QueryFilterButton.this),
					QueryFilterButton.INSERT_FILTER_NAME_MESSAGE, this.bundle);
			if (s != null) {
				QueryFilterButton.this.storeCurrentFilter(s.toString());
			}
			if (QueryFilterButton.this.filterMenu != null) {
				QueryFilterButton.this.filterMenu.setVisible(false);
			}
		}

		public void setResourceBundle(final ResourceBundle recursos) {
			this.bundle = recursos;
		}

	}

	protected class DefaultItemListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof JCheckBox) {
				final JCheckBox checkBox = (JCheckBox) o;
				if (!checkBox.isSelected()) {
					// Uncheck
					QueryFilterButton.this.setDefaultQueryFilter(null);
				} else {
					final String name = checkBox.getActionCommand();
					QueryFilterButton.this.setDefaultQueryFilter(name);
				}
			}
			QueryFilterButton.this.filterMenu.setVisible(false);
		}

	}

	/**
	 * Listener that is invoked when a filter configuration is deleted
	 *
	 * @author Imatia Innovation
	 */
	protected class ItemDeleteListener implements ActionListener {

		protected ResourceBundle bundle = null;

		public ItemDeleteListener(final ResourceBundle resource) {
			this.bundle = resource;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof AbstractButton) {
				final int i = JOptionPane.showConfirmDialog((Component) o,
						ApplicationManager.getTranslation(QueryFilterButton.DELETE_KEY, this.bundle), "",
						JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.OK_OPTION) {
					final String command = ((AbstractButton) o).getActionCommand();
					QueryFilterButton.this.deleteFilterConfiguration(command);
				}
			}
			QueryFilterButton.this.filterMenu.setVisible(false);
		}

		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

	}

	protected void createFilterMenu() {
		if (this.filterMenu == null) {
			this.filterMenu = new ExtendedJPopupMenu();
			this.itemListener = new ItemListener();
			this.defaultItemListener = new DefaultItemListener();
			this.itemDeleteListener = new ItemDeleteListener(this.bundle);
		}

		this.filterMenu.removeAll();

		final java.util.List lList = this.getFilterConfigurations();
		final int originalSize = lList.size();

		if (originalSize != 0) {
			final String defaultQueryFilter = this.getDefaultQueryFilter();
			for (int i = 0; i < lList.size(); i++) {
				final JPanel panel = new JPanel(new GridBagLayout());
				final String currentName = (String) lList.get(i);

				final JCheckBox defaultValue = new JCheckBox();
				defaultValue.setActionCommand(currentName);
				if (currentName.equals(defaultQueryFilter)) {
					defaultValue.setSelected(true);
				}
				defaultValue.setBorderPainted(false);
				defaultValue.addActionListener(this.defaultItemListener);
				panel.add(defaultValue, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
						GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
				final JButton item = new MenuButton(currentName);
				item.addActionListener(this.itemListener);
				panel.add(item, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				final ImageIcon icon = ImageManager.getIcon(ImageManager.RECYCLER);
				item.setMargin(new Insets(0, 0, 0, 0));
				final JButton delete = new MenuButton(icon);
				delete.setActionCommand((String) lList.get(i));
				delete.addActionListener(this.itemDeleteListener);
				delete.setMargin(new Insets(0, 0, 0, 0));
				panel.add(delete,
						new GridBagConstraints(2, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.EAST,
								GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
				this.filterMenu.add(panel);
			}
		} else {
			final JLabel label = new JLabel(
					ApplicationManager.getTranslation(QueryFilterButton.NO_RESULT_MESSAGE, this.bundle));
			this.filterMenu.add(label);
		}
	}

	private java.util.List getFilterConfigurations() {
		final List list = new ArrayList();
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						if (!(token.indexOf(QueryFilterButton.PREFERENCE_DEFAULT_KEY) >= 0)) {
							list.add(token);
						}
					}
				}
			}
		} catch (final Exception e) {
			QueryFilterButton.logger.error(null, e);
		}
		return list;
	}

	protected String getFilterListPreferenceKey() {
		final Form f = this.parentForm;
		return QueryFilterButton.FORM_QUERY_FILTER_PREFERENCE_KEY + "_" + f.getArchiveName();
	}

	protected String getFilterPreferenceKey(final String name) {
		final Form f = this.parentForm;
		return name != null ? QueryFilterButton.FORM_QUERY_FILTER_PREFERENCE_KEY + "_" + f.getArchiveName() + "_" + name
				: QueryFilterButton.FORM_QUERY_FILTER_PREFERENCE_KEY + "_" + f.getArchiveName();
	}

	protected String getUser() {
		if (this.locator instanceof ClientReferenceLocator) {
			return ((ClientReferenceLocator) this.locator).getUser();
		}
		return null;
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	private void deleteFilterConfiguration(final String conf) {
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);

				final String pout = this.deleteQueryFilterToList(conf, p);
				if (pout != null) {
					prefs.setPreference(this.getUser(), this.getFilterPreferenceKey(conf), null);
					prefs.setPreference(this.getUser(), preferenceKey, pout);
					prefs.savePreferences();
				}
			}
		} catch (final Exception e) {
			QueryFilterButton.logger.error(null, e);
		}
	}

	protected boolean performFilter(final String filtername) {
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						if (token.equalsIgnoreCase(filtername)) {
							final String currentPreferences = prefs.getPreference(this.getUser(),
									this.getFilterPreferenceKey(token));
							this.retrieveFilter(currentPreferences);
							return true;
						}
					}
				}
			}
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
		}
		return false;
	}

	protected boolean existCurrentFilter(final String preferences, final String name) {
		final StringTokenizer tokens = new StringTokenizer(preferences, ";");
		while (tokens.hasMoreElements()) {
			final String token = tokens.nextToken();
			if (name.equals(token)) {
				return true;
			}
		}
		return false;
	}

	protected boolean storeCurrentFilter(final String filtername) {
		final Map currentData = this.retrieveFilterFormData();
		if (currentData.isEmpty()) {
			this.parentForm.message(QueryFilterButton.NO_DATA_FOR_FILTER_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
			// Message to indicate that nothing is stored
			return true;
		}
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				String p = prefs.getPreference(this.getUser(), preferenceKey);
				if (p == null) {
					p = QueryFilterButton.PREFERENCE_DEFAULT_KEY;
				}
				// Checks if other preference with the same name exists
				if (this.existCurrentFilter(p, filtername)) {
					final int op = this.parentForm.message(QueryFilterButton.QUERY_FILTER_EXIST_QUESTION,
							JOptionPane.QUESTION_MESSAGE);
					if (JOptionPane.YES_OPTION != op) {
						return false;
					}
				}

				p = this.insertQueryFilterToList(filtername, p);
				final String currentPreference = this.convertFilterDataToString(currentData);
				prefs.setPreference(this.getUser(), this.getFilterPreferenceKey(filtername), currentPreference);
				prefs.setPreference(this.getUser(), preferenceKey, p);
			}
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
		}
		return false;

	}

	protected Map retrieveFilterFormData() {
		final Map data = new Hashtable();
		final List fieldList = this.parentForm.getDataComponents();
		for (int i = 0; i < fieldList.size(); i++) {
			Object currentValue = null;
			final Object currentField = fieldList.get(i);
			if (currentField instanceof IdentifiedElement) {
				final Object currentAttr = ((IdentifiedElement) currentField).getAttribute();
				if (currentAttr instanceof String) {
					currentValue = this.parentForm.getDataFieldValue(currentAttr.toString());
				} else if (currentAttr instanceof ReferenceFieldAttribute) {
					currentValue = this.parentForm.getDataFieldValue(((ReferenceFieldAttribute) currentAttr).getAttr());
				}
				if (currentValue != null) {
					data.put(currentAttr, currentValue);
				}
			}
		}
		return data;
	}

	protected void retrieveFilter(final String data) throws Exception {
		final Map values = this.convertFilterDataToHashtable(data);
		this.parentForm.deleteDataFields();
		final List vSetValueOrder = this.parentForm.getSetValuesOrder();
		final List vsetDataFields = new Vector(values.keySet());
		if ((vSetValueOrder != null) && !vSetValueOrder.isEmpty()) {
			for (int j = vSetValueOrder.size() - 1; j >= 0; j--) {
				final Object currentSetValueOrder = vSetValueOrder.get(j);
				if (vsetDataFields.contains(currentSetValueOrder)) {
					final Object dataField = vsetDataFields.remove(vsetDataFields.indexOf(currentSetValueOrder));
					vsetDataFields.add(0, dataField);
				}
			}
		}

		// Until the comment with slashes, performs the checks of parent keys
		// and sets it the field in correct load order

		final List orderedElements = new ArrayList();
		final List filterComponentList = new ArrayList();
		for (int i = 0; i < vsetDataFields.size(); i++) {
			final String attr = (String) vsetDataFields.get(i);
			final Object comp = this.parentForm.getDataFieldReference(attr);
			if (comp instanceof IFilterElement) {
				filterComponentList.add(comp);
			} else {
				orderedElements.add(vsetDataFields.get(i));
			}
		}

		for (final Object component : filterComponentList) {
			final IFilterElement comp = (IFilterElement) component;
			if (comp.hasParentKeys()) {
				for (final Object parentKey : comp.getParentKeyList()) {
					this.addFilterElementToOrderedFields(orderedElements, (String) parentKey);
				}
			}

			final String attr = (String) ((DataField) comp).getAttribute();
			if (!orderedElements.contains(attr)) {
				orderedElements.add(((DataField) comp).getAttribute());
			}

		}

		vsetDataFields.clear();
		vsetDataFields.addAll(orderedElements);

		/////////////////////////////////////////////////////////////////////////////////////////////

		for (int i = 0; i < vsetDataFields.size(); i++) {
			final Object currentKey = vsetDataFields.get(i);
			final Object currentValue = values.get(currentKey);
			this.parentForm.setDataFieldValue(currentKey, currentValue);
		}
	}

	/**
	 * Checks if the attribute of an {@link IFilter} element field has parent keys, to add them to the
	 * list of elements to load before the field itself. Recursively checks that this is the case,
	 * avoiding adding the attribute if it already exists in the List
	 * @param orderedList A {@link List} wich contains the ordered elements
	 * @param attr A {@link String} with the attr to perform the check
	 */
	protected void addFilterElementToOrderedFields(final List orderedList, final String attr) {
		if (!orderedList.contains(attr)) {
			final IFilterElement comp = (IFilterElement) this.parentForm.getDataFieldReference(attr);
			if (comp.hasParentKeys()) {
				for (final Object parentKey : comp.getParentKeyList()) {
					this.addFilterElementToOrderedFields(orderedList, (String) parentKey);
				}
			} else {
				orderedList.add(attr);
			}
		}

	}

	protected String convertFilterDataToString(final Map data) {
		try {
			return this.serializerManager.serializeMapToString(data);
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
		}
		return null;
	}

	protected Map convertFilterDataToHashtable(final String buffer) {
		try {
			return this.serializerManager.deserializeStringToMap(buffer);
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
			try {
				return SerializerManagerFactory.getDefaultSerializerManager()
						.deserializeStringToMap(buffer);
			} catch (final Exception e) {
				QueryFilterButton.logger.error(null, e);
			}
		}
		return null;
	}

	protected String getDefaultQueryFilter(final String preference) {
		if (preference != null) {
			final StringTokenizer st = new StringTokenizer(preference, ";");
			if (st.hasMoreTokens()) {
				String token = st.nextToken();
				final int index = token.indexOf(QueryFilterButton.PREFERENCE_DEFAULT_KEY);
				if (index >= 0) {
					token = token.substring(index + QueryFilterButton.PREFERENCE_DEFAULT_KEY.length()).trim();
					if ((token != null) && (token.length() > 0)) {
						return token;
					}
					return null;
				}
			}
		}
		return null;
	}

	protected String getDefaultQueryFilter() {
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				return this.getDefaultQueryFilter(p);
			}
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
		}
		return null;
	}

	protected String setDefaultQueryFilter(final String filterName, final String preferences) {
		if (preferences != null) {
			final StringTokenizer st = new StringTokenizer(preferences, ";");
			final StringBuilder result = new StringBuilder();
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				final int index = token.indexOf(QueryFilterButton.PREFERENCE_DEFAULT_KEY);
				if (index >= 0) {
					result.append(QueryFilterButton.PREFERENCE_DEFAULT_KEY);
					result.append(filterName == null ? "" : filterName);
				} else {
					result.append(";");
					result.append(token);
				}
			}
			return result.toString();
		}
		return preferences;
	}

	protected void setDefaultQueryFilter(final String filterName) {
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getFilterListPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				final String result = this.setDefaultQueryFilter(filterName, p);
				if (result != null) {
					prefs.setPreference(this.getUser(), preferenceKey, result);
					prefs.savePreferences();
				}
			}
		} catch (final Exception ex) {
			QueryFilterButton.logger.error(null, ex);
		}
	}

	protected String deleteQueryFilterToList(final String filterName, final String preferences) {
		if (preferences != null) {
			final StringTokenizer st = new StringTokenizer(preferences, ";");
			final StringBuilder result = new StringBuilder();
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				final int index = token.indexOf(QueryFilterButton.PREFERENCE_DEFAULT_KEY);
				if (index >= 0) {
					if (!filterName
							.equals(token.substring(index + QueryFilterButton.PREFERENCE_DEFAULT_KEY.length()))) {
						result.append(token);
					} else {
						result.append(QueryFilterButton.PREFERENCE_DEFAULT_KEY);
					}
				} else {
					if (!filterName.equals(token)) {
						result.append(";");
						result.append(token);
					}
				}
			}
			return result.toString();
		}
		return preferences;
	}

	protected String insertQueryFilterToList(final String filterName, final String preferences) {
		if (preferences != null) {
			final StringTokenizer st = new StringTokenizer(preferences, ";");
			final StringBuilder result = new StringBuilder();
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				if (filterName.equals(token)) {
					return preferences;
				}
				result.append(token);
				result.append(";");
			}
			result.append(filterName);
			return result.toString();
		}
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle recursos) {
		super.setResourceBundle(recursos);
		if (this.itemSaveListener != null) {
			this.itemSaveListener.setResourceBundle(recursos);
		}
		if (this.itemDeleteListener != null) {
			this.itemDeleteListener.setResourceBundle(recursos);
		}
	}

}
