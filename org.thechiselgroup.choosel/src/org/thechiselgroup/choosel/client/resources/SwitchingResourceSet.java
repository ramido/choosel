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
import org.thechiselgroup.choosel.client.util.collections.CollectionFactory;
import org.thechiselgroup.choosel.client.util.collections.LightweightList;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * <p>
 * Resource set that delegates most operations to a delegate resource set. The
 * event handlers, however, are managed by the switching resource set. The
 * delegate of the resource set can be changed. If this happens, appropriate
 * ResourcesAddedEvents and ResourcesRemovedEvents events are generated that
 * reflect the changes in the contained resources because of the delegate set
 * exchange.
 * </p>
 * <p>
 * Labeling is ignored here and in fact just delegated.
 * </p>
 * 
 * @author Lars Grammel
 */
// TODO separate labeling concept from resource set
public class SwitchingResourceSet extends DelegatingResourceSet implements
        Disposable {

    protected transient HandlerManager eventBus;

    private ResourcesAddedEventHandler resourcesAddedToDelegateHandler = new ResourcesAddedEventHandler() {
        @Override
        public void onResourcesAdded(ResourcesAddedEvent e) {
            forwardEvent(e);
        }
    };

    private ResourcesRemovedEventHandler resourcesRemovedFromDelegateHandler = new ResourcesRemovedEventHandler() {
        @Override
        public void onResourcesRemoved(ResourcesRemovedEvent e) {
            forwardEvent(e);
        }
    };

    private HandlerRegistration resourcesAddedToDelegateHandlerRegistration;

    private HandlerRegistration resourcesRemovedFromDelegateHandlerRegistration;

    public SwitchingResourceSet() {
        super(NullResourceSet.NULL_RESOURCE_SET);
        eventBus = new HandlerManager(this);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesAddedEventHandler handler) {

        assert handler != null;
        return eventBus.addHandler(ResourcesAddedEvent.TYPE, handler);
    }

    public HandlerRegistration addEventHandler(
            ResourceSetDelegateChangedEventHandler handler) {

        assert handler != null;
        return eventBus.addHandler(ResourceSetDelegateChangedEvent.TYPE,
                handler);
    }

    @Override
    public HandlerRegistration addEventHandler(
            ResourcesRemovedEventHandler handler) {

        assert handler != null;
        return eventBus.addHandler(ResourcesRemovedEvent.TYPE, handler);
    }

    private void addEventHandlersToDelegate() {
        resourcesAddedToDelegateHandlerRegistration = delegate
                .addEventHandler(resourcesAddedToDelegateHandler);
        resourcesRemovedFromDelegateHandlerRegistration = delegate
                .addEventHandler(resourcesRemovedFromDelegateHandler);
    }

    @Override
    public void dispose() {
        removeEventHandlersFromDelegate();
    }

    private void fireDelegateChanged(ResourceSet newDelegate) {
        eventBus.fireEvent(new ResourceSetDelegateChangedEvent(newDelegate));
    }

    private void fireResourceChanges(ResourceSet newDelegate,
            ResourceSet oldDelegate) {

        /*
         * the add event is fire before the remove event such that the
         * intermediate state does not include an empty set in some cases.
         */

        assert oldDelegate != null;
        assert newDelegate != null;

        LightweightList<Resource> addedResources = CollectionFactory
                .createLightweightList();
        for (Resource resource : newDelegate) {
            if (!oldDelegate.contains(resource)) {
                addedResources.add(resource);
            }
        }
        if (!addedResources.isEmpty()) {
            fireResourcesAdded(addedResources);
        }

        LightweightList<Resource> removedResources = CollectionFactory
                .createLightweightList();
        for (Resource resource : oldDelegate) {
            if (!newDelegate.contains(resource)) {
                removedResources.add(resource);
            }
        }
        if (!removedResources.isEmpty()) {
            fireResourcesRemoved(removedResources);
        }
    }

    private void fireResourcesAdded(LightweightList<Resource> addedResources) {
        eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
    }

    private void fireResourcesRemoved(LightweightList<Resource> removedResources) {
        eventBus.fireEvent(new ResourcesRemovedEvent(this, removedResources));
    }

    protected void forwardEvent(ResourcesAddedEvent e) {
        fireResourcesAdded(e.getAddedResources());
    }

    protected void forwardEvent(ResourcesRemovedEvent e) {
        fireResourcesRemoved(e.getRemovedResources());
    }

    public boolean hasDelegate() {
        return !NullResourceSet.isNullResourceSet(delegate);
    }

    private void removeEventHandlersFromDelegate() {
        if (hasDelegate()) {
            resourcesAddedToDelegateHandlerRegistration.removeHandler();
            resourcesRemovedFromDelegateHandlerRegistration.removeHandler();
        }

        resourcesAddedToDelegateHandlerRegistration = null;
        resourcesRemovedFromDelegateHandlerRegistration = null;
    }

    public void setDelegate(ResourceSet newDelegate) {
        if (newDelegate == null) {
            newDelegate = NullResourceSet.NULL_RESOURCE_SET;
        }

        if (newDelegate == delegate) {
            return;
        }

        removeEventHandlersFromDelegate();

        ResourceSet oldDelegate = delegate;
        delegate = newDelegate;

        fireDelegateChanged(newDelegate);
        fireResourceChanges(newDelegate, oldDelegate);

        addEventHandlersToDelegate();
    }
}