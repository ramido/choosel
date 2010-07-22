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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class CombinedResourceSet extends DelegatingResourceSet {

    static class ResourceSetElement {

        private HandlerRegistration addHandlerRegistration;

        private HandlerRegistration removeHandlerRegistration;

        private ResourceSet resourceSet;

        public ResourceSetElement(ResourceSet resourceSet) {
            this.resourceSet = resourceSet;
        }

        public void removeHandlers() {
            addHandlerRegistration.removeHandler();
            removeHandlerRegistration.removeHandler();
        }

    }

    private List<ResourceSetElement> containedResourceSets = new ArrayList<ResourceSetElement>();

    private ResourcesAddedEventHandler resourceAddedHandler = new ResourcesAddedEventHandler() {
        @Override
        public void onResourcesAdded(ResourcesAddedEvent e) {
            addAll(e.getAddedResources());
        }
    };

    private ResourcesRemovedEventHandler resourceRemovedHandler = new ResourcesRemovedEventHandler() {
        @Override
        public void onResourcesRemoved(ResourcesRemovedEvent e) {
            ResourceSet uniqueResources = new DefaultResourceSet();
            // test that not contained in other resources
            for (Resource resource : e.getRemovedResources()) {
                boolean isUnique = true;
                for (ResourceSetElement resourceSetElement : containedResourceSets) {
                    if (resourceSetElement.resourceSet.contains(resource)) {
                        isUnique = false;
                        break;
                    }
                }
                if (isUnique) {
                    uniqueResources.add(resource);
                }
            }
            removeAll(uniqueResources);
        }
    };

    private final HandlerManager setEventBus;

    public CombinedResourceSet(ResourceSet delegate) {
        super(delegate);

        this.setEventBus = new HandlerManager(this);
    }

    public void addResourceSet(ResourceSet resourceSet) {
        if (containsResourceSet(resourceSet)) {
            return;
        }

        ResourceSetElement resourceSetElement = new ResourceSetElement(
                resourceSet);
        containedResourceSets.add(resourceSetElement);
        addAll(resourceSet);

        resourceSetElement.addHandlerRegistration = resourceSet.addHandler(
                ResourcesAddedEvent.TYPE, resourceAddedHandler);
        resourceSetElement.removeHandlerRegistration = resourceSet.addHandler(
                ResourcesRemovedEvent.TYPE, resourceRemovedHandler);

        setEventBus.fireEvent(new ResourceSetAddedEvent(resourceSet));
    }

    public <H extends ResourceSetEventHandler> HandlerRegistration addSetEventsHandler(
            Type<H> type, H handler) {
        return setEventBus.addHandler(type, handler);
    }

    @Override
    public void clear() {
        List<ResourceSetElement> toRemove = new ArrayList<ResourceSetElement>(
                containedResourceSets);

        for (ResourceSetElement resourceSetElement : toRemove) {
            removeResourceSet(resourceSetElement.resourceSet);
        }

        assert toList().isEmpty();
    }

    // TODO specify behavior in javadoc
    public boolean containsResourceSet(ResourceSet resourceSet) {
        assert resourceSet != null;
        return getResourceSetElement(resourceSet) != null;
    }

    private ResourceSetElement getResourceSetElement(ResourceSet resources) {
        for (ResourceSetElement resourceSetElement : containedResourceSets) {
            if (resourceSetElement.resourceSet.equals(resources)) {
                return resourceSetElement;
            }
        }
        return null;
    }

    public List<ResourceSet> getResourceSets() {
        List<ResourceSet> resourceSets = new ArrayList<ResourceSet>();

        for (ResourceSetElement element : containedResourceSets) {
            resourceSets.add(element.resourceSet);
        }

        return resourceSets;
    }

    /**
     * @return false: should only be changed by adding / removing resource sets
     *         via {@link #addResourceSet(ResourceSet)} and
     *         {@link #removeResourceSet(ResourceSet)}
     */
    @Override
    public boolean isModifiable() {
        return false;
    }

    public void removeResourceSet(ResourceSet resourceSet) {
        ResourceSetElement resourceSetElement = getResourceSetElement(resourceSet);

        if (resourceSetElement == null) {
            return;
        }

        containedResourceSets.remove(resourceSetElement);
        resourceSetElement.removeHandlers();

        List<Resource> toRemove = new ArrayList<Resource>(resourceSet.toList());
        for (ResourceSetElement rse : containedResourceSets) {
            toRemove.removeAll(rse.resourceSet.toList());
        }

        removeAll(toRemove);

        setEventBus.fireEvent(new ResourceSetRemovedEvent(resourceSet));
    }

}
