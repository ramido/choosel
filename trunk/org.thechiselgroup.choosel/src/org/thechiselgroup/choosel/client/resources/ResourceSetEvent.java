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

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

public abstract class ResourceSetEvent<H extends ResourceEventHandler> extends
        GwtEvent<H> {

    private final List<Resource> changedResources;

    private final ResourceSet target;

    public ResourceSetEvent(ResourceSet target, List<Resource> changedResources) {
        this.changedResources = changedResources;
        this.target = target;
    }

    public Resource getChangedResource() {
        return changedResources.get(0);
    }

    public List<Resource> getChangedResources() {
        return changedResources;
    }

    public ResourceSet getTarget() {
        return target;
    }

}