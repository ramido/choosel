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
package org.thechiselgroup.choosel.core.client.resources;

import java.util.Map;

import org.thechiselgroup.choosel.core.client.resources.ResourceGroupingChange.Delta;
import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

import com.google.gwt.event.shared.GwtEvent;

public class ResourceGroupingChangedEvent extends
        GwtEvent<ResourceGroupingChangedHandler> {

    public static final GwtEvent.Type<ResourceGroupingChangedHandler> TYPE = new GwtEvent.Type<ResourceGroupingChangedHandler>();

    /**
     * A list is used here because maintaining the order in which the changes
     * have been made is important. If dependent elements are added to a map in
     * a client class before similar elements are removed, this can cause
     * errors, and the unordered nature of sets allows for these errors.
     */
    private final LightweightList<ResourceGroupingChange> changes;

    /**
     * Alternatively, the changes can be retrieved based on their type.
     */
    private final Map<String, LightweightList<ResourceGroupingChange>> changesByDeltaType;

    public ResourceGroupingChangedEvent(
            LightweightList<ResourceGroupingChange> changes) {

        assert changes != null;
        assert !changes.isEmpty();

        this.changes = changes;

        this.changesByDeltaType = CollectionFactory.createStringMap();
        for (Delta deltaType : Delta.values()) {
            changesByDeltaType.put(deltaType.name(), CollectionFactory
                    .<ResourceGroupingChange> createLightweightList());
        }
        for (ResourceGroupingChange change : changes) {
            changesByDeltaType.get(change.getDelta().name()).add(change);
        }
    }

    @Override
    protected void dispatch(ResourceGroupingChangedHandler handler) {
        handler.onResourceCategoriesChanged(this);
    }

    @Override
    public GwtEvent.Type<ResourceGroupingChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public LightweightList<ResourceGroupingChange> getChanges() {
        return changes;
    }

    /**
     * Return the changes for the given {@link Delta}.
     */
    public LightweightList<ResourceGroupingChange> getChanges(Delta deltaType) {
        return changesByDeltaType.get(deltaType.name());
    }

}