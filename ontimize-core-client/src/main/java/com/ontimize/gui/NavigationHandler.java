package com.ontimize.gui;

import java.util.List;
import java.util.Vector;

import javax.swing.event.EventListenerList;

/**
 * This class is a singleton to manage the navigation between forms. It is used in component
 * {@link ApToolBarNavigator} to get the last visited forms. List with last visited forms is updated
 * in {@link MainApplication#showFormManagerContainer(String)}.
 *
 * @author Imatia Innovation SL
 * @since 5.2057EN-1.0
 *
 */
public class NavigationHandler {

	private static NavigationHandler handler;

	protected String previousFormManager = new String();

	private NavigationHandler() {
	}

	public static NavigationHandler getInstance() {
		if (NavigationHandler.handler == null) {
			NavigationHandler.handler = new NavigationHandler();
		}
		return NavigationHandler.handler;
	}

	protected int currentNavigationPosition = 0;

	public int previousNavigationPosition = 0;

	protected int defaultNumberFormManagersShowed = 15;

	protected List vLastVisitedFormManagers = new Vector();

	protected int numberFormManagersShowed = this.defaultNumberFormManagersShowed;

	public List getLastVisitedFormManagers() {
		return this.vLastVisitedFormManagers;
	}

	public void setNavigationPosition(final int position) {
		this.currentNavigationPosition = position;
	}

	public int getNavigationPosition() {
		return this.currentNavigationPosition;
	}

	public void updateLastVisitedFormManagers(final String formManager) {
		if (formManager != null) {
			if (this.vLastVisitedFormManagers.size() == 0) {
				this.previousFormManager = ((MainApplication) ApplicationManager.getApplication()).panelIds.get(0)
						.toString();
				if (!formManager.equals(this.previousFormManager)) {
					this.vLastVisitedFormManagers
					.add(((MainApplication) ApplicationManager.getApplication()).panelIds.get(0).toString());
					this.fireNavigationEvent(
							new NavigationEvent(this, NavigationEvent.FORM_MANAGER_CHANGED,
									((MainApplication) ApplicationManager.getApplication()).panelIds.get(0)
									.toString()));
				}
			}
			if (!formManager.equals(this.previousFormManager)) {
				this.vLastVisitedFormManagers.add(formManager);
				this.fireNavigationEvent(new NavigationEvent(this, NavigationEvent.FORM_MANAGER_CHANGED, formManager));
			}
			this.previousFormManager = formManager;
		}
	}

	protected EventListenerList navigationListenerList = new EventListenerList();

	public void addNavigationListener(final INavigationEvent l) {
		this.navigationListenerList.add(INavigationEvent.class, l);
	}

	public void removeNavigationListener(final INavigationEvent l) {
		this.navigationListenerList.remove(INavigationEvent.class, l);
	}

	protected void fireNavigationEvent(final NavigationEvent formManagerChangedEvent) {
		final Object[] listeners = this.navigationListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == INavigationEvent.class) {
				((INavigationEvent) listeners[i + 1]).formManagerChanged(formManagerChangedEvent);
			}
		}
	}

}
