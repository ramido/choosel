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

import org.thechiselgroup.choosel.client.label.DefaultHasLabel;
import org.thechiselgroup.choosel.client.label.HasLabel;
import org.thechiselgroup.choosel.client.label.LabelChangedEventHandler;
import org.thechiselgroup.choosel.client.util.SingleItemCollection;
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractResourceSet implements ResourceSet {

    protected transient HandlerManager eventBus;

    private HasLabel labelDelegate;

    public AbstractResourceSet() {
        eventBus = new HandlerManager(this);
        labelDelegate = new DefaultHasLabel(this);
    }

    @Override
    public boolean add(Resource resource) {
        assert resource != null;

        return addAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public boolean addAll(Iterable<Resource> resources) {
        assert resources != null;

        LightweightList<Resource> addedResources = CollectionFactory
                .createLightweightList();

        for (Resource resource : resources) {
            if (!contains(resource)) {
                doAdd(resource, addedResources);
            }
        }

        if (!addedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
        }

        return !addedResources.isEmpty();
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {

        return eventBus.addHandler(ResourcesAddedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {

        return eventBus.addHandler(ResourcesRemovedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addLabelChangedEventHandler(
            LabelChangedEventHandler eventHandler) {

        return labelDelegate.addLabelChangedEventHandler(eventHandler);
    }

    @Override
    public void clear() {
        removeAll(toList());
    }

    @Override
    public abstract boolean contains(Resource resource);

    @Override
    public boolean containsAll(Iterable<Resource> resources) {
        for (Resource resource : resources) {
            if (!contains(resource)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final boolean containsEqualResources(ResourceSet other) {
        if (size() != other.size()) {
            return false;
        }

        return containsAll(other);
    }

    @Override
    public boolean containsResourceWithUri(String uri) {
        return getByUri(uri) != null;
    }

    protected abstract void doAdd(Resource resource,
            LightweightList<Resource> addedResources);

    protected abstract void doRemove(Resource resource,
            LightweightList<Resource> removedResources);

    @Override
    public Resource getFirstResource() {
        assert !isEmpty();
        return toList().get(0);
    }

    public int getHandlerCount(Type<?> type) {
        return eventBus.getHandlerCount(type);
    }

    @Override
    public String getLabel() {
        return labelDelegate.getLabel();
    }

    @Override
    public boolean hasLabel() {
        return labelDelegate.hasLabel();
    }

    @Override
    public boolean isModifiable() {
        return true;
    }

    @Override
    public boolean remove(Resource resource) {
        assert resource != null;

        return removeAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public boolean removeAll(Iterable<Resource> resources) {
        assert resources != null;

        LightweightList<Resource> removedResources = CollectionFactory
                .createLightweightList();

        for (Resource resource : resources) {
            if (contains(resource)) {
                doRemove(resource, removedResources);
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    @Override
    public boolean removeAll(ResourceSet resources) {
        return removeAll((Iterable<Resource>) resources);
    }

    // TODO implement faster retains if both are default resource sets
    @Override
    public boolean retainAll(ResourceSet resources) {
        assert resources != null;

        LightweightList<Resource> removedResources = CollectionFactory
                .createLightweightList();
        for (Resource resource : toList()) {
            if (!resources.contains(resource)) {
                doRemove(resource, removedResources);
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    @Override
    public void setLabel(String label) {
        labelDelegate.setLabel(label);
    }

    @Override
    public final void switchContainment(Resource resource) {
        assert resource != null;

        if (contains(resource)) {
            remove(resource);
            assert !contains(resource);
        } else {
            add(resource);
            assert contains(resource);
        }
    }

    @Override
    public void switchContainment(ResourceSet resources) {
        // TODO fix: this fires several events
        assert resources != null;

        for (Resource resource : resources) {
            switchContainment(resource);
        }
    }

}
