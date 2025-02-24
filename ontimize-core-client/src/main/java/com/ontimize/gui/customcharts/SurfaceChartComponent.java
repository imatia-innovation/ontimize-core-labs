package com.ontimize.gui.customcharts;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

public class SurfaceChartComponent extends SurfaceChart implements FormComponent, IdentifiedElement, Freeable {

	public static final String CHANGE_EXPRESSION = "changeexpression";

	public static final String EXPRESSION = "expression";

	public static final String POPUP_MENU = "popupmenu";

	public static final String ATTR = "attr";

	protected Object attr = null;

	public SurfaceChartComponent(final Map parameters) {
		super();
		this.init(parameters);
	}

	@Override
	public Object getAttribute() {
		return this.attr;
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public void initPermissions() {
	}

	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get(SurfaceChartComponent.ATTR);
		if (attr != null) {
			this.attr = attr.toString();
		}

		final Object expr = parameters.get(SurfaceChartComponent.EXPRESSION);
		if (expr != null) {
			this.setExpression(expr.toString());
		}

		Object v = parameters.get(SurfaceChartComponent.CHANGE_EXPRESSION);
		if (v != null) {
			final boolean allow = ApplicationManager.parseStringValue(v.toString(), true);
			this.canvas.setAllowChangeExpression(allow);
		}
		v = parameters.get(SurfaceChartComponent.POPUP_MENU);
		if (v != null) {
			final boolean p = ApplicationManager.parseStringValue(v.toString(), true);
			this.canvas.setPopupMenuEnabled(p);
		}
	}

	@Override
	public Object getConstraints(final LayoutManager paretLayout) {
		if (paretLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
