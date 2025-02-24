package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.actions.CreateFormInDialog;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a sensitive hyperlink label. It allows developers to open a detail form
 * from a normal field (in this case a label) which is inserted in the source form. Several
 * parameters can be set to show different layouts, such as, icon, background color, underlined
 * option, and so on.
 * <p>
 * s
 *
 * @author Imatia Innovation
 */
public class HyperlinkLabel extends Label implements HyperlinkComponent {

	/**
	 * The reference to mouse listener. By default, null.
	 */
	protected MouseListener mouseListener = null;

	/**
	 * The reference to create a form in a dialog. By default, null.
	 */
	protected CreateFormInDialog action = null;

	/**
	 * A reference for the name of form. By default, null.
	 */
	protected String formName = null;

	/**
	 * The name of forms manager. By default, null.
	 */
	protected String formsManagerName = null;

	/**
	 * The key for the title. By default, " ".
	 */
	protected String keyTitle = "";

	/**
	 * The disabled color. By default, black.
	 * <p>
	 *
	 * @see Color#brighter()
	 */
	protected static Color DISABLE_COLOR = Color.black.brighter();

	/**
	 * The activated condition. By default, false.
	 */
	protected boolean activated = false;

	/**
	 * The equivalences <code>Hashtable</code>.
	 */
	protected Map equivalences = null;

	/**
	 * The class constructor. Calls to super() with parameters.
	 * <p>
	 * @throws IllegalArgumentException when an Exception occurs
	 */
	public HyperlinkLabel(final Map parameters) throws IllegalArgumentException {
		super(parameters);

		final Object equivalences = parameters.get("equivalences");
		if (equivalences != null) {
			this.equivalences = new Hashtable();
			final StringTokenizer st = new StringTokenizer(equivalences.toString(), ";");
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				final StringTokenizer st2 = new StringTokenizer(token, "=");
				if (st2.countTokens() != 2) {
					throw new IllegalArgumentException("Error in parameter 'equivalences'");
				}
				this.equivalences.put(st2.nextToken(), st2.nextToken());
			}
		}

		// TODO Delete gforms parameter. This parameter has been changed by
		// 'fmanager'.
		Object oFManager = parameters.get("gforms");
		if (oFManager != null) {
			this.formsManagerName = oFManager.toString();
		}

		oFManager = parameters.get("fmanager");
		if (oFManager != null) {
			this.formsManagerName = oFManager.toString();
		}

		final Object form = parameters.get("form");
		if (form != null) {
			this.formName = form.toString();
		} else {
			throw new IllegalArgumentException("Parameter 'form' is required");
		}
		final Object title = parameters.get("title");
		if (title != null) {
			this.keyTitle = title.toString();
		}

		final Object oUnderlined = parameters.get("underlined");
		final boolean bUnderlined = ParseUtils.getBoolean((String) oUnderlined, true);
		((EJLabel) this.label).setUnderlined(bUnderlined);

		this.mouseListener = this.createMouseListener();
		this.label.addMouseListener(this.mouseListener);
	}

	protected MouseAdapter createMouseListener() {
		return new MouseAdapter() {

			@Override
			public void mouseEntered(final MouseEvent e) {
				if (HyperlinkLabel.this.label == null) {
					return;
				}
				if (HyperlinkLabel.this.isEnabled()) {
					HyperlinkLabel.this.label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					HyperlinkLabel.this.label.setCursor(ApplicationManager.getDisabledLinkCursor());
				}
			}

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					if (HyperlinkLabel.this.isEnabled()) {
						HyperlinkLabel.this.activateLink();
						HyperlinkLabel.this.parentForm.refreshCurrentDataRecord();
					}
				}
			}
		};
	}

	@Override
	public boolean isEnabled() {
		return this.activated;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				this.setEnabled(false);
				return;
			}
		}
		this.activated = enabled;
		if ((this.label != null) && !enabled) {
			this.label.setForeground(HyperlinkLabel.DISABLE_COLOR);
			this.label.setCursor(ApplicationManager.getDisabledLinkCursor());
		} else if (this.label != null) {
			this.label.setForeground(this.fontColor);
		}
	}

	protected void activateLink() {
		if (this.label != null) {
			this.label.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		if (this.action == null) {
			this.action = new CreateFormInDialog(this.formsManagerName, this.formName, this.keyTitle,
					this.equivalences) {

				@Override
				public void windowWillShow() {
					try {
						this.goToSourceRecord();
					} catch (final Exception e) {
						HyperlinkLabel.this.parentForm.message(e.getMessage(), Form.ERROR_MESSAGE, e);
					}
				}
			};
		}
		this.action.actionPerformed(new ActionEvent(this.label, -1, ""));
		if (this.label != null) {
			this.label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Calls to <code>super()</code> to initialize parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 *        <p>
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
	 *        <td>fmanager</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of form manager responsible of the form creation.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>form</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Form name. Keys are needed in form.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The title of form. Do not confuse with the text parameter, which is the text for this
	 *        label</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>underlined</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>underlined text option</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>equivalences</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The equivalences between navigation form fields and the form where component is
	 *        placed. For instance, it can be useful when alias are used<br>
	 *        The format must be:<br>
	 *        <b>nav1=val1<br>
	 *        <b>nav2=val2</td>
	 *        </tr>
	 *
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		if (!parameters.containsKey("fontcolor")) {
			parameters.put("fontcolor", "blue");
		}

		super.init(parameters);
	}

}
