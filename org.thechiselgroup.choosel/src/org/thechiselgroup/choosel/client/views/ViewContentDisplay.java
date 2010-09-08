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

import java.util.List;
import java.util.Set;

import org.thechiselgroup.choosel.client.persistence.Memento;
import org.thechiselgroup.choosel.client.ui.WidgetAdaptable;
import org.thechiselgroup.choosel.client.util.Disposable;

public interface ViewContentDisplay extends WidgetAdaptable, Disposable {

    void checkResize();

    void endRestore();

    /**
     * Returns an unmodifiable set of actions that can be executed on this view
     * content display.
     */
    List<ViewContentDisplayAction> getActions();

    /**
     * Returns an unmodifiable set of configurations that can be executed on
     * this view content display.
     */
    List<ViewContentDisplayConfiguration> getConfigurations();

    /**
     * @return identifiers of the visualization slots (retinal properties etc)
     *         that are supported by this view content display.
     */
    String[] getSlotIDs();

    void init(ViewContentDisplayCallback callback);

    boolean isReady();

    void restore(Memento state);

    Memento save();

    void startRestore();

    /**
     * <p>
     * Updates the view content display. There is no overlap between the three
     * different resource sets (added, updated, and removed resource items). We
     * use a single method to enable the different view content displays to do a
     * single refresh of the view instead of multiple operations.
     * </p>
     * <p>
     * The resource items can be referenced during a session for reference
     * testing. When a resource item is created, it is passed in as part of the
     * added resource items, when it changes, it is part of the updated resource
     * items, and when it is removed, it is part of the removed resource items.
     * </p>
     * 
     * @param addedResourceItems
     *            ResourceItems that have been added to the view. Is never
     *            <code>null</code>, but can be an empty set.
     * @param updatedResourceItems
     *            ResourceItems which have changed (status, data, etc.) such
     *            that their representation needs to be updated. Is never
     *            <code>null</code>, but can be an empty set.
     * @param removedResourceItems
     *            ResourceItems that have been removed from the view. Is never
     *            <code>null</code>, but can be an empty set.
     */
    void update(Set<ResourceItem> addedResourceItems,
            Set<ResourceItem> updatedResourceItems,
            Set<ResourceItem> removedResourceItems);

}