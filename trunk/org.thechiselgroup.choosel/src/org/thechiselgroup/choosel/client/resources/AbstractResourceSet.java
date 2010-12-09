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
import org.thechiselgroup.choosel.client.util.collections.LightweightCollection;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;
import org.thechiselgroup.choosel.client.util.event.PrioritizedHandlerManager;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractResourceSet implements ResourceSet {

    protected transient PrioritizedHandlerManager handlerManager;

    private HasLabel labelDelegate;

    public AbstractResourceSet() {
        handlerManager = new PrioritizedHandlerManager(this);
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
            handlerManager.fireEvent(ResourceSetChangedEvent
                    .createResourcesAddedEvent(this, addedResources));
        }

        return !addedResources.isEmpty();
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourceSetChangedEventHandler handler) {

        return handlerManager.addHandler(ResourceSetChangedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addLabelChangedEventHandler(
            LabelChangedEventHandler eventHandler) {

        return labelDelegate.addLabelChangedEventHandler(eventHandler);
    }

    /**
     * Intersection calculation based on contains.
     * 
     * @param iteratedResources
     *            Resources that are iterated over. Usually this set should be
     *            smaller than the other one, unless its contains check is slow.
     * @param containmentCheckedResources
     *            Resources on which contains is called. Make sure contains is
     *            fast for this set.
     * 
     * @see #getIntersection(Iterable)
     */
    private LightweightList<Resource> calculateIntersection(
            Iterable<Resource> iteratedResources,
            LightweightCollection<Resource> containmentCheckedResources) {

        LightweightList<Resource> result = CollectionFactory
                .createLightweightList();

        for (Resource resource : iteratedResources) {
            if (containmentCheckedResources.contains(resource)) {
                result.add(resource);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        removeAll(toList());
    }

    @Override
    public boolean contains(Resource resource) {
        assert resource != null;
        return containsResourceWithUri(resource.getUri());
    }

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

    protected abstract void doAdd(Resource resource,
            LightweightList<Resource> addedResources);

    protected abstract void doRemove(Resource resource,
            LightweightList<Resource> removedResources);

    @Override
    public Resource getFirstResource() {
        assert !isEmpty();
        return toList().get(0);
    }

    /**
     * <b>FOR TEST USAGE</b>
     */
    public int getHandlerCount(Type<?> type) {
        return handlerManager.getHandlerCount(type);
    }

    @Override
    public LightweightList<Resource> getIntersection(
            LightweightCollection<Resource> resources) {

        assert resources != null;

        // special case: one collection is empty
        if (isEmpty() || resources.isEmpty()) {
            return CollectionFactory.createLightweightList();
        }

        /*
         * special case: other collection is resource set. Resource sets are
         * assumed to be hash-based and to use a fast contains method, so we can
         * just iterate over the smaller set and check for containment.
         */
        if (size() < resources.size() && resources instanceof ResourceSet) {
            return calculateIntersection(this, resources);
        }

        return calculateIntersection(resources, this);
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
            handlerManager.fireEvent(ResourceSetChangedEvent
                    .createResourcesRemovedEvent(this, removedResources));
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
            handlerManager.fireEvent(ResourceSetChangedEvent
                    .createResourcesRemovedEvent(this, removedResources));
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

    // XXX what if resource is contained in several selected resource items?
    @Override
    public void switchContainment(ResourceSet resources) {
        // TODO fix: this fires several events
        assert resources != null;

        for (Resource resource : resources) {
            switchContainment(resource);
        }
    }

}
