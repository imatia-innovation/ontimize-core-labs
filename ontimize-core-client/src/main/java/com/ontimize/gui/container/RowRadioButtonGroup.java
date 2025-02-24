package com.ontimize.gui.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.RadioButtonDataField;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.NullableButtonGroup;

/**
 * This class implements a group of radio buttons placed in a row.
 * <p>
 *
 * @author Imatia Innovation
 */
public class RowRadioButtonGroup extends Row {

	private static final Logger logger = LoggerFactory.getLogger(RowRadioButtonGroup.class);

	/**
	 * A instance of a button group.
	 */
	protected NullableButtonGroup buttonGroup;

	/**
	 * Boolean to allow null selection.
	 */
	protected boolean allowNullSelection = false;

	/**
	 * A string reference for the initial selection. By default, null.
	 */
	protected String initSelection = null;

	/**
	 * The class constructor. Calls to <code>super()</code> with <code>Hashtable</code> parameters and
	 * initializes parameters.
	 * <p>
	 *
	 */
	public RowRadioButtonGroup(final Map parameters) {
		super(parameters);
		this.init(parameters);
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
	 *        <td>selected</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The initial selection for radio buttons.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>nullselection</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>By default this component always tries to select one component placed inside. With
	 *        this parameter is allowed null selection (a non-visible radio button is selected).</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		if (this.titleKey != null) {
			final Border border = this.getBorder();
			if ((border != null) && (border instanceof TitledBorder)) {
				((TitledBorder) border).setTitleColor(Color.blue);
				((TitledBorder) border).setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			}
		}

		if (parameters.containsKey("nullselection")) {
			this.allowNullSelection = ParseUtils.getBoolean((String) parameters.get("nullselection"),
					this.allowNullSelection);
		}
		this.buttonGroup = new NullableButtonGroup(this.allowNullSelection);

		final Object selected = parameters.get("selected");
		if (selected != null) {
			this.initSelection = selected.toString();
		} else {
			RowRadioButtonGroup.logger
			.debug(this.getClass().toString() + " .Info: 'selected' parameter hasn't been specified");
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Gets the selected item in radio button.
	 * <p>
	 * @return the selected field
	 */
	public RadioButtonDataField getSelected() {
		final Component[] comp = this.getComponents();
		for (int i = 0; i < comp.length; i++) {
			if (comp[i] instanceof RadioButtonDataField) {
				if (((RadioButtonDataField) comp[i]).isSelected()) {
					return (RadioButtonDataField) comp[i];
				}
			}
		}
		return null;
	}

	@Override
	public void add(final Component comp, final Object constraints) {
		super.add(comp, constraints);
		if (comp instanceof RadioButtonDataField) {
			this.buttonGroup.add(((RadioButtonDataField) comp).getAbstractButton());
			if (this.initSelection != null) {
				if (this.initSelection.equals(((RadioButtonDataField) comp).getAttribute())) {
					((RadioButtonDataField) comp).setValue(new Short((short) 1));
				}
			}
		}
	}

}
