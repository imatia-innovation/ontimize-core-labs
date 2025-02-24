package com.ontimize.gui.button;

import java.awt.event.ActionListener;
import java.util.Map;

import com.ontimize.gui.ApplicationManager;

/**
 * The button to refresh a table. Table will be refreshed with parent key values of fields. It is
 * required fields referred to parent keys for table are not empty at refresh moment.
 * <p>
 *
 * @author Imatia Innovation
 */
public class RefreshTableButton extends Button {

	/**
	 * The entity reference. By default, null.
	 */
	protected String entity = null;

	/**
	 * The reference for the message when there are not results after refreshing. By default, null.
	 */
	protected String withoutDataMessage = null;

	/**
	 * The class constructor. Calls to <code>super()</code>, sets icon and adds a listener.
	 * <p>
	 *
	 *
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>table</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The name of table for button.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>nodatamessage</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The message to show when no results after refreshing. By default, no message.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>autoadjusttablecolumns</td>
	 * <td><i>yes/no</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Indicates whether table auto adjust the columns after refreshing.</td>
	 * </tr>
	 *
	 * </TABLE>
	 * @param parameters the Map with parameters. Adds the next parameters:
	 * @throws Exception when a exception occurs
	 */
	public RefreshTableButton(final Map parameters) throws Exception {
		super(parameters);
		if (!parameters.containsKey("table")) {
			throw new Exception("Parameter 'table' is required");
		}
		this.entity = parameters.get("table").toString();

		this.withoutDataMessage = (String) parameters.get("nodatamessage");

		boolean ajust = false;
		final Object autoadjusttablecolumns = parameters.get("autoadjusttablecolumns");
		if (autoadjusttablecolumns != null) {
			if (autoadjusttablecolumns.equals("yes") || autoadjusttablecolumns.equals("true")) {
				ajust = true;
			}
		}

		if (!parameters.containsKey(Button.ICON)) {
			this.setIcon(ApplicationManager.getDefaultRefreshTableIcon());
		}

		super.addActionListener(
				new com.ontimize.gui.actions.RefreshTableAction(this.entity, this.withoutDataMessage, ajust));
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
