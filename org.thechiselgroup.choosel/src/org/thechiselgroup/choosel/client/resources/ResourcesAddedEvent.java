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

import org.thechiselgroup.choosel.client.util.collections.LightweightList;

import com.google.gwt.event.shared.GwtEvent;

public class ResourcesAddedEvent extends
        ResourceSetEvent<ResourcesAddedEventHandler> {

    public static final GwtEvent.Type<ResourcesAddedEventHandler> TYPE = new GwtEvent.Type<ResourcesAddedEventHandler>();

    public ResourcesAddedEvent(ResourceSet target,
            LightweightList<Resource> addedResources) {

        super(target, addedResources);
    }

    @Override
    protected void dispatch(ResourcesAddedEventHandler handler) {
        handler.onResourcesAdded(this);
    }

    public LightweightList<Resource> getAddedResources() {
        return affectedResources;
    }

    @Override
    public GwtEvent.Type<ResourcesAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

}