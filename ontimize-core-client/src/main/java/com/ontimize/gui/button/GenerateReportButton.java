package com.ontimize.gui.button;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;

/**
 * This button allows to generate a report to view and/or print.<BR>
 */
public class GenerateReportButton extends Button {

	/**
	 * @param parameters In addition to the standard parameters of Button class:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of the data entity.This entity must implement PrintDataEntity. The data request
	 *        send a Map object with the values of the form keys in the request moment. It is
	 *        possible select the values to send as keys in the 'keys' parameter and which of this
	 *        values are mandatory in the 'required' parameter</td>
	 *        </tr>
	 *        <tr>
	 *        <td>report</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>XML description file path. This is a relative path, i.e.
	 *        com/ontimize/reports/report.xml</td>
	 *        </tr>
	 *        <tr>
	 *        <td>preview</td>
	 *        <td>yes,no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if a report preview is shown before printing the report.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>keys</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Form data fields to use in the report data query</td>
	 *        </tr>
	 *        <tr>
	 *        <td>required</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Subset of values specified by the 'keys' parameter. These values are necessary to be
	 *        able to generate the report</td>
	 *        </tr>
	 *        </table>
	 *        <br>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        </table>
	 * @throws Exception
	 */
	public GenerateReportButton(final Map parameters) throws Exception {
		super(parameters);
		if (!parameters.containsKey("entity")) {
			throw new Exception("The 'entity' parameter is mandatory");
		}
		final String sEntityName = parameters.get("entity").toString();

		if (!parameters.containsKey("report")) {
			throw new Exception("The 'report' parameter is mandatory");
		}
		final String report = parameters.get("report").toString();
		boolean preview = true;
		List keys = null;
		List required = null;
		boolean printdialog = true;

		if (parameters.containsKey("preview")) {
			preview = ApplicationManager.parseStringValue(parameters.get("preview").toString(), false);
		}
		if (parameters.containsKey("keys")) {
			keys = ApplicationManager.getTokensAt(parameters.get("keys").toString(), ";");
		}
		if (parameters.containsKey("required")) {
			required = ApplicationManager.getTokensAt(parameters.get("required").toString(), ";");
		}

		if (parameters.containsKey("printdialog")) {
			printdialog = ApplicationManager.parseStringValue(parameters.get("printdialog").toString(), true);
		}

		if (this.getIcon() == null) {
			this.setIcon(ImageManager.getIcon(ImageManager.PAGE));
		}
		super.addActionListener(new com.ontimize.gui.actions.GenerateReportAction(sEntityName, report, preview, keys,
				required, printdialog));
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
