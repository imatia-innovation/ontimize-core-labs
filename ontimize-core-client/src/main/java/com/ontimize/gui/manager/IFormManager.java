package com.ontimize.gui.manager;

import java.awt.Container;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import com.ontimize.builder.FormBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.DataNavigationListener;
import com.ontimize.gui.FontAndEncodingSelector;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.SecureElement;
import com.ontimize.gui.SelectCurrencyValues;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.rule.RuleEngine;

public interface IFormManager extends Internationalization, SecureElement, Freeable, SelectCurrencyValues,
        FontAndEncodingSelector, DataNavigationListener {

    /** XML Attribute */
    public static final String FORM_BUILDER = "formbuilder";

    /** XML Attribute */
    public static final String FRAME = "frame";

    /** XML Attribute */
    public static final String CONTAINER = "container";

    /** XML Attribute */
    public static final String USE_CLASS_PATH = "useclasspath";

    /** XML Attribute */
    public static final String APPLICATION = "application";

    /** XML Attribute */
    public static final String LOCATOR = "locator";

    public static final String DETAIL = "detail";

    /** XML Attribute */
    public static final String IM_LOADER = "imloader";

    /** XML Attribute */
    public static final String RULE_LOADER = "ruleloader";

    /** XML Attribute */
    public static final String DELAYED_LOAD = "delayedload";

    /** XML Attribute */
    public static final String DYNAMIC_FORM = "dynamicform";

    /** XML Attribute */
    public static final String RESOURCES = "resources";

    /** XML Attribute */
    public static final String INTERACTION_MANAGER = "imanager";

    /** XML Attribute */
    public static final String FORM = "form";

    /** XML Attribute */
    public static final String RULES = "rules";

    /** XML Attribute */
    public static final String ACTION = "action";

    /** XML Attribute */
    public static final String LOAD = "load";

    /** XML Attribute */
    public static final String ID = "id";

    /** XML Attribute */
    public static final String SHOW_FRAME = "showframe";

    /** XML Attribute */
    public static final String ICON = "icon";

    /**
     * Gets the unique identifier for this <code>FormManager</code>
     * @return a <code>String</code> with the unique identifier.
     */
    public String getId();

    public void loadInEDTh();

    /**
     * Gets the Form URL which name is passed as entry parameter. The base classpath is retrieved from
     * {@link #baseCP} variable.
     * @param formName a <code>String<code> with the <code>Form</code> file name.
     * @return a <code>URL</code> reference
     */
    public URL getURL(String formName);

    public void reload(Form form);

    /**
     * Gets the <code>EntityReferenceLocator</code> reference that is stored inside.
     * @return a <code>EntityReferenceLocator</code>
     */
    public EntityReferenceLocator getReferenceLocator();

    /**
     * Gets the <code>Application</code> reference.
     * @return a <code>Application</code> reference
     */
    public Application getApplication();

    public RuleEngine getRuleEngine();

    public FormBuilder getFormBuilder();

    public String getLabelFileURI();

    public Locale getLocale();

    /**
     * Gets the <code>ResourceBundle</code> reference stored in this <code>FormManager</code>
     * @return a <code>ResourceBundle</code> reference
     */
    public ResourceBundle getResourceBundle();

    /**
     * Gets the <code>ResourceBundle</code> name stored in this <code>FormManager</code>
     * @return a <code>String</code> reference
     */

    public String getResourceFileName();

    /**
     * Gets a <code>Form</code> instance copy which form name is passed as entry parameter.
     * @param formName a <code>String</code> with the Form name.
     */

    public Form getFormCopy(String formName);

    /**
     * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. This
     * <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
     * reference that will be returned have registered the <code>InteractionManager</code> that has been
     * defined in application xml description. This method calls the {@link #getFormCopyInEDTh} method
     * from EventDispatchThread.
     * @param formName a <code>String</code> with the <code>Form</code> name.
     * @param container a <code>Container</code> where the <code>Form</code> will be displayed in.
     * @return a <code>Form</code> reference.
     */
    public Form getFormCopy(final String formName, final Container container);

    public Form getFormCopyInEDTh(String formName);

    /**
     * Gets a <code>Form</code> instance copy which form resource name is passed as entry parameter.
     * This <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
     * reference that will be returned have registered the <code>InteractionManager</code> that has been
     * passed as entry parameter. This method calls the {@link #getFormCopyInEDTh} method from
     * EventDispatchThread.
     * @param formResourceName a <code>String</code> with the <code>Form</code> name.
     * @param imQualifiedName a <code>String</code> with the interaction manager qualified class name.
     * @return a <code>Form</code> reference.
     */

    public Form getFormCopy(final String formResourceName, final String imQualifiedName);

    /**
     * Loads the <code>FormManager</code>.The initial <code>Form</code>, initial
     * <code>InteractionManager</code> and <code>Tree</code> are instanced and are layered out in
     * container to be shown. All this operations are performed in <code>EventDispatchThread</code>.
     */

    public void load();

    /**
     * Registers the <code>InteractionManager</code> which is passed as entry parameter to the Form
     * which name is passed as entry parameter as well. If the Form has just been loaded, the
     * <code>InteractionManager</code> is registered else the Form name and the
     * <code>InteractionManager</code> instance are stored in {@link #interactionManagerList} variable
     * to be registered when the <code>Form</code> is loaded.
     * @param interactionManager <code>InteractionManager</code> reference where the <code>Form</code>
     *        is registered in.
     * @param formName a <code>String</code> with the <code>Form</code> file name.
     */
    public void setInteractionManager(InteractionManager interactionManager, String formName);

    /**
     * Sets the <code>InteractionManager</code> for a
     * <code>Form<code> which file name is passed as entry parameter. The <code>InteractionManager</code>
     * instance is loaded by reflection and the class constructor mustn't have any parameter.
     * @param interactionManagerClass String with the <code>InteractionManager</code> Class name.
     * @param formName String with the <code>Form</code> file name.
     */

    public void setInteractionManager(String interactionManagerClass, String formName);

    public InteractionManager getInteractionManager(String formName);

    /**
     * Shows the form which file name is passed as entry parameter If this form isn't loaded, this
     * method will load the form. Before the form will be shown, this method checks if the current form
     * has modified data. This method call the {@link #showFormInEDTh} from EventDispatchThread.
     * @param formName a <code>String</code> with the file name.
     * @return true if the form is showed.
     */

    public boolean showForm(final String formName);

    public boolean showFormInEDTh(String form);

    public void setActiveForm(Form f);

    /**
     * Returns the active <code>Form</code> reference. This method takes into account both the forms
     * displayed in container and the forms layered out in <code>DetailForms</code>. Returns the
     * <code>Form</code> reference established in method {@link #setActiveForm(Form)}. If the
     * <code>Form</code> reference hasn't been established then this method returns the
     * <code>Form</code> reference that is visible into the FormManager container.
     * @return
     */

    public Form getActiveForm();

    public void addFormToContainer(JPanel panelForm, String formFileName);

    public Container getContainer();

    public void setApplication(Application application);

    public void setReferenceLocator(EntityReferenceLocator locator);

    public void setApplicationPreferences(ApplicationPreferences ap);

    public boolean isLoaded();

    /**
     * Gets the form reference which name is passed as entry parameter. This method call the
     * {@link #load} if is necessary and the loadIfNecessary parameter is true.
     * @param formName a Form name.
     * @param loadIfNecessary true to be loaded the <code>FormManager</code>
     */
    public Form getFormReference(String formName, boolean loadIfNecessary);

    /**
     * Gets a <code>Form</code> reference which name is passed as entry parameter.
     * @return a <code>Form</code> reference; null if the Form hasn't been loaded.
     */

    public Form getFormReference(String form);

    /**
     * Determines whether this FormManager should be shown in a new Frame.
     * @return true if is shown in a new Frame
     */
    public boolean showFrame();

    /**
     * Determines the icon that should be shown in a new Frame
     * @return
     */
    public String getIcon();

}
