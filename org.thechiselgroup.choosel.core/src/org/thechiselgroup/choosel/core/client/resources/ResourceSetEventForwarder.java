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

import org.thechiselgroup.choosel.core.client.util.Disposable;

import com.google.gwt.event.shared.HandlerRegistration;

public class ResourceSetEventForwarder implements
        ResourceSetChangedEventHandler, Disposable {

    private HandlerRegistration handlerRegistration;

    private final ResourceSet source;

    private final ResourceContainer target;

    public ResourceSetEventForwarder(ResourceSet source,
            ResourceContainer target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void dispose() {
        handlerRegistration.removeHandler();
        handlerRegistration = null;
    }

    public void init() {
        handlerRegistration = source.addEventHandler(this);
    }

    @Override
    public void onResourceSetChanged(ResourceSetChangedEvent event) {
        // XXX performance - should be a single operation on target
        target.addAll(event.getAddedResources());
        target.removeAll(event.getRemovedResources());
    }
}