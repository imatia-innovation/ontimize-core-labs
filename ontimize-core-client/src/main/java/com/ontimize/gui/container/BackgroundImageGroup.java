package com.ontimize.gui.container;

import java.awt.Component;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.DataComponent;

public class BackgroundImageGroup extends JImageContainer implements DataComponentGroup {

	private static final Logger logger = LoggerFactory.getLogger(BackgroundImageGroup.class);

	protected Map<Object, DataComponent>	dataComponent	= new Hashtable<>();

	protected Object attribute = null;

	public BackgroundImageGroup(final Map parameters) {
		// Parent constructor execute the initialization
		super(parameters);
		final Object attr = parameters.get("attr");
		if (attr == null) {
			BackgroundImageGroup.logger.debug(this.getClass().toString() + " 'attr' parameter is required");
		} else {
			this.attribute = attr;
		}

	}

	@Override
	public void add(final Component comp, final Object constraints) {
		if (comp instanceof DataComponent) {
			this.dataComponent.put(((DataComponent) comp).getAttribute(), (DataComponent) comp);
		}
		super.add(comp, constraints);
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public Map getGroupValue() {
		final Map hValue = new Hashtable();
		for (final Entry<Object, DataComponent> entry : this.dataComponent.entrySet()) {
			final Object oKey = entry.getKey();
			final DataComponent pDataComponent = entry.getValue();
			hValue.put(oKey, pDataComponent.getValue());
		}
		return hValue;
	}

	@Override
	public void setAllModificable(final boolean modif) {
		for (final Entry<Object, DataComponent> entry : this.dataComponent.entrySet()) {
			final DataComponent oDataComponent = entry.getValue();
			oDataComponent.setModifiable(modif);
		}
	}

	@Override
	public void setAllEnabled(final boolean en) {
		for (final Entry<Object, DataComponent> entry : this.dataComponent.entrySet()) {
			final DataComponent oDataComponent = entry.getValue();
			oDataComponent.setEnabled(en);
		}
	}

	@Override
	public void setGroupValue(final Map value) {
		for (final Entry<Object, DataComponent> entry : this.dataComponent.entrySet()) {
			final Object oKey = entry.getKey();
			final DataComponent oDataComponent = entry.getValue();
			oDataComponent.setValue(value.get(oDataComponent.getAttribute()));
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
		return new Vector(this.dataComponent.keySet());
	}

}
