package com.ontimize.util.wia;

import java.awt.event.ActionListener;
import java.util.Map;

import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.TableButton;
import com.ontimize.util.ParseUtils;

public class AttachtmentScanWIAFileButton extends TableButton {

	protected String entity = null;

	protected boolean scanAddDescription = true;

	protected boolean scanRefreshForm = true;

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
	 *        <td>scanAddDescription</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if a description will be added to the file</td>
	 *        </tr>
	 *        <tr>
	 *        <td>scanRefreshForm</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the form refresh the values.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>scanWait</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Parameter to indicate if the attachment process is synchronized or not. If it is a
	 *        synchronized process a progress window appears during the attachment process and blocks
	 *        the application until the attachment finishes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>scanSound</td>
	 *        <td><i>String</td>
	 *        <td>null</td>
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
	public AttachtmentScanWIAFileButton(final Map params) throws Exception {
		if (!params.containsKey("entity")) {
			throw new Exception("The 'entity' parameter is mandatory");
		}

		this.entity = params.get("entity").toString();

		if (params.containsKey("scanAddDescription")) {
			this.scanAddDescription = ParseUtils.getBoolean(params.get("scanAddDescription").toString(), false);
		}

		if (params.containsKey("scanRefreshForm")) {
			this.scanRefreshForm = ParseUtils.getBoolean(params.get("scanRefreshForm").toString(), false);
		}

		boolean bWait = true;
		if (params.containsKey("scanWait")) {
			bWait = ParseUtils.getBoolean(params.get("scanWait").toString(), true);
		}
		String uriSound = null;
		if (params.containsKey("scanSound")) {
			uriSound = params.get("scanSound").toString();
		}

		if (this.getIcon() == null) {
			this.setIcon(ImageManager.getIcon(ImageManager.SCANNER));
		}

		super.addActionListener(new AttachmentScanWIAFileAction(this.entity, this.scanAddDescription,
				this.scanRefreshForm, bWait, uriSound));

	}

	@Override
	public void addActionListener(final ActionListener al) {

	}

	@Override
	public void removeActionListener(final ActionListener al) {

	}

}
