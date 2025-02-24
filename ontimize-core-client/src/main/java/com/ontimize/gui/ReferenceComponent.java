package com.ontimize.gui;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Interface to be implemented by components that need entity references. These entity references
 * will be obtained with a reference to the <code>ReferenceLocator</code>.
 *
 * @author Imatia Innovation
 */
public interface ReferenceComponent extends FormComponent {

    /**
     * Establishes the reference locator that will be used by component. This method is automatically
     * called by Form when it is being loaded.
     * @param referenceLocator a reference locator to set in component
     */
    public void setReferenceLocator(EntityReferenceLocator referenceLocator);

}
