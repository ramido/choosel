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
package org.thechiselgroup.choosel.client.views;

import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.resources.ResourceSet;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.Disposable;

public interface ViewContentDisplay extends WidgetAdaptable, Disposable {

    void checkResize();

    ResourceItem createResourceItem(ResourceItemValueResolver resolver,
            String category, ResourceSet resources, HoverModel hoverModel);

    void endRestore();

    /**
     * @return identifiers of the visualization slots (retinal properties etc)
     *         that are supported by this view content display.
     */
    String[] getSlotIDs();

    void init(ViewContentDisplayCallback callback);

    boolean isReady();

    void removeResourceItem(ResourceItem resourceItem);

    void restore(Memento state);

    Memento save();

    void startRestore();

    /**
     * Updates the view content display. There is no overlap between the three
     * different resource sets (TODO --> use 3 different methods).
     * 
     * @param addedResourceItems
     *            ResourceItems that have been added to the view. Is never
     *            <code>null</code>.
     * @param updatedResourceItems
     *            ResourceItems which have changed (status, data, etc.) such
     *            that their representation needs to be updated. Is never
     *            <code>null</code>.
     * @param removedResourceItems
     *            ResourceItems that have been removed from the view. Is never
     *            <code>null</code>.
     */
    void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems);

}