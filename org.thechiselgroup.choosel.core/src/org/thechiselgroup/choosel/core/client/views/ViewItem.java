/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/
package org.thechiselgroup.choosel.core.client.views;

import org.thechiselgroup.choosel.core.client.resources.ResourceSet;
import org.thechiselgroup.choosel.core.client.views.slots.Slot;

/**
 * Model of several resources that should be displayed as a visual item in the
 * view. The <code>ViewItem</code> provides the highlighting and selection
 * status, enables {@link ViewContentDisplay}s to store a display object, and
 * facilitates the resolution of {@link Slot}s.
 * 
 * @author Lars Grammel
 * 
 * @see View
 */
public interface ViewItem {

    public static enum Status {

        DEFAULT, HIGHLIGHTED, HIGHLIGHTED_SELECTED, SELECTED, PARTIALLY_HIGHLIGHTED, PARTIALLY_HIGHLIGHTED_SELECTED, PARTIALLY_SELECTED;

    }

    public static enum Subset {

        ALL, SELECTED, HIGHLIGHTED

    }

    public static enum SubsetStatus {

        NONE, PARTIAL, COMPLETE

    }

    /**
     * @see #setDisplayObject(Object)
     */
    Object getDisplayObject();

    /**
     * @return all resources in this resource item that are highlighted.
     *         Resources that are not contained in this resource item are not
     *         included in the result.
     */
    ResourceSet getHighlightedResources();

    ResourceSet getHighlightedSelectedResources();

    /**
     * @return highlighting manager that manages the highlighting for this
     *         visual representation of the resource item. For the popup, there
     *         is a separate highlighting manager.
     */
    // HighlightingManager getHighlightingManager();

    SubsetStatus getHighlightStatus();

    // PopupManager getPopupManager();

    ResourceSet getResourceSet();

    ResourceSet getSelectedResources();

    SubsetStatus getSelectionStatus();

    // TODO move to super interface
    Slot[] getSlots();

    <T> T getSlotValue(Slot slot);

    // TODO test
    <T> T getSlotValue(Slot slot, Subset subset);

    Status getStatus();

    /**
     * Returns the identifier of the view item.
     */
    String getViewItemID();

    /**
     * Events from the visual representations of view items in concrete
     * visualizations must be forwarded to their corresponding view item. This
     * is especially important for mouse events, and also for keyboard events.
     * Separating the visual representation from the event handling facilitates
     * customization and maintenance.
     */
    void reportInteraction(ViewItemInteraction interaction);

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