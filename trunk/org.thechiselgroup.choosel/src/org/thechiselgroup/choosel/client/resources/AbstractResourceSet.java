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
import java.util.Collection;
import java.util.List;

import org.thechiselgroup.choosel.client.label.DefaultHasLabel;
import org.thechiselgroup.choosel.client.label.HasLabel;
import org.thechiselgroup.choosel.client.label.LabelChangedEventHandler;
import org.thechiselgroup.choosel.client.util.SingleItemCollection;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractResourceSet implements ResourceSet {

    protected transient HandlerManager eventBus;

    private HasLabel labelDelegate;

    public AbstractResourceSet() {
        this.eventBus = new HandlerManager(this);
        this.labelDelegate = new DefaultHasLabel(this);
    }

    @Override
    public boolean add(Resource resource) {
        assert resource != null;

        return addAll(new SingleItemCollection<Resource>(resource));
    }

    @Override
    public boolean addAll(Collection<? extends Resource> resources) {
        assert resources != null;

        List<Resource> addedResources = new ArrayList<Resource>();
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
    public boolean contains(Object o) {
        if (!(o instanceof Resource)) {
            return false;
        }

        return contains((Resource) o);
    }

    @Override
    public boolean containsAll(Collection<?> resources) {
        for (Object o : resources) {
            if (!contains(o)) {
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
            List<Resource> addedResources);

    protected abstract void doRemove(Resource resource,
            List<Resource> removedResources);

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
    public boolean remove(Object o) {
        assert o != null;

        if (!(o instanceof Resource)) {
            return false;
        }

        return removeAll(new SingleItemCollection<Resource>((Resource) o));
    }

    @Override
    public boolean removeAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList();
        for (Object o : resources) {
            if (contains(o)) {
                assert o instanceof Resource;
                Resource resource = (Resource) o;
                doRemove(resource, removedResources);
            }
        }

        if (!removedResources.isEmpty()) {
            eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
        }

        return !removedResources.isEmpty();
    }

    // TODO implement faster retains if both are default resource sets
    @Override
    public boolean retainAll(Collection<?> resources) {
        assert resources != null;

        List<Resource> removedResources = new ArrayList<Resource>();
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

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return toList().toArray(a);
    }

}
