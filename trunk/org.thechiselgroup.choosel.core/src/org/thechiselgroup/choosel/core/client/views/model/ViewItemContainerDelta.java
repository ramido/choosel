/*******************************************************************************
 * Copyright (C) 2011 Lars Grammel 
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
package org.thechiselgroup.choosel.core.client.views.model;

import org.thechiselgroup.choosel.core.client.util.collections.LightweightCollection;

/**
 * Delta of changes to a {@link ViewItemContainer}: new, changed, and removed
 * {@link ViewItem}s.
 * 
 * @author Lars Grammel
 */
public class ViewItemContainerDelta {

    private LightweightCollection<ViewItem> addedViewItems;

    private LightweightCollection<ViewItem> removedViewItems;

    private LightweightCollection<ViewItem> updatedViewItems;

    public ViewItemContainerDelta(
            LightweightCollection<ViewItem> addedViewItems,
            LightweightCollection<ViewItem> updatedViewItems,
            LightweightCollection<ViewItem> removedViewItems) {

        assert addedViewItems != null;
        assert removedViewItems != null;
        assert updatedViewItems != null;

        this.addedViewItems = addedViewItems;
        this.removedViewItems = removedViewItems;
        this.updatedViewItems = updatedViewItems;
    }

    public LightweightCollection<ViewItem> getAddedViewItems() {
        return addedViewItems;
    }

    public LightweightCollection<ViewItem> getRemovedViewItems() {
        return removedViewItems;
    }

    public LightweightCollection<ViewItem> getUpdatedViewItems() {
        return updatedViewItems;
    }

    public boolean isEmpty() {
        return addedViewItems.isEmpty() && updatedViewItems.isEmpty()
                && removedViewItems.isEmpty();
    }

}