package com.ontimize.gui.container;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.util.ObjectTools;

/**
 * This class implements a basic group of data components in a panel.
 * <p>
 *
 * @author Imatia Innovation
 */
public class BasicDataComponentGroup extends JPanel implements DataComponentGroup, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(BasicDataComponentGroup.class);

	/**
	 * The attribute reference. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The attribute list instance.
	 */
	protected List attributeList = new Vector();

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The preferred size instance.
	 */
	protected Dimension prefSize = new Dimension(0, 0);

	/**
	 * The constraints for component.
	 */
	protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * The class constructor. Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public BasicDataComponentGroup(final Map parameters) {
		this.init(parameters);
	}

	@Override
	public Object getConstraints(final LayoutManager layout) {
		if (layout instanceof GridBagLayout) {
			return this.constraints;
		} else {
			return null;
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return this.prefSize;
	}

	@Override
	public Dimension getMinimumSize() {
		return this.prefSize;
	}

	@Override
	public Dimension getPreferredSize() {
		return this.prefSize;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Adds the next parameters:
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute name.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>group</td>
	 *        <td><i>attr1;attr2;...;attrn</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The list of data components attributes to form the component.</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr == null) {
			BasicDataComponentGroup.logger.debug(this.getClass().toString() + ": 'attr' parameter is required");
			throw new IllegalArgumentException(this.getClass().toString() + ": 'attr' parameter is required");
		} else {
			this.attribute = attr;
		}

		final Object group = parameters.get("group");
		if (group == null) {
			throw new IllegalArgumentException(this.getClass().toString() + ": 'group' parameter is required");
		} else {
			final StringTokenizer st = new StringTokenizer(group.toString(), ";");
			while (st.hasMoreElements()) {
				final String token = st.nextToken();
				this.attributeList.add(token);
			}
		}

	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public Map getGroupValue() {
		final Map hValue = new Hashtable();
		for (int i = 0; i < this.attributeList.size(); i++) {
			final Object oKey = this.attributeList.get(i);
			final Object v = this.parentForm.getDataFieldValue((String) oKey);
			if (v != null) {
				hValue.put(oKey, v);
			}
		}
		return hValue;
	}

	@Override
	public void setGroupValue(final Map values) {
		for (int i = 0; i < this.attributeList.size(); i++) {
			final Object oKey = this.attributeList.get(i);
			this.parentForm.setDataFieldValue(oKey, values.get(oKey));
		}
	}

	/**
	 * Deletes the data group.
	 * <p>
	 *
	 * @see Form#deleteDataField(String)
	 */
	public void deleteDataGroup() {
		for (int i = 0; i < this.attributeList.size(); i++) {
			final Object oKey = this.attributeList.get(i);
			this.parentForm.deleteDataField(oKey.toString());
		}
	}

	@Override
	public void setAllModificable(final boolean modif) {
		for (int i = 0; i < this.attributeList.size(); i++) {
			final Object oKey = this.attributeList.get(i);
			this.parentForm.setModifiable((String) oKey, modif);
		}
	}

	@Override
	public void setAllEnabled(final boolean en) {
		for (int i = 0; i < this.attributeList.size(); i++) {
			final Object oKey = this.attributeList.get(i);
			if (!en) {
				this.parentForm.disableDataField((String) oKey);
			} else {
				this.parentForm.enableDataField((String) oKey);
			}
		}
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public void initPermissions() {
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public List getAttributes() {
		return ObjectTools.clone(this.attributeList);
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
