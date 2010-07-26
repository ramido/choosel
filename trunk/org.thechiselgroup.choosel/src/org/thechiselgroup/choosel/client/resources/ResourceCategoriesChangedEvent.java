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
package org.thechiselgroup.choosel.client.resources;

import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

public class ResourceCategoriesChangedEvent extends
        GwtEvent<ResourceCategoriesChangedHandler> {

    public static final GwtEvent.Type<ResourceCategoriesChangedHandler> TYPE = new GwtEvent.Type<ResourceCategoriesChangedHandler>();

    private final Set<ResourceCategoryChange> changes;

    public ResourceCategoriesChangedEvent(Set<ResourceCategoryChange> changes) {
        assert changes != null;
        assert !changes.isEmpty();

        this.changes = changes;
    }

    @Override
    protected void dispatch(ResourceCategoriesChangedHandler handler) {
        handler.onResourceCategoriesChanged(this);
    }

    @Override
    public GwtEvent.Type<ResourceCategoriesChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public Set<ResourceCategoryChange> getChanges() {
        return changes;
    }

}