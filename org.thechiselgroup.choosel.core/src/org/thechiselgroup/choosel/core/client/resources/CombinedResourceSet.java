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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.thechiselgroup.choosel.core.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.core.client.util.collections.LightweightList;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

// TODO violates LSP --> need for ReadableResourceSet, WriteableResourceSet
public class CombinedResourceSet extends DelegatingResourceSet {

    static class ResourceSetElement {

        private HandlerRegistration handlerRegistration;

        private ResourceSet resourceSet;

        public ResourceSetElement(ResourceSet resourceSet) {
            this.resourceSet = resourceSet;
        }

        public void removeHandler() {
            handlerRegistration.removeHandler();
        }

    }

    private List<ResourceSetElement> containedResourceSets = new ArrayList<ResourceSetElement>();

    private ResourceSetChangedEventHandler resourceSetChangedHandler = new ResourceSetChangedEventHandler() {

        @Override
        public void onResourceSetChanged(ResourceSetChangedEvent event) {
            // TODO introduce combined add/remove processing
            /*
             * addAll is called to keep the add iteration atomic (one event
             * should get fired)
             */
            addAll(event.getAddedResources());

            // BEGIN old remove handling
            ResourceSet uniqueResources = new DefaultResourceSet();
            // test that not contained in other resources
            for (Resource resource : event.getRemovedResources()) {
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

    private final HandlerManager eventBus;

    public CombinedResourceSet(ResourceSet delegate) {
        super(delegate);

        eventBus = new HandlerManager(this);
    }

    public HandlerRegistration addEventHandler(
            ResourceSetAddedEventHandler handler) {
        return eventBus.addHandler(ResourceSetAddedEvent.TYPE, handler);
    }

    public HandlerRegistration addEventHandler(
            ResourceSetRemovedEventHandler handler) {
        return eventBus.addHandler(ResourceSetRemovedEvent.TYPE, handler);
    }

    public void addResourceSet(ResourceSet resourceSet) {
        if (containsResourceSet(resourceSet)) {
            return;
        }

        ResourceSetElement resourceSetElement = new ResourceSetElement(
                resourceSet);
        containedResourceSets.add(resourceSetElement);
        addAll(resourceSet);

        resourceSetElement.handlerRegistration = resourceSet
                .addEventHandler(resourceSetChangedHandler);

        eventBus.fireEvent(new ResourceSetAddedEvent(resourceSet));
    }

    private LightweightList<Resource> calculateResourcesToRemove(
            ResourceSet resourceSet) {

        LightweightList<Resource> toRemove = CollectionFactory
                .createLightweightList();
        for (Resource resource : resourceSet) {
            boolean contained = false;
            for (Iterator<ResourceSetElement> it = containedResourceSets
                    .iterator(); !contained && it.hasNext();) {
                ResourceSetElement rse = it.next();
                if (rse.resourceSet.contains(resource)) {
                    contained = true;
                }
            }

            if (!contained) {
                toRemove.add(resource);
            }
        }
        return toRemove;
    }

    @Override
    public void clear() {
        List<ResourceSetElement> toRemove = new ArrayList<ResourceSetElement>(
                containedResourceSets);

        for (ResourceSetElement resourceSetElement : toRemove) {
            removeResourceSet(resourceSetElement.resourceSet);
        }

        assert isEmpty();
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
        resourceSetElement.removeHandler();

        removeAll(calculateResourcesToRemove(resourceSet));

        eventBus.fireEvent(new ResourceSetRemovedEvent(resourceSet));
    }

}
