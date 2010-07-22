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
public class SwitchingResourceSet extends DelegatingResourceSet {

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
        this.eventBus = new HandlerManager(this);
    }

    private void addEventHandlersToDelegate() {
        resourcesAddedToDelegateHandlerRegistration = delegate.addHandler(
                ResourcesAddedEvent.TYPE, resourcesAddedToDelegateHandler);
        resourcesRemovedFromDelegateHandlerRegistration = delegate
                .addHandler(ResourcesRemovedEvent.TYPE,
                        resourcesRemovedFromDelegateHandler);
    }

    @Override
    public <H extends ResourceEventHandler> HandlerRegistration addHandler(
            Type<H> type, H handler) {
        return eventBus.addHandler(type, handler);
    }

    private void fireResourceChanges(ResourceSet newDelegate,
            ResourceSet oldDelegate) {

        assert oldDelegate != null;
        assert newDelegate != null;

        List<Resource> removedResources = new ArrayList<Resource>();
        removedResources.addAll(oldDelegate.toList());
        removedResources.removeAll(newDelegate.toList());
        if (!removedResources.isEmpty()) {
            fireResourcesRemoved(removedResources);
        }

        List<Resource> addedResources = new ArrayList<Resource>();
        addedResources.addAll(newDelegate.toList());
        addedResources.removeAll(oldDelegate.toList());
        if (!addedResources.isEmpty()) {
            fireResourcesAdded(addedResources);
        }
    }

    private void fireResourcesAdded(List<Resource> addedResources) {
        eventBus.fireEvent(new ResourcesAddedEvent(this, addedResources));
    }

    private void fireResourcesRemoved(List<Resource> removedResources) {
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

        removeEventHandlersFromDelegate();

        ResourceSet oldDelegate = this.delegate;
        this.delegate = newDelegate;

        fireResourceChanges(newDelegate, oldDelegate);

        addEventHandlersToDelegate();
    }
}