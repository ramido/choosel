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

import org.thechiselgroup.choosel.client.util.Disposable;

import com.google.gwt.event.shared.HandlerRegistration;

public class ResourceEventsForwarder implements ResourcesAddedEventHandler,
        ResourceRemovedEventHandler, Disposable {

    private HandlerRegistration addHandlerRegistration;

    private HandlerRegistration removeHandlerRegistration;

    private final ResourceSet source;

    private final ResourceContainer target;

    public ResourceEventsForwarder(ResourceSet source, ResourceContainer target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void dispose() {
        addHandlerRegistration.removeHandler();
        addHandlerRegistration = null;
        removeHandlerRegistration.removeHandler();
        removeHandlerRegistration = null;
    }

    public void init() {
        addHandlerRegistration = source.addHandler(ResourcesAddedEvent.TYPE,
                this);
        removeHandlerRegistration = source.addHandler(
                ResourceRemovedEvent.TYPE, this);
    }

    @Override
    public void onResourcesAdded(ResourcesAddedEvent e) {
        target.add(e.getChangedResource());
    }

    @Override
    public void onResourceRemoved(ResourceRemovedEvent e) {
        target.remove(e.getChangedResource());
    }
}