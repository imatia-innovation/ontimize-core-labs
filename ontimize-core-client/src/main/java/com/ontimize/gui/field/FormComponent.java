package com.ontimize.gui.field;

import java.awt.LayoutManager;
import java.util.Map;

import com.ontimize.gui.i18n.Internationalization;

/**
 * This interface defines the methods that allows to initialize objects with their parameters, also
 * allows to get information about their position in forms and finally, sets this active or inactive
 * state. All components that will be created by <code>xml</code> constructor must implement this
 * interface and a <code>public constructor</code> like:</br>
 * <B>public <I>constructor</I>(Map params)</B>
 * <p>
 *
 * @author Imatia Innovation
 */

public interface FormComponent extends Internationalization {

	/**
	 * Main method to init an object from parameters <code>Hashtable</code>.
	 * <p>
	 * @param parameters the Map with pairs (Key,Value). For example:<br>
	 *        <p>
	 *        <TABLE BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td>Key</td>
	 *        <td>Value</td>
	 *        </tr>
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>center</i></td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td><i>attribute</i></td>
	 *        </tr>
	 *        <tr>
	 *        <td>...</td>
	 *        <td>...</td>
	 *        </tr>
	 *        </TABLE>
	 * @throws Exception when Exception occurs
	 */
	public void init(Map parameters) throws Exception;

	/**
	 * Description method to return the <code>constraints</code> component to place correctly in parent
	 * container.
	 * <p>
	 * @param parentLayout the parent container reference
	 * @return the constraints for the component
	 */
	public Object getConstraints(LayoutManager parentLayout);

	/**
	 * Description method to set enabled or not the component according the boolean condition in
	 * parameter.
	 * <p>
	 * @param enabled the enabled condition
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Description method to set visible or not the component according the boolean condition in
	 * parameter.
	 * <p>
	 * @param visible the visibility condition
	 */
	public void setVisible(boolean visible);

	/**
	 * Description method to check when a component is or not enabled.
	 * <p>
	 * @return the enabled condition
	 */
	public boolean isEnabled();

}
