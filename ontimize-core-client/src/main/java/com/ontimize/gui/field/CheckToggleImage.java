package com.ontimize.gui.field;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class implements a checkable image. This one provides two level states corresponding with
 * two integer values in database (Number and Short is allowed). So, "0" will be the value obtained
 * from getValue() while the state is disabled. When a click is registered, changes the image and
 * state for component. A new click over the image returns the component to "0" state. By default,
 * "0" is the initial state.
 *
 * @author Imatia Innovation S.L.
 */

public class CheckToggleImage extends ToggleImage implements DataComponent {

	private final List changeListeners = new Vector();

	protected boolean required = false;

	protected boolean show = true;

	protected boolean modificable = true;

	protected Object storedValue = null;

	private boolean processMouse = false;

	public MouseAdapter checkToggleImageMouse = new MouseAdapter() {

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (CheckToggleImage.this.isEnabled()) {
				if (CheckToggleImage.this.processMouse) {
					CheckToggleImage.this.fireStateChanged();
					if (CheckToggleImage.this.getStatus() == ToggleImage.OFF) {
						CheckToggleImage.this.setStatus(ToggleImage.ON);
					} else {
						CheckToggleImage.this.setStatus(ToggleImage.OFF);
					}
				}
			}
		}
	};

	public CheckToggleImage(final Map parameters) {
		super(parameters);
		final Object visible = parameters.get("visible");
		if (visible != null) {
			if (visible.equals("no")) {
				this.show = false;
			} else {
				this.show = true;
			}
		}

		final Object required = parameters.get("required");
		if (required != null) {
			if (required.equals("yes")) {
				this.required = true;
			} else {
				this.required = false;
			}
		}
		final Object enableMouse = parameters.get("enablemouse");
		if (enableMouse == null) {
			this.processMouse = false;
		} else {
			if (enableMouse.toString().equalsIgnoreCase("yes") || enableMouse.toString().equalsIgnoreCase("true")) {
				this.processMouse = true;
			} else {
				this.processMouse = false;
			}
		}
		this.addMouseListener(this.checkToggleImageMouse);
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		if (this.getStatus() == 1) {
			this.repaint();
			return new Short((short) 1);
		} else {
			this.repaint();
			return new Short((short) 0);
		}
	}

	@Override
	public void setValue(final Object value) {
		if (value instanceof Number) {
			this.setStatus(((Number) value).shortValue());
		} else {
			this.deleteData();
		}
		this.storedValue = this.getValue();
	}

	@Override
	public void deleteData() {
		this.setStatus(this.preferredStatus);
		this.storedValue = this.getValue();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return this.modificable;
	}

	@Override
	public void setModifiable(final boolean modif) {
		this.modificable = modif;
	}

	@Override
	public boolean isHidden() {
		return !this.show;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.SMALLINT;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	public void disableMouseListener() {
		this.processMouse = false;
	}

	public void addChangeListener(final ChangeListener l) {
		if (!this.changeListeners.contains(l)) {
			this.changeListeners.add(l);
		}
	}

	public void removeChangeListener(final ChangeListener l) {
		this.changeListeners.remove(l);
	}

	public void fireStateChanged() {
		for (int i = 0; i < this.changeListeners.size(); i++) {
			((ChangeListener) this.changeListeners.get(i)).stateChanged(new ChangeEvent(this));
		}
	}

}
