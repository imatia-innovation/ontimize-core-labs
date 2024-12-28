package com.ontimize.gui.button;

import java.util.Map;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.actions.DownloadTableAttachmentFileAction;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;

public class DownloadTableAttachmentFileButton extends TableButton {

	protected String entity = null;

	/**
	 * @param parameters
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
	 *        <td><i>String</td>
	 *        <td>Table entity</td>
	 *        <td>yes</td>
	 *        <td>Parameter to indicate the entity which stores the attachment. By default, this is the
	 *        table entity</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opendownload</td>
	 *        <td><i>yes,no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate the file will be open when the process finishes file</td>
	 *        </tr>
	 *        <tr>
	 *        <tr>
	 *        <td>askopen</td>
	 *        <td><i>yes,no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if a message will be shown to open the file</td>
	 *        </tr>
	 *        <tr>
	 *        <td>defaultnameattr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Data field attributes which is used as suggestion for the name to save</td>
	 *        </tr>
	 *        <tr>
	 *        <td>wait</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the process is synchronized or not. If it is a synchronized
	 *        process a progress window appears during the process and blocks the application until the
	 *        download finishes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sound</td>
	 *        <td>String</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>URI to a sound file that will be reproduced when the process finishes</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tempfile</td>
	 *        <td>yes,no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>If open=yes and wait=yes and tempfile=yes a temporal file will be created. In this way
	 *        user has not to select the file name and location</td>
	 *        </tr>
	 *        <tr>
	 *        <td>originalfnamecolumn</td>
	 *        <td>String</td>
	 *        <td>null</td>
	 *        <td>no</td>
	 *        <td>Column which stores the original name of the file, to set the name of the original
	 *        file as the name of the download file by default</td>
	 *        </tr>
	 *        </table>
	 *        <br>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        </table>
	 * @throws Exception
	 */
	public DownloadTableAttachmentFileButton(final Map parameters, final Table table) throws Exception {
		if (!parameters.containsKey("entity")) {
			throw new Exception("The 'entity' parameter is mandatory");
		}
		this.entity = parameters.get("entity").toString();
		boolean openFile = true;
		boolean askOpen = true;
		boolean temporal = false;
		if (parameters.containsKey("openattachment")) {
			openFile = ApplicationManager.parseStringValue(parameters.get("openattachment").toString(), false);
		}
		if (parameters.containsKey("askopenattachment")) {
			askOpen = ApplicationManager.parseStringValue(parameters.get("askopenattachment").toString(), true);
		}

		if (parameters.containsKey("attachmenttempfile")) {
			temporal = ApplicationManager.parseStringValue(parameters.get("attachmenttempfile").toString(), false);
		}
		String sName = null;
		if (parameters.containsKey("defaultnameattr")) {
			sName = (String) parameters.get("defaultnameattr");
		}
		boolean bWait = true;
		if (parameters.containsKey("waitattachdownload")) {
			bWait = ApplicationManager.parseStringValue(parameters.get("waitattachdownload").toString(), true);
		}
		String uriSound = null;
		if (parameters.containsKey("soundattachdownload")) {
			uriSound = parameters.get("soundattachdownload").toString();
		}
		String originalfnamecolumn = null;
		if (parameters.containsKey("originalfnamecolumn")) {
			originalfnamecolumn = parameters.get("originalfnamecolumn").toString();
		}
		if (this.getIcon() == null) {
			this.setIcon(ApplicationManager.getDefaultDownloadAttachIcon());
		}

		super.addActionListener(new DownloadTableAttachmentFileAction(this.entity, openFile, sName, bWait, uriSound,
				askOpen, temporal, table));
	}

}
