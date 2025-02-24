package com.ontimize.util.rule;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.manager.IFormManager;

/**
 * This class is notified when any event occurs in form (value change, click, form changes current
 * mode ...). For this reason it implements the most common component interfaces:
 * {@link ValueChangeListener}, {@link ActionListener}, {@link ListSelectionListener} ... to execute
 * these methods. <br>
 * So, when form event is fired each implemented method: <br>
 * -get related events for this type <br>
 * <br>
 * -for each rule in event check conditions and execute list of actions.
 *
 * @author Imatia Innovation
 * @since 5.2075EN
 */
public class RuleEngine implements ValueChangeListener, ActionListener, ListSelectionListener {

	private static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);

	protected IFormManager formManager;

	protected Map formRules = new Hashtable();

	public RuleEngine() {

	}

	public RuleEngine(final IFormManager formManager) {
		this.formManager = formManager;
	}

	public IFormManager getFormManager() {
		return this.formManager;
	}

	public void setFormManager(final IFormManager formManager) {
		this.formManager = formManager;
	}

	@Override
	public void valueChanged(final ValueEvent valueEvent) {
		if (ValueEvent.USER_CHANGE == valueEvent.getType()) {
			final DataComponent comp = (DataComponent) valueEvent.getSource();
			final Form form = (Form) SwingUtilities.getAncestorOfClass(Form.class, (Component) comp);
			if (form != null) {
				final String formName = form.getArchiveName();
				final Rules rules = (Rules) this.formRules.get(formName);
				if (rules != null) {
					final Map filterEvents = new Hashtable();
					final List lActions = RuleUtils.findActionsByTypeAndField(rules, RuleParser.Attributes.VALUE_TYPE_EVENT,
							filterEvents, comp.getAttribute().toString(), null);
					ActionDispatcher.execute(lActions, form);
				}
			} else {
				if (ApplicationManager.DEBUG) {
					RuleEngine.logger.debug(this.getClass().getName() + ": rules cannot be set to component:"
							+ comp.getAttribute() + " due to form parent not detected");
				}
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent actionEvent) {
	}

	@Override
	public void valueChanged(final ListSelectionEvent listSelectEvent) {
	}

	public void setRules(final String formName, final IRules rules) {
		this.formRules.put(formName, rules);
	}

}
