package com.ontimize.gui.button;

import java.awt.event.ActionListener;
import java.util.Map;

import com.ontimize.gui.ApplicationManager;

/**
 * Button to download an attached file to an entity record.<BR>
 *
 * @version 1.0
 */
public class DownloadAttachmentFileButton extends Button {

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
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Entity name to get the file. Usually the same as the form entity</td>
	 *        </tr>
	 *        <tr>
	 *        <td>open</td>
	 *        <td><i>yes/no<i></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate the file will be open when the process finishes file</td>
	 *        </tr>
	 *        <tr>
	 *        <tr>
	 *        <td>askopen</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if a message will be shown to open the file</td>
	 *        </tr>
	 *        <tr>
	 *        <td>defaultnameattr</td>
	 *        <td><i>String</td>
	 *        <td>null</td>
	 *        <td>no</td>
	 *        <td>Data field attributes which is used as suggestion for the name to save</td>
	 *        </tr>
	 *        <tr>
	 *        <td>wait</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the process is synchronized or not. If it is a synchronized
	 *        process a progress window appears during the process and blocks the application until the
	 *        download finishes. In a not synchronized process more than one file can be downloaded at
	 *        the same time</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sound</td>
	 *        <td><i>String</td>
	 *        <td>null</td>
	 *        <td>no</td>
	 *        <td>URI to a sound file that will be reproduced when the process finishes</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tempfile</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>If open=yes and wait=yes and tempfile=yes a temporal file will be created. In this way
	 *        user has not to select the file name and location</td>
	 *        </tr>
	 *        </table>
	 *        <br>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        </table>
	 * @throws Exception
	 */
	public DownloadAttachmentFileButton(final Map parameters) throws Exception {
		super(parameters);
		if (!parameters.containsKey("entity")) {
			throw new Exception("The 'entity' parameter is mandatory");
		}
		this.entity = parameters.get("entity").toString();
		boolean openFile = true;
		boolean askOpen = true;
		boolean temporal = false;
		if (parameters.containsKey("open")) {
			openFile = ApplicationManager.parseStringValue(parameters.get("open").toString(), false);
		}
		if (parameters.containsKey("askopen")) {
			askOpen = ApplicationManager.parseStringValue(parameters.get("askopen").toString(), true);
		}

		if (parameters.containsKey("tempfile")) {
			temporal = ApplicationManager.parseStringValue(parameters.get("tempfile").toString(), false);
		}
		String sName = null;
		if (parameters.containsKey("defaultnameattr")) {
			sName = (String) parameters.get("defaultnameattr");
		}
		boolean bWait = true;
		if (parameters.containsKey("wait")) {
			bWait = ApplicationManager.parseStringValue(parameters.get("wait").toString(), true);
		}
		String uriSound = null;
		if (parameters.containsKey("sound")) {
			uriSound = parameters.get("sound").toString();
		}
		if (this.getIcon() == null) {
			this.setIcon(ApplicationManager.getDefaultDownloadAttachIcon());
		}

		super.addActionListener(new com.ontimize.gui.actions.DownloadAttachmentFileAction(this.entity, openFile, sName,
				bWait, uriSound, askOpen, temporal));
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
