package org.thechiselgroup.choosel.client.views;

import java.util.Collection;

import org.thechiselgroup.choosel.client.resources.Resource;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.popup.PopupManager;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem.Status;
import org.thechiselgroup.choosel.client.views.DefaultResourceItem.SubsetStatus;

public interface ResourceItem {

    Object getDisplayObject();

    /**
     * @return all resources in this resource item that are highlighted.
     *         Resources that are not contained in this resource item are not
     *         included in the result.
     */
    ResourceSet getHighlightedResources();

    Collection<Resource> getHighlightedSelectedResources();

    /**
     * @return highlighting manager that manages the highlighting for this
     *         visual representation of the resource item. For the popup, there
     *         is a separate highlighting manager.
     */
    HighlightingManager getHighlightingManager();

    SubsetStatus getHighlightStatus();

    PopupManager getPopupManager();

    ResourceSet getResourceSet();

    Object getResourceValue(Slot slot);

    Collection<Resource> getSelectedResources();

    SubsetStatus getSelectionStatus();

    Status getStatus();

    /**
     * The display object is an arbitrary objects that can be set by a view
     * content display. Usually it would the visual representation of this
     * resource item to facilitate fast lookup operations.
     * 
     * @param displayObject
     * 
     * @see #getDisplayObject()
     */
    void setDisplayObject(Object displayObject);

}