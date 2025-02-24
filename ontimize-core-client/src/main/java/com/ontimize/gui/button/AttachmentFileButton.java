package com.ontimize.gui.button;

import java.awt.event.ActionListener;
import java.util.Map;

import com.ontimize.gui.ApplicationManager;

/**
 * Button to attach a file relates with an entity record.<BR>
 *
 * @version 1.0
 */
public class AttachmentFileButton extends Button {

	protected String entity = null;

	protected boolean addDescription = false;

	protected boolean refreshForm = false;

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
	 *        <td>Entity name to attach the file. Usually the same as the form entity</td>
	 *        </tr>
	 *        <tr>
	 *        <td>adddescripcion</td>
	 *        <td><i></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if a description will be added to the file</td>
	 *        </tr>
	 *        <tr>
	 *        <td>refresh</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the form refresh the values.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>wait</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the attachment process is synchronized or not. If it is a
	 *        synchronized process a progress window appears during the attachment process and blocks
	 *        the application until the attachment finishes. In a not synchronized process more than one
	 *        file can be attached at the same time</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sound</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>URI to a sound file that will be reproduced when the attachment process finishes</td>
	 *        </tr>
	 *        </table>
	 *        <br>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        </table>
	 * @throws Exception
	 */
	public AttachmentFileButton(final Map parameters) throws Exception {
		super(parameters);
		if (!parameters.containsKey("entity")) {
			throw new Exception("The 'entity' parameter is mandatory");
		}
		this.entity = parameters.get("entity").toString();
		if (parameters.containsKey("adddescription")) {
			this.addDescription = ApplicationManager.parseStringValue(parameters.get("adddescription").toString(),
					false);
		}
		if (parameters.containsKey("refresh")) {
			this.refreshForm = ApplicationManager.parseStringValue(parameters.get("refresh").toString(), false);
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
			this.setIcon(ApplicationManager.getDefaultAttachIcon());
		}
		super.addActionListener(new com.ontimize.gui.actions.AttachmentFileAction(this.entity, this.addDescription,
				this.refreshForm, bWait, uriSound));
	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
